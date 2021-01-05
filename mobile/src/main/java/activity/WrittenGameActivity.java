package activity;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import entity_display.MTerms;
import others.AppDialog;
import others.ImageLoader;
import others.MyFunc;
import pacard.AnimationFactory;
import pacard.AnimationFactory.FlipDirection;

public class WrittenGameActivity extends ParentActivity {

    private static final int MENU_HINT = 0;
    private static final int MENU_INFO = 1;
    private ArrayList<MTerms> list;
    private TextView tvTerm;
    private TextView tvTerm2;
    private TextView tvDef;
    private ImageView img_card;
    private ProgressBar pb;
    private Button btnAnswer;

    private TermsHandler mTermsHandler;
    private TextView tvResult;
    private RelativeLayout rlResult;
    private RelativeLayout rlDef;

    private ViewAnimator viewAnimator;
    private ImageView btnFlip1;
    private ImageView btnFlip2;
    private boolean allowFlip = false;
    private EditText etAnswer;
    private int numCorrect = 0;
    private TextView tvEnd;
    private Button btnEnd;
    private int life = 3;

    private AppDialog mAppDialog;
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
        setContentView(R.layout.activity_flashcardwrittengame_layout);
        mAppDialog = new AppDialog(context);

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
        etAnswer = (EditText) findViewById(R.id.etAnswer);

        viewAnimator = (ViewAnimator) findViewById(R.id.viewFlipper);

        btnFlip1 = (ImageView) findViewById(R.id.btnFlip1);
        btnFlip2 = (ImageView) findViewById(R.id.btnFlip2);
        btnFlip1.setOnClickListener(flipListen);
        btnFlip2.setOnClickListener(flipListen);
        viewAnimator.setOnClickListener(flipListen);


        tvTerm = (TextView) findViewById(R.id.tvTerm);
        tvTerm.setTextSize(1.5f * tvTerm.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);

        tvTerm2 = (TextView) findViewById(R.id.tvTerm2);
        tvTerm2.setTextSize(1.5f * tvTerm2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);

        tvDef = (TextView) findViewById(R.id.tvDef);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tvDef.setTextIsSelectable(true);
        }
        tvDef.setTextSize(1.2f * tvDef.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        img_card = (ImageView) findViewById(R.id.img_card);
        pb = (ProgressBar) findViewById(R.id.pb);

        btnAnswer = (Button) findViewById(R.id.btnAnswer);
        rlResult = (RelativeLayout) findViewById(R.id.rlResult);
        rlDef = (RelativeLayout) findViewById(R.id.rlDef);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvResult.setTextSize(2.0f * tvResult.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        btnAnswer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userAnswer(etAnswer.getText().toString().trim().toLowerCase());

            }
        });

        rlResult.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createGame();
            }
        });

        mTermsHandler = new TermsHandler(context);
        list = mTermsHandler.getAllBy(DataBaseHandler.MARK + "<=?", new String[]{"1"}, "Random()");
        Collections.sort(list);



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        createGame();
        super.onWindowFocusChanged(hasFocus);
    }

    private int i = -1;

    private void createGame() {
        viewAnimator.setInAnimation(null);
        viewAnimator.setOutAnimation(null);
        viewAnimator.setDisplayedChild(0);
        allowFlip = false;
        btnFlip1.setVisibility(View.INVISIBLE);
        btnFlip2.setVisibility(View.INVISIBLE);
        tvTerm.setVisibility(View.INVISIBLE);
        tvTerm2.setPaintFlags(tvTerm2.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        i++;
        if (i >= list.size())
            i = 0;
        numHint = 0;
        supportInvalidateOptionsMenu();
        btnAnswer.setVisibility(View.VISIBLE);
        etAnswer.setText("");

        rlResult.setBackgroundDrawable(null);
        if (list.size() > 0) {
            MTerms term = list.get(i);
            tvTerm.setText(term.getTerm());
            String hint = term.getTerm().trim().replaceAll("\\w", "_ ");
            tvTerm2.setText(hint);
            // Image
            if (term.getImage() != null && term.getImage().getUrl() != null) {
                img_card.setVisibility(View.VISIBLE);
                String url = term.getImage().getUrl();
                if (url.startsWith("img")) {
                    Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + url, rlDef);
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

    boolean first = true;
    private void userAnswer(final String answer) {
        if (first) {
            MyFunc.writeUserLog(context, UActivity.PLAY_WRITTEN);
            first = false;
        }

        if (answer.length() == 0) {
            etAnswer.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake));
        } else {
            tvTerm2.setText(answer);
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
                    numHint = 2;
                    supportInvalidateOptionsMenu();
                    btnAnswer.setVisibility(View.INVISIBLE);

                    if (tvTerm.getText().toString().toLowerCase().trim().equals(answer.toLowerCase().trim())) {
                        numCorrect++;
                        tvResult.setText(getString(R.string.correct).toUpperCase());
                        new MyFunc(context).answer(true, list.get(i).getItemID(), 4);
                        rlResult.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
                    } else {
                        tvResult.setText(getString(R.string.wrong).toUpperCase());
                        new MyFunc(context).answer(false, list.get(i).getItemID(), 4);
                        rlResult.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
                        tvTerm2.setPaintFlags(tvTerm2.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        life--;
                        if (life < 0) { // Game Over
                            first = true;
                            ((LinearLayout) findViewById(R.id.llGame)).setVisibility(View.INVISIBLE);
                            tvEnd.setText(Html.fromHtml("<big>"+getResources().getString(R.string.game_over)+"</big><br>&nbsp<br>"+getString(R.string.score)+": " + numCorrect));

                        }
                    }
                    supportInvalidateOptionsMenu();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && life >= 0) {
            menu.add(0, MENU_INFO, 0, getResources().getString(R.string.life)+": " + life).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            if (numHint < 2 || tvTerm.getText().toString().trim().length() <= 2) {
                menu.add(0, MENU_HINT, 0, "Hint").setIcon(R.drawable.btn_hint).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        return true;
    }

    int numHint = 0;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_HINT) {
            numHint++;
            String hint = tvTerm.getText().toString().trim().replaceAll("\\w", "_ ");
            if (numHint == 1) {
                hint = tvTerm.getText().toString().trim().charAt(0) + hint.substring(1, hint.length() - 1);
            } else if (numHint == 2) {
                hint = tvTerm.getText().toString().trim().charAt(0) + hint.substring(1, hint.length() - 2) + tvTerm.getText().toString().trim().charAt(tvTerm.getText().toString().trim().length() - 1);
                invalidateOptionsMenu();
            }
            tvTerm2.setText(hint.trim());
            return true;
        } else if (item.getItemId() == MENU_INFO) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


}
