package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jquiz.ezSyntax.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startActivity(new Intent(this, MetroActivity.class));
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
