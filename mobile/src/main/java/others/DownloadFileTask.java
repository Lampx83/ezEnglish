package others;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.dropbox.core.v2.DbxClientV2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Task to download a file from Dropbox and put it in the Downloads folder
 */
public class DownloadFileTask extends AsyncTask<String, Void, File> {

    private final Context mContext;
    private final DbxClientV2 mDbxClient;


    public DownloadFileTask(Context context, DbxClientV2 dbxClient) {
        mContext = context;
        mDbxClient = dbxClient;

    }

    @Override
    protected void onPostExecute(File result) {
        super.onPostExecute(result);
    }

    @Override
    protected File doInBackground(String... params) {
        String fileName = params[0];
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, fileName);

            // Make sure the Downloads directory exists.


            // Download the file.
            OutputStream outputStream = new FileOutputStream(file);
            mDbxClient.files().download("/" + fileName).download(outputStream);


            // Tell android about the file
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            mContext.sendBroadcast(intent);

            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
