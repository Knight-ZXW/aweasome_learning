# RxJava-创建Observable操作符


---
尚未熟悉的操作符:repeateWhen
> Create:
>最基本的创建Obserable的操作符，基本上所有的其他创建类型的操作符，内部都是通过调用这个方法创建Obsera>ble对象。

----------


 
> Defer
这个方法很有用，Defer操作符需要指定一个Obserable的工厂方法，然后它只会在有订阅者订阅它时才会创建Obserable，而且是为每个订阅者创建自己的Obserable

----------


>Empty/Nerver/Throw:
Empty：创建一个空的不发射如何事件的Obserabel，当有订阅者订阅时，它立即执行 订阅者的onCompleted函数。
Nerver：创建一个不发射数据也不终止的Observable 
Throw：创建一个不发射数据以一个错误终止的Observable
不太懂这个Nerver操作符创建的Observable有什么用，官方文档给的注释是 This Observable is useful primarily for testing purposes. 测试的时候很有用


----------
From：
>RxJava中，from操作符可以转换Future、Iterable和数组。对于Iterable和数组，产生的Observable会发射Iterable或数组的每一项数据。对于Future，它会发射Future.get()方法返回的单个数据。from方法有一个可接受两个可选参数的版本，分别指定超时时长和时间单位。如果过了指定的时长Future还没有返回一个值，这个Observable会发射错误通知并终止

----------

> Interval:
这个方法接受一个表示时间间隔参数和一个表示时间的参数，返回的Observable 按照固定的时间间隔发射一个无限递增的整数序列。

----------

>Just:
这个方法将单个数据转换为发射那个数据的Observable，From操作符会将数组中的每个项单独发射，而Just等于将这个数组作为一个事件发射出去。Just方法接受一至9个参数，返回一个按参数列表顺序发射这些数据的Obserable.

----------

>Range:
创建一个发射指定范围内的有序整数序列的Obserable


----------

>Repeate:
重复发射数据。这个方法是Obserabel对象上的方法，它不是创建一个Observable（https://mcxiaoke.gitbooks.io/rxdocs/content/operators/Repeat.html，这个翻译文章上是这样说的，但是代码中事件上是返回了一个新的Obseravble）。

>repeateWhen:
对某一个Observable，有条件地重新订阅从而产生多次结果
**翻译原文**：
**将原始Observable的终止通知（完成或错误）当做一个void数据传递给一个通知处理器，它以此来决定是否要重新订阅和发射原来的Observable。这个通知处理器就像一个Observable操作符，接受一个发射void通知的Observable为输入，返回一个发射void数据（意思是，重新订阅和发射原始Observable）或者直接终止（意思是，使用repeatWhen终止发射数据）的Observable**


----------


>start;toAsync;startFuture;deferFuture;fromAction;fromCallble;fromRunable;forEachFuture
　　可选包rxjava-async的一些操作符，是转换一些函数、futuree、runnable等的操作符
　　


----------

>timer:
　创建一个Observable在一个给定的延迟后发射指定的值，类似于定时器任务，