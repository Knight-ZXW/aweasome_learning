# Object Common to all object
# 对所有对象都通用的方法

---

　　本章主要讲述 **Object** 类的 **equals()**、**hashCode()**、**toString()**方法的意义和作用，在什么时候应该重写该方法，而什么时候不要这样做。在想重写该方法时 应该注意的事项
## 什么时候不用覆盖 equls 方法
 1. **类的每个实例本质上都是唯一的。** 对于代表活动实体而不是值(value) 的类来说确实如此，比如TThread。Object提供的equls实现对于这些类来说正是正确的行为，它保证不同的活动实体equals() 结果为false。
 2. **不关心类是够提供了“逻辑相等”的测试功能。** 例如 java.util.Random 覆盖了 equals ，以检查两个Random实例是否能够产生相同的随机数序列，但是设计者并不认为客户需要或者期望这这样的功能，在这样的情况下，从Object继承得到的 equls 实现一句足够了。相反，如果你想实现自己的 “逻辑相等”功能，你就可以重写该方法，但是要注意重写后 ,equls的变现可能会影响一些集合类的行为。
 3. **超类已经覆盖了equls方法**，从超类继承下来行为对于子类也是合适的，比如大多数的 Set 实现都从 AbstractSet继承equls 实现，List实现从AbstractList继承equls 实现..
 4. **类是私有的或者包级私有的**，可以确定它的equls方法永远都不会被调用，我们可以覆盖equals 方法，在方法实现中抛出异常，避免该方法被调用，在
## 覆盖equls方法应该遵守的约定
 
 5. 自反性 x.equals(x) 返回true
 6. 对称性  对于非null 的引用x,y  当且仅当 x.equls(y) == true  时, y.equals(x) == true
 7. 传递性 非null x,y,z   x.equals(y)==y.equals(z)==z.equals(x)==true
 8. 一致性 除非对对象作出修改，否则 equals()的调用永远返回相同的值
 9. 对于任何 非null的引用值 x , x.equls(null) 必须返回false
 　　为什么需要遵守这些约定，如果你不遵守这些约定，会导致其他客户段使用你的类时，发生意想不到的行为，没有哪个类是孤立的。有许多集合类都依赖于传递给它的对象是否遵守了这些约定。
  对称性，比如你在自己实现的类中完成一个不区分大小写的　equalst(Object o)功能
```
//伪代码
    Class  CaseInsensitiveString{
    String s;
     CaseInsensitiveString（String str）{s = str};
    public boolean equqls(Object o){....}
    }
```
对于你的实现，您的实现类知道 CaseInsensitiveString("Abc").equals("abc") == true,但是对于 String 类来说它并不知道，这返回了相反的结果，这明显违背了一致性，假设你把不缺分大小写的字符创方法哦一个集合中
```
List<CaseInsensitiveString> list = new ArrayList<CaseInsensitiveString>();
list.add(new CaseInsensitiveString("abc"));
```
此时的list.contains("abc")会返回什么结果呢，没人知道，在Sun当前的实现中，这刚好返回false,但是这只是这个特定实现得出的结果而已。在其他实现中它有可能返回 ture,或者抛出异常。  所以 你一旦违反了equls规定，当其它对象面向你的类时，你完全不知道这些对象的行为会怎么样。
为了解决这个问题，你应该不要让equls 其他与String 互操作
```
    public boolean equls(Object o){
         return o instanceof CaseInsensitiveString && (CaseInsensitiveString o).equalsIgnoreCase(s);
    }
```
### 一些覆盖equals()方法 的告诫
　　
 1. 覆盖equals时，应该问自己这三个问题:它是否是对称的、传递的、一致的。严格上来说你应该编写测试用例来检验这些属性！如果答案是否定的，你就应该找出原因，再相遇修改equls方法代码，当然equals()方法也必须满足其他两个属性，(自反性和非空行)，但是这两个属性通常会自动满足
 2. 不要企图让equls过于只能。如果只是简单地测试域中的值是否相等，则不难做到遵守 equls 约定，但是如果过分的地去寻求各种等价关系，则很容易陷入麻烦之中。把如何一种别名形式考虑到等价的范围内，往往不是一个号注意。
 3. 不要讲 equls 申明中的 Object 对象替换为其他的类型。 程序员编写出下面这样的 equls方法并不鲜见，这往往会让程序员花上数个小时搞不清楚为什么它们不能很好地工作
