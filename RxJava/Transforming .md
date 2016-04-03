# RxJava-变换操作


---

未了解的操作符:buffer 多个变体
>buffer(bufferClosingSelector)
buffer(boundary)


----------

>buffer:
buffer操作符，将原有Observable发射的数据缓存起来，比如buffer(2)，就每2个数据放进一个集合，然后发射这个集合出去。buffer方法有很多重载的方法。


----------
>FlatMap: 该操作符，使用一个指定的函数对原始Observable发射的每一项数据执行变换操作(lift),这个函数返回一个本身也发射数据的Observable，然后FlatMap合并这些Observbales发射的数据，最后将合并后的结果当做它自己的数据㤡发射。简单的理解为，为原有Observabel的每一项数据进行操作，然后发射这些经过过滤操作的数据。
**alert:**FlatMap对这些Observables发射的数据做的是合并(merge)的操作，因此它们可能是交错的。如果要保证返回的数据集合不交错，可以使用ConcatMap


----------
>groupBy操作符将原始Observable分拆为一些Observables集合，它们中的每一个发射原始Observable序列的一个子序列。那个数据项由哪一个Observbale发射是由 一个函数判定的，通过在函数中返回一个Key ,key 相同的数据会被同一个Observbale发射。
**如果你取消订阅一个 GroupObservable，那么那个Observable 将会被终止。如果之后的Observbale又发射了一个与这个 Observable的 key 匹配的数据，grouby将会为这个key创建一个新的GroupObservable**


----------
>map
对Observable发射的每一项数据应用一个函数，执行变换操作,实际上flatmap内部是通过map对数据进行变化的，在map的基础上加了merge的操作符，合并多个Observables的发射物。


----------
>scan
连续地对数据序列的每一项应用一个函数，然后连续发射结.Scan操作符对原始Observable发射的第一项数据应用一个函数，然后将那个函数的结果作为自己的第一项数据发射。它将函数的结果同第二项数据一起填充给这个函数来产生它自己的第二项数据。它持续进行这个过程来产生剩余的数据序列。这个操作符在某些情况下被叫做accumulator(有点像函数递归调用)。


----------
>window
这个操作符和buffer()很像。但是它发射的是Observable而不是列表。，它发射一个个Observbale出去，这些Observables中的每一个都发射原始Observable数据的一个子集，数量由count指定,最后发射一个onCompleted()结束。正如buffer()一样,window()也有一个skip变体。


----------
>cast
它是map()操作符的特殊版本。它将源Observable中的每一项数据都转换为新的类型，把它变成了不同的Class.它的参数就是一个 Class，在订阅的时候，会将原Observable发射的数据都强转成这个类型



