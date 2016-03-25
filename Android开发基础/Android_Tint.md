# Android Tint使用

---
## Tint 属性
　　**Tint** 是 **Android5.0** 引入的一个属性，它可以在Android5.0 系统上，对视图进行颜色渲染。
下面是网上一个使用tint属性给背景调整不同颜色的例子：
```
 <LinearLayout  
        android:orientation="horizontal"  
        android:layout_width="wrap_content"  
        android:layout_height="wrap_content"  
        android:layout_gravity="center_horizontal">  
        <ImageView  
            ...  
            android:src="@drawable/xamarin_white"  
            android:background="@drawable/mycircle"/>  
        <ImageView  
            ...  
            android:src="@drawable/xamarin_white"  
            android:background="@drawable/mycircle"  
            android:tint="#2C3E50"/>  
        <ImageView  
            ...  
            android:src="@drawable/xamarin_white"  
            android:background="@drawable/mycircle"  
            android:tint="#B4BCBC"/>  
    </LinearLayout>  
```
效果图：
![此处输入图片的描述][1]

　　tint这个属性，是ImageView有的，它可以给ImageView的src设置，除了tint 之外，还有backgroundTint,foregroundTint,drawableTint,它们分别对应对背景、前景、drawable进行着色处理。 如果，我们给上面的例子设置 backgroundTint，那么蓝色背景就会被着色，替换成你设置的颜色。

## 原理
　　在5.0以上，View类中增加了对tint属性的获取
```
  case R.styleable.View_backgroundTint:
                    // This will get applied later during setBackground().
                    if (mBackgroundTint == null) {
                        mBackgroundTint = new TintInfo();
                    }
                    mBackgroundTint.mTintList = a.getColorStateList(
                            R.styleable.View_backgroundTint);
                    mBackgroundTint.mHasTintList = true;
                    break;
case R.styleable.View_backgroundTintMode:
    // This will get applied later during setBackground().
    if (mBackgroundTint == null) {
        mBackgroundTint = new TintInfo();
    }
    mBackgroundTint.mTintMode = Drawable.parseTintMode(a.getInt(
            R.styleable.View_backgroundTintMode, -1), null);
    mBackgroundTint.mHasTintMode = true;
    break;
```

以具体的 ImageView 为例 
```
//在构造函数中调用了这个方法
private void applyImageTint() {
        if (mDrawable != null && (mHasDrawableTint || mHasDrawableTintMode)) {
            mDrawable = mDrawable.mutate();

            if (mHasDrawableTint) {
                mDrawable.setTintList(mDrawableTintList);
            }

            if (mHasDrawableTintMode) {
                mDrawable.setTintMode(mDrawableTintMode);
            }

            // The drawable (or one of its children) may not have been
            // stateful before applying the tint, so let's try again.
            if (mDrawable.isStateful()) {
                mDrawable.setState(getDrawableState());
            }
        }
```

setTintList 方法的实现
```
public void setTintList(ColorStateList tint) {
        mBitmapState.mTint = tint;
        mTintFilter = updateTintFilter(mTintFilter, tint, mBitmapState.mTintMode);
        invalidateSelf();
    }
```

当追踪到 updateTintFilter（）这个方法的时候，我们就无法继续向下追踪了，不过研究参数也可以得出它实现的方式(PorterDuffColorFilter,BitmapDrawable.BitmapState,PorterDuff.Mode), 很明显，还是利用的PorterDuff那些相关类来实现的操作，ProterDuff 网上也有很多说明的例子，最重要的还是下图： 有关这些Mode的详细解释，大家自行查阅
![此处输入图片的描述][2]

##版本问题
Tint在默认只在Android5.0以上的系统生效，为了向下支持，系统提供了相应的Compact类，包括
**AppCompatTextView、AppCompatImageView**等。我们使用 **ViewCompat.setBackgroundTintMode** 在懂吗中动态的为 View 进行tint 操作，观察 这些Compat类发现都实现了 **TintableBackgroundView** 这个接口，如果需要让自定义的 View实现tint功能，我们可以仿照系统的实现类来实现。
```
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v4.view;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;

/**
 * Interface which allows a {@link android.view.View} to receive background tinting calls from
 * {@code ViewCompat} when running on API v20 devices or lower.
 */
public interface TintableBackgroundView {

    /**
     * Applies a tint to the background drawable. Does not modify the current tint
     * mode, which is {@link PorterDuff.Mode#SRC_IN} by default.
     * <p>
     * Subsequent calls to {@code View.setBackground(Drawable)} will automatically
     * mutate the drawable and apply the specified tint and tint mode.
     *
     * @param tint the tint to apply, may be {@code null} to clear tint
     *
     * @see #getSupportBackgroundTintList()
     */
    void setSupportBackgroundTintList(@Nullable ColorStateList tint);

    /**
     * Return the tint applied to the background drawable, if specified.
     *
     * @return the tint applied to the background drawable
     */
    @Nullable
    ColorStateList getSupportBackgroundTintList();

    /**
     * Specifies the blending mode used to apply the tint specified by
     * {@link #setSupportBackgroundTintList(ColorStateList)}} to the background
     * drawable. The default mode is {@link PorterDuff.Mode#SRC_IN}.
     *
     * @param tintMode the blending mode used to apply the tint, may be
     *                 {@code null} to clear tint
     * @see #getSupportBackgroundTintMode()
     */
    void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode tintMode);

    /**
     * Return the blending mode used to apply the tint to the background
     * drawable, if specified.
     *
     * @return the blending mode used to apply the tint to the background
     *         drawable
     */
    @Nullable
    PorterDuff.Mode getSupportBackgroundTintMode();
}

```

  [1]: http://7xq84c.com1.z0.glb.clouddn.com/Androidtint_.png
  [2]: http://img.blog.csdn.net/20130828212947609?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdDEyeDM0NTY=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast