# Enforce the singleton property with a private constructor or an enum type
# 用私有构造器或枚举类型强化Singletion属性


---

　　本章讲解的其实是 **单例模式**　的实现方式，单例模式一般的实现是有以下几种
　　

##私有化构造函数的形式
 　通过私有化构造函数，使客户端不能通过　**new** 关键字的形式来创建对象，同时我们需要提供静态方法来使得外部能够通过调用调用静态方法取得该类的唯一实例，其中这又分成了经常说的 饿汉式 和懒汉式的实现
```
// 饿汉式，就是一口吃个饱，在类被加载器加载的时候 就创建了 这个实例
    public class Elvis{
        public static final Elvis INSTANCE = new Elvis();
        private Elvis(){...}
        public static Evlvis getSingleInstance(){
            return INSTANCE;
        }
    }
```

```
// 懒汉式，就是等到客户端调用 方法的时候 如果不存在实例，才去创建实例，其中一般又加上 DCL(双重判断)
    public class Elvis{
        public static final Elvis INSTANCE；
        private Elvis(){...}
        public static Evlvis getSingleInstance(){
            if (INSTANCE == null){//如果不存在
                sychronized(Elvis.class){//加锁
                    if(INSTANCE == null){//从判断为null到执行sychronized的过程中，另外一个线程已经创建了实例，所以又加了一个判断
                        INSTANCE = new Elvis();
                    }
                }
            }
            return INSTANCE;
        }
    }
```

　　其中，在　**《Android源码设计模式解析与实战** 中提及 在JDK1.5之前，由于允许处理器乱序执行，会导致 为实例初始化成员字段 ，和 为 INSTANCE 对象指向分配的内存空间 的顺序不一定，所以可能会出现调用时访问字段依然为空等状况，在 JDK1.5 后，SUN 调整了JVM ,具体化了 **volatile** 关键字，所以 写成
```
public volatile static final Elvis INSTANCE；
```
保证，每次 INSTANCE duixiang 对象都是从 主内存读取的。

##使用 枚举实现
　　以上的方式，存在一个问题是　通过反序列的形式，依然能够创建多个实例，如果要杜绝这种方式，需要为类　加上如下方法
```
    privagte Object readResolve(） throws ObjectStreamException{
        return sInstance;
    }
```

枚举的方式
```
    public Enum Evlis{
        INSTANCE;
        public void leaveTheBuilding(){ ...}
    }
```

##静态内部类的方式
　　目前还有另外一种，更优雅简单的实现，既能保证线程安全，不需要加入 sychornized 的方式，并且运用的也比较广泛，那就是好使用 静态内部类 的方式,该方式的关键点 在于，内部静态类只有在第一次引用的使用才被加载，我们在内部静态类的静态字段中 实例化需要的实例。
```
public class Singleton  
{  
    private Singleton(){ }  
      
    public static Singleton getInstance()  
    {  
        return Nested.instance;       
    }  
      
    //在第一次被引用时被加载  
    static class Nested  
    {  
        private static Singleton instance = new Singleton();  
    }  
      
    public static void main(String args[])  
    {  
        Singleton instance = Singleton.getInstance();  
        Singleton instance2 = Singleton.getInstance();  
        System.out.println(instance == instance2);  
    }  
}  
```


