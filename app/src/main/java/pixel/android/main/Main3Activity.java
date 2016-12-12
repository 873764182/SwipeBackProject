package pixel.android.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pixel.android.swipeback.PixelSwipeBackView;

public class Main3Activity extends Activity {

    private PixelSwipeBackView pixelSwipeBackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pixelSwipeBackView = new PixelSwipeBackView(this, R.layout.activity_main3);
        setContentView(pixelSwipeBackView);
    }

    public void showNext(View view) {
        startActivity(new Intent(this, Main4Activity.class));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        pixelSwipeBackView.finishActivity();
    }
}
