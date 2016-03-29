# RxJava-Left


---
　　lift 是Obserable对象的方法，参数类型是Operator,Operator是一个表示变化的类
　　Obserable对象通过调用lift方法，返回了一个新的Obserable对象。
```
 public final <R> Observable<R> lift(final Operator<? extends R, ? super T> operator) {
        return new Observable<R>(new OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> o) {
                try {
                    Subscriber<? super T> st = hook.onLift(operator).call(o);
                    try {
                        // new Subscriber created and being subscribed with so 'onStart' it
                        st.onStart();
                        onSubscribe.call(st);
                    } catch (Throwable e) {
                        // localized capture of errors rather than it skipping all operators 
                        // and ending up in the try/catch of the subscribe method which then
                        // prevents onErrorResumeNext and other similar approaches to error handling
                        Exceptions.throwIfFatal(e);
                        st.onError(e);
                    }
                } catch (Throwable e) {
                    Exceptions.throwIfFatal(e);
                    // if the lift function failed all we can do is pass the error to the final Subscriber
                    // as we don't have the operator available to us
                    o.onError(e);
                }
            }
        });
    }
```

　　源码中这段代码中， hook.onLift(operator).call(o) 迷惑了我，我以为是调用了原有Obserable对象的call方法。但是hook.onLift(operator) 默认的hook其实什么都没做，等于调用了operator.call(o)方法。其实这里的call方法是 具体类**Operator**
call方法，它返回了一个新的Subscriber(Obserable) 对象
```
       @Override
    public Subscriber<? super T> call(final Subscriber<? super R> o) {
        return new Subscriber<T>(o) {

            @Override
            public void onCompleted() {
                o.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                o.onError(e);
            }

            @Override
            public void onNext(T t) {
                try {
                    o.onNext(transformer.call(t));
                } catch (Throwable e) {
                    Exceptions.throwOrReport(e, this, t);
                }
            }

        };
    }
```

　　




