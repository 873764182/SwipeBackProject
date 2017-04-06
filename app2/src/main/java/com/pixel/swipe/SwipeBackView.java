package com.pixel.swipe;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by pixel on 2017/4/5.
 * <p>
 * 直接基于FrameLayout边距/截图实现,不需要设置Activity透明主题,Activity不能有ActionBar.
 */

public class SwipeBackView extends FrameLayout {
    protected static final Map<String, Bitmap> BITMAP_ARRAY = new LinkedHashMap<>();

    protected float PROPORTION = 2f;   // 两个界面的位置差值倍数
    protected float TRIGGER = 50f;     // 边缘触发范围
    protected int DEF_BG_COLOR = Color.argb(250, 255, 255, 255);    // 默认背景色
    protected Activity activity;
    protected View contentView;
    protected ImageView prevView;
    protected Bitmap prevBitmap;
    protected float rawX;
    protected int moveX;
    protected int contentX;
    protected boolean ON = true;

    protected LayoutParams contentLayoutParams;
    protected LayoutParams prevLayoutParams;

    protected class ILayout extends FrameLayout {

        public ILayout(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (ON && ev.getRawX() <= TRIGGER) {
                return true;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }

    public SwipeBackView(Activity activity, int resId) {
        super(activity);
        this.activity = activity;
        this.contentView = LayoutInflater.from(activity).inflate(resId, null);
        this.init();
    }

    public SwipeBackView(Activity activity, View contentView) {
        super(activity);
        this.activity = activity;
        this.contentView = contentView;
        this.init();
    }

    protected LayoutParams getMatchLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        return layoutParams;
    }

    protected Bitmap getLastBitmap() {
        if (BITMAP_ARRAY.size() > 0) {
            int flag = 0;
            for (Map.Entry<String, Bitmap> entry : BITMAP_ARRAY.entrySet()) {
                if (flag++ == BITMAP_ARRAY.size() - 1) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    protected void init() {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) actionBar.hide();
        SwipeBackView.this.setPadding(0, 0, 0, 0);

        ILayout iLayout = new ILayout(activity);
        iLayout.addView(contentView);
        this.contentView = iLayout;

        prevView = new ImageView(activity);
        prevView.setScaleType(ImageView.ScaleType.FIT_XY);
        prevBitmap = getLastBitmap();
        if (prevBitmap == null) {
            prevView.setImageDrawable(new ColorDrawable(DEF_BG_COLOR));
        } else {
            prevView.setImageBitmap(prevBitmap);
        }
        prevLayoutParams = getMatchLayoutParams();
        prevView.setLayoutParams(prevLayoutParams);

        contentLayoutParams = getMatchLayoutParams();
        contentView.setLayoutParams(contentLayoutParams);

        if (contentView.getBackground() == null) {
            contentView.setBackgroundColor(DEF_BG_COLOR);   // 设置默认背景
        }

        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!ON) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rawX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (rawX <= 0) rawX = event.getRawX();
                        if (rawX <= TRIGGER) {
                            moveX = (int) (event.getRawX() - rawX);

                            contentLayoutParams.leftMargin = moveX;
                            contentLayoutParams.width = contentX;
                            contentView.setLayoutParams(contentLayoutParams);

                            int prevMatgin = (int) (contentX / PROPORTION - moveX / PROPORTION);
                            if (prevMatgin >= 0) {
                                prevLayoutParams.width = contentX;
                                prevLayoutParams.leftMargin = -prevMatgin;
                                prevView.setLayoutParams(prevLayoutParams);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        rawX = 0;

                        if (moveX >= contentX / 2) {
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                        } else {
                            contentLayoutParams.leftMargin = (int) rawX;
                            contentView.setLayoutParams(contentLayoutParams);

                            prevLayoutParams.leftMargin = (int) -(contentX / PROPORTION);
                            prevView.setLayoutParams(prevLayoutParams);
                        }
                        break;
                }
                return true;
            }
        });

        SwipeBackView.this.addView(prevView, 0);
        SwipeBackView.this.addView(contentView, 1);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                contentView.setDrawingCacheEnabled(true);
                contentView.buildDrawingCache();
                BITMAP_ARRAY.put(activity.getClass().getName(), contentView.getDrawingCache());

                contentX = contentView.getWidth();
                prevLayoutParams.leftMargin = (int) -(contentX / PROPORTION);
                prevView.setLayoutParams(prevLayoutParams);
            }
        }, 100);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        BITMAP_ARRAY.remove(activity.getClass().getName());
    }

    public void setOpen(boolean ON) {
        this.ON = ON;
    }

    public void setDefBgColor(int defBgColor) {
        DEF_BG_COLOR = defBgColor;
    }

    public void setProportion(float proportion) {
        this.PROPORTION = proportion;
    }

    public void setTrigger(float trigger) {
        this.TRIGGER = trigger;
    }
}
