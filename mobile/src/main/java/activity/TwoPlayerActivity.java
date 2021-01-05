package activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jquiz.project2.R;

import java.util.ArrayList;
import java.util.Random;

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.TermsHandler;
import entity.Term;
import entity_display.MTerms;

import others.ImageLoader;
import others.MyFunc;


public class TwoPlayerActivity extends ParentActivity {

    private ArrayList<MTerms> list;
    private TermsHandler mTermsHandler;

   // private SmoothProgressBar smoothProgressBar;
    private Handler mHandler;
    private int progress = 0;
    private int i = 0;
    private boolean currentAnswer = true;
    private ImageView hiddenImage;
    private String Correct = "";
    private LinearLayout llEndScreen;

    private TextView tvTerm1;
    private TextView tvDef1;
    private ImageView imgView1;
    private ProgressBar pb1;
    private Button btnTrue1;
    private Button btnFalse1;
    private TextView tvResult1;
    private TextView tvScore1;
    private int score1 = 0;
    private TextView tvIntro1;
    private Button btnStart1;

    private TextView tvTerm2;
    private TextView tvDef2;
    private ImageView imgView2;
    private ProgressBar pb2;
    private Button btnTrue2;
    private Button btnFalse2;
    private TextView tvResult2;
    private TextView tvScore2;
    private int score2 = 0;
    private TextView tvIntro2;
    private Button btnStart2;

    @Override
    protected void onCreate(Bundle arg0) {
        context = this;
        hiddenImage = new ImageView(context);
        main = true;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(arg0);
        setContentView(R.layout.activity_flashcardtruefalsegame2player_layout);

        getSupportActionBar().hide();
//        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.pb);
//        smoothProgressBar.setIndeterminate(false);

        ShapeDrawable shape = new ShapeDrawable();
        shape.setShape(new RectShape());
//        shape.getPaint().setColor(getResources().getColor(R.color.holo_blue_dark));
//        ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER, ClipDrawable.HORIZONTAL);
//        smoothProgressBar.setProgressDrawable(clipDrawable);
        llEndScreen = (LinearLayout) findViewById(R.id.llEndScreen);

        mTermsHandler = new TermsHandler(context);
        list = mTermsHandler.getAllBy("", null, "Random() limit 10"); // All
        mHandler = new Handler();
        initPlayer1();
        initPlayer2();
        String intro = getResources().getString(R.string.tut_multiplayer1) + " " + list.size() + " " + getResources().getString(R.string.tut_multiplayer2);
        tvIntro1.setText(Html.fromHtml(intro));
        tvIntro2.setText(Html.fromHtml(intro));
    }

    void startRound() {
        tvIntro1.setTextColor(getResources().getColor(R.color.mau_den));
        tvIntro2.setTextColor(getResources().getColor(R.color.mau_den));
        tvIntro1.setBackgroundColor(Color.WHITE);
        tvIntro2.setBackgroundColor(Color.WHITE);
        String intro = getResources().getString(R.string.tut_multiplayer1) + " " + list.size() + " " + getResources().getString(R.string.tut_multiplayer2);
        tvIntro1.setText(Html.fromHtml(intro));
        tvIntro2.setText(Html.fromHtml(intro));
        btnStart1.setText(getString(R.string.try_again));
        btnStart2.setText(getString(R.string.try_again));
        llEndScreen.setVisibility(View.INVISIBLE);
        score1 = 0;
        score2 = 0;
        tvScore1.setText("" + score1);
        tvScore2.setText("" + score2);
        i = 0;
        tvTerm1.setTextColor(getResources().getColor(R.color.mau_den));
        tvTerm2.setTextColor(getResources().getColor(R.color.mau_den));
        tvDef1.setVisibility(View.INVISIBLE);
        tvDef2.setVisibility(View.INVISIBLE);
        tvTerm1.setVisibility(View.INVISIBLE);
        tvTerm2.setVisibility(View.INVISIBLE);
        btnFalse1.setVisibility(View.VISIBLE);
        btnTrue1.setVisibility(View.VISIBLE);
        imgView1.setVisibility(View.GONE);
        imgView2.setVisibility(View.GONE);
        btnFalse2.setVisibility(View.VISIBLE);
        btnTrue2.setVisibility(View.VISIBLE);
        createGame();
    }

