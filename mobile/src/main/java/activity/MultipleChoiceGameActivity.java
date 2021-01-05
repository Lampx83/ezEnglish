package activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.jquiz.project2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import entity_display.MTerms;
import others.ImageLoader;
import others.MyFunc;
import pacard.AnimationFactory;
import pacard.AnimationFactory.FlipDirection;

public class MultipleChoiceGameActivity extends ParentActivity {

    private static final int MENU_INFO = 0;
    private ArrayList<MTerms> list;
    private final TextView[] listTermView = new TextView[4];
    private TextView tvDef;
    private ImageView img_card;
    private ProgressBar pb;
    private RelativeLayout rlResult;
    private TextView tvResult;

    private TermsHandler mTermsHandler;


    private TextView tvTerm;
    private ViewAnimator viewAnimator;
    private ImageView btnFlip1;
    private ImageView btnFlip2;
    private boolean allowFlip = false;
    private int numCorrect = 0;
    private TextView tvEnd;
    private Button btnEnd;
    private int life = 3;


    OnClickListener flipListen = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (allowFlip)
                AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        context = this;
        super.onCreate(arg0);
        setContentView(R.layout.activity_flashcardmultiplechoicegame_layout);


        tvEnd = (TextView) findViewById(R.id.tvEnd);
        btnEnd = (Button) findViewById(R.id.btnEnd);
        btnEnd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                numCorrect = 0;
                life = 3;
                createGame();
                ((LinearLayout) findViewById(R.id.llGame)).setVisibility(View.VISIBLE);
                supportInvalidateOptionsMenu();
            }
        });

        // viewingSetsID = 84933388;
        tvTerm = (TextView) findViewById(R.id.tvTerm);
        tvTerm.setMovementMethod(new ScrollingMovementMethod());
        tvTerm.setTextSize(1.5f * tvTerm.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        viewAnimator = (ViewAnimator) findViewById(R.id.viewFlipper);
        btnFlip1 = (ImageView) findViewById(R.id.btnFlip1);
        btnFlip2 = (ImageView) findViewById(R.id.btnFlip2);
        btnFlip1.setOnClickListener(flipListen);
        btnFlip2.setOnClickListener(flipListen);
        viewAnimator.setOnClickListener(flipListen);
        tvDef = (TextView) findViewById(R.id.tvDef);
        tvDef.setMovementMethod(new ScrollingMovementMethod());
        tvDef.setTextSize(1.2f * tvDef.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        img_card = (ImageView) findViewById(R.id.img_card);
        pb = (ProgressBar) findViewById(R.id.pb);

        rlResult = (RelativeLayout) findViewById(R.id.rlResult);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setTextSize(2.5f * tvResult.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);

        listTermView[0] = (TextView) findViewById(R.id.term1);
        listTermView[1] = (TextView) findViewById(R.id.term2);
        listTermView[2] = (TextView) findViewById(R.id.term3);
        listTermView[3] = (TextView) findViewById(R.id.term4);


        listTermView[0].setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userChoose(0);
            }
        });
        listTermView[1].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoose(1);
            }
        });
        listTermView[2].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoose(2);
            }
        });
        listTermView[3].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoose(3);
            }
        });
        rlResult.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        mTermsHandler = new TermsHandler(context);
        list = mTermsHandler.getAllBy(  DataBaseHandler.MARK + "<=?", new String[]{ "1"}, "Random()");
        Collections.sort(list);


    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        createGame();
        super.onWindowFocusChanged(hasFocus);
    }

    private int i = -1;
    ArrayList<MTerms> list_choices;

    private void createGame() {
        viewAnimator.setInAnimation(null);
        viewAnimator.setOutAnimation(null);
        viewAnimator.setDisplayedChild(0);
        allowFlip = false;
        btnFlip1.setVisibility(View.INVISIBLE);
        btnFlip2.setVisibility(View.INVISIBLE);
        tvTerm.setVisibility(View.INVISIBLE);

        i++;
        if (i >= list.size())
            i = 0;
        tvTerm.setText("?");
        listTermView[0].setVisibility(View.VISIBLE);
        listTermView[1].setVisibility(View.VISIBLE);
        listTermView[2].setVisibility(View.VISIBLE);
        listTermView[3].setVisibility(View.VISIBLE);
        if (list.size() > 0) {

            MTerms term = list.get(i);

            list_choices = mTermsHandler.getAllBy( DataBaseHandler.MARK + "<=? and " + DataBaseHandler.CARD_ID + "!=?", new String[]{ "1", "" + term.getItemID()}, "Random() limit 3");

            Random r = new Random();
            float x = r.nextFloat();
            currentAnswerIndex = (int) (x * 4);
            currentAnswerString = term.getTerm();
            int k = 0;
            for (int j = 0; j < 4; j++) {
                if (j == currentAnswerIndex) {
                    listTermView[j].setText(term.getTerm());
                } else {
                    listTermView[j].setText(list_choices.get(k).getTerm());
                    k++;
                }
            }

            // Image
            if (term.getImage() != null && term.getImage().getUrl() != null) {
                img_card.setVisibility(View.VISIBLE);
                String url = term.getImage().getUrl();
                if (url.startsWith("img")) {
                    Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + url, rlResult);
                    img_card.setImageBitmap(bitmap);
                } else {
                    pb.setVisibility(View.VISIBLE);
                    new ImageLoader(context, R.drawable.img_holder, pb).DisplayImage(term.getImage().getUrl(), img_card);
                }

                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                rParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                tvDef.setLayoutParams(rParam);
                img_card.setPadding(2 * MyGlobal.fivedp, 0, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp);
            } else {
                img_card.setVisibility(View.GONE);
                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                tvDef.setLayoutParams(rParam);
            }
            // Definitation
            if (term.getDefinition().trim().equals("")) {
                tvDef.setVisibility(View.GONE);
                img_card.setPadding(2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp);
            } else {
                tvDef.setText(term.getDefinition());
                tvDef.setVisibility(View.VISIBLE);
            }
        }
    }

    private int currentAnswerIndex = -1;
    private String currentAnswerString;
    boolean first = true;

    private void userChoose(final int choose) {
        if (first) {
            MyFunc.writeUserLog(context, UActivity.PLAY_MULTICHOICE);
            first = false;
        }
        AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);

        viewAnimator.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                allowFlip = true;
                btnFlip1.setVisibility(View.VISIBLE);
                btnFlip2.setVisibility(View.VISIBLE);
                tvTerm.setVisibility(View.VISIBLE);
                listTermView[0].setVisibility(View.INVISIBLE);
                listTermView[1].setVisibility(View.INVISIBLE);
                listTermView[2].setVisibility(View.INVISIBLE);
                listTermView[3].setVisibility(View.INVISIBLE);

                if (currentAnswerIndex == choose) {
                    numCorrect++;
                    tvResult.setText(getString(R.string.correct).toUpperCase());
                    new MyFunc(context).answer(true, list.get(i).getItemID(), 4);
                    rlResult.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
                } else {
                    tvResult.setText(getString(R.string.wrong).toUpperCase());
                    new MyFunc(context).answer(false, list.get(i).getItemID(), 4);
                    rlResult.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
                    life--;
                    if (life < 0) { // Game Over
                        first = true;
                        ((LinearLayout) findViewById(R.id.llGame)).setVisibility(View.INVISIBLE);
                            tvEnd.setText(Html.fromHtml("<big>"+getResources().getString(R.string.game_over)+"</big><br>&nbsp<br>"+getString(R.string.score)+": " + numCorrect));
                    }
                }
                tvTerm.setText(currentAnswerString);
                supportInvalidateOptionsMenu();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (life >= 0) {
            MenuItemCompat.setShowAsAction(menu.add(0, MENU_INFO, 0, getResources().getString(R.string.life)+": " + life), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
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
