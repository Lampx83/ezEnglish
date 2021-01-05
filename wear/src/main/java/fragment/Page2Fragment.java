
package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jquiz.project2.R;


public class Page2Fragment extends Fragment {

    Context context;

    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_layout, container, false);
        context = getActivity();
        textView = (TextView) rootView.findViewById(R.id.textView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateView(final String translate) {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                String history = preferences.getString("history1", "");
                textView.setText(Html.fromHtml("<b>HISTORY</b>" + history));

            }
        });
    }


}
