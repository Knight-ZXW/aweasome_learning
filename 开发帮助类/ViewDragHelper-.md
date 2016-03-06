# ViewDraHelper解析
---

## ViewDragHelper 是什么？
　　**ViewDragHelper**　是Google 编写的帮助我们处理拖拽的一个帮助类，如果我们自己去处理TouchEvent事件，判断处理TouchEvent的数据，达到我们拖拽View的一个目的也是可以的，但是过程肯定是有点繁琐的，ViewDragHelper 通过处理 MotionEvent 事件，判断是否是拖拽 以及拖拽方向、速率等然后回调给我们去实现我们自己真正想要实现的功能。
## 如何使用ViewDragHelper
  **ViewDragHelper** 是通过工厂方法的方式创建的，下面是它的构造函数
```
/**
* forParent 必须是ViewGroup,ViewDragHelper *并不是直接作用于要被拖动的View，而是使其控制的视图容器中的子View可以被拖动
* sensitivity 敏感参数，一般都指定1.0f 表示正常速率
* Callback 回调类，ViewDragHelper 在处理 touchevent 事件后，回调这个类的方法
*/
public static ViewDragHelper create(ViewGroup forParent, float sensitivity, Callback cb) {
        final ViewDragHelper helper = create(forParent, cb);
        helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
        return helper;
    }
```
一般，我们在构造函数中，实例化ViewDragHelper
```
public MyDragLayout(Context context) {
  this(context, null);
}
public MyDragLayout(Context context, AttributeSet attrs) {
  super(context, attrs, defStyle);
  //DragHelperCallback 是我们继承抽象接口Callback的一个实现类
  mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
}
```
ViewGroup区别于View的一个特点是，它有一个 **onInterceptTouchEvent(MotionEvent ev)** 方法，事件最先传递给这个方法，如果返回值返回 **true**,表示自己拦截 **这一整个事件序列**，不向子View进行分发，自己处理，即调用自己的**onTouchEvent**方法，我们需要再这个方法中，调用 **ViewDragHelper** 的**shouldInterceptTouchEvent（）** 方法，让他处理是否拦截事件。
```
@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }
```
同样，在 **onTouchEvnet** 中，也应该将事件交给 ViewDragHelper 处理
```
@Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }
```

