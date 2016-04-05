# RxJava-过滤操作

标签（空格分隔）： RxJava

---

过滤操作符相对来说比较简单和易于理解，（建议在学习这些过滤操作符的时候，点进源码查看一下具体使用的过滤函数，因为Rx有很多平台的语言，可能为了与其他平台一致，所以有很多功能实际相同的函数，很多操作符内部调用了其他操作符，可以加深理解）

- filter( ) — 过滤数据
- takeLast( ) — 只发射最后的N项数据
- last( ) — 只发射最后的一项数据
- lastOrDefault( ) — 只发射最后的一项数据，如果Observable为空就发射默认值
- takeLastBuffer( ) — 将最后的N项数据当做单个数据发射
- skip( ) — 跳过开始的N项数据
- skipLast( ) — 跳过最后的N项数据
- take( ) — 只发射开始的N项数据
- first( ) and takeFirst( ) — 只发射第一项数据，或者满足某种条件的第一项数据
- firstOrDefault( ) — 只发射第一项数据，如果Observable为空就发射默认值
- elementAt( ) — 发射第N项数据
- elementAtOrDefault( ) — 发射第N项数据，如果Observable数据少于N项就发射默认值
- sample( ) or throttleLast( ) — 定期发射Observable最近的数据
- throttleFirst( ) — 定期发射Observable发射的第一项数据
- throttleWithTimeout( ) or debounce( ) — - 只有当Observable在指定的时间后还没有发射数据时，才发射一个数据,可以过滤掉发射速率过快的情况，throttleWithTimeout有更多的重载方法。
- timeout( ) — 如果在一个指定的时间段后还没发射数据，就发射一个异常
- distinct( ) — 过滤掉重复数据，它只允许还没有发射过的数据通过，跟很多操作符一样，它允许你定义自己的判断标准
- distinctUntilChanged( ) — 过滤掉连续重复的数据
- ofType( ) — 只发射指定类型的数据，它过滤一个Observable只返回指定类型的数据，我觉得这个在实现Rxbus的时候还是挺有用的
- ignoreElements( ) — 丢弃所有的正常数据，只发射错误或完成通知，这表示你不关系它发射的数据，你只希望在它完成或者出错的时候接受到通知。




