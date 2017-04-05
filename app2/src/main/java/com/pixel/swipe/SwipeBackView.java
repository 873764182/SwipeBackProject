package com.pixel.swipe;

import android.app.ActionBar;
import android.app.Activity;
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
 * 直接基于FrameLayout边距实现
 */

public class SwipeBackView extends FrameLayout {
    protected static final Map<String, Bitmap> BITMAP_ARRAY = new LinkedHashMap<String, Bitmap>();

    protected static Bitmap getLastBitmap() {
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

    protected Activity activity;
    protected View contentView;
    protected View prevView;
    protected Bitmap prevBitmap;
    protected float rawX;
    protected int moveX;
    protected int contentX;

    protected LayoutParams contentLayoutParams;
    protected LayoutParams prevLayoutParams;
    protected static final float PROPORTION = 2f;

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

    protected void init() {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) actionBar.hide();
        SwipeBackView.this.setPadding(0, 0, 0, 0);

        prevBitmap = getLastBitmap();
        if (prevBitmap == null) {
            prevView = new View(activity);
            prevView.setBackground(new ColorDrawable(Color.rgb(0, 0, 0)));
        } else {
            prevView = new ImageView(activity);
            ((ImageView) prevView).setScaleType(ImageView.ScaleType.FIT_XY);
            ((ImageView) prevView).setImageBitmap(prevBitmap);
        }
        prevLayoutParams = getMatchLayoutParams();
        prevView.setLayoutParams(prevLayoutParams);

        contentLayoutParams = getMatchLayoutParams();
        contentView.setLayoutParams(contentLayoutParams);

        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rawX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (rawX <= 0) rawX = event.getRawX();
                        if (rawX <= 50) {
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
                        contentLayoutParams.leftMargin = (int) rawX;
                        contentView.setLayoutParams(contentLayoutParams);

                        prevLayoutParams.leftMargin = (int) -(contentX / PROPORTION);
                        prevView.setLayoutParams(prevLayoutParams);

                        if (moveX >= contentX / 2) {
                            activity.finish();
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

}
