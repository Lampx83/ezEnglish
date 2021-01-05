package activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import com.jquiz.project2.R;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import others.MyFunc;
import pedometer.PedometerSettings;
import pedometer.StepService;


public abstract class AbsPedometerActivity extends ParentActivity {

    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private TextView tvStep;
    private TextView tvDistance;
    private TextView tvCalories;
    private boolean mIsRunning;
    private StepService mService;

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getBoolean(MyPref.pref_showpedo, MyGlobal.ENABLEPEDO)) {
            Log.i(TAG, "[ACTIVITY] onResume");
            mSettings = PreferenceManager.getDefaultSharedPreferences(this);
            mPedometerSettings = new PedometerSettings(mSettings);


            // Read from preferences if the service was running on the last onPause
            mIsRunning = mPedometerSettings.isServiceRunning();

            // Start the service if this is considered to be an application start (last onPause was long ago)
            if (!mIsRunning) {
                startStepService();
                bindStepService();
            } else if (mIsRunning) {
                bindStepService();
            }

            mPedometerSettings.clearServiceRunning();
            tvStep = (TextView) findViewById(R.id.tvStep);
            tvDistance = (TextView) findViewById(R.id.tvDistance);
            tvCalories = (TextView) findViewById(R.id.tvCalories);
        }
    }

    @Override
    protected void onPause() {
        if (preferences.getBoolean(MyPref.pref_showpedo, MyGlobal.ENABLEPEDO)) {
            Log.i(TAG, "[ACTIVITY] onPause");
            if (mIsRunning) {
                //   unbindStepService();
            }
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
        super.onPause();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder) service).getService();
            mService.registerCallback(mCallback);
            mService.reloadSettings();
        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

    private void startStepService() {
        if (!mIsRunning) {
            Log.i(TAG, "[SERVICE] Start");
            mIsRunning = true;
            startService(new Intent(this, StepService.class));
        }
    }

    private void bindStepService() {
        Log.i(TAG, "[SERVICE] Bind");
        bindService(new Intent(this, StepService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindStepService() {
        Log.i(TAG, "[SERVICE] Unbind");
        unbindService(mConnection);
    }

//    private void stopStepService() {
//        Log.i(TAG, "[SERVICE] Stop");
//        if (mService != null) {
//            Log.i(TAG, "[SERVICE] stopService");
//            stopService(new Intent(this,
//                    StepService.class));
//        }
//        mIsRunning = false;
//    }

    // TODO: unite all into 1 type of message
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(0, value, 0));
        }


    };


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mStepValue = (int) msg.arg1;
            tvStep.setText("" + mStepValue);
            tvDistance.setText("" + MyFunc.getDistance(mStepValue));
            String calo = String.format("%.01f", MyFunc.getCalo(mStepValue));
            tvCalories.setText(calo);
        }

    };

}