package activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;

/**
 * Created by xuanlam on 10/19/15.
 */
public class ParentActivity extends WearableActivity {
    public SharedPreferences preferences;
    public SharedPreferences.Editor preferences_edit;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = this;
        restore_static();
        save_static();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        context = this;
        ((MyApplication) getApplication()).context = this;
        super.onResume();
    }

    private void restore_static() {
//        // Boolean
//        if (MyGlobal.isSquare == null)
//            MyGlobal.isSquare = preferences.getBoolean("isSquare", false);
    }

    private void save_static() {
//        preferences_edit = preferences.edit();
//        preferences_edit.putBoolean("isSquare", MyGlobal.isSquare);
//
//        preferences_edit.commit();
    }

    @Override
    protected void onStart() {
        context = this;
        super.onStart();
    }
}
