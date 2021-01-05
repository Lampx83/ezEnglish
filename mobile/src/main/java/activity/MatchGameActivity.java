package activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jquiz.project2.R;

import java.util.ArrayList;
import java.util.Collections;

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import entity_display.MTerms;
import others.AppDialog;
import others.ImageLoader;
import others.MyFunc;

public class MatchGameActivity extends ParentActivity {

    private static final int MENU_INFO = 0;


    private int termSelected = -1;
    private int defSelected = -1;
    private ArrayList<MTerms> list = new ArrayList<MTerms>();

    private final TextView[] listTermView = new TextView[4];
    private final TextView[] listDefView = new TextView[4];
    private final ImageView[] listImgView = new ImageView[4];
    private final ProgressBar[] listProgressView = new ProgressBar[4];
    private final LinearLayout[] listDefLayoutView = new LinearLayout[4];
    private int viewingSetsID;
    private TermsHandler mTermsHandler;
    private int numCorrect = 0;


    private TextView tvEnd;
    private Button btnEnd;
    private int life = 3;

    private AppDialog mAppDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        context = this;
        super.onCreate(arg0);
        setContentView(R.layout.activity_flashcardmatchgame_layout);
        mAppDialog = new AppDialog(context);

        tvEnd = (TextView) findViewById(R.id.tvEnd);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                numCorrect = 0;
                life = 3;
                createGame();
                ((LinearLayout) findViewById(R.id.llGame)).setVisibility(View.VISIBLE);
                supportInvalidateOptionsMenu();
            }
        });


        listTermView[0] = (TextView) findViewById(R.id.term1);
        listTermView[1] = (TextView) findViewById(R.id.term2);
        listTermView[2] = (TextView) findViewById(R.id.term3);
        listTermView[3] = (TextView) findViewById(R.id.term4);

        listTermView[0].setTextSize(1.2f * listTermView[0].getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        listTermView[1].setTextSize(1.2f * listTermView[1].getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        listTermView[2].setTextSize(1.2f * listTermView[2].getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        listTermView[3].setTextSize(1.2f * listTermView[3].getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);

        listDefView[0] = (TextView) findViewById(R.id.def1);
        listDefView[1] = (TextView) findViewById(R.id.def2);
        listDefView[2] = (TextView) findViewById(R.id.def3);
        listDefView[3] = (TextView) findViewById(R.id.def4);

        listImgView[0] = (ImageView) findViewById(R.id.img_card1);
        listImgView[1] = (ImageView) findViewById(R.id.img_card2);
        listImgView[2] = (ImageView) findViewById(R.id.img_card3);
        listImgView[3] = (ImageView) findViewById(R.id.img_card4);

        listProgressView[0] = (ProgressBar) findViewById(R.id.pb1);
        listProgressView[1] = (ProgressBar) findViewById(R.id.pb2);
        listProgressView[2] = (ProgressBar) findViewById(R.id.pb3);
        listProgressView[3] = (ProgressBar) findViewById(R.id.pb4);


        listDefLayoutView[0] = (LinearLayout) findViewById(R.id.ll1);
        listDefLayoutView[1] = (LinearLayout) findViewById(R.id.ll2);
        listDefLayoutView[2] = (LinearLayout) findViewById(R.id.ll3);
        listDefLayoutView[3] = (LinearLayout) findViewById(R.id.ll4);

        mTermsHandler = new TermsHandler(context);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        createGame();
        super.onWindowFocusChanged(hasFocus);
    }

    private void createGame() {

        list = mTermsHandler.getAllBy(DataBaseHandler.MARK + "<=?", new String[]{"1"}, "Random() Limit 4");
        int i = 0;
        for (MTerms term : list) {
            listTermView[i].setEnabled(true);
            listTermView[i].setText(term.getTerm());
            listTermView[i].setTag(term.getItemID());

            listTermView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selected = 0;
                    for (int i = 0; i < 4; i++) {
                        if (v.getId() == listTermView[i].getId()) {
                            selected = i;
                            break;
                        }
                    }
                    termSelected = selected;
                    if (defSelected != -1) {
                        match();
                    } else {
                        resetSeclect();
                        v.setBackgroundColor(Color.BLACK);
                        ((TextView) v).setTextColor(Color.WHITE);
                    }
                }
            });
            i++;
        }
        i = 0;
        Collections.shuffle(list);
        for (MTerms term : list) {
            listImgView[i].setVisibility(View.GONE);
            listDefView[i].setText(term.getDefinition());
            listDefLayoutView[i].setTag(term.getItemID());
            listDefLayoutView[i].setEnabled(true);
            if (term.getImage() != null && term.getImage().getUrl() != null) {
                listImgView[i].setVisibility(View.VISIBLE);

                String url = term.getImage().getUrl();
                if (url.startsWith("img")) {
                    Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + url, listDefLayoutView[i]);
                    listImgView[i].setImageBitmap(bitmap);
                } else {
                    listProgressView[i].setVisibility(View.VISIBLE);

                    new ImageLoader(context, R.drawable.img_holder, listProgressView[i]).DisplayImage(term.getImage().getUrl(), listImgView[i]);
                }


            }
            listDefLayoutView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setBackgroundColor(Color.BLACK);
                    int selected = 0;
                    for (int i = 0; i < 4; i++) {
                        if (v.getId() == listDefLayoutView[i].getId()) {
                            selected = i;
                            break;
                        }
                    }
                    defSelected = selected;
                    if (termSelected != -1) {
                        match();
                    } else {

                        resetSeclect();
                        v.setBackgroundColor(Color.BLACK);
                        listDefView[selected].setTextColor(Color.WHITE);

                    }
                }
            });
            i++;
        }
        resetSeclect();
    }

    private void resetSeclect() {
        for (int i = 0; i < 4; i++) {
            if (listTermView[i].getTag() != null) {
                if (!listTermView[i].getTag().equals(0)) {
                    listTermView[i].setBackgroundResource(R.drawable.border_top_xamc_pink);
                    //listTermView[i].setTextColor(Color.BLACK);
                }
                if (!listDefLayoutView[i].getTag().equals(0)) {
                    listDefLayoutView[i].setBackgroundResource(R.drawable.border_topleft_xamc);
                    listDefView[i].setTextColor(Color.BLACK);
                }
            }

        }
    }

    boolean first = true;

    private void match() {
        if (first) {
            MyFunc.writeUserLog(context, UActivity.PLAY_MATCHING);
            first = false;
        }
        if (listTermView[termSelected].getTag().equals(listDefLayoutView[defSelected].getTag())) { // Correct
            new MyFunc(context).answer(true, "" + listTermView[termSelected].getTag(), 4);

            listTermView[termSelected].setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
            listTermView[termSelected].setText("");
            listTermView[termSelected].setEnabled(false);

            listDefLayoutView[defSelected].setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
            listDefView[defSelected].setText("");
            listImgView[defSelected].setVisibility(View.INVISIBLE);
            listDefLayoutView[defSelected].setEnabled(false);

            listTermView[termSelected].setTag(0);
            listDefLayoutView[defSelected].setTag(0);

            numCorrect++;
            if (numCorrect % 4 == 0) {
                createGame();
            }
        } else {
            new MyFunc(context).answer(false, "" + listTermView[termSelected].getTag(), 4);
            resetSeclect();
            life--;
            if (life < 0) { // Game Over

                ((LinearLayout) findViewById(R.id.llGame)).setVisibility(View.INVISIBLE);

                int highscore = 0;


                tvEnd.setText(Html.fromHtml("<big>" + getResources().getString(R.string.game_over) + "</big><br>&nbsp<br>" + getString(R.string.score) + ": " + numCorrect));
                first=true;

            } else {
                listDefLayoutView[defSelected].setAnimation(new MyFunc(context).blinkAnimation());
                listTermView[termSelected].setAnimation(new MyFunc(context).blinkAnimation());
            }
            supportInvalidateOptionsMenu();
        }
        termSelected = -1;
        defSelected = -1;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (life >= 0) {
            MenuItemCompat.setShowAsAction(menu.add(0, MENU_INFO, 0, getResources().getString(R.string.life) + ": " + life), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_INFO) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

}
