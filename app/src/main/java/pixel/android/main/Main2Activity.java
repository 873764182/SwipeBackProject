package pixel.android.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pixel.android.swipeback.PixelSwipeBackView;

public class Main2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new PixelSwipeBackView(this, R.layout.activity_main2));
    }

    public void showToast(View view) {
        startActivity(new Intent(this, Main3Activity.class));
    }
}