    public void initPlayer1() {
        btnStart1 = (Button) findViewById(R.id.btnStart1);
        btnStart1.setTextSize(1.5f * btnStart1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        btnStart1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startRound();

            }
        });
        tvIntro1 = (TextView) findViewById(R.id.tvIntro1);
        tvIntro1.setTextSize(1.4f * tvIntro1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        tvTerm1 = (TextView) findViewById(R.id.tvTerm1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tvTerm1.setTextIsSelectable(true);
        }
        tvTerm1.setTextSize(1.3f * tvTerm1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        tvDef1 = (TextView) findViewById(R.id.tvDef1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tvDef1.setTextIsSelectable(true);
        }
        tvDef1.setTextSize(1.1f * tvDef1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        imgView1 = (ImageView) findViewById(R.id.imgView1);
        pb1 = (ProgressBar) findViewById(R.id.pb1);

        btnTrue1 = (Button) findViewById(R.id.btnTrue1);
        btnFalse1 = (Button) findViewById(R.id.btnFalse1);
        tvResult1 = (TextView) findViewById(R.id.tvResult1);
        tvResult1.setTextSize(1.5f * tvResult1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        btnTrue1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoose(true, 1);
            }
        });
        btnFalse1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userChoose(false, 1);
            }
        });
        tvScore1 = (TextView) findViewById(R.id.tvScore1);
        tvScore1.setTextSize(4f * tvScore1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
    }

    public void initPlayer2() {
        btnStart2 = (Button) findViewById(R.id.btnStart2);
        btnStart2.setTextSize(1.5f * btnStart2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        btnStart2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startRound();

            }
        });
        tvIntro2 = (TextView) findViewById(R.id.tvIntro2);
        tvIntro2.setTextSize(1.4f * tvIntro2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        tvTerm2 = (TextView) findViewById(R.id.tvTerm2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tvTerm2.setTextIsSelectable(true);
        }
        tvTerm2.setTextSize(1.3f * tvTerm2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        tvDef2 = (TextView) findViewById(R.id.tvDef2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            tvDef2.setTextIsSelectable(true);
        }
        tvDef2.setTextSize(1.1f * tvDef2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        imgView2 = (ImageView) findViewById(R.id.imgView2);
        pb2 = (ProgressBar) findViewById(R.id.pb2);

        btnTrue2 = (Button) findViewById(R.id.btnTrue2);
        btnFalse2 = (Button) findViewById(R.id.btnFalse2);
        tvResult2 = (TextView) findViewById(R.id.tvResult2);
        tvResult2.setTextSize(1.5f * tvResult1.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        btnTrue2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                userChoose(true, 2);
            }
        });
        btnFalse2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                userChoose(false, 2);
            }
        });
        tvScore2 = (TextView) findViewById(R.id.tvScore2);
        tvScore2.setTextSize(4f * tvScore2.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
    }

    private void createGame() {
        if (score1 + score2 == list.size()) { // Game Over
            MyFunc.writeUserLog(context, UActivity.PLAY_2_PLAYER);
            llEndScreen.setVisibility(View.VISIBLE);
            if (score1 == score2) {
                tvIntro1.setText(Html.fromHtml("Draw " + score1 + "-" + score2));
                tvIntro2.setText(Html.fromHtml("Draw " + score1 + "-" + score2));
            } else if (score1 > score2) {
                tvIntro1.setText(Html.fromHtml("Win " + score1 + "-" + score2));
                tvIntro2.setText(Html.fromHtml("Lose " + score2 + "-" + score1));

                tvIntro1.setTextColor(Color.WHITE);
                tvIntro1.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
                tvIntro2.setTextColor(Color.WHITE);
                tvIntro2.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
            } else {
                tvIntro1.setText(Html.fromHtml("Lose " + score1 + "-" + score2));
                tvIntro2.setText(Html.fromHtml("Win " + score2 + "-" + score1));

                tvIntro1.setTextColor(Color.WHITE);
                tvIntro1.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
                tvIntro2.setTextColor(Color.WHITE);
                tvIntro2.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
            }

        } else {
            progress = 0;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progress <= 100) {
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                smoothProgressBar.setProgress(progress++);
//                            }
//                        });
                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            createView();
                        }
                    });
                }
            }).start();
        }
    }

    private void createView() {
        tvTerm1.setTextColor(getResources().getColor(R.color.mau_den));
        tvTerm2.setTextColor(getResources().getColor(R.color.mau_den));
        tvTerm1.setVisibility(View.VISIBLE);
        tvTerm2.setVisibility(View.VISIBLE);
        btnFalse1.setVisibility(View.VISIBLE);
        btnTrue1.setVisibility(View.VISIBLE);
        imgView1.setVisibility(View.GONE);
        imgView2.setVisibility(View.GONE);
        btnFalse2.setVisibility(View.VISIBLE);
        btnTrue2.setVisibility(View.VISIBLE);
        if (list.size() > 0) {

            MTerms term = list.get(i);
            Correct = term.getTerm();
            if (term.getDefinition().trim().equals("")) {
                tvDef1.setVisibility(View.GONE);
                tvDef2.setVisibility(View.GONE);
            } else {
                tvDef1.setText(term.getDefinition());
                tvDef1.setVisibility(View.VISIBLE);
                tvDef2.setText(term.getDefinition());
                tvDef2.setVisibility(View.VISIBLE);
            }
            if (term.getImage() != null && term.getImage().getUrl() != null) {
                imgView1.setVisibility(View.VISIBLE);
                imgView2.setVisibility(View.VISIBLE);
                String url = term.getImage().getUrl();
                if (url.startsWith("img")) {
                    Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + url, llEndScreen);
                    imgView1.setImageBitmap(bitmap);
                    imgView2.setImageBitmap(bitmap);

                } else {
                    pb1.setVisibility(View.VISIBLE);
                    pb2.setVisibility(View.VISIBLE);
                    new ImageLoader(context, R.drawable.img_holder, pb1, pb2, imgView1, imgView2).DisplayImage(term.getImage().getUrl(), hiddenImage);
                }


                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                rParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                tvDef1.setLayoutParams(rParam);
                tvDef2.setLayoutParams(rParam);
            } else {

                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                rParam.addRule(RelativeLayout.CENTER_IN_PARENT);
                tvDef1.setLayoutParams(rParam);
                tvDef2.setLayoutParams(rParam);
            }

            Random r = new Random();
            float x = r.nextFloat();
            int ranInt = 0;
            if (x < 0.5) { // false
                ranInt = (int) (x * list.size());
                currentAnswer = false;
                if (ranInt == i)
                    currentAnswer = true;
            } else {
                currentAnswer = true;
            }

            if (currentAnswer == true)
                showTerm(term);
            else
                showTerm(list.get(ranInt));
            i++;
        }
    }

    private void showTerm(Term term) {
        tvTerm1.setText(term.getTerm());
        tvTerm2.setText(term.getTerm());
    }

    private void userChoose(boolean choose, int player) {
        btnFalse1.setVisibility(View.INVISIBLE);
        btnTrue1.setVisibility(View.INVISIBLE);
        btnFalse2.setVisibility(View.INVISIBLE);
        btnTrue2.setVisibility(View.INVISIBLE);

        if (player == 1) {
            if (currentAnswer == choose) {
                tvResult1.setText(getString(R.string.correct).toUpperCase());
                score1++;
                tvResult2.setText(":(");
                new MyFunc(context).playsound(R.raw.correct);
                tvResult1.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
                tvResult2.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
            } else {
                tvResult1.setText(getString(R.string.wrong).toUpperCase());
                tvResult2.setText(":)");
                score2++;
                new MyFunc(context).playsound(R.raw.wrong);
                tvResult1.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
                tvResult2.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
            }
        }
        if (player == 2) {
            if (currentAnswer == choose) {
                tvResult2.setText(getString(R.string.correct).toUpperCase());
                tvResult1.setText(":(");
                score2++;
                new MyFunc(context).playsound(R.raw.correct);
                tvResult2.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
                tvResult1.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
            } else {
                score1++;
                tvResult2.setText(getString(R.string.wrong).toUpperCase());
                tvResult1.setText(":)");
                new MyFunc(context).playsound(R.raw.wrong);
                tvResult2.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
                tvResult1.setBackgroundColor(getResources().getColor(R.color.mau_xanhlo));
            }
        }
        tvScore1.setText("" + score1);
        tvScore2.setText("" + score2);
        if (!currentAnswer) {
            tvTerm1.setText(Correct);
            tvTerm2.setText(Correct);
            tvTerm1.setTextColor(getResources().getColor(R.color.mau_xanhlo));
            tvTerm2.setTextColor(getResources().getColor(R.color.mau_xanhlo));

        }
        createGame();

    }
}
