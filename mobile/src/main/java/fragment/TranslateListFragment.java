package fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.core.v2.files.ListFolderResult;
import com.jquiz.project2.R;

import java.util.ArrayList;
import java.util.List;

import activity.HistoryDetailActivity;
import activity.MapsActivity;
import controlvariable.MyGlobal;
import controlvariable.MyPref;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import entity.Translate;
import entity_display.MTranslate;
import listview.TranslateAdapter;
import others.DropboxClientFactory;
import others.ListFolderTask;

/**
 * Created by xuanlam on 12/11/15.
 */
public class TranslateListFragment extends Fragment {
    FloatingActionButton fabMap;
    FloatingActionButton fabFlashcard;
    private TranslateHandler mTranslateHandler;
    private TermsHandler mTermsHandler;
    private ArrayList<Translate> m_Objects;
    private TranslateAdapter lvAdapter;
    protected volatile boolean loading;
    ListView listView;
    protected boolean empty = true;
    protected int page = 1;
    private boolean boolGetItem = true;
    Context context;
    public String type;
    public String learn_context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translatedlist, container, false);
        context = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTranslateHandler = new TranslateHandler(context);
        mTermsHandler = new TermsHandler(context);
        page = 1;
        empty = true;
        boolGetItem = true;
        listView = (ListView) rootView.findViewById(R.id.lv);
        fabMap = (FloatingActionButton) rootView.findViewById(R.id.fabMap);
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                context.startActivity(intent);
            }
        });

        fabFlashcard = (FloatingActionButton) rootView.findViewById(R.id.fabFlashcard);
        fabFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showFlashcardlistDialog();
            }
        });

        m_Objects = new ArrayList<>();
        lvAdapter = new TranslateAdapter(context, m_Objects);
        listView.setAdapter(lvAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, HistoryDetailActivity.class);
                intent.putExtra("translateid", m_Objects.get(position).translateid);
                intent.putExtra("text", m_Objects.get(position).text);

                gPosition = position;
                imgMark = (ImageView) view.findViewById(R.id.imgmark);
                context.startActivity(intent);
            }
        });


        return rootView;
    }

    boolean viewShared = false;

    boolean firstTime = true;

    int gPosition = -1;
    ImageView imgMark = null;

    @Override
    public void onResume() {
        if (gPosition != -1 && imgMark != null) {

            if (mTranslateHandler.checkIdExist(m_Objects.get(gPosition))) {
                int mark = 0;
                if (mTermsHandler.checkTranslateIdExist(m_Objects.get(gPosition).translateid))
                    mark = 1;
                if (mark == 1)
                    imgMark.setImageResource(R.drawable.mark_done);
                else
                    imgMark.setImageResource(R.drawable.mark);
                m_Objects.get(gPosition).mark = mark;
            } else {
                m_Objects.remove(gPosition);
                lvAdapter.notifyDataSetChanged();
            }
        }
        gPosition = -1;
        imgMark = null;
        super.onResume();
    }

    public void update(boolean viewShared) {
        this.viewShared = viewShared;
        int lastupdatetime = preferences.getInt(MyPref.pref_lastupdatetime, 0);


        if (viewShared && firstTime && ((System.currentTimeMillis() / 1000L - lastupdatetime) > MyGlobal.TIME_TO_UPDATE)) {
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setMessage(getString(R.string.downloading));
            dialog.show();
            new ListFolderTask(context, DropboxClientFactory.getClient(), new ListFolderTask.Callback() {
                @Override
                public void onDataLoaded(ListFolderResult result) {
                    m_Objects.clear();
                    empty = true;
                    loading = false;
                    boolGetItem = true;
                    firstTime = false;
                    page = 0;
                    listView.setOnScrollListener(new EndlessScrollListener());
                    //Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                    preferences.edit().putInt(MyPref.pref_lastupdatetime, (int) (System.currentTimeMillis() / 1000L)).commit();
                }

                @Override
                public void onError(Exception e) {
                    m_Objects.clear();
                    empty = true;
                    loading = false;
                    boolGetItem = true;
                    page = 0;
                    listView.setOnScrollListener(new EndlessScrollListener());
                    Toast.makeText(context, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).execute("/Translate");

        } else { //View offline
            m_Objects.clear();
            empty = true;
            loading = false;
            boolGetItem = true;
            page = 0;
            listView.setOnScrollListener(new EndlessScrollListener());
        }
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
            if (firstVisibleItem == 0)
                fabMap.setVisibility(View.VISIBLE);
            else {
                if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                    fabMap.setVisibility(View.GONE);
                } else {
                    fabMap.setVisibility(View.VISIBLE);
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

            String filter = DataBaseHandler.TRANSLATE_USERID + "= '" + MyGlobal.user_id + "'";
            if (viewShared) {
                filter = "1 = 1";
            }
            String strContext = "1 = 1";
            if (!learn_context.equals("All")) {
                strContext = DataBaseHandler.TRANSLATE_CONTEXT + "='" + learn_context + "'";
            }
            List<MTranslate> list = mTranslateHandler.getAllBy("where " + strContext + " and " + filter, "Limit 30 offset " + (page - 1) * 30);
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
            // if (result == 1) {
            lvAdapter.notifyDataSetChanged();
            //}
            loading = false;
            super.onPostExecute(result);
        }
    }


}
