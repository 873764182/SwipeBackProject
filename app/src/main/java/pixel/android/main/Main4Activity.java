package pixel.android.main;

import android.app.Activity;
import android.os.Bundle;

import pixel.android.swipeback.PixelSwipeBackView;

public class Main4Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PixelSwipeBackView(this, R.layout.activity_main4));
    }
}
