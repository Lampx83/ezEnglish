package activity;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.jquiz.project2.R;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import fragment.TranslateListFragment;


public class HistoryActivity extends ParentActivity {

    SwitchCompat switchForActionBar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitle(getResources().getString(R.string.history));
        setContentView(R.layout.fragment_container);
        final TranslateListFragment fragment = new TranslateListFragment();
        fragment.learn_context="All";
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        //    setContentView(R.layout.activity_history);
        // Get the ViewPager and set it's PagerAdapter so that it can display items

//        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
//        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), this));
//        // Give the TabLayout the ViewPager
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
//        tabLayout.setupWithViewPager(viewPager);

        //Shared items

        getSupportActionBar().setCustomView(R.layout.view_actionbar_history);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        Spinner spContext = (Spinner) getSupportActionBar().getCustomView().findViewById(R.id.spContext);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.arrContext, android.R.layout.simple_spinner_item);

        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(preferences.getString(MyPref.pref_context, MyGlobal.others))) {
                spContext.setSelection(position);
                break;
            }
        }
        switchForActionBar = (SwitchCompat) getSupportActionBar().getCustomView().findViewById(R.id.switchForActionBar);

        spContext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fragment.learn_context = adapter.getItem(i).toString();
                fragment.update(switchForActionBar.isChecked());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        switchForActionBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fragment.update(isChecked);
            }
        });

    }
}
