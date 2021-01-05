package others;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import entity.Stats;
import entity.Translate;
import json.User_Translate;

/**
 * Async task for getting user account info
 */
public class DataAnalyzeTask extends AsyncTask<Void, String, Void> {
    private final Context context;
    private final DbxClientV2 mDbxClient;

    Callback mCallback;
    private Exception mException;

    public DataAnalyzeTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        this.context = context;
        mDbxClient = dbxClient;
        mCallback = callback;
    }

    public interface Callback {
        void onComplete(Void result);

        void onError(Exception e, String s);
    }

    ProgressDialog progressDialog;

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait!");
        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void mVoid) {
        super.onPostExecute(mVoid);
        progressDialog.dismiss();
        if (mException != null) {
            mCallback.onError(mException,s);
        } else {
            mCallback.onComplete(mVoid);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressDialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }
    String s="";
    @Override
    protected Void doInBackground(Void... params) {

        try {

            //DOWNLOAD
            ListFolderResult list = mDbxClient.files().listFolder("/Translate");
            StringBuilder full = new StringBuilder();
            StringBuilder stat = new StringBuilder();

            int i = 0;
            int total = list.getEntries().size();
            full.append("userid,username,textfrom,textto,langfrom,langto,note,delete,add,edit,tts,practice_times,highest_score,unixtime,time,mark,lat,long,device\r\n");
            stat.append("userid,username,translated,listen,practice_times,photo,note,delete,add,edit,flashcard,review,trueFalse,multichoice,maching,written,twoplayer,en_cn,cn_en,en_en,search_photo,steps,steps_on\r\n");



            for (Metadata metadata : list.getEntries()) {
                publishProgress(i + "/" + total);

                i++;
                if (metadata.getName().endsWith(".txt")) {
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(path, metadata.getName());

                    OutputStream outputStream = new FileOutputStream(file);
                    mDbxClient.files().download(metadata.getPathLower()).download(outputStream);
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder text = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    s= metadata.getName();
                    if (metadata.getName().endsWith("a6db75c212e2000f.txt")) {
                        int x=3;
                        x=x+2;
                    }
                    User_Translate user_translate = new Gson().fromJson(text.toString(), User_Translate.class);
                    for (Translate translate : user_translate.list_translate) {
                        full.append(user_translate.user_id + ",");
                        full.append(user_translate.user_name + ",");
                        full.append("\""+translate.text + "\",");
                        full.append("\""+translate.result + "\",");
                        full.append(translate.langfrom + ",");
                        full.append(translate.note   + ",");
                        full.append(translate.delete + ",");
                        full.append(translate.add + ",");
                        full.append(translate.edit + ",");
                        full.append(translate.langto + ",");
                        full.append(translate.tts + ",");
                        full.append(translate.practice_times + ",");
                        full.append(translate.score + ",");
                        full.append(translate.time + ",");
                        String strDate = MyFunc.getDateTimeFormat().format(new java.util.Date((long)translate.time*1000));
                        full.append(strDate+ ",");
                        full.append(translate.mark + ",");
                        full.append(translate.latitude + ",");
                        full.append(translate.longitude + ",");
                        full.append(translate.device);

                        full.append("\r\n");
                    }
                    br.close();
                    String statFile=metadata.getPathLower().replace("translate", "Statistics");
                    //Stats



                    file = new File(path, "temp.txt");

                    outputStream = new FileOutputStream(file);
                    mDbxClient.files().download(statFile).download(outputStream);
                    br = new BufferedReader(new FileReader(file));

                    text = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }


                    Stats stats = new Gson().fromJson(text.toString(), Stats.class);
                    stat.append(user_translate.user_id + ",");
                    stat.append(user_translate.user_name + ",");
                    stat.append(user_translate.list_translate.size() + ",");
                    stat.append(stats.listen + ",");
                    stat.append(stats.practice_times + ",");
                    stat.append(stats.photo + ",");
                    stat.append(stats.note + ",");
                    stat.append(stats.delete + ",");
                    stat.append(stats.add + ",");
                    stat.append(stats.edit + ",");
                    stat.append(stats.flashcard + ",");
                    stat.append(stats.review + ",");
                    stat.append(stats.trueFalse + ",");
                    stat.append(stats.multichoice + ",");
                    stat.append(stats.maching + ",");
                    stat.append(stats.written + ",");
                    stat.append(stats.twoplayer + ",");
                    stat.append(stats.en_cn + ",");
                    stat.append(stats.cn_en + ",");
                    stat.append(stats.en_en + ",");
                    stat.append(stats.en_en + ",");
                    stat.append(stats.steps_on + ",");
                    stat.append(stats.steps);
                    stat.append("\r\n");
                    br.close();

                }
            }
            //UPLOAD DATA FULL
            final String filename = "file.txt";
            final File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + filename);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(full.toString());
            myOutWriter.close();
            fOut.close();
            FileInputStream fileInputStream = new FileInputStream(myFile);
            mDbxClient.files().uploadBuilder("/Data_Full.csv").withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);


            myFile.createNewFile();
            fOut = new FileOutputStream(myFile);
            myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(stat.toString());
            myOutWriter.close();
            fOut.close();
            fileInputStream = new FileInputStream(myFile);
            mDbxClient.files().uploadBuilder("/Data_Summary.csv").withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);

        } catch (Exception e) {
            mException = e;


        }
        return null;
    }
}
