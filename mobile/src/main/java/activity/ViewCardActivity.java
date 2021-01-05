package activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jquiz.project2.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import entity.Translate;
import entity_display.MTerms;
import fragment.ToolsFragment;
import others.MyFunc;
import viewPager.NonSwipeableViewPager;
import viewPager.QuestionTabStrip;

public class ViewCardActivity extends ParentActivity {
    public QuestionTabStrip pager_tab_strip;
    private ViewPager mPager;
    private NonSwipeableViewPager.CardSliderAdapter mPagerAdapter;
    private TermsHandler mTermsHandler;
    TextToSpeech tts;
    int gPosition = 0;
    public List<MTerms> list_terms;

    public Object read_english_text = "";
    private TranslateHandler mTranslateHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewcard_layout);
        mTranslateHandler = new TranslateHandler(context);

        pager_tab_strip = (QuestionTabStrip) findViewById(R.id.pager_tab_strip);
        mTermsHandler = new TermsHandler(context);


        list_terms = mTermsHandler.getAllBy(DataBaseHandler.MARK + "=?", new String[]{"1"}, "Random()");

        Collections.sort(list_terms);

        // MTerms endCard = new MTerms();
        // endCard.setTerm("End");
        // endCard.setDefinition("End");
        // list_terms.add(endCard);


        if (list_terms.size() > 0) {
            create_pager();
        } else {
            Toast.makeText(context, "All items are already mastered", Toast.LENGTH_LONG).show();
            mPager = (ViewPager) findViewById(R.id.pager);
            mPager.setVisibility(View.INVISIBLE);
        }


        pager_tab_strip.for_doquiz = true;
        pager_tab_strip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {


                gPosition = position;
                MyFunc.writeUserLog(context, UActivity.CHANGE_FLASHCARD);


            }
        });

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(new Locale(MyGlobal.lang2));
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts_done = true;
//                        CardFragment mCardFragment = (CardFragment) mPagerAdapter.getFragment(mPager.getCurrentItem());
//                        mCardFragment.updateSpeaker();
                    }
                }
            }

        });

        hasTool = true;
        if (hasTool) {
            mToolsFragment = (ToolsFragment) getSupportFragmentManager().findFragmentById(R.id.tool_fragment);

            btnWhiteboard = (ImageButton) findViewById(R.id.btnWhiteboard);
            if (MyGlobal.screen_small) {
                Bitmap bm_drawer_big = BitmapFactory.decodeResource(getResources(), R.drawable.drawer_web);
                btnWhiteboard.setImageBitmap(Bitmap.createScaledBitmap(bm_drawer_big, (int) (0.6f * bm_drawer_big.getWidth()), (int) (0.6f * bm_drawer_big.getHeight()), false));
            } else
                btnWhiteboard.setImageResource(R.drawable.drawer_web);

            move_able = (RelativeLayout) findViewById(R.id.move_able);
            layoutParams = (RelativeLayout.LayoutParams) move_able.getLayoutParams();
        }



    }

    public boolean tts_done = false;

    private void create_pager() {
        // Create pager
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.commit();
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new NonSwipeableViewPager.CardSliderAdapter(context, fm, list_terms);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);
        pager_tab_strip.setViewPager(mPager);
    }

    private static final int GROUP1 = 1;
    private static final int MENU_MARK = 2;
    private static final int MENU_SPEAK = 3;
    private static final int MENU_SHUFFLE = 5;// SHUFFLE

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (list_terms.size() > 0) { // phong truong hop ko co flashcard nao (vi du da mastered het)
//            SubMenu submenu = menu.addSubMenu(GROUP1, GROUP1, 1, "Mark");
//            if (list_terms.get(gPosition).mark == 0)
//                submenu.add(GROUP1, MENU_MARK, 0, "None").setChecked(true);
//            else
//                submenu.add(GROUP1, MENU_MARK, 0, "None");
//            if (list_terms.get(gPosition).mark == 1)
//                submenu.add(GROUP1, MENU_MARK, 1, "Bookmark").setChecked(true);
//            else
//                submenu.add(GROUP1, MENU_MARK, 1, "Bookmark");
//            if (list_terms.get(gPosition).mark == 2)
//                submenu.add(GROUP1, MENU_MARK, 2, "Mastered").setChecked(true);
//            else
//                submenu.add(GROUP1, MENU_MARK, 2, "Mastered");
//
//
//            if (list_terms.get(gPosition).mark == 0)
//                MenuItemCompat.setShowAsAction(submenu.getItem().setIcon(R.drawable.mark), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//            else if (list_terms.get(gPosition).mark == 1) {
//                MenuItemCompat.setShowAsAction(submenu.getItem().setIcon(R.drawable.mark_b), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//            } else if (list_terms.get(gPosition).mark == 2) {
//                MenuItemCompat.setShowAsAction(submenu.getItem().setIcon(R.drawable.mark_m), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
//            }
//            submenu.setGroupCheckable(GROUP1, true, true);

            MenuItemCompat.setShowAsAction(menu.add(0, MENU_SHUFFLE, 0, getResources().getString(R.string.shuffle)), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_MARK) {
            MTerms terms = mTermsHandler.getByID(Long.parseLong(list_terms.get(gPosition).getItemID()));
            terms.mark = item.getOrder();
            list_terms.get(gPosition).mark = item.getOrder();
            if (item.getOrder() == 0)
                MyFunc.writeUserLog(context, UActivity.MARK_NONE);
            else if (item.getOrder() == 1)
                MyFunc.writeUserLog(context, UActivity.MARK_BOOKMARK);
            else if (item.getOrder() == 2)
                MyFunc.writeUserLog(context, UActivity.MARK_MASTERED);

            terms.bookmarkTime = (int) (System.currentTimeMillis() / 1000);
            mTermsHandler.update(terms);

            // neu mark=2 thi tang so luong cau mastered


            supportInvalidateOptionsMenu();
        } else if (item.getItemId() == GROUP1) {

        } else if (item.getItemId() == MENU_SHUFFLE) {
            Collections.shuffle(list_terms);
            create_pager();
            supportInvalidateOptionsMenu();
        } else if (item.getItemId() == MENU_SPEAK) {
            // tts(list_terms.get(gPosition).getTerm(), cardset.getLang_terms(), list_terms.get(gPosition).getDefinition(), cardset.getLang_definitions());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                final UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {

                    @Override
                    public void onStart(String utteranceId) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onError(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {

                    }
                };


//                if (currentIsTerm) {
//                    tts.setOnUtteranceProgressListener(mUtteranceProgressListener);
//                    tts(list_terms.get(gPosition).getTerm(), list_terms.get(gPosition).langfrom);
//                } else {
//                    tts.setOnUtteranceProgressListener(mUtteranceProgressListener);
//                    tts(list_terms.get(gPosition).getDefinition(), list_terms.get(gPosition).langto);
//                }
            }
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private HashMap<String, String> map = new HashMap<String, String>();

    public void tts(String word, String locale) {
        if (locale != null) {
            Locale l1 = new Locale(locale);
            int result = tts.setLanguage(l1);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + System.currentTimeMillis());
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, map);
                if (list_terms.get(gPosition).translateID != -1) {
                    Translate translate = mTranslateHandler.getByID(list_terms.get(gPosition).translateID);
                    translate.tts++;
                    mTranslateHandler.update(translate);
                }
            } else {
                Toast.makeText(context, l1.getDisplayLanguage() + " is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        int numMissed = 0;
        int numTotal = 0;
        for (MTerms mTerms : list_terms) {
            if (mTerms.status != 0) {
                numTotal++;
                if (mTerms.status == 2)
                    numMissed++;
            }
        }

        super.onStop();
    }
}