```
    public boolean equlas(Mycalss o){
    }
```
以上代码的问题在于，并没有覆盖Object.equals，因为它的参数应该是 Object类型，相反，我们这样写的代码其实是重载了父类的方法，在原有方法的基础上，提供了一个强类型的方法。
## hasCode()方法
　　在每个覆盖了 **equals()**方法的鳄类中，也必须覆盖 hashCode 方法。如果不这样做的话，就会违法 Object.hashCode 的通用约定 **从而导致该类无法结合所有基散列的集合一起正常工作**，主要是 依靠 hasCode 工作的类.
　　下面是有关Object的相关规范
1. 在应用程序的执行期间，只要对象的equals方法的比较操作所用到的信息没有被修改，那么对这同一个对象调用多次，hashCode方法都必须始终如一的返回同一个整数。在一个应用程序的多次执行过程中，每次执行所返回的整数可以不一致。 
2. 如果连个对象根绝equals方法比较是相等的，那么调用这两个对象中任意一个对象的hashCode方法都必须产生同样的整数结果。 
3. 如果两个对象根据equals方法比较是不相等的，那么调用这两个对象中任意一个对象的hashCode方法，则不一定要蚕声不同的整数结果。但是程序员应该知道，给不相等的对象产生截然不同的整数结果，有可能提高散列表（hash table）的性能。（比如，当你一个entity只根据id比较是否相等，但是在没实例化之前，没有id数值，那么默认的equals返回false，但是hashCode返回的值却相等。） 
### hashCode 返回值的重要性
  如果你将每个对象的 hashCode() 方法都返回相同的值，这依然可以让相应的集合正常工作，但是这会让每个对象都被映射到同一个散列桶中，使散列桶退化为链表。它使得本应该线性时间运行的线程变成了以平方级时间再运行。。
### 一些hasCode 返回值的解决方法
1. 把某个非零的常数值，比如 17，保存在一个名为 result 的 int 类型变量中。
2. 对于对象中每个关键域f (指equlas方法中涉及的每个域) 完成以下的步骤
a.为该域计算int类型的散列吗c：

 i. 如果该域是boolean类型，则计算

```
f?0:1  
```
ii. 如果该域是byte、char、short或者int类型，则计算
```
(int)f  
```
iii.如果该域是long类型，则计算
```
(int)(f ^ (f >>> 32))  
```
iv. 如果该域是double类型，则计算
```
Double.doubleToLongBits(f)  
```
得到一个long类型的值，然后按照2.a.iii，对该long型值计算散列值。
vi. 如果该域是一个对象引用，并且该类的equals方法通过递归调用equals的方式来比较这个域，则同样对这个域递归调用hashCode，如果要求一个更为复杂的比较，则为这个域计算一个“规范表示（canonical representation）”，然后针对这个范式调用hashCode。如果这个域为null，则返回0（或者是某个常数，但习惯上使用0）。

vii. 如果该域是一个数组，则把每一个元素当做单独的域处理。也就是说，递归地应用上诉规则，对每一个重要的元素计算散列值，然后根据2.b的方法把这些散列值组合起来。

b.按照下面的公式，把步骤a得到的散列值组合到result中：

```
result = 37 * result + c;  
```
3.返回result
4.写完了hashCode后，问自己相等的实例具有相同的hashCode么?假如不是，找出原因并修正。

在散列码的计算过程中，把冗余域排除在计算之外是可以接受的。换句话说，如果一个域的值可以根据其他域值计算出来，则把这样的域排除在外是可以接受的。


举例，假如一个类PhoneNumber有三个关键域：areaCode,exchange,extension,都是short类型，则hashCode的计算过程为：

```
@Override  
public int hashCode(){  
    int result = 17;  
    result = result * 37 + areaCode;  
    result = result * 37 + exchange;  
    result = result * 37 + extension;  
    return result;  
}  
```
　　如果一个类是可变的，并且计算散列码的代价也比较大，那么你应该考虑把散列码缓存到对象的内部，而不是每次请求的时候都计算散列值。如果你觉得这种类型的大都数值会被用作散列键，那么你应该在实例被创建的时候计算散列值，否则，你可以选择“延迟初始化”散列码，一直到hashCode第一次调用才开始计算。
假如PhoneNumber这样处理，那么代码为：

```
//延迟初始化  
private volatile int hashCode = 0;  
  
@Override  
public int hashCode(){  
      if(hashCode == 0){  
         int result = 17;  
         result = result * 37 + areaCode;  
         result = result * 37 + exchange;  
         result = result * 37 + extension;  
         hashCode = result;  
      }  
      return hashCode;  
}  
```
### 额外的一些注意点
  1. 如果 计算 hash 的开销过大，且该类不可变，可以将该值存储起来
  2. 编写 hashCode 函数是个研究课题，课题留给数学家去研究吧，以上的建议基本够使用了
  3.  不要试图从散列码计算中排除掉一个对象的关键部分来提高性能，因为这可能会导致散列函数根本无法使用。特别是如果实例竖向非常大的话，会讲很多实例映射到极少数的散列码上，这会使得基于散列的集合显示出平方级的性能指标


## toString()方法
　这个方法相比前面2个不是很重要，只是**提供好的toString()方法，可以时类使用起来更加舒适，比如 打打log什么的。。。。**
 
