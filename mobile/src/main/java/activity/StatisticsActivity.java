package activity;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.jquiz.project2.R;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import database.UserLogHandler;


public class StatisticsActivity extends ParentActivity {

    SwitchCompat switchForActionBar;
    TranslateHandler mTranslateHandler;
    UserLogHandler mUserLogHandler;
    TermsHandler mTermsHandler;
    int gPosition = -1;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitle(getResources().getString(R.string.statistics));
        setContentView(R.layout.activity_statistics);

        mTranslateHandler = new TranslateHandler(context);
        mTermsHandler = new TermsHandler(context);
        mUserLogHandler = new UserLogHandler(context);

        getSupportActionBar().setCustomView(R.layout.view_actionbar_context);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);
        Spinner spContext = (Spinner) getSupportActionBar().getCustomView().findViewById(R.id.spContext);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.arrContext, android.R.layout.simple_spinner_item);
        switchForActionBar = (SwitchCompat) getSupportActionBar().getCustomView().findViewById(R.id.switchForActionBar);

        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).equals(preferences.getString(MyPref.pref_context, MyGlobal.others))) {
                spContext.setSelection(position);
                break;
            }
        }

        spContext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                gPosition = i;
                update(adapter.getItem(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // update(preferences.getString(MyPref.pref_context, "Others"));
    }

    public void update(String learn_context) {
        String where = DataBaseHandler.TRANSLATE_CONTEXT + "='" + learn_context + "'";
        if (learn_context.equals("All")) {
            where = "1=1";
        }
        int translated = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_TTS + ")", where);
        int listen = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_TTS + ")", where);
        int practice_times = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_PRACTICE_TIMES + ")", where);
        int photo = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_IMAGE + ")", where + " and " + DataBaseHandler.TRANSLATE_USERID + "='" + MyGlobal.user_id + "' and " + DataBaseHandler.TRANSLATE_IMAGE + " is not null");
        int note = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_NOTE + ")", where + " and " + DataBaseHandler.TRANSLATE_USERID + "='" + MyGlobal.user_id + "' and " + DataBaseHandler.TRANSLATE_NOTE + " is not null");

        int flashcard = mTermsHandler.getNumber("count(*)", where);
        String practice = ": " + getString(R.string.practiced) + "<b> " + practice_times + "</b> " + getString(R.string.times);
        int average_score = mTranslateHandler.getNumber("avg(" + DataBaseHandler.TRANSLATE_SCORE + ")", where + " and " + DataBaseHandler.TRANSLATE_SCORE + ">=0");
        if (average_score > 0)
            practice = practice + " (" + getString(R.string.average_score) + ": " + average_score + ")";

        int review = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.REVIEW_FLASHCARD);
        int trueFalse = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_TRUE_FALSE);
        int multichoice = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_MULTICHOICE);
        int maching = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_MATCHING);
        int written = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_WRITTEN);
        int twoplayer = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_2_PLAYER);
        int en_cn = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.EN_CN_DICT);
        int cn_en = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.CN_EN_DICT);
        int en_en = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.EN_EN_DICT);
        int search_photo = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.IMAGE_SEARCH);


        ((TextView) findViewById(R.id.tvTranslated)).setText(Html.fromHtml(": " + getString(R.string.translated) + "<b> " + translated + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvListen)).setText(Html.fromHtml(": " + getString(R.string.listened) + "<b> " + listen + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvPractice)).setText(Html.fromHtml(practice));
        ((TextView) findViewById(R.id.tvPhoto)).setText(Html.fromHtml(": " + getString(R.string.taken_found) + "<b> " + photo + "</b> " + getString(R.string.photo_s)));
        ((TextView) findViewById(R.id.tvNote)).setText(Html.fromHtml(": " + getString(R.string.taken) + "<b> " + note + "</b> " + getString(R.string.note_s)));
        ((TextView) findViewById(R.id.tvFlashcard)).setText(Html.fromHtml(": " + getString(R.string.collected) + "<b> " + flashcard + "</b> " + getString(R.string.flashcard_s)));
        if (preferences.getBoolean(MyPref.pref_homework_uploaded + gPosition, false)) {
            ((TextView) findViewById(R.id.tvHomework)).setText(Html.fromHtml(getString(R.string.homework) + ": <b>" + getString(R.string.completed) + "</b>"));
        } else {
            ((TextView) findViewById(R.id.tvHomework)).setText(Html.fromHtml(getString(R.string.homework) + " : <b>" + getString(R.string.uncompleted) + "</b>"));
        }
        ((TextView) findViewById(R.id.tvStudy)).setText(Html.fromHtml(": " + getString(R.string.reviewed) + "<b> " + review + "</b> " + getString(R.string.flashcard_s)));
        ((TextView) findViewById(R.id.tvTrueFalse)).setText(Html.fromHtml(": " + getString(R.string.played) + " " + getString(R.string.game_true_false) + "<b> " + trueFalse + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvMultichoice)).setText(Html.fromHtml(": " + getString(R.string.played) + " " + getString(R.string.game_multichoice) + "<b> " + multichoice + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvMatching)).setText(Html.fromHtml(": " + getString(R.string.played) + " " + getString(R.string.game_matching) + "<b> " + maching + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvWritten)).setText(Html.fromHtml(": " + getString(R.string.played) + " " + getString(R.string.game_written) + "<b> " + written + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tv2Player)).setText(Html.fromHtml(": " + getString(R.string.played) + " " + getString(R.string.game_multiplayer) + "<b> " + twoplayer + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvEnglish_chinese)).setText(Html.fromHtml(": " + getString(R.string.looked_up) + " " + getString(R.string.english_chinese_dictionary) + "<b> " + en_cn + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvChinese_english)).setText(Html.fromHtml(": " + getString(R.string.looked_up) + " " + getString(R.string.chinese_english_dictionary) + "<b> " + cn_en + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvEnglish_english)).setText(Html.fromHtml(": " + getString(R.string.looked_up) + " " + getString(R.string.english_english_dictionary) + "<b> " + en_en + "</b> " + getString(R.string.times)));
        ((TextView) findViewById(R.id.tvImage_search)).setText(Html.fromHtml(": " + getString(R.string.searched_photo) + "<b> " + search_photo + "</b> " + getString(R.string.times)));

    }
}
