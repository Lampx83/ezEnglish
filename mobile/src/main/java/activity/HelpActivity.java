package activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jquiz.project2.R;

/**
 * Created by xuanlam on 12/19/15.
 */
public class HelpActivity extends ParentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView wb = new WebView(this);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            setTitle(getString(R.string.help) + " " + getApplicationName(this) + " " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        wb.loadUrl("file:///android_asset/help.html");
        wb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wb.getSettings().setBuiltInZoomControls(true); // will give pinch zoom
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wb.getSettings().setDisplayZoomControls(false); // but won't display the zoom buttons
        }

        setContentView(wb);

    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}
