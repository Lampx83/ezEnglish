package activity;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MyApplication extends Application{
    boolean isfirst = true;

    /**
     * //	 * The Analytics singleton. The field is set in onCreate method override when the application class is initially created.
     * //
     */
    private static GoogleAnalytics analytics;

    /**
     * The default app tracker. The field is from onCreate callback when the application is initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either set android:name="&lt;this.class.name&gt;" attribute on your application element in AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set android:name="&lt;this.class.name&gt;" attribute on your application element in AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        analytics = GoogleAnalytics.getInstance(this);

        // TODO: Replace the tracker-id with your app one from https://www.google.com/analytics/web/
        tracker = analytics.newTracker("UA-71589158-1");

        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        tracker.enableAutoActivityTracking(true);

        tracker.send(new HitBuilders.ScreenViewBuilder().setNewSession().build());
    }


    public static Context context;




}