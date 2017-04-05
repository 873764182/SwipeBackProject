package com.pixel.swipe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(new SwipeBackView(this, R.layout.activity_main));
    }

    public void onViewClick(View view) {
//        View tv = view.getRootView();
//        tv.setDrawingCacheEnabled(true);
//        tv.buildDrawingCache();
//
//        try {
//            Bitmap bitmap = tv.getDrawingCache();
//            ImageView sv = new ImageView(this);
//            sv.setImageBitmap(bitmap);
//
//            new AlertDialog.Builder(this).setView(sv).show();
//        } catch (Exception e) {
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
//        }

        startActivity(new Intent(this, Main2Activity.class));

    }
}
