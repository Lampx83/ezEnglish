package service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import com.jquiz.project2.R;

import activity.MainActivity;

public class SampleSchedulingService extends IntentService {
    public SampleSchedulingService() {
        super("SchedulingService");
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();

        //Submit to server

        //sendNotification("A","BCD");



        SampleAlarmReceiver.completeWakefulIntent(intent);

    }


    private void sendNotification(String title, String msg) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.btn_hint).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
