package pixel.android.swipeback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pixel on 2016/12/8.
 * <p>
 * 滑动返回控件
 * 使用方式:
 * 1. 将setContentView改为setContentView(new PixelSwipeBackView(this, R.layout.activity_main2));
 * 2. 设置Activity背景透明@android:style/Theme.Translucent.NoTitleBar
 */

public class PixelSwipeBackView extends HorizontalScrollView {
    /*保存返回控件的引用*/
    public static final List<PixelSwipeBackView> SWIPE_BACK_STACK = new ArrayList<>();
    /*触发滑动的位置*/
    private static final float ACTIV_SIZE = 50.0f;
    /*线程管理*/
    private final Handler mHandler = new Handler();
    /*上一个界面的控件引用*/
    private PixelSwipeBackView upPixelSwipeBackView;    // 上一个界面的容器
    /*当前界面引用*/
    private Activity mActivity;
    /*保存UI的容器*/
    private LinearLayout rootView;
    /*用户内容View*/
    private View mView;
    /*左右的空白填充视图*/
    private View leftFillView, rightFillView;
    /*屏幕宽高*/
    private int screenX, screenY;
    /*右边填充View的宽*/
    private int rightViewWidth;
    /*屏幕按下位置*/
    private float downX;
    /*滑动距离*/
    private float moveX;
    // 是否已经滑动关闭
    private boolean isClose = false;

    public PixelSwipeBackView(Activity activity, int layoutResID) {
        super(activity);
        // 赋值参数
        this.mActivity = activity;
        LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mView = mInflater.inflate(layoutResID, null);
        // 去掉滚动条
        this.setHorizontalScrollBarEnabled(false);
        // 获取屏幕宽高
        WindowManager windowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        this.screenX = point.x;
        this.screenY = point.y;
        this.rightViewWidth = (int) (this.screenX * (2d / 3d));
        // 设置内容View的宽高
        ViewGroup.LayoutParams mViewLayoutParams = new ViewGroup.LayoutParams(screenX, screenY);
        this.mView.setLayoutParams(mViewLayoutParams);
        // 建立容器设置容器的宽高
        this.rootView = new LinearLayout(mActivity);
        this.rootView.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rootLayoutParams = new LinearLayout.LayoutParams(screenX * 2 + this.rightViewWidth, screenY); // 设置容器的宽高为屏幕的2倍再加1/4
        this.rootView.setLayoutParams(rootLayoutParams);
        // 建立一个背景透明的左边填充View
        this.leftFillView = new View(mActivity);
        ViewGroup.LayoutParams leftFillViewLayoutParams = new ViewGroup.LayoutParams(screenX, screenY); // 设置填充View的宽高为屏幕的宽高
        this.leftFillView.setLayoutParams(leftFillViewLayoutParams);
//        this.leftFillView.setBackgroundResource();    // TODO 可以设置一张右边带有阴影的背景图使界面更有层次感
        // 建立一个背景透明的右边填充View
        this.rightFillView = new View(mActivity);
        ViewGroup.LayoutParams rightFillViewLayoutParams = new ViewGroup.LayoutParams(this.rightViewWidth, screenY); // 设置填充View的宽高为屏幕的宽高
        this.rightFillView.setLayoutParams(rightFillViewLayoutParams);
        // 将View填入容器注意顺序
        this.rootView.addView(leftFillView, 0);
        this.rootView.addView(mView, 1);
        this.rootView.addView(rightFillView, 2);
        PixelSwipeBackView.this.addView(rootView);  // 加入屏幕显示
        // 获取上一个页面的容器引用
        int p = SWIPE_BACK_STACK.size() - 1;
        if (p >= 0) upPixelSwipeBackView = SWIPE_BACK_STACK.get(p);
        // 必须延迟加载
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(screenX, 0);
                if (upPixelSwipeBackView != null) {
                    upPixelSwipeBackView.smoothScrollTo(screenX + rightViewWidth, 0);
                }
            }
        }, 10);
        // 加入引用
        PixelSwipeBackView.SWIPE_BACK_STACK.add(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (downX <= 0f) downX = ev.getRawX();
                if (downX > ACTIV_SIZE) return true;
                moveX = ev.getRawX() - downX;
                if (moveX <= 0) return true;    // 过滤反向滑动
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(moveX) > Math.abs(screenX) / 2) {
                    this.isClose = true;
                    // 滚动页面到开始位置(会触发关闭操作)
                    smoothScrollTo(0, 0);
                    // 将上一个页面滚动到正确位置
                    if (upPixelSwipeBackView != null)
                        upPixelSwipeBackView.smoothScrollTo(screenX, 0);
                } else {
                    smoothScrollTo(screenX, 0);
                }
                downX = 0f;
                return true;    // 消除HorizontalScrollView漂移
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (l <= 0 && oldl > 0) {
            // 移除引用
            PixelSwipeBackView.SWIPE_BACK_STACK.remove(this);
            // 关闭Activity
            this.mActivity.finish();
            return;
        }
        if (upPixelSwipeBackView != null) {
            int upMove = (int) ((screenX + rightViewWidth) - (((double) moveX / (double) screenX) * rightViewWidth));
            if (upMove > screenX) upPixelSwipeBackView.scrollTo(upMove, 0);
        }
    }

    @Override
    protected void onDetachedFromWindow() { // 处理用户是按返回键关闭界面的事件
        super.onDetachedFromWindow();
        if (isClose) return;
        // 移除引用
        PixelSwipeBackView.SWIPE_BACK_STACK.remove(this);
        // 将上一个页面滚动到正确位置
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (upPixelSwipeBackView != null) {
                    upPixelSwipeBackView.smoothScrollTo(screenX, 0);
                }
            }
        }, 10);
    }

}
