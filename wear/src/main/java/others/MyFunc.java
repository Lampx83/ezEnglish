package others;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import controlvariable.MyGlobal;
import json.MyPojo;
import json.Yandex;

/**
 * Created by xuanlam on 10/29/15.
 */
public class MyFunc {
    Context context;

    public MyFunc(Context context) {
        this.context = context;
    }


    public String translate(String text) throws IOException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String server = "translated";
        String result = null;
        String langfrom=MyGlobal.lang1;

        if (server.equals("yandex")) {
            String trans = MyGlobal.lang1 + "_" + MyGlobal.lang2;
            if (langfrom.equals(MyGlobal.lang2)) {
                trans = MyGlobal.lang2 + "_" + MyGlobal.lang1;
            }
            URL website = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20151028T141023Z.241b0968e802421c.762b90e59c1a641ef5be6625d14f10aaa28780aa&lang=" + trans + "&text=" + URLEncoder.encode(text, "UTF-8").toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection) website.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String content = writer.toString();
            Yandex yandex = new Gson().fromJson(content, Yandex.class);
            result = yandex.getText()[0];
        } else if (server.equals("translated")) {
            String trans = "zh-tw|en";
            if (langfrom.equals(MyGlobal.lang2)) {
                trans = "en|zh-tw";
            }
            URL website = new URL("http://mymemory.translated.net/api/get?q=" + URLEncoder.encode(text, "UTF-8").toString() + "&langpair=" + trans);
            HttpURLConnection urlConnection = (HttpURLConnection) website.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8");
            String content = writer.toString();
            MyPojo myPojo = new Gson().fromJson(content, MyPojo.class);
            result = (myPojo.getMatches()[0]).translation;
        }


        return result;
    }

}
