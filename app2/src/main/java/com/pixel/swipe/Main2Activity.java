package com.pixel.swipe;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(new SwipeBackView(this, R.layout.activity_main2));
    }
}
