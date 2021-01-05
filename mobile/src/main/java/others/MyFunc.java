package others;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jquiz.project2.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import activity.MainActivity;
import controlvariable.MyGlobal;
import controlvariable.MyPref;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import database.UserLogHandler;
import entity.Translate;
import entity.UserLog;
import entity_display.MTerms;
import flashcard.Image;
import json.MyPojo;
import json.Yandex;

import static com.jquiz.project2.R.drawable.mark;


public class MyFunc {
    Context context;

    public MyFunc(Context context) {
        this.context = context;
    }

    public int convertPixelsToDp(float px) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return (int) dp;
    }

    public String addTranslate(String text, int device, String langfrom, String langto, String image, String learn_context, Boolean autoTranslate) throws IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String server = preferences.getString(MyPref.pref_server, "translated");
        if (!autoTranslate)
            server = "m";

        Translate translate = new Translate();
        translate.image = image;
        translate.langfrom = langfrom;
        translate.langto = langto;
        translate.text = text;
        translate.device = device;
        translate.server = server;
        translate.time = System.currentTimeMillis() / 1000L;
        translate.date = MyFunc.getDateTimeFormat().format(new Date());
        translate.learn_context = learn_context;
        translate.user_id = MyGlobal.user_id;
        translate.user_name = MyGlobal.user_name;
        if (!preferences.getBoolean(MyPref.pref_practicemode, false)) {
            translate.score = -2;
        }

        SharedPreferences mState = context.getSharedPreferences("state", 0);
        translate.step = mState.getInt("steps", 0);
        translate.stepon = mState.getInt("stepson", 0);
        if (autoTranslate) {
            if (server.equals("yandex")) {
                String trans = MyGlobal.lang1 + "_" + MyGlobal.lang2;
                if (langfrom.equals(MyGlobal.lang2)) {
                    trans = MyGlobal.lang2 + "_" + MyGlobal.lang1;
                }
                URL website = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20151028T141023Z.241b0968e802421c.762b90e59c1a641ef5be6625d14f10aaa28780aa&lang=" + trans + "&text=" + URLEncoder.encode(text, "UTF-8").toString());
                HttpsURLConnection urlConnection = (HttpsURLConnection) website.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StringWriter writer = new StringWriter();
//                IOUtils.copy(in, writer, "UTF-8");
                String content = writer.toString();
                Yandex yandex = new Gson().fromJson(content, Yandex.class);
                translate.result = yandex.getText()[0];
            } else if (server.equals("translated")) {
                String trans = "zh-tw|en";
                if (langfrom.equals(MyGlobal.lang2)) {
                    trans = "en|zh-tw";
                }
                URL website = new URL("http://mymemory.translated.net/api/get?q=" + URLEncoder.encode(text, "UTF-8").toString() + "&langpair=" + trans);
                HttpURLConnection urlConnection = (HttpURLConnection) website.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StringWriter writer = new StringWriter();
//                IOUtils.copy(in, writer, "UTF-8");
                String content = writer.toString();
                MyPojo myPojo = new Gson().fromJson(content, MyPojo.class);
                translate.result = (myPojo.getMatches()[0]).translation;
            }
        } else {
            translate.result = "";
        }
        TranslateHandler mTranslateHandler = new TranslateHandler(context);
        long translateID = mTranslateHandler.insert(translate);
        if (context instanceof MainActivity) {
            ((MainActivity) context).mTranslateFragment.current_translateid = translateID;
        }

        return translate.result;
    }

    public String addCloudVision(String text, int device, String langfrom, String langto, String image, String learn_context, String tagCloud) throws IOException {

        Translate translate = new Translate();
        translate.result = tagCloud;
        translate.image = image;
        translate.langfrom = langfrom;
        translate.langto = langto;
        translate.text = image;
        translate.device = device;
        translate.server = "Google";
        translate.time = System.currentTimeMillis() / 1000L;
        translate.date = MyFunc.getDateTimeFormat().format(new Date());
        translate.learn_context = learn_context;
        translate.user_id = MyGlobal.user_id;
        translate.user_name = MyGlobal.user_name;
        translate.score = -2;

        SharedPreferences mState = context.getSharedPreferences("state", 0);
        translate.step = mState.getInt("steps", 0);
        translate.stepon = mState.getInt("stepson", 0);

        TranslateHandler mTranslateHandler = new TranslateHandler(context);
        long translateID = mTranslateHandler.insert(translate);
        if (context instanceof MainActivity) {
            ((MainActivity) context).mTranslateFragment.current_translateid = translateID;
        }

        return translate.result;
    }


    public String addNL(String text, int device, String langfrom, String langto, String image, String learn_context, Boolean autoTranslate) throws IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String server = preferences.getString(MyPref.pref_server, "translated");
        if (!autoTranslate)
            server = "m";

        Translate translate = new Translate();
        translate.image = image;
        translate.langfrom = langfrom;
        translate.langto = langto;
        translate.text = text;
        translate.device = device;
        translate.server = server;
        translate.time = System.currentTimeMillis() / 1000L;
        translate.date = MyFunc.getDateTimeFormat().format(new Date());
        translate.learn_context = learn_context;
        translate.user_id = MyGlobal.user_id;
        translate.user_name = MyGlobal.user_name;
        if (!preferences.getBoolean(MyPref.pref_practicemode, false)) {
            translate.score = -2;
        }

        SharedPreferences mState = context.getSharedPreferences("state", 0);
        translate.step = mState.getInt("steps", 0);
        translate.stepon = mState.getInt("stepson", 0);
        if (autoTranslate) {
            HttpClient hc = new DefaultHttpClient();
            HttpPost p = new HttpPost("https://language.googleapis.com/v1/documents:analyzeSyntax?key=AIzaSyB4ZClhhGeRTo3aHokc-XhYj6UxGyD5a2c");
            JSONObject body = new JSONObject();
            JSONObject document = new JSONObject();
            try {
                document.put("type", "PLAIN_TEXT");
                document.put("language", langfrom);
                document.put("content", text);
                body.put("document", document);
                p.setEntity(new StringEntity(body.toString(), "UTF8"));
                p.setHeader("content-type", "application/json");
                org.apache.http.HttpResponse resp = hc.execute(p);
                if (resp != null) {
                    String json = EntityUtils.toString(resp.getEntity());
//                    String json = "{\n" +
//                            "    \"sentences\": [\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"I Love HaNoi.\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            }\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"HaNoi is the capital of Vietnam\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            }\n" +
//                            "        }\n" +
//                            "    ],\n" +
//                            "    \"tokens\": [\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"I\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"PRON\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"NOMINATIVE\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"FIRST\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 1,\n" +
//                            "                \"label\": \"NSUBJ\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"I\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"Love\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"VERB\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"INDICATIVE\",\n" +
//                            "                \"number\": \"NUMBER_UNKNOWN\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"PRESENT\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 1,\n" +
//                            "                \"label\": \"ROOT\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"Love\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"HaNoi\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"NOUN\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 1,\n" +
//                            "                \"label\": \"DOBJ\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"HaNoi\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \".\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"PUNCT\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"NUMBER_UNKNOWN\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 1,\n" +
//                            "                \"label\": \"P\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \".\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"HaNoi\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"NOUN\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 5,\n" +
//                            "                \"label\": \"NSUBJ\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"HaNoi\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"is\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"VERB\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"INDICATIVE\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"THIRD\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"PRESENT\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 5,\n" +
//                            "                \"label\": \"ROOT\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"be\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"the\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"DET\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"NUMBER_UNKNOWN\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 7,\n" +
//                            "                \"label\": \"DET\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"the\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"capital\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"NOUN\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 5,\n" +
//                            "                \"label\": \"ATTR\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"capital\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"of\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"ADP\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"NUMBER_UNKNOWN\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER_UNKNOWN\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 7,\n" +
//                            "                \"label\": \"PREP\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"of\"\n" +
//                            "        },\n" +
//                            "        {\n" +
//                            "            \"text\": {\n" +
//                            "                \"content\": \"Vietnam\",\n" +
//                            "                \"beginOffset\": -1\n" +
//                            "            },\n" +
//                            "            \"partOfSpeech\": {\n" +
//                            "                \"tag\": \"NOUN\",\n" +
//                            "                \"aspect\": \"ASPECT_UNKNOWN\",\n" +
//                            "                \"case\": \"CASE_UNKNOWN\",\n" +
//                            "                \"form\": \"FORM_UNKNOWN\",\n" +
//                            "                \"gender\": \"GENDER_UNKNOWN\",\n" +
//                            "                \"mood\": \"MOOD_UNKNOWN\",\n" +
//                            "                \"number\": \"SINGULAR\",\n" +
//                            "                \"person\": \"PERSON_UNKNOWN\",\n" +
//                            "                \"proper\": \"PROPER\",\n" +
//                            "                \"reciprocity\": \"RECIPROCITY_UNKNOWN\",\n" +
//                            "                \"tense\": \"TENSE_UNKNOWN\",\n" +
//                            "                \"voice\": \"VOICE_UNKNOWN\"\n" +
//                            "            },\n" +
//                            "            \"dependencyEdge\": {\n" +
//                            "                \"headTokenIndex\": 8,\n" +
//                            "                \"label\": \"POBJ\"\n" +
//                            "            },\n" +
//                            "            \"lemma\": \"Vietnam\"\n" +
//                            "        }\n" +
//                            "    ],\n" +
//                            "    \"language\": \"en\"\n" +
//                            "}";


                    translate.result = json;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            translate.result = "";
        }
        TranslateHandler mTranslateHandler = new TranslateHandler(context);
        long translateID = mTranslateHandler.insert(translate);
        if (context instanceof MainActivity) {
            ((MainActivity) context).mTranslateFragment.current_translateid = translateID;
        }
        return translate.result;
    }


    public boolean isStoragePermissionGranted(boolean ask) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else if (ask) {

                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            return false;
        } else {
            return true;
        }
    }


    public boolean isRecordPermissionGranted(boolean ask, int requestCode) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else if (ask) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
            return false;
        } else {
            return true;
        }
    }


    public static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy/MM/dd");
    }

    public static SimpleDateFormat getDateFormatFolder() {
        return new SimpleDateFormat("yyyy_MM_dd");
    }

    public static SimpleDateFormat getShortDateFormat() {
        return new SimpleDateFormat("MMM d");
    }


    public void answer(final boolean correct, final String itemID, int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // play sound
                if (correct) {
                    playsound(R.raw.correct);
                } else {
                    playsound(R.raw.wrong);
                }

                if (correct)
                    MyFunc.writeUserLog(context, UActivity.CHOOSE_KNOW_FLASHCARD);
                else
                    MyFunc.writeUserLog(context, UActivity.CHOOSE_UNKNOW_FLASHCARD);

                TermsHandler mTermsHandler = new TermsHandler(context);
                MTerms mTerms = mTermsHandler.getByID(Long.parseLong(itemID));
                mTerms.box = changebox(correct, mTerms.box);
                mTermsHandler.update(mTerms);

            }
        }).start();

    }

    private int changebox(boolean correct, int box) {
        if (correct) { // CORRECT
            if (box == -1) { // Not yet -> Never
                box = 3;
            } else if (box == 0) { // Often -> Sometimes
                box = 1;
            } else if (box == 1) { // Sometimes -> Seldom
                box = 2;
            }
        } else { // WRONG
            if (box == -1) { // Not yet -> Often
                box = 0;
            } else if (box == 1) { // Sometimes -> Often
                box = 0;
            } else if (box == 2) { // Seldom -> Often
                box = 0;
            } else if (box == 3) { // Never -> Sometimes
                box = 1;
            }
        }
        return box;
    }

    public void playsound(final int f) {
        MediaPlayer mp = MediaPlayer.create(context, f);
        mp.start();
    }

    public Animation blinkAnimation() {
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
        fadeIn.setDuration(100);
        fadeIn.setRepeatCount(3);
        return fadeIn;
    }


    public int changeMarkTranslate(final Translate translate, final View btnMark, boolean manualMark) {
        final TermsHandler mTermsHandler = new TermsHandler(context);
        TranslateHandler mTranslateHandler = new TranslateHandler(context);
        //check and add to study list
        //check and remove from study list
        final int[] return_mark = {0};


        String extra = " (" + translate.result + ")";
        if (translate.device == 4)
            extra = "";

        if (mark == 0) {
            if (!mTermsHandler.checkTermExist(translate.text)) {
                addFlashcard(translate.image, mark, translate.translateid, translate.text, translate.result, translate.langfrom, translate.langto);
                if (manualMark)
                    Toast.makeText(context, translate.text + extra + " is added to your flashcard set", Toast.LENGTH_SHORT).show();
                return_mark[0] = 1;
                MyFunc.writeUserLog(context, UActivity.MARK_BOOKMARK);
                if (btnMark instanceof Button)
                    ((Button) btnMark).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_done, 0, 0);
                else if (btnMark instanceof ImageView)
                    ((ImageView) btnMark).setImageResource(R.drawable.mark_done);
            } else {
                if (!manualMark) {
                    return_mark[0] = 0;
                    if (btnMark instanceof Button)
                        ((Button) btnMark).setCompoundDrawablesWithIntrinsicBounds(0, mark, 0, 0);
                    else if (btnMark instanceof ImageView)
                        ((ImageView) btnMark).setImageResource(mark);
                    Toast.makeText(context, translate.text + extra + " aleardy exists in your flashcard set", Toast.LENGTH_SHORT).show();
                } else
                    new AlertDialog.Builder(context)
                            .setTitle("Add to flashcard set")
                            .setMessage(translate.text + " (" + translate.result + ") was added, replace it?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mTermsHandler.deleteBy(DataBaseHandler.CARD_TERM + "=?", new String[]{translate.text});
                                    addFlashcard(translate.image, mark, translate.translateid, translate.text, translate.result, translate.langfrom, translate.langto);
                                    //Toast.makeText(context, context.getString(R.string.done), Toast.LENGTH_LONG).show();
                                    return_mark[0] = 1;
                                    MyFunc.writeUserLog(context, UActivity.MARK_BOOKMARK);
                                    if (btnMark instanceof Button)
                                        ((Button) btnMark).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_done, 0, 0);
                                    else if (btnMark instanceof ImageView)
                                        ((ImageView) btnMark).setImageResource(R.drawable.mark_done);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Button) btnMark).setCompoundDrawablesWithIntrinsicBounds(0, mark, 0, 0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
            }
        } else {
            MyFunc.writeUserLog(context, UActivity.MARK_NONE);
            //Xoa khoi
            Toast.makeText(context, translate.text + extra + " is removed from your flashcard set", Toast.LENGTH_SHORT).show();
            mTermsHandler.deleteBy(DataBaseHandler.TRANSLATEID + "=?", new String[]{"" + translate.translateid});
            if (btnMark instanceof Button)
                ((Button) btnMark).setCompoundDrawablesWithIntrinsicBounds(0, mark, 0, 0);
            else if (btnMark instanceof ImageView)
                ((ImageView) btnMark).setImageResource(mark);
        }

        return return_mark[0];
    }


    public void addFlashcard(String image, int mark, long current_translateid, String text, String translated_text, String langfrom, String langto) {
        TermsHandler mTermsHandler = new TermsHandler(context);
        MTerms mTerms = new MTerms();
        mTerms.setDefinition(translated_text);
        mTerms.setItemID("" + System.currentTimeMillis() / 1000L);
        mTerms.setTerm(text);
        mTerms.mark = 1;
        mTerms.bookmarkTime = System.currentTimeMillis() / 1000L;
        mTerms.box = -1;
        Image mImage = new Image();
        mImage.setUrl(image);
        mTerms.setImage(mImage);
        mTerms.langfrom = langfrom;
        mTerms.langto = langto;
        mTerms.translateID = current_translateid;
        mTermsHandler.add(mTerms);

    }

    public static void writeUserLog(Context context, int code, String id) {
        UserLog userLog = new UserLog();
        userLog.code = code;
        userLog.id = id;
        new UserLogHandler(context).insert(userLog);
    }

    public static void writeUserLog(Context context, int code) {
        UserLog userLog = new UserLog();
        userLog.code = code;
        if (code == 1)
            userLog.id = context.getClass().getSimpleName().substring(0, 3);
        else
            userLog.id = "";
        new UserLogHandler(context).insert(userLog);
    }

