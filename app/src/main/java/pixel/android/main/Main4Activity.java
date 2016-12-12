package pixel.android.main;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import pixel.android.swipeback.PixelSwipeBackView;

public class Main4Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new PixelSwipeBackView(this, R.layout.activity_main4) {
            @Override
            public Drawable getBackgroundDrawable() {
                return new ColorDrawable(Color.argb(200, 255, 255, 255));   // 自定义窗口背景色.默认是白色不透明颜色背景.
            }
        });

        // 通过代码的方式设置背景透明 无效
//        getWindow().setBackgroundDrawableResource(R.drawable.ic_launcher2);
//
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.5f;
//        getWindow().setAttributes(lp);
//
//        getWindow().getDecorView().setBackgroundResource(R.drawable.ic_launcher2);
    }
}
