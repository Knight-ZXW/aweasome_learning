Django Signal 解析
@(Django)[signal]

   Siganl 是 Django  框架中提供的一个 “信号分发器”，其实就是设计模式中经常提到的 观察者模式的一个应用。Django 中Siganl 机制的典型应用是，框架为 Models 创建了 **pre_save** **post_save**等 与模型的某些方法调用相关联的信号，如 pre_save 和 post_save 分别 会在 Modle 的 save()方法的调用之前和之后 通知观察者。
   
## Signal 机制的实现方式

Siganl的源码位于 django **dispatch**包下，主要的代码位于 **dispatcher.py** 中。
    在 **dispatcher** 中 定义了 **Signal** 类，以及一个 用于 使用Python装饰器的方式来 连接 信号 以及信号接受者的方法 **receiver(signal,**kwargs)**

### f
  在**__init__**方法中，创建了用户保存 接收信号的对象的列表 **receivers**,

### connect方法
　　 **connect** 方法 用于 连接 信号 和 信号处理函数，类似的概念 相当于 为 某个事件（信号发出表示一个事件）注册观察者（处理函数）, 函数 参数中 receiver 就是 信号处理函数（函数也是对象，这太方便了），
  sender 表示 信号的发送者，比如 Django 框架中的 post_save() 这个 信号，任何一个模型 在save()函数调用之后都会发出这个信号，但是 我们只想关注 某一个模型 save()方法调用的事件发生，就可以指定sender 为 我们需要关注的模型类。
  　　**weak** 参数 表示  是否将 receiver 转换成 弱引用对象，Siganl 中默认 会将所有的 receiver 转成弱引用，所以 如果你的receiver是个局部对象的话，那么receiver 可能会被垃圾回收期回收，receiver 也就变成一个 dead_receiver 了，Siganl 会在 **connect** 和 **disconnect** 方法调用的时候，清楚 dead_receiver.
  　　**dispatch_uid**, 这个参数用于唯一标识这个receiver函数，主要的作用是防止 receiver函数被注册多次，这样会导致 receiver函数会执行多次，这可能是我们不想要的一个结果。

### disconnect 方法
　　**disconnect**方法 用于 断开  信号的接收器，函数内 首先 会生成 根据 sender 和 receiver 对象构造出的一个 标识 lookup_key，在遍历  receiver数组时，根据lookup_key 找到 需要disconnect 的receiver 然后从数组中删除这个receiver。 

###  send 和 send_robust
  **send** 和 **send_robust** 方法都是用于发送事件的函数，不同点在于 **send_robust** 函数中 会捕获 信号接收函数 发生的异常，添加到 返回的 responses数组中。
  ```
   responses = []
        if not self.receivers or self.sender_receivers_cache.get(sender) is NO_RECEIVERS:
            return responses

        # Call each receiver with whatever arguments it can accept.
        # Return a list of tuple pairs [(receiver, response), ... ].
        for receiver in self._live_receivers(sender):
            try:
                response = receiver(signal=self, sender=sender, **named)
            except Exception as err:
                if not hasattr(err, '__traceback__'):
                    err.__traceback__ = sys.exc_info()[2]
                responses.append((receiver, err))
            else:
                responses.append((receiver, response))
        return responses
  ```
