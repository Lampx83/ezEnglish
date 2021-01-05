package others;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import database.UserLogHandler;
import entity.Stats;
import entity.Translate;
import json.User_Translate;

/**
 * Async task to upload a file to a directory
 */
public class UploadFileTask extends AsyncTask<String, Void, FileMetadata> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;
    private final Callback mCallback;
    private int type;
    private String path;
    private String text;
    private Exception mException;

    public interface Callback {
        void onUploadComplete(FileMetadata result);

        void onError(Exception e);
    }

    public UploadFileTask(Context context, DbxClientV2 dbxClient, String path, int type, String text, Callback callback) {
        mContext = context;
        mDbxClient = dbxClient;
        this.type = type;
        this.path = path;
        this.text = text;
        mCallback = callback;
    }

    @Override
    protected void onPostExecute(FileMetadata result) {
        super.onPostExecute(result);
        if (mException != null) {
            mCallback.onError(mException);
        } else if (result == null) {
            mCallback.onError(null);
        } else {
            mCallback.onUploadComplete(result);
        }
    }

    @Override
    protected FileMetadata doInBackground(String... params) {
        try {
            if (type == 0) { //Translated
                final String filename = "file.txt";
                final File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + filename);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                //translate
                TranslateHandler mTranslateHandler = new TranslateHandler(mContext);
                ArrayList<Translate> list_translate = mTranslateHandler.getAllByforJson("Where " + DataBaseHandler.TRANSLATE_USERID + "='" + MyGlobal.user_id + "'", "");

                Gson gson = new Gson();
                User_Translate user_translate = new User_Translate();
                user_translate.list_translate = list_translate;
                user_translate.user_id = MyGlobal.user_id;
                user_translate.user_name = MyGlobal.user_name;
                String s = gson.toJson(user_translate);
                myOutWriter.append(s);

                myOutWriter.close();
                fOut.close();
                FileInputStream fileInputStream = new FileInputStream(myFile);
                return mDbxClient.files().uploadBuilder(path + MyGlobal.user_id + ".txt").withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);

            } else if (type == 1) {
                final File myFile = new File(path);
                FileInputStream fileInputStream = new FileInputStream(myFile);
                return mDbxClient.files().uploadBuilder("/Images/" + MyGlobal.user_id + "___" + text).withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);
            } else if (type == 2) {
                final File myFile = new File(path);
                FileInputStream fileInputStream = new FileInputStream(myFile);
                return mDbxClient.files().uploadBuilder("/Homework/" + MyGlobal.user_name + "___" + MyGlobal.user_id + text).withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);
            } else if (type == 3) {
                final String filename = "file.txt";
                final File myFile = new File(Environment.getExternalStorageDirectory() + File.separator + filename);
                myFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(myFile);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                //translate
                TranslateHandler mTranslateHandler = new TranslateHandler(mContext);
                TermsHandler mTermsHandler = new TermsHandler(mContext);
                UserLogHandler mUserLogHandler = new UserLogHandler(mContext);
                String where = "1=1";
                Stats stats = new Stats();
                stats.translated = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_TTS + ")", where);
                stats.listen = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_TTS + ")", where);
                stats.practice_times = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_PRACTICE_TIMES + ")", where);
                stats.photo = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_IMAGE + ")", where + " and " + DataBaseHandler.TRANSLATE_USERID + "='" + MyGlobal.user_id + "' and " + DataBaseHandler.TRANSLATE_IMAGE + " is not null");
                stats.note = mTranslateHandler.getNumber("count(" + DataBaseHandler.TRANSLATE_NOTE + ")", where + " and " + DataBaseHandler.TRANSLATE_USERID + "='" + MyGlobal.user_id + "' and " + DataBaseHandler.TRANSLATE_NOTE + " is not null");
                stats.flashcard = mTermsHandler.getNumber("count(*)", where);
                stats.review = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.REVIEW_FLASHCARD);
                stats.trueFalse = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_TRUE_FALSE);
                stats.multichoice = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_MULTICHOICE);
                stats.maching = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_MATCHING);
                stats.written = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_WRITTEN);
                stats.twoplayer = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.PLAY_2_PLAYER);
                stats.en_cn = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.EN_CN_DICT);
                stats.cn_en = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.CN_EN_DICT);
                stats.en_en = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.EN_EN_DICT);
                stats.search_photo = mUserLogHandler.getNumber("count(*)", DataBaseHandler.USERLOG_CODE + "=" + UActivity.IMAGE_SEARCH);

                stats.add = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_ADD + ")", where);
                stats.delete = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_DELETE + ")", where);
                stats.edit = mTranslateHandler.getNumber("sum(" + DataBaseHandler.TRANSLATE_EDIT + ")", where);


                Gson gson = new Gson();
                String s = gson.toJson(stats);
                myOutWriter.append(s);

                myOutWriter.close();
                fOut.close();
                FileInputStream fileInputStream = new FileInputStream(myFile);
                return mDbxClient.files().uploadBuilder(path + MyGlobal.user_id + ".txt").withMode(WriteMode.OVERWRITE).uploadAndFinish(fileInputStream);
            }

        } catch (Exception e) {
            mException = e;
        }

        return null;
    }
}
