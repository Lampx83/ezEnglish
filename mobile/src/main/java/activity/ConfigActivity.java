package activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jquiz.project2.R;

import java.util.Locale;

import controlvariable.MyGlobal;
import controlvariable.MyPref;

public class ConfigActivity extends ParentActivity {

    //ToggleButton tbLanguage;
    Spinner spLanguage;

    SwitchCompat swRead;
    SwitchCompat swPractice;
    SwitchCompat swBookmark;
    SwitchCompat swPedo;

    SwitchCompat swTranslate;
    EditText etName;
    TextView tvShowPedo;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(getString(R.string.settings));
        tvShowPedo = (TextView) findViewById(R.id.tvShowPedo);
        //  tbLanguage = (ToggleButton) findViewById(R.id.tbLanguage);
        spLanguage = (Spinner) findViewById(R.id.spLanguage);

        swRead = (SwitchCompat) findViewById(R.id.swRead);
        swPractice = (SwitchCompat) findViewById(R.id.swPractice);
        swBookmark = (SwitchCompat) findViewById(R.id.swBookmark);

        swPedo = (SwitchCompat) findViewById(R.id.swPedo);
       // if (MyGlobal.user_name.toLowerCase().startsWith("cg") || MyGlobal.user_name.toLowerCase().startsWith("eg")) {
      //      swPedo.setEnabled(false);
     //   }
        swTranslate = (SwitchCompat) findViewById(R.id.swTranslate);
        etName = (EditText) findViewById(R.id.etName);

        tvShowPedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!swPedo.isChecked() && !swPractice.isChecked() && !swRead.isChecked())
                    count++;
                if (count > 10) {
                    etName.setVisibility(View.VISIBLE);
                }
            }
        });

        swTranslate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swRead.setEnabled(true);
                    swPractice.setEnabled(true);
                    swBookmark.setEnabled(true);
                } else {
                    swRead.setEnabled(false);
                    swPractice.setEnabled(false);
                    swBookmark.setEnabled(false);
                }
            }
        });


        if (preferences.getString(MyPref.pref_language, "cn").equals("en"))
            spLanguage.setSelection(0);
        else
            spLanguage.setSelection(1);

        spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spLanguage.getSelectedItemPosition() == 0) {
                    preferences.edit().putString(MyPref.pref_language, "en").commit();
                } else if (spLanguage.getSelectedItemPosition() == 1) {
                    preferences.edit().putString(MyPref.pref_language, "cn").commit();
                }
                setLocale(context);
                if(!first) {
                    changeLanguage = true;
                    finish();
                    startActivity(new Intent(context, ConfigActivity.class));
                }else{
                    first=false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        changeLanguage = false;

        swTranslate.setChecked(preferences.getBoolean(MyPref.pref_translate, true));
        swRead.setChecked(preferences.getBoolean(MyPref.pref_autoread, true));
        swPractice.setChecked(preferences.getBoolean(MyPref.pref_practicemode, false));
        swBookmark.setChecked(preferences.getBoolean(MyPref.pref_autobookmark, MyGlobal.AUTOBOOKMARK));
        swPedo.setChecked(preferences.getBoolean(MyPref.pref_showpedo, MyGlobal.ENABLEPEDO));
        etName.setText(preferences.getString("user_name", MyGlobal.user_name));



    }

    boolean first = true;
    boolean changeLanguage = true;



    @Override
    protected void onPause() {

        if (spLanguage.getSelectedItemPosition() == 0) {
            preferences.edit().putString(MyPref.pref_language, "en").commit();
        } else if (spLanguage.getSelectedItemPosition() == 1) {
            preferences.edit().putString(MyPref.pref_language, "cn");
        }

        preferences.edit().putBoolean(MyPref.pref_translate, swTranslate.isChecked()).commit();
        preferences.edit().putBoolean(MyPref.pref_autoread, swRead.isChecked()).commit();
        preferences.edit().putBoolean(MyPref.pref_practicemode, swPractice.isChecked()).commit();
        preferences.edit().putBoolean(MyPref.pref_autobookmark, swBookmark.isChecked()).commit();
        preferences.edit().putBoolean(MyPref.pref_showpedo, swPedo.isChecked()).commit();
        preferences.edit().putString("user_name", etName.getText().toString()).commit();


        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!changeLanguage) {
            setLocale(context);
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    public void setLocale(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Locale myLocale = new Locale(preferences.getString(MyPref.pref_language, "cn"));
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
