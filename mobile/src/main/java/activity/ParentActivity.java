package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jquiz.project2.R;

import java.io.File;
import java.util.List;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import controlvariable.UActivity;
import fragment.ToolsFragment;
import fragment.TranslateFragment;
import others.AppDialog;
import others.DropboxClientFactory;
import others.ImageLoader;
import others.MyFunc;

public abstract class ParentActivity extends AppCompatActivity {
    protected static final String TAG = "debug";
    public SharedPreferences preferences;
    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    public static final int PERMISSIONS_REQUEST_AUDIO_RECORD = 1;
    public static final int PERMISSIONS_REQUEST_MANAGE_DOCUMENTS = 2;
    protected boolean main = false;
    private String accessToken = "IeifUOakhDAAAAAAAAAFZ25EQpJi8d0kljqCHOcXPSfmqcj12vUa2x5uWYNWc31g";
    protected boolean hasTool = false;
    protected ImageButton btnWhiteboard;
    public RelativeLayout move_able;
    public ToolsFragment mToolsFragment;
    public RelativeLayout.LayoutParams layoutParams;
    private boolean first = true;
    private int dpWidth;
    private float mPreviousX = 0;
    private int deltaX_;
    public TranslateFragment mTranslateFragment;
    protected  Context context;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        context = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        restore_static(this, true);
        save_static();
        if (!preferences.getBoolean(MyGlobal.APP_VER, false)) {
            context.deleteDatabase("translate.db");
            preferences.edit().putBoolean(MyGlobal.APP_VER, true).commit();
        }
        if (!main) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MyGlobal.image_folder = Environment.getExternalStorageDirectory().toString() + "/ezTranslate/image/";
        MyGlobal.record_folder = Environment.getExternalStorageDirectory().toString() + "/ezTranslate/record/";
    }

    void save_static() {
        preferences.edit().putFloat("ScreenScale", MyGlobal.ScreenScale).commit();
    }

    public void restore_static(Context context, boolean cal) {
        MyGlobal.user_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        MyGlobal.user_name = preferences.getString("user_name", "User_" + MyGlobal.user_id.substring(0, 3));

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();
        float change = 0;

        change = 0.5f * ((new MyFunc(context).convertPixelsToDp(width) - 360) / 360);

        MyGlobal.ScreenScale = 1.0f;
        if (change > 0)
            MyGlobal.ScreenScale = 1.0f + change;

        if (cal) {
            if (MyGlobal.scaledDensity == null)
                MyGlobal.scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.edit().putBoolean(MyPref.pref_onScreen, false).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DropboxClientFactory.init(accessToken);
        //  uploadFile();
        //  downloadFile("abc.txt");
    }


    private void viewFileInExternalApp(File result) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = result.getName().substring(result.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(ext);

        intent.setDataAndType(Uri.fromFile(result), type);

        // Check for a handler first to avoid a crash
        PackageManager manager = getPackageManager();
        List<ResolveInfo> resolveInfo = manager.queryIntentActivities(intent, 0);
        if (resolveInfo.size() > 0) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyFunc.writeUserLog(context, UActivity.START_ACTIVITY);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (first && hasTool) {
            first = false;
            setAlphaBtnWhiteboard(0.5f);
            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            dpWidth = displayMetrics.widthPixels;
            layoutParams.width = dpWidth;
            layoutParams.setMargins(-dpWidth + btnWhiteboard.getWidth(), 0, 0, 0);
            move_able.requestLayout();
            btnWhiteboard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int X = (int) event.getRawX();
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            deltaX_ = X - layoutParams.leftMargin;
                            mPreviousX = X;
                            setAlphaBtnWhiteboard(1.0f);
                            break;
                        case MotionEvent.ACTION_UP:
                            if (X - mPreviousX > 0 || (X - mPreviousX == 0 && layoutParams.leftMargin == -dpWidth + btnWhiteboard.getWidth())) { // Open
                                layoutParams.leftMargin = 0; // Open definitely
                                move_able.requestLayout();
                                new MyFunc(context).writeUserLog(context, UActivity.SHOW_TOOLS);
                            } else { // Close
                                layoutParams.leftMargin = -dpWidth + btnWhiteboard.getWidth(); // close definitely
                                move_able.requestLayout();
                                setAlphaBtnWhiteboard(0.5f);
                                new MyFunc(context).writeUserLog(context, UActivity.CLOSE_TOOLS);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (layoutParams.leftMargin <= 0 && layoutParams.leftMargin >= -dpWidth + btnWhiteboard.getWidth()) {
                                layoutParams.leftMargin = X - deltaX_;
                                move_able.requestLayout();
                            }
                            break;
                    }
                    return true;
                }
            });
            move_able.setVisibility(View.VISIBLE);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public void setAlphaBtnWhiteboard(float a) {
        if (btnWhiteboard != null) {
            AlphaAnimation alpha = new AlphaAnimation(a, a);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            btnWhiteboard.startAnimation(alpha);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (hasTool && layoutParams.leftMargin == 0) { //Close when click back
                layoutParams.leftMargin = -dpWidth + btnWhiteboard.getWidth(); // close definitely
                move_able.requestLayout();
                setAlphaBtnWhiteboard(0.5f);
                new MyFunc(context).writeUserLog(context, UActivity.CLOSE_TOOLS);
                return false;
            } else {
                if (main)
                    new AppDialog(context).showDialogExitApp();
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, final View webview, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, webview, menuInfo);
        if (webview instanceof WebView) {
            final WebView.HitTestResult result = ((WebView) webview).getHitTestResult();
            if (result.getType() == WebView.HitTestResult.IMAGE_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                String source = result.getExtra();
                if (mTranslateFragment.current_translateid != -1) {
                    if (source.startsWith("data:image")) {
                        source = source.substring(source.indexOf(",") + 1);
                        byte[] decodedString = Base64.decode(source, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        mTranslateFragment.showImage(decodedByte);

                    } else if (source.startsWith("http")) {
                        mTranslateFragment.pb.setVisibility(View.VISIBLE);
                        mTranslateFragment.preShowImage();
                        new ImageLoader(context, R.drawable.img_holder, mTranslateFragment.pb).DisplayImage(source, mTranslateFragment.img_card);
                        mTranslateFragment.updateImagetoDB(source);
                    }
                }
                layoutParams.leftMargin = -dpWidth + btnWhiteboard.getWidth(); // close definitely
                move_able.requestLayout();
                setAlphaBtnWhiteboard(0.5f);
                new MyFunc(context).writeUserLog(context, UActivity.CLOSE_TOOLS);
            }
        }
    }
}
