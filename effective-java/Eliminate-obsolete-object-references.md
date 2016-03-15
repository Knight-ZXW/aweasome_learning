# Eliminate obsolete object references
# 清楚过期的引用


---
　　Java虚拟机具有自动垃圾回收的功能，程序员不需要像 **C/C++** 一样，手动释放内存，但是这并不意味着你不需要注意不再需要使用的对象的内存空间的释放问题。
##只要类是自己管理内存，程序员就应该警惕内存泄露问题
  考虑以下一端简单的栈实现的代码
```
    public class Stack{
        private Object[] elements;
        private size = 0;
        private static final int DEFAULT_INITAL_CAPACITY = 16;
        public Stack(){
        elements = new Object[DEFAULT_INITAL_CAOACITY];
        }
        public void push(Object e){
            ensureCapcity();
            elements[size++] = e;
      }
        public Object pop(){
            if (size == 0) throw new EmptyStackException();
            return elements[--size];
        }
        
        private void ensureCapacity(){
            if (elements.length == size){
                elements = Array.copyOf(elements,2*size +1);
            }
        }
    }
```
　　以上这段代码存在的问题再与，当栈增加，再收缩时，被pop出的 元素 实际上是不会被 Java垃圾收集器回收的，因为从GC的角度看，elements数组一直持有那个对象的强引用，需要你认为pop出的元素是"过期的","不会再被使用"的。以上代码的解决方案是在pop操作时，及时的将 elements 对该对象的引用去掉，即将 elemtns[size] = null 指向null。
```
 public Object pop(){
            if (size == 0) throw new EmptyStackException();
            Object result = elements[--size];
            elements[size] = null;
            return result;
        }
```
##不要被类似的问题困扰，而导致代码编写"过分小心"
　　当程序员第一次被类似这样的问题困扰的时候，他们往往会过分小心；对于每一个对象引用，一旦不再使用它，就把它清空，这是没有必要的，这样反而会把代码弄的混乱。清空对象的引用应该是一种例外，而不是一种规范行为。清楚过期引用最好的办法是让包含该引用的变量结束其生命周期。（在Android开发中，如果不注意对象的生命周期，是很容易造成内存泄露的，特别是 Activity  Fragment 这类对象）
##对象缓存的实现应该注意的点
　　内存泄露的另一个常见的来源是缓存。一旦你把对象引用放到缓存中，这也意味你应该能够管理好缓存对象的生命周期，如果你把它遗忘的话就很容易出问题。对于这类问题，有几种可能的解决方案，如果你正好要实现这样的缓存；只要在缓存只外存在某个项的键的引用，该项就有意义，那么久可以用WeakHashMap代码缓存(弱引用对象集合);当缓存中的项过期后，它们就会被自动删除。记住只有当所要的缓存项的生命周期是有该键的外部引用而不是由其值决定时，WeakHashMap才有作用
　　更为常见的情形是，"缓存项的生命周期是否意义"，并不是很容易确定，随着时间的推移，其中的项会变的越来越没有价值，这种情况下，缓存应该时不时的清楚掉没有用的项。这项清楚工作可以由一个后台线程（可能是Timer或者ScheduledThreadPoolExecutor）来完成，removeEldestEntry  方法可以很容易地实现后一种方案。对于更加复杂的缓存，必须直接使用 java.lang.ref。
## 监听器、回调
　　监听器、回调的使用，如果没有显示地取消注册，那么除非你采取某些动作，不然这些监听器就会一致聚集。确保回调立即被当做垃圾回收的最佳方法是只保存它们的弱引用。例如你在被监听的对象中维护一个Weak数组用于保存这些监听者，这样就不会存在强引用而造成的问题。在基于 观察者模式实现的 事件总线通信框架中，一般会以弱引用的形式持有订阅者，再者 像Activity、Fragment 这类生命周期严格的对象，可以在合适的回调中，从订阅者中将自己 unRegister(从队列中移除)

##内存泄露的结果
　内存泄露的无限堆积造成的结果就是内存溢出，只有此时程序才会奔溃，在Android 开发中，内存是有限的资源，应该时刻注意内存泄露的情况，可以考虑使用 **Bugly** 框架帮你分析你项目中发生内存泄露发生的地方，具体使用请 google。



