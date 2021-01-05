package others;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;

import controlvariable.MyGlobal;
import database.TranslateHandler;
import entity.Translate;
import json.User_Translate;

/**
 * Async task to list items in a folder
 */
public class ListFolderTask extends AsyncTask<String, Void, ListFolderResult> {

    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private Exception mException;
    Context context;

    public interface Callback {
        void onDataLoaded(ListFolderResult result);

        void onError(Exception e);
    }

    public ListFolderTask(Context context, DbxClientV2 dbxClient, Callback callback) {
        mDbxClient = dbxClient;
        mCallback = callback;
        this.context = context;
    }

    @Override
    protected void onPostExecute(ListFolderResult result) {
        super.onPostExecute(result);

        if (mException != null) {
            mCallback.onError(mException);
        } else {
            mCallback.onDataLoaded(result);
        }
    }

    @Override
    protected ListFolderResult doInBackground(String... params) {
        try {
            ListFolderResult list = mDbxClient.files().listFolder(params[0]);
            TranslateHandler mTranslateHandler = new TranslateHandler(context);

            for (Metadata metadata : list.getEntries()) {
                if (metadata.getName().endsWith(".txt") && !metadata.getName().equals(MyGlobal.user_id + ".txt")) {
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
                    User_Translate user_translate = new Gson().fromJson(text.toString(), User_Translate.class);
                    for (Translate translate : user_translate.list_translate) {
                       // if (translate.mark == 1) {
                            translate.user_id = user_translate.user_id;
                            translate.user_name = user_translate.user_name;
                            translate.tts = 0;
                            translate.practice_times = 0;
                            translate.score = -1;
                            translate.mark = 0;
                            mTranslateHandler.insert(translate);
                      //  }
                    }
                    br.close();
                }
            }

            return list;
        } catch (Exception e) {
            mException = e;
        }

        return null;
    }
}
