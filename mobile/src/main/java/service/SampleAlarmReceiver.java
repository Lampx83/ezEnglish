package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.legacy.content.WakefulBroadcastReceiver;

public class SampleAlarmReceiver extends WakefulBroadcastReceiver {
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, SampleSchedulingService.class);
		startWakefulService(context, service);
	}

	public void setAlarm(Context context) {
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, SampleAlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		// alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 6000, 6000, alarmIntent);
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0,  AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent); // 3 ngay hien 1 lan

		ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
	}

	public void cancelAlarm(Context context) {
		if (alarmMgr != null) {
			alarmMgr.cancel(alarmIntent);
		}
		ComponentName receiver = new ComponentName(context, SampleBootReceiver.class);
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
	}
}