### Siganl类的主要
```
class Signal(object):
    """
    Base class for all signals

    Internal attributes:

        receivers
            { receiverkey (id) : weakref(receiver) }
    """
    def __init__(self, providing_args=None, use_caching=False):
        """
        创建一个新的Signal
        providing_args 参数，指定这个Siganl 在发出事件（调用send方法）时，可以提供给观察者的信息参数
        比如 post_save（）会带上 对应的instance对象，以及update_fields等
        """
        self.receivers = []
        if providing_args is None:
            providing_args = []
        self.providing_args = set(providing_args)
        self.lock = threading.Lock()
        self.use_caching = use_caching
        # For convenience we create empty caches even if they are not used.
        # A note about caching: if use_caching is defined, then for each
        # distinct sender we cache the receivers that sender has in
        # 'sender_receivers_cache'. The cache is cleaned when .connect() or
        # .disconnect() is called and populated on send().
        self.sender_receivers_cache = weakref.WeakKeyDictionary() if use_caching else {}
        self._dead_receivers = False

    def connect(self, receiver, sender=None, weak=True, dispatch_uid=None):
       
        from django.conf import settings

        if dispatch_uid:
            lookup_key = (dispatch_uid, _make_id(sender))
        else:
            lookup_key = (_make_id(receiver), _make_id(sender))

        if weak:
            ref = weakref.ref
            receiver_object = receiver
            # Check for bound methods
            # 构造弱引用的的receiver
            if hasattr(receiver, '__self__') and hasattr(receiver, '__func__'):
                ref = WeakMethod
                receiver_object = receiver.__self__
            if sys.version_info >= (3, 4):
                receiver = ref(receiver)
                weakref.finalize(receiver_object, self._remove_receiver)
            else:
                receiver = ref(receiver, self._remove_receiver)

        with self.lock:
	        #clear掉 由于弱引用 已被垃圾回收期回收的receivers
            self._clear_dead_receivers()
            for r_key, _ in self.receivers:
                if r_key == lookup_key:
                    break
            else:
                self.receivers.append((lookup_key, receiver))
            self.sender_receivers_cache.clear()

    def disconnect(self, receiver=None, sender=None, weak=True, dispatch_uid=None):
       
        if dispatch_uid:
            lookup_key = (dispatch_uid, _make_id(sender))
        else:
            lookup_key = (_make_id(receiver), _make_id(sender))

        disconnected = False
        with self.lock:
            self._clear_dead_receivers()
            for index in range(len(self.receivers)):
                (r_key, _) = self.receivers[index]
                if r_key == lookup_key:
                    disconnected = True
                    del self.receivers[index]
                    break
            self.sender_receivers_cache.clear()
        return disconnected

    def has_listeners(self, sender=None):
        return bool(self._live_receivers(sender))

    def send(self, sender, **named):
      
        responses = []
        if not self.receivers or self.sender_receivers_cache.get(sender) is NO_RECEIVERS:
            return responses

        for receiver in self._live_receivers(sender):
            response = receiver(signal=self, sender=sender, **named)
            responses.append((receiver, response))
        return responses

    def send_robust(self, sender, **named):
     
        responses = []
        if not self.receivers or self.sender_receivers_cache.get(sender) is NO_RECEIVERS:
            return responses

        # Call each receiver with whatever arguments it can accept.
        # Return a list of tuple pairs [(receiver, response), ... ].
        for receiver in self._live_receivers(sender):
            try:
                response = receiver(signal=self, sender=sender, **named)
            except Exception as err:
                if not hasattr(err, '__traceback__'):
                    err.__traceback__ = sys.exc_info()[2]
                responses.append((receiver, err))
            else:
                responses.append((receiver, response))
        return responses

    def _clear_dead_receivers(self):
        # Note: caller is assumed to hold self.lock.
        if self._dead_receivers:
            self._dead_receivers = False
            new_receivers = []
            for r in self.receivers:
                if isinstance(r[1], weakref.ReferenceType) and r[1]() is None:
                    continue
                new_receivers.append(r)
            self.receivers = new_receivers

    def _live_receivers(self, sender):
        """
		过滤掉 已经被 垃圾回收的receiver
        """
        receivers = None
        # 如果使用了cache , 并且没有调用过_remove_receiver 函数 则去 sender_receivers_cache中查找
        if self.use_caching and not self._dead_receivers:
            receivers = self.sender_receivers_cache.get(sender)
            # We could end up here with NO_RECEIVERS even if we do check this case in
            # .send() prior to calling _live_receivers() due to concurrent .send() call.
            if receivers is NO_RECEIVERS:
                return []
        if receivers is None:
            with self.lock:
                self._clear_dead_receivers()
                senderkey = _make_id(sender)
                receivers = []
                for (receiverkey, r_senderkey), receiver in self.receivers:
                    if r_senderkey == NONE_ID or r_senderkey == senderkey:
                        receivers.append(receiver)
                if self.use_caching:
                    if not receivers:
                        self.sender_receivers_cache[sender] = NO_RECEIVERS
                    else:
                        # Note, we must cache the weakref versions.
                        self.sender_receivers_cache[sender] = receivers
        non_weak_receivers = []
        for receiver in receivers:
            if isinstance(receiver, weakref.ReferenceType):
                # Dereference the weak reference.
                receiver = receiver()
                if receiver is not None:
                    non_weak_receivers.append(receiver)
            else:
                non_weak_receivers.append(receiver)
        return non_weak_receivers

    def _remove_receiver(self, receiver=None):

        self._dead_receivers = True
```