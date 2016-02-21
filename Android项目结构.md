# Android项目结构

---

　　　在Android开发的整个项目起步之前，我们当然应该粗略地制定好项目的结构，合理的规划项目结构是一个好的项目的开始。

　　一般来说，一个App有哪些类组成？在我还不太了解Andorid开发项目的分包对我来说是件痛苦的事情，我喜欢将所有的 Activity 置于 activity 包下，所有的 Fragment 置于 fragment 包下,或者是将 adapter 和 UI 组件等置于一个包下，在项目非常简单，如果一共只有几个　activity、fragment　的话，这样做当然也是可以的。但是一旦界面一复杂，类就变得很多可能达到上百个，这时候还是推荐在业务复杂界面多的时候，按界面来划分 fragment 和 activity 。
　
　　一个项目大概可以分为这样几个部分，**界面层**：负责视图呈现的**Activity,Fragment,View,Widget等**，**数据层：**数据对应的实体类 **->**一些**JavaBean**，数据的来源获取需要 **网络层**的操作，**业务逻辑层：**一些具体的业务逻辑的控制操作。
　　所以android的框架应该包括这些：
　　1. UI控件
　　2. JavaBean
　　3. 网络连接
　　4. 数据缓存（本地缓存、内存缓存）
  
　　使用Andorid Studio开发的话，我们可以把一些业务无关的常用的功能统一放在一个 common Moudle下，或者是弄成 jar 包。一些对与不同项目都通用的封装，比如一些常用的基类、引导页、相册界面、Mvp模式的一些 base 封装等都可以弄成单独的工程 来和 自己主业务的module区分开来。
　　
　　具体的项目结构规划和命名等都会因为个人或者团队习惯和具体的业务需求而有所不同；不过总的来说，（参考知乎马天宇的回答），一个良好的架构设计可以分为三层，
- 上层是Activity、fragment、Views&Widget等视图和业务调用。
- 中层是针对业务的第三方库，以及主要逻辑实现，业务流程在这完。
- 底层是业务无光的框架层，类库内高聚合，不同库库之间低耦合甚至是无关的

　　这里给出一些我在项目开发中常用的包命名和相应功能仅供参考：
- ui 这个包用于存放所有项目的ui界面，包下继续分包，根据不同业务界面分包。类包括activity、fragment等
- entity 所有的实体类
- db SQLLite的一些封装
- adapter 适配器
- utils 一些帮助类
- widgets 一些自定义view、widget
- constant app全局使用的一些静态变量
- net 网络层的一些封装
- listener 基于listener的接口，命名以On作为开头
- interface 真正意义上的极客，命名以I作为开头
- presenter 使用mvp模式的话
- event 如果有使用 eventBus 或者 Otto 的话，存放一些 Event类