先不去了解 **ViewDragHelpe** 对 TouchEvent 的处理流程，先看一下，**ViewDragHelper.Callback** 为我们提供了哪些回调方法
```
    public static abstract class Callback {
        /**
         * 在拖拽状态改变的时候调用.
         * @param state 新的拖拽状态
         *
         * @see #STATE_IDLE 处于空闲状态
         * @see #STATE_DRAGGING 拖拽中
         * @see #STATE_SETTLING 自动沉落
         */
        public void onViewDragStateChanged(int state) {}

        /**
         * 在 一个子 View 的位置改变的时候调用
         *
         * @param changedView View whose position changed
         * @param left New X coordinate of the left edge of the view
         * @param top New Y coordinate of the top edge of the view
         * @param dx Change in X position from the last call
         * @param dy Change in Y position from the last call
         */
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {}

        /**
         * 当 captured View 被捕获的时候调用
         * Called when a child view is captured for dragging or settling. The ID of the pointer
         * currently dragging the captured view is supplied. If activePointerId is
         * identified as {@link #INVALID_POINTER} the capture is programmatic instead of
         * pointer-initiated.
         *
         * @param capturedChild Child view that was captured
         * @param activePointerId Pointer id tracking the child capture
         */
        public void onViewCaptured(View capturedChild, int activePointerId) {}

        /**
         * 当View 被释放，其实也就是 手指抬起的时候调用
         * xvel yvel 分别表示 x y方向上拖动的 px/每秒
         * Called when the child view is no longer being actively dragged.
         * The fling velocity is also supplied, if relevant. The velocity values may
         * be clamped to system minimums or maximums.
         *
         * <p>Calling code may decide to fling or otherwise release the view to let it
         * settle into place. It should do so using {@link #settleCapturedViewAt(int, int)}
         * or {@link #flingCapturedView(int, int, int, int)}. If the Callback invokes
         * one of these methods, the ViewDragHelper will enter {@link #STATE_SETTLING}
         * and the view capture will not fully end until it comes to a complete stop.
         * If neither of these methods is invoked before <code>onViewReleased</code> returns,
         * the view will stop in place and the ViewDragHelper will return to
         * {@link #STATE_IDLE}.</p>
         *
         * @param releasedChild The captured child view now being released
         * @param xvel X velocity of the pointer as it left the screen in pixels per second.
         * @param yvel Y velocity of the pointer as it left the screen in pixels per second.
         */
        public void onViewReleased(View releasedChild, float xvel, float yvel) {}

        /**
         * 当触摸到边界时回调
         * Called when one of the subscribed edges in the parent view has been touched
         * by the user while no child view is currently captured.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) currently touched
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeTouched(int edgeFlags, int pointerId) {}

        /**
        *  返回true的时候会锁住当前的边界，false则unLock。
         * Called when the given edge may become locked. This can happen if an edge drag
         * was preliminarily rejected before beginning, but after {@link #onEdgeTouched(int, int)}
         * was called. This method should return true to lock this edge or false to leave it
         * unlocked. The default behavior is to leave edges unlocked.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) locked
         * @return true to lock the edge, false to leave it unlocked
         */
        public boolean onEdgeLock(int edgeFlags) {
            return false;
        }

        /**
         *  在边界拖动时回调
         * Called when the user has started a deliberate drag away from one
         * of the subscribed edges in the parent view while no child view is currently captured.
         *
         * @param edgeFlags A combination of edge flags describing the edge(s) dragged
         * @param pointerId ID of the pointer touching the described edge(s)
         * @see #EDGE_LEFT
         * @see #EDGE_TOP
         * @see #EDGE_RIGHT
         * @see #EDGE_BOTTOM
         */
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {}

        /**
         * 改变同一个坐标（x,y）去寻找captureView位置的方法。
         * Called to determine the Z-order of child views.
         *
         * @param index the ordered position to query for
         * @return index of the view that should be ordered at position <code>index</code>
         */
        public int getOrderedChildIndex(int index) {
            return index;
        }

        /**
         * 返回值表示这个child View 竖向可以 拖拽的范围，0表示不能拖拽
         * Return the magnitude of a draggable child view's horizontal range of motion in pixels.
         * This method should return 0 for views that cannot move horizontally.
         *
         * @param child Child view to check
         * @return range of horizontal motion in pixels
         */
        public int getViewHorizontalDragRange(View child) {
            return 0;
        }

        /**
         * Return the magnitude of a draggable child view's vertical range of motion in pixels.
         * This method should return 0 for views that cannot move vertically.
         *
         * @param child Child view to check
         * @return range of vertical motion in pixels
         */
        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        /**
         * 
         * @param 拖拽点击的child view
         * @param pointerId ID of the pointer attempting the capture
         * @return true 表示可以捕获这个子view；即可以被拖拽，false 不运行
         */
        public abstract boolean tryCaptureView(View child, int pointerId);

        /**
         * 返回值表示child View left的位置，一般直接返回left
         * Restrict the motion of the dragged child view along the horizontal axis.
         * The default implementation does not allow horizontal motion; the extending
         * class must override this method and provide the desired clamping.
         *
         *
         * @param 拖拽的子View
         * @param left Attempted motion along the X axis
         * @param dx Proposed change in position for left
         * @return The new clamped position for left
         */
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        /**
         * Restrict the motion of the dragged child view along the vertical axis.
         * The default implementation does not allow vertical motion; the extending
         * class must override this method and provide the desired clamping.
         *
         *
         * @param child Child view being dragged
         * @param top Attempted motion along the Y axis
         * @param dy Proposed change in position for top
         * @return The new clamped position for top
         */
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }
    }
```
## 那么如何让子view 可拖拽呢，简单的实现3个回调方法
```
class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }
        //返回值表示view 新的left
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }
```

　　这样实现的时候，我们发现了一个问题，view的拖拽可以超出父view的边界，这时候**clampViewPostion..** 方法就不能简单的返回 left 了
```
class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //可能我们给自定义的layout 设置了padding属性
            final int leftBound = getPaddingLeft();
            //left 的 最大界限 是布局的宽 - padding - 拖拽view的宽
            final int rightBound = getWidth() - leftBound -child.getWidth();
            //系统帮我们判断的left 和 我们的margin（margin可能为负） 取最大值，在和 最大界限取最小值
            return Math.min(Math.max(leftBound,left),rightBound);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            final int topBound = getPaddingTop();

            final int bottomBound = getHeight() - topBound -child.getHeight();
            
            return Math.min(Math.max(topBound,top),bottomBound);
        }
    }
```

## 实现拖拽释放后的自动滚动（拖拽）功能
```
// 如果我们应该在这个方法中
        // 调用settleCapturedViewAt(int, int) or flingCapturedView(int, int, int, int).
        // 如果调用了其中一个 ViewDragHelper会进入 STATE_SETTLING 状态
        // 如果没有 会进入STATE_IDLE 状态
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mDragHelper.settleCapturedViewAt(200,200);
            //不要忘记调用invalidate(),
            invalidate();
        }
```
拖拽后的移动用的是scroller 实现的 ，所以重写 layout 的 **computeScroll**  方法
```
@Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)){
            invalidate();
        }
    }
```

