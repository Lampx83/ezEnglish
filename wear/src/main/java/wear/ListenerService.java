package wear;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import activity.MainActivity1;
import activity.MyApplication;
import controlvariable.MyPref;

/**
 * Created by xuanlam on 9/23/15.
 */
public class ListenerService extends WearableListenerService {
    Context context;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
      //  Toast.makeText(getApplicationContext(), "onMessageReceived", Toast.LENGTH_LONG).show();
        if (messageEvent.getPath().equals("/data") || messageEvent.getPath().equals("/data_show")) {
            final String message = new String(messageEvent.getData());

            context = getApplicationContext();
            try {
                //Luu Message vào preference
                if (!message.equals("") && !message.equals("null") && !message.equals("update")) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    preferences.edit().putString(MyPref.pref_message, message).commit();
                }
                Intent intent = null;
                if (MyApplication.context != null) {
                    context = MyApplication.context;
                    if (context instanceof MainActivity1) {
                        if (((MainActivity1) context).inApp) {
                            //Toast.makeText(context, "Dang o trong MainActivity", Toast.LENGTH_SHORT).show();
                            ((MainActivity1) context).updateView(message);
                        } else {
                            //Toast.makeText(context, "Dang o trong MainActivity nhung chay background", Toast.LENGTH_SHORT).show();
                            if (messageEvent.getPath().equals("/data_show")) {
                                intent = new Intent(context, MainActivity1.class);
                                context.startActivity(intent);
                                ((MainActivity1) context).finish();
                            }
                        }
                    }
                } else {
                    if (messageEvent.getPath().equals("/data_show")) {
                        //Toast.makeText(context, "Dang o ngoai app", Toast.LENGTH_SHORT).show();
                        intent = new Intent(context, MainActivity1.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                //Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    GoogleApiClient mGoogleApiClient;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        context = getApplicationContext();
        if (MyApplication.context != null)
            context = MyApplication.context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("imageAssert");
                String filename = dataMapItem.getDataMap().getString("filename");
                Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                SaveImage(bitmap, filename);
                // Do something with the bitmap
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result = mGoogleApiClient.blockingConnect(3, TimeUnit.SECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();
        if (assetInputStream == null) {
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }


    private void SaveImage(Bitmap finalBitmap, String fname) {

        File myDir = new File(context.getFilesDir() + "/saved_images");
        myDir.mkdirs();
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}