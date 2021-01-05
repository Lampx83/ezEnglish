/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pedometer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import database.PedometerHandler;
import entity.Pedometer;
import others.MyFunc;


public class StepService extends Service {
    private static final String TAG = "debug";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;

    private StepDisplayer mStepDisplayer;


    private PowerManager.WakeLock wakeLock;


    private int mSteps;
    private int mStepsOn;


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "[SERVICE] onCreate");
        super.onCreate();


        showNotification();

        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);


        acquireWakeLock();

        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);


        String today = MyFunc.getDateFormat().format(new Date()); //Get today
        String date = mState.getString("date", "");  //get Date in pref
        if (!date.equals(today)) {//New day
            savetoDBandAndResetPref(today, date);
        }


        mStepDisplayer = new StepDisplayer(mPedometerSettings, getApplicationContext());
        mStepDisplayer.setSteps(mState.getInt("steps", 0));  //Lay du lieu tu DB ra nao
        mStepDisplayer.setStepsOn(mState.getInt("stepson", 0));  //Lay du lieu tu DB ra nao
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);


        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings();

        // Tell the user we started.
        // Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }

    private void savetoDBandAndResetPref(String today, String preday) {
        //Save to DB
        mSteps = mState.getInt("steps", 0);
        mStepsOn = mState.getInt("stepson", 0);

        if (!preday.equals("")) {
            PedometerHandler mPedometerHandler = new PedometerHandler(getApplicationContext());
            entity.Pedometer pedometer = new Pedometer();
            pedometer.date = preday;
            pedometer.steps = mSteps;
            pedometer.stepson = mStepsOn;
            mPedometerHandler.insert(pedometer);
        }

        mStateEditor = mState.edit();
        //Reset Pref
        mStateEditor.putString("date", today);
        mStateEditor.putInt("steps", 0);
        mStateEditor.putInt("stepson", 0);
        mStateEditor.commit();


        resetValues();

    }

    private void saveToPref() {
        String today = MyFunc.getDateFormat().format(new Date()); //get today
        mStateEditor = mState.edit();
        String date = mState.getString("date", "");  //get date in pref
        if (date.equals("") || date.equals(today)) {  // continue count
            mStateEditor.putString("date", today);
            mStateEditor.putInt("steps", mSteps);
            mStateEditor.putInt("stepson", mStepsOn);
        } else {  //new day
            savetoDBandAndResetPref(today, date);
        }
        mStateEditor.commit();
    }

    @Override
    public void onDestroy() {

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();


        wakeLock.release();

        super.onDestroy();

        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);

        // Tell the user we stopped.
        //Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER /*|
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
                mSensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value);


    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }

    private int mDesiredPace;
    private float mDesiredSpeed;


    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        if (mStepDetector != null) {
            mStepDetector.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }

        if (mStepDisplayer != null) mStepDisplayer.reloadSettings();

    }

    public void resetValues() {
        if (mStepDisplayer != null) {
            mStepDisplayer.setSteps(0);


        }
    }


    /**
     * Forwards pace values from PaceNotifier to the activity.
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value, int valueon) {
            mSteps = value;
            mStepsOn = valueon;
            passValue();
            saveToPref();
            Log.d("debug", "step " + mSteps + "    stepon " + mStepsOn);
        }

        public void passValue() {
            if (mCallback != null) {
                mCallback.stepsChanged(mSteps);
            }
        }
    };


    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
//        CharSequence text = getText(R.string.app_name);
//        Notification notification = new Notification(R.drawable.ic_notification, null,
//                System.currentTimeMillis());
//        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
//        Intent pedometerIntent = new Intent();
//        pedometerIntent.setComponent(new ComponentName(this, Pedometer.class));
//        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                pedometerIntent, 0);
//        notification.setLatestEventInfo(this, text,
//                getText(R.string.notification_subtitle), contentIntent);
//
//        mNM.notify(R.string.app_name, notification);


    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
        }
    };

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        } else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        } else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }

}