ViewDragHelper还有一个移动View的方法是
```
smoothSlideViewTo(View child, int finalLeft, int finalTop)
```
可以在任何地方调用，效果和settleCapturedViewAt()类似，因为它们最终都调用了forceSettleCapturedViewAt()来启动自动滚动，区别在于settleCapturedViewAt()会以最后松手前的滑动速率为初速度将View滚动到最终位置，而smoothSlideViewTo()滚动的初速度是0。forceSettleCapturedViewAt()里有地方调用了Callback里的方法，所以再来看看这个方法：
```
/**
 * Settle the captured view at the given (left, top) position.
 *
 * @param finalLeft Target left position for the captured view
 * @param finalTop Target top position for the captured view
 * @param xvel Horizontal velocity
 * @param yvel Vertical velocity
 * @return true if animation should continue through {@link #continueSettling(boolean)} calls
 */
private boolean forceSettleCapturedViewAt(int finalLeft, int finalTop, int xvel, int yvel) {
    final int startLeft = mCapturedView.getLeft();
    final int startTop = mCapturedView.getTop();
    final int dx = finalLeft - startLeft;
    final int dy = finalTop - startTop;

    if (dx == 0 && dy == 0) {
        // Nothing to do. Send callbacks, be done.
        mScroller.abortAnimation();
        setDragState(STATE_IDLE);
        return false;
    }

    final int duration = computeSettleDuration(mCapturedView, dx, dy, xvel, yvel);
    mScroller.startScroll(startLeft, startTop, dx, dy, duration);

    setDragState(STATE_SETTLING);
    return true;
}
```
可以看到自动滑动是靠Scroll类完成，在这里生成了调用mScroller.startScroll()需要的参数。再来看看计算滚动时间的方法computeSettleDuration()：
```
private int computeSettleDuration(View child, int dx, int dy, int xvel, int yvel) {
    xvel = clampMag(xvel, (int) mMinVelocity, (int) mMaxVelocity);
    yvel = clampMag(yvel, (int) mMinVelocity, (int) mMaxVelocity);
    final int absDx = Math.abs(dx);
    final int absDy = Math.abs(dy);
    final int absXVel = Math.abs(xvel);
    final int absYVel = Math.abs(yvel);
    final int addedVel = absXVel + absYVel;
    final int addedDistance = absDx + absDy;

    final float xweight = xvel != 0 ? (float) absXVel / addedVel :
            (float) absDx / addedDistance;
    final float yweight = yvel != 0 ? (float) absYVel / addedVel :
            (float) absDy / addedDistance;

    int xduration = computeAxisDuration(dx, xvel, mCallback.getViewHorizontalDragRange(child));
    int yduration = computeAxisDuration(dy, yvel, mCallback.getViewVerticalDragRange(child));

    return (int) (xduration * xweight + yduration * yweight);
}
```
clampMag()方法确保参数中给定的速率在正常范围之内。最终的滚动时间还要经过computeAxisDuration()算出来，通过它的参数可以看到最终的滚动时间是由dx、xvel、mCallback.getViewHorizontalDragRange()共同影响的。看computeAxisDuration()：
```
private int computeAxisDuration(int delta, int velocity, int motionRange) {
    if (delta == 0) {
        return 0;
    }

    final int width = mParentView.getWidth();
    final int halfWidth = width / 2;
    final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
    final float distance = halfWidth + halfWidth *
            distanceInfluenceForSnapDuration(distanceRatio);

    int duration;
    velocity = Math.abs(velocity);
    if (velocity > 0) {
        duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
    } else {
        final float range = (float) Math.abs(delta) / motionRange;
        duration = (int) ((range + 1) * BASE_SETTLE_DURATION);
    }
    return Math.min(duration, MAX_SETTLE_DURATION);
}
```
直接看14~19行，如果给定的速率velocity不为0，就通过距离除以速率来算出时间；如果velocity为0，就通过要滑动的距离（delta）除以总的移动范围（motionRange，就是Callback里getViewHorizontalDragRange()、getViewVerticalDragRange()返回值）来算出时间。最后还会对计算出的时间做过滤，最终时间反正是不会超过MAX_SETTLE_DURATION的，源码里的取值是600毫秒，所以不用担心在Callback里getViewHorizontalDragRange()、getViewVerticalDragRange()返回错误的数而导致自动滚动时间过长了。

在调用settleCapturedViewAt()、flingCapturedView()和smoothSlideViewTo()时，还需要实现mParentView的computeScroll()：
```
@Override
public void computeScroll() {
	if (mDragHelper.continueSettling(true)) {
		ViewCompat.postInvalidateOnAnimation(this);
	}
}
```






