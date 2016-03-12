# Avoid creating unnecessary objects
# 避免创建不必要的对象
---
　　这章讲的其实是 **Java** 语言的一些细节，比如
## 重用不可变的对象　　
```
    Stirng s = new String("hello");//每次都创建了一个新的对象，实际情况中，应该没人会这样写
    Stirng s = "hello"; 每次都重用的一个对象
```
## 重用已知不会被修改的对象
　　假设我们需要 检验一个人是否是出生在1946-1964年之间
```
    public class Person{
        private final Date birthDate;
        //Dont do this
        public boolean isBabyBoomer(){
            // Unecessary allocationof expensive object
            Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
            Data boomStart = gmtCal.getTime();
            gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
            Data boomEnd = gmtCal.getTime();
            return birthDate.compareTo(boomStart) >= 0 &&
                bitrthDate.compareTo(boomEnd) < 0;
        }
    }
```
　　在以上这个实例中，我们发现，每次每次方法的调用 都会创建 一个 Calendar 、一个TimeZone、和两个Date实例，而且我们只是需要 比较 Date而已，并且这个Date的值其实是不变，我们可以用一个静态的初始化器，避免这种效率低下的实现方式
```
    public class Person{
        private final Date birthDate;
        
        private static final Date BOOMT_START;
        private static final Date BOOMT_END;
        static{//我们在静态方法块中初始化这些字段
            Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            gmtCal.set(1946,Calendar.JANUARY,1,0,0,0);
            BOOMT_START = gmtCal.getTime();
            gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
            gmtCal.set(1965,Calendar.JANUARY,1,0,0,0);
            BOOMT_END = gmtCal.getTime();
        }

        //Dont do this
        public boolean isBabyBoomer(){
            return birthDate.compareTo(BOOM_START)>=0&&
            birthDate.compareTo(BOOM_END) <0;
    }
```
　　优化用，那些实例十中只会创建一次，当然如果isBodyBoomer()这个方法永远都不会被调用，那么这种方式又显得有点多余，之后会介绍延迟初始化的方式；
　　
## !!!重点,不要错误的认为本条目介绍的内容暗示着"创建对象的代价非常昂贵，我们应该尽可能地避免创建对象"
　　相反，由于小对象的构造器只做很少量的事情，所以它们的创建和销毁的代价都是非常廉价的，尤其是在现代的的ＪＶＭ实现上更是的如此。通过创建附加的对象，提升程序的清晰性、间接性、和功能性，这通常是件好事。
　　反之，通过维护不必要的线程池来避免创建兑现更并不是一种好的做法，除非对象池中的对象非常重量级比如 数据库连接池、线程池，这些对象创建的代价非常昂贵、因此重用这些对象就显得非常有意义。一般而言，维护自己的对象池，可能会把代码弄的非常乱，同时增加了内存的占用（除非你的对象池有非常好释放）。