package fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.jquiz.project2.R;

import java.util.ArrayList;
import java.util.List;

import database.PedometerHandler;
import entity.Pedometer;
import listview.PedometerAdapter;

/**
 * Created by xuanlam on 12/11/15.
 */
public class PedometerFragment extends Fragment {

    private PedometerHandler mPedometerHandler;
    private ArrayList<Pedometer> m_Objects;
    private PedometerAdapter lvAdapter;
    protected volatile boolean loading;
    ListView listView;
    protected boolean empty = true;
    protected int page = 1;
    private boolean boolGetItem = true;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pedometor, container, false);
        context = getActivity();
        mPedometerHandler = new PedometerHandler(context);
        page = 1;
        empty = true;
        boolGetItem = true;
        listView = (ListView) rootView.findViewById(R.id.lv);
        m_Objects = new ArrayList<>();


        Pedometer pedometer = new Pedometer();
        SharedPreferences mState = context.getSharedPreferences("state", 0);
        pedometer.date = "Today";
        pedometer.steps = mState.getInt("steps", 0);

        m_Objects.add(pedometer);
        lvAdapter = new PedometerAdapter(context, m_Objects);
        listView.setAdapter(lvAdapter);
        listView.setOnScrollListener(new EndlessScrollListener());

        return rootView;
    }


    private class EndlessScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if ((empty && !loading) || (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 10))) {
                if (boolGetItem) {
                    loading = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new getSetTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    } else {
                        new getSetTask().execute();
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    public class getSetTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {

            List<Pedometer> list = mPedometerHandler.getAllBy("", "Limit 30 offset " + (page - 1) * 30);

            if (list.size() < 30) {
                boolGetItem = false;
                if (list.size() == 0)
                    return 0;
            }
            page++;
            m_Objects.addAll(list);
            empty = false;
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                lvAdapter.notifyDataSetChanged();
            }
            loading = false;
            super.onPostExecute(result);
        }
    }
}
