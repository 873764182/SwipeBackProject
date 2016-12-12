package pixel.android.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import pixel.android.swipeback.PixelSwipeBackView;

public class Main4Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new PixelSwipeBackView(this, R.layout.activity_main4));

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
