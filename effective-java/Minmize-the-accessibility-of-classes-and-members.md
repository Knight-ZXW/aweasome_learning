
# Minmize the accessibility of classes and members
# 使类和成员的可访问性最小化
该篇章讲述的也是面向对象的原则之一：迪米特原则，也称最少知识原则。一个好的设计模块，对于使用
者来说应该隐藏内部数据和实现细节，通过简单的API 
把实现清晰的隔离开俩，这样可以有效的解除系统的各个模块之间的耦合关系，使得这些模块可以独立开发、
测试、优化、使用、理解和修改。
## Java语言中的实现细节
规则很简单，尽可能地使得每个类或者成员不被外界访问，对于顶层的（非嵌套）的类和接口，应该只有
两种可能的访问级别：**包级私有** 和 **公有** 
的，尽可能的封闭实现细节的接口，调用者和依赖者，只需要它需要的方法即可。
如果一个包级私有的类（或者接口）只是在某一个类的内部被用到，就应该考虑它成为唯一使用它的那个
类的私有嵌套类（内部类）。这样就可以将它的可访问范围从包中的所有类缩小到了使用它的那个类。**其实
，降低不必要的公有类的可访问性，比降低包级私有的顶级类要简单的多：因为公有类是包的API的一部分，而
包级私有则是这个包的实现的一部分**
实例域不应该是公有的，这里实例域可能比较难以理解，看以下代码
```
public class UnmodifiableArray {
       // 潜在安全漏洞
       public static final String[] VALUES = { "RED", "GREEN" };
       public static void main(String[] args) {
         UnmodifiableArray UF = new UnmodifiableArray();
         UF.VALUES[1] = "YELLO";//设置final数组成员
         System.out.println(UF);
      }
      //使用Guava
     @Override
     public String toString() {
         return Objects.toStringHelper(this).add("VALUES0", UnmodifiableArray.VALUES[0])
                 .add("VALUES1", UnmodifiableArray.VALUES[1]).toString();
     }
 }
```
这里的 **VALUES** 可以理解为作为类**UnmodifiableArray**的一个域，我们希望它是不可变的，但是我
们又将它设为公有的，这使它有潜在的被改变的风险。所以应该要确保公有静态ｆｉｎａｌ域锁引用的对象都
是不可变的。
zhuoxiuwu     发布    新文稿         
    
使类和成员的可访问性最小化

EffectiveJava

迪米特原则

　　该篇章讲述的也是面向对象的原则之一：迪米特原则，也称最少知识原则。一个好的设计模块，对于使用者来说应该隐藏内部数据和实现细节，通过简单的API 把实现清晰的隔离开俩，这样可以有效的解除系统的各个模块之间的耦合关系，使得这些模块可以独立开发、测试、优化、使用、理解和修改。

Java语言中的实现细节

　　规则很简单，尽可能地使得每个类或者成员不被外界访问，对于顶层的（非嵌套）的类和接口，应该只有两种可能的访问级别：包级私有 和 公有 的，尽可能的封闭实现细节的接口，调用者和依赖者，只需要它需要的方法即可。 
　　如果一个包级私有的类（或者接口）只是在某一个类的内部被用到，就应该考虑它成为唯一使用它的那个类的私有嵌套类（内部类）。这样就可以将它的可访问范围从包中的所有类缩小到了使用它的那个类。其实，降低不必要的公有类的可访问性，比降低包级私有的顶级类要简单的多：因为公有类是包的API的一部分，而包级私有则是这个包的实现的一部分 
　　实例域不应该是公有的，这里实例域可能比较难以理解，看以下代码

public class UnmodifiableArray {
       // 潜在安全漏洞
       public static final String[] VALUES = { "RED", "GREEN" };
       public static void main(String[] args) {
         UnmodifiableArray UF = new UnmodifiableArray();
         UF.VALUES[1] = "YELLO";//设置final数组成员
         System.out.println(UF);
      }
      //使用Guava
     @Override
     public String toString() {
         return Objects.toStringHelper(this).add("VALUES0", UnmodifiableArray.VALUES[0])
                 .add("VALUES1", UnmodifiableArray.VALUES[1]).toString();
     }
 }
这里的 VALUES 可以理解为　作为类UnmodifiableArray　的一个域，我们希望它是不可变的，但是我们又将它设为公有的，这使它有潜在的被改变的风险。所以应该要确保公有静态ｆｉｎａｌ域锁引用的对象都是不可变的。

+
@zhuoxiuwu 2016-03-23 23:41 字数 阅读 0