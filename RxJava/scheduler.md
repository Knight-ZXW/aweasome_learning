
# RxJava-Scheduler

---
RxJava可以给Obserable操作符的链式过程中添加多线程功能，可以指定操作符（或者特定的Obserable）
在特定的调度器(Scheduler)上执行。
  RxJava有多种调度器类型可供择.
  >Schedulers.computatoin() 
一般用于计算任务，比如比如事件循环和回调处理。不要用来做IO操作，IO操作大部分的事件用于等待IO连接
的建立和数据的读取，而computation 内部是一个拥有固定线程数的线程池， 
并且默认线程数等于处理器的数量。
  >schedulers.immediate() 不指定线程池，在当前线程立即开始执行任务
  >scheduler.io 用于IO密集型任务，创建的是CachedThreadScheduler，内部调度的线程池会根据需要增加。
  >Schedulers.from(executor) 使用自己指定的Executor作为调度器
  >Schedulers.trampoline()当其他排队的任务完成后，在当前线程排队开始执行
tip:
> 可以用Scheduler调度运行自己的任务
```java
Worker worker = Schedulers.newThread().createWorker();
Subscription mySubscription = worker.schedule(new Action0() {
    @Override
    public void call() {
        while(!worker.isUnsubscribed()) {
            status = yourWork();
            //QUIT 是否退出循环的标志判断
            if(QUIT == status) { worker.unsubscribe(); }
        }
    }
});
```
> 延迟执行任务
```
zhuoxiuwu     发布    新文稿         
    
RxJava-Scheduler

RxJava

　　RxJava可以给Obserable操作符的链式过程中添加多线程功能，可以指定操作符（或者特定的Obserable）在特定的调度器(Scheduler)上执行。 
RxJava有多种调度器类型可供择.

Schedulers.computatoin() 一般用于计算任务，比如比如事件循环和回调处理。不要用来做IO操作，IO操作大部分的事件用于等待IO连接的建立和数据的读取，而computation 内部是一个拥有固定线程数的线程池， 并且默认线程数等于处理器的数量。 
schedulers.immediate() 不指定线程池，在当前线程立即开始执行任务 
scheduler.io 用于IO密集型任务，创建的是CachedThreadScheduler，内部调度的线程池会根据需要增加。 
Schedulers.from(executor) 使用自己指定的Executor作为调度器 
Schedulers.trampoline()当其他排队的任务完成后，在当前线程排队开始执行
tip:

可以用Scheduler调度运行自己的任务
Worker worker = Schedulers.newThread().createWorker();
Subscription mySubscription = worker.schedule(new Action0() {
    @Override
    public void call() {
        while(!worker.isUnsubscribed()) {
            status = yourWork();
            //QUIT 是否退出循环的标志判断
            if(QUIT == status) { worker.unsubscribe(); }
        }
    }
});
延迟执行任务
someScheduler.schedule(someAction, 500, TimeUnit.MILLISECONDS);
mcXiaoke-RxJava中文翻译

+
@zhuoxiuwu 2016-04-03 13:56 字数 阅读 0