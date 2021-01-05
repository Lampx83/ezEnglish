package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jquiz.project2.R;

import java.util.ArrayList;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import entity_display.MTranslate;
import listview.CardAdapter;

public class CardListActivity extends ParentActivity {


    private ListView listView;
    private CardAdapter lvAdapter;
    private ArrayList<MTranslate> m_Objects;
    private ArrayList<MTranslate> m_Objects_temp;

    private TermsHandler mTermsHandler;
    private TranslateHandler mTranslateHandler;

    private Button btnStudy;
    private Button btnTrueFalse;
    private Button btnMultiChoice;
    private Button btnMatching;
    private Button btnWritten;
    private Button btnMultiplayer;
    String learn_context;
    int collected_flashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cardlist_layout);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_context);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        Spinner spContext = (Spinner) getSupportActionBar().getCustomView().findViewById(R.id.spContext);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.arrContext, android.R.layout.simple_spinner_item);

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
                learn_context = adapter.getItem(i).toString();

                update();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnStudy = (Button) findViewById(R.id.btnStudy);
        btnTrueFalse = (Button) findViewById(R.id.btnTrueFalse);
        btnMultiChoice = (Button) findViewById(R.id.btnMultiChoice);
        btnMatching = (Button) findViewById(R.id.btnMatching);
        btnWritten = (Button) findViewById(R.id.btnWritten);
        btnMultiplayer = (Button) findViewById(R.id.btnMultiplayer);

        mTermsHandler = new TermsHandler(context);
        mTranslateHandler = new TranslateHandler(context);

        btnStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 1) {

                    CharSequence[] items = new CharSequence[2];
                    items[0] = getResources().getString(R.string.show_original_text_first);
                    items[1] = getResources().getString(R.string.show_translated_text_first);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected) {
                            if (indexSelected == 0)
                                MyGlobal.showTermFirst = false;
                            else
                                MyGlobal.showTermFirst = true;
                        }
                    });

                    builder.setPositiveButton(getResources().getString(R.string.start), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, ViewCardActivity.class);
                            context.startActivity(intent);

                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else
                    Toast.makeText(context, getResources().getString(R.string.you_dont_have_any_flashcard), Toast.LENGTH_LONG).show();
            }
        });

        btnTrueFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 2) {
                    Intent intent = new Intent(context, TrueFalseGameActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.need_2_flashcard), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnMultiChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 4) {
                    Intent intent = new Intent(context, MultipleChoiceGameActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.need_4_flashcard), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMatching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 4) {
                    Intent intent = new Intent(context, MatchGameActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.need_4_flashcard), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 1) {
                    Intent intent = new Intent(context, WrittenGameActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.need_1_flashcard), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberOfItem() >= 2) {
                    Intent intent = new Intent(context, TwoPlayerActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, getResources().getString(R.string.need_2_flashcard), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // viewingSetsID = 84954615;
        listView = (ListView) findViewById(R.id.cardList);
        m_Objects = new ArrayList<MTranslate>();
        lvAdapter = new CardAdapter(context, m_Objects);
        listView.setAdapter(lvAdapter);
    }

    int numberOfItem() {
        int nog = 0;
        for (MTranslate translate : m_Objects) {
            nog = nog + translate.status;
        }
        return m_Objects.size() + nog;
    }

    @Override
    protected void onResume() {
        update();
        super.onResume();
    }

    public void update() {
        if (learn_context != null) {
            m_Objects.clear();
            getCard(0, getString(R.string.often_missed));
            getCard(1, getString(R.string.sometimes_missed));
            getCard(-1, getString(R.string.no_answer_yet));
            getCard(2, getString(R.string.seldom_missed));
            getCard(3, getString(R.string.never_missed));

//        String where = DataBaseHandler.TRANSLATE_CONTEXT + "='" + learn_context + "'";
//        collected_flashcard = mTermsHandler.getNumber("count(*)", where);

            lvAdapter.notifyDataSetChanged();
        }
    }

    private void getCard(int box, String title) {
        m_Objects_temp = mTranslateHandler.getAllBy("where " + DataBaseHandler.TRANSLATE_CONTEXT + "='" + learn_context + "'" + " and " + DataBaseHandler.BOX + "= " + box, "");
        if (m_Objects_temp.size() > 0) {
            MTranslate mTerms = new MTranslate();
            mTerms.mark = m_Objects_temp.size();
            mTerms.status = -1;
            mTerms.box = box;
            mTerms.text = title;
            m_Objects.add(mTerms);
            m_Objects.addAll(m_Objects_temp);
        }
    }
}