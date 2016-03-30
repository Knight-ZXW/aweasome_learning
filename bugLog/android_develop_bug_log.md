# Android 踩坑集合


------

在此记录一些Android开发中的踩坑记录。
> 问题：小米 MIUI系统 相机无法调用 Camer2 新Api的问题
  过程：在运行 Android-23 Samples 中 media文件夹下的 Camera2Basic时，在模拟器 Nexus_5 上可以正常运行，当然由于是模拟器，所以没有图像，在使用真机红米Note2时。在以下代码调用时出错了：
```Java
   manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
```
> 错误：Lacking privileges to access camera service
原因：应该是，MIUI系统 相机没有使用 Camer2 的API来编写的的原因，这个原因只是猜测。其实具体原因我也不太确定，自己也只测试了这一个机型，记录这么一个错误是提醒 API 出错要考虑到 各大厂商 ROM 定制机型的原因。考虑到稳定性，不要使用可能引起兼容性的API。 
```

-----
> 问题：在请求网络时报 java.net.UnknownHostException: Unable to resolve host
  过程：在测试无网络情况下，app的运行状况时报了这个错误，我使用ConnectivityManager获取网络状态，在无网络时做出相应的应对。
  错误：java.net.UnknownHostException: Unable to resolve host
  原因：忘记加检测网络状态的权限了，加上android.permission.ACCESS_WIFI_STATE等权限，这个坑它也不报是应为没有权限而访问网络状态的相关异常，直接报个unabl to resolve host ，让我不知所措，不过 google 了一下很快就解决了。

-----

> 问题：AlertDialog自定义View时， editText无法自动弹出软键盘的问题
  过程：Show The Code
  ```
  mDialog..setContentView(dialogView);//or layout
  ```
  setContentView时，设置的contentView 内部的editText获得焦点时也无法弹出软键盘，原因不明= =
  错误：无（无法弹出软键盘）
  原因：不知道原因，这里只给出一个解决方案，是google出来的，不使用 setContentView ,使用 setView(dial   ogView) ,就正常了。真是奇怪！！，追踪源码

-----
> 问题: baidu地图拖动时闪烁
  过程: 我父布局用的是 CoordinatorLayout，测试了很多可能的原因，最后改成framLayout正常了，从而找   到了原因
  错误: mapView的父布局不要是能CoordinatorLayout，我们知道CoordinatorLayout在包裹toolbar 时，子view如果有behavior之类的设置，能够隐藏toolbar,toolbar其实是作为CoordinatorLayout的子view,它的隐藏应该改变了layout的属性，所以invalidate的时候，地图也会闪烁。
  原因：layout的变化造成了地图的闪烁
  
-----

-----
> recycleview 的  viewtype 重写时要从0开始，系统默认返回0

-----
> gradle 开发 **buildToolsVersion** 和 **targetSdkVersion**，保持一致，不要问我为什么，在低版本报了各种 类找不到的错误，修改了一下发现正常了。要弄懂各个versoin的实际行为