//    public static void writeUserLog(Context context, int code) {
//        // 2392749234923$1:DoquizActivity#
//
//        String log = "";
//        if (code == 1) // Start Activity
//            log = code + ":" + System.currentTimeMillis() / 1000L + "$" + context.getClass().getSimpleName().substring(0, 3) + "#";
//        else
//            log = code + ":" + System.currentTimeMillis() / 1000L + "#";
//        // Toast.makeText(context, log, Toast.LENGTH_SHORT).show();
//
//        // Save to memory
//        ContextWrapper cw = new ContextWrapper(context);
//        final File directory = cw.getDir("logDir", Context.MODE_PRIVATE);
//        File file = new File(directory, MyGlobal.user_id + "_log");
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file, true);
//            if (!file.exists()) {
//                file.createNewFile();
//                log = MyGlobal.user_id + "#" + log;
//            }
//            // get the content in bytes
//            byte[] contentInBytes = log.getBytes();
//
//            fos.write(contentInBytes);
//            fos.flush();
//            fos.close();
//
//            fos.close();
//        } catch (Exception ex) {
//
//        }
//        return;
//    }
//
//    public static String readUserLog(Context context) {
//        StringBuilder stringBuilder = new StringBuilder();
//        String line;
//        BufferedReader in = null;
//        try {
//            ContextWrapper cw = new ContextWrapper(context);
//            final File directory = cw.getDir("logDir", Context.MODE_PRIVATE);
//            File file = new File(directory, MyGlobal.user_id + "_log");
//            in = new BufferedReader(new FileReader(file));
//            while ((line = in.readLine()) != null)
//                stringBuilder.append(line);
//        } catch (Exception e) {
//        }
//        return stringBuilder.toString();
//    }


    public static Bitmap decodePic(String mCurrentPhotoPath, ViewGroup vg) {
        // Get the dimensions of the View
        int targetW = vg.getWidth();
        int targetH = vg.getHeight();
        if (targetH == 0)
            targetH = 500;
        if (targetW == 0)
            targetW = 500;
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public static File createImageFile(String imageFileName) throws IOException {
        File storageDir = new File(MyGlobal.image_folder);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        image.getName();

        return image;
    }


    public static int getDistance(int mStepValue) {
        return (int) (mStepValue / 4.68321f);
    }

    public static float getCalo(int mStepValue) {

        return mStepValue * 0.0044351f;
    }


    public void setToolforTextView(TextView textview) {
        if (textview != null) {
            textview.setTextIsSelectable(true);
            textview.setCustomSelectionActionModeCallback(new TextViewActionModeAPI11(context, textview));
        }
    }


    public String getLangCodeSyntax(int position) {
        ArrayList<String> list = new ArrayList();
        list.add("en");
        list.add("fr");
        list.add("de");
        list.add("it");
        list.add("ja");
        list.add("ko");
        list.add("pt");
        list.add("es");
        list.add("es");
        list.add("zh");
        list.add(" zh-Hant");
        return list.get(position);
    }
}
