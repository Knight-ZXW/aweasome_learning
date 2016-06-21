# Java中的AnnotationProcessor 是什么

@(Android)[AnnotaionProcess]

　　在这篇博客中，我将会向展示 如何编写 annotation process ，不过首先，我会解释到底什么是 annotation processing ,你可以使用它做些哪些  hack的事情 以及哪些方面不是它能够处理的，接着，我们会一步步实现一个 annotation processing的例子

## The Basices
   在开始之前，一个很重要的需要说明的事情是，annotation processor 并不是在运行时通过反射做一些事情，Annotaion processing 是在Java编译器编译你的代码时 做一些 nice 的事情
   Annotation processing 是一个在编译的时候扫描和处理你编写的注解的工具，你可以编写注册你自己的annotation processor 来处理注解（你应该已经了解什么是注解，以及注解的基本使用）。Annotation processing i在java 5的时候就已经被引入，但是直到java6 它才真正的可以被使用。