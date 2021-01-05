package activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.jquiz.project2.R;

import java.io.File;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import fragment.ToolsFragment;
import fragment.TranslateFragment;
import others.DataAnalyzeTask;
import others.DropboxClientFactory;
import others.MyLocation;
import others.UploadFileTask;


public class MainActivity extends AbsPedometerActivity {


    public static double longitude = 0;
    public static double latitude = 0;
    private MyLocation myLocation;
    private Spinner spContext;
    private ArrayAdapter<CharSequence> adapter;
    private boolean submitLogStart = false;

    private boolean first = true;

    @Override
    public void onCreate(Bundle arg0) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        MyGlobal.PHIENBAN = "PHIENBAN_8";
        if (!preferences.getBoolean(MyGlobal.PHIENBAN, false)) {
            this.deleteDatabase("translate.db");
            preferences.edit().clear().commit();
            preferences.edit().putBoolean(MyGlobal.PHIENBAN, true).commit();
        }
        super.onCreate(arg0);
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, ParentActivity.PERMISSIONS_REQUEST_AUDIO_RECORD);
//        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ParentActivity.PERMISSIONS_REQUEST_AUDIO_RECORD);
        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ParentActivity.PERMISSIONS_REQUEST_MANAGE_DOCUMENTS);
        }

        if (first) {
            first = false;
            new ConfigActivity().setLocale(context);
        }
        setContentView(R.layout.activity_main);
        mTranslateFragment = new TranslateFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mTranslateFragment).commit();
        getSupportActionBar().setCustomView(R.layout.view_actionbar_context);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        spContext = (Spinner) getSupportActionBar().getCustomView().findViewById(R.id.spContext);
        adapter = ArrayAdapter.createFromResource(this, R.array.arrContext, android.R.layout.simple_spinner_item);

        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(preferences.getString(MyPref.pref_context, MyGlobal.others))) {
                spContext.setSelection(position);
                break;
            }
        }

        spContext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferences.edit().putString(MyPref.pref_context, adapter.getItem(i).toString()).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        File wallpaperDirectory = new File(MyGlobal.image_folder);
        wallpaperDirectory.mkdirs();
        wallpaperDirectory = new File(MyGlobal.record_folder);
        wallpaperDirectory.mkdirs();
        myLocation = new MyLocation();
        showNameDialog();
        hasTool = true;
        if (hasTool) {
            mToolsFragment = (ToolsFragment) getSupportFragmentManager().findFragmentById(R.id.tool_fragment);

            btnWhiteboard = (ImageButton) findViewById(R.id.btnWhiteboard);
            if (MyGlobal.screen_small) {
                Bitmap bm_drawer_big = BitmapFactory.decodeResource(getResources(), R.drawable.drawer_web);
                btnWhiteboard.setImageBitmap(Bitmap.createScaledBitmap(bm_drawer_big, (int) (0.6f * bm_drawer_big.getWidth()), (int) (0.6f * bm_drawer_big.getHeight()), false));
            } else
                btnWhiteboard.setImageResource(R.drawable.drawer_web);

            move_able = (RelativeLayout) findViewById(R.id.move_able);
            layoutParams = (RelativeLayout.LayoutParams) move_able.getLayoutParams();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (MyGlobal.user_name.equals("joyadmin") || MyGlobal.user_name.equals("alexadmin"))
            menu.add("Analyze");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (item.getTitle().equals("Analyze")) {
            //Starting analyze
            new DataAnalyzeTask(context, DropboxClientFactory.getClient(), new DataAnalyzeTask.Callback() {

                @Override
                public void onComplete(Void result) {
                    Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e, String s) {
                    new AlertDialog.Builder(context)
                            .setTitle("Error")
                            .setMessage(s)
                            .show();
                }
            }).execute();
        }

        if (id == R.id.action_settings) {
            this.finish();
            startActivity(new Intent(context, ConfigActivity.class));
        } else if (id == R.id.action_flashcard) {
            //  showFlashcardlistDialog();
            Intent intent = new Intent(context, CardListActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.action_homework) {
            startActivity(new Intent(context, HomeWorkActivity.class));
        } else if (id == R.id.action_summary) {
            startActivity(new Intent(context, StatisticsActivity.class));
        } else if (id == R.id.action_listtranslate) {
            startActivity(new Intent(context, HistoryActivity.class));
        } else if (id == R.id.action_help) {
           // startActivity(new Intent(context, HelpActivity.class));
        } else
            return super.onOptionsItemSelected(item);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(preferences.getString(MyPref.pref_context, MyGlobal.others))) {
                spContext.setSelection(position);
                break;
            }
        }


        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            myLocation.getLocation(context, new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    //Cap nhat vao nhung cai gan day nhat ma chua co
                }
            });
        }

        if (!submitLogStart) {
            submitLogStart = true;
            new UploadFileTask(context, DropboxClientFactory.getClient(), "/Translate/", 0, "", new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {

                }

                @Override
                public void onError(Exception e) {

                }
            }).execute();
        }
        int lastuploadtime = preferences.getInt(MyPref.pref_lastuploadtime, 0);
        if (((System.currentTimeMillis() / 1000L - lastuploadtime) > MyGlobal.TIME_TO_UPLOAD)) {
            new UploadFileTask(context, DropboxClientFactory.getClient(), "/Statistics/", 3, "", new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {
                    preferences.edit().putInt(MyPref.pref_lastuploadtime, (int) (System.currentTimeMillis() / 1000L)).commit();
                }

                @Override
                public void onError(Exception e) {

                }
            }).execute();
        }


        if (preferences.getBoolean(MyPref.pref_showpedo, MyGlobal.ENABLEPEDO)) {
            ((LinearLayout) findViewById(R.id.llPedo)).setVisibility(View.VISIBLE);
        } else {
            ((LinearLayout) findViewById(R.id.llPedo)).setVisibility(View.GONE);
        }
    }

    public void showNameDialog() {
        if (MyGlobal.user_name.equals("User_" + MyGlobal.user_id.substring(0, 3))) {
            final EditText input = new AppCompatEditText(context);
            new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.enter_your_name)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    String text = input.getText().toString().trim();
                    if (!text.equals("")) {
                        MyGlobal.user_name = text;
                        preferences.edit().putString("user_name", text).commit();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.name_cannot_be_blank), Toast.LENGTH_SHORT).show();
                        showNameDialog();
                    }
                }
            }).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp).setCancelable(false).show();
        }

        if (MyGlobal.user_name.toLowerCase().startsWith("cg") || MyGlobal.user_name.toLowerCase().startsWith("eg")) {
            MyGlobal.ENABLEPEDO = true;
            preferences.edit().putBoolean(MyPref.pref_showpedo, true).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myLocation.getLocation(context, new MyLocation.LocationResult() {
                        @Override
                        public void gotLocation(Location location) {
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            //Cap nhat vao nhung cai gan day nhat ma chua co
                        }
                    });
                }
                return;
            }

        }
    }
}
