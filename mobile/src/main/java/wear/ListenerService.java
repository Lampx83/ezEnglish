//package wear;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.wearable.Asset;
//import com.google.android.gms.wearable.MessageApi;
//import com.google.android.gms.wearable.MessageEvent;
//import com.google.android.gms.wearable.Node;
//import com.google.android.gms.wearable.NodeApi;
//import com.google.android.gms.wearable.Wearable;
//import com.google.android.gms.wearable.WearableListenerService;
//
//import java.io.ByteArrayOutputStream;
//
//import others.MyFunc;
//
////import android.widget.Toast;
//
///**
// * Created by xuanlam on 9/23/15.
// */
//public class ListenerService extends WearableListenerService {
//    Context context;
//
//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//
////        if (MyGlobal.end_name == null) {
////            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
////            new MainActivity().restore_static(getApplicationContext(), false);
////        }
//        //Toast.makeText(getApplicationContext(), "onMessageReceived", Toast.LENGTH_SHORT).show();
//        if (messageEvent.getPath().equals("/message_path")) {
//            final String message = new String(messageEvent.getData());
//            context = getApplicationContext();
//            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//
//            try {
//                String language_from = "zh_TW";
//                String language_to = "en";
//                sentCardsToWear(new MyFunc(getApplicationContext()).addTranslate(message, 0, language_from, language_to, null,"All",true));
//
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
//                sentCardsToWear("Error");
//            }
//
//
////            MLearningfeed mLearningfeed = new Gson().fromJson(message, MLearningfeed.class);
////            if (mLearningfeed.type < 0) {//Xin lay cards
////                if (mLearningfeed.type == -1 || mLearningfeed.type == -2) { //-1 la vao app lay card, -2 la resync
////
////                    LearningFeedFragment x = new LearningFeedFragment();
////                    x.buildDbHandle(context);
////                    MyGlobal.cards = x.buildList(context);
////                    sentCardsToWear("/data", getApplicationContext(), MyGlobal.cards, true);
////
////                } else if (mLearningfeed.type == -3) { //Lay 10 random cards
////                    LearningFeedFragment x = new LearningFeedFragment();
////                    x.buildDbHandle(context);
////                    sentCardsToWear("/data", getApplicationContext(), x.buildList10Random(context), true);
////                }
////            } else { // Open Cards and Update Cards
////                MyGlobal.cards = null;
////                if (mLearningfeed.dutru0) { //Chi update khong scroll
////                    updateItem(mLearningfeed);
////                } else {  //Open cards
////                    Intent intent;
////                    if (MyApplication.context != null) {
////                        intent = new Intent(context, PACardActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        if (MyApplication.context instanceof PACardActivity) {
////                            if (((PACardActivity) MyApplication.context).mLearningFeedFragment != null)
////                                if (((PACardActivity) MyApplication.context).inApp) {
////                                    // Toast.makeText(context, "dang o trong cards page", Toast.LENGTH_SHORT).show();
////                                    ((PACardActivity) MyApplication.context).mLearningFeedFragment.scrollTo(mLearningfeed, true);
////                                } else {
////                                    //Toast.makeText(context, "dang o trong cards page, but app is in background", Toast.LENGTH_SHORT).show();
////                                    intent.putExtra(MyPref.extra_card, message);
////                                    MyApplication.context.startActivity(intent);
////                                    ((PACardActivity) MyApplication.context).finish();
////                                }
////                        } else {
////                            intent.putExtra(MyPref.extra_card, message);
////                            MyApplication.context.startActivity(intent);
////                        }
////                    } else {
////                        intent = new Intent(context, MainActivity.class); //dang o ngoai app
////                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                        intent.putExtra(MyPref.extra_goto, PACardActivity.class.getSimpleName());
////                        intent.putExtra(MyPref.extra_card, message);
////                        context.startActivity(intent);
////
////                    }
////                }
////            }
//
//        } else {
//            super.onMessageReceived(messageEvent);
//        }
//    }
//
//    //
////    //ANDROID WEAR
//    GoogleApiClient googleClient;
//
//    //
//    public void sentCardsToWear(final String message) {
//
//
//        googleClient = new GoogleApiClient.Builder(context)
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle bundle) {
//                        new SendToDataLayerThread(message).start();
//
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//
//                    }
//                })
//
//                .build();
//        googleClient.connect();
//
//    }
//
//    //
////
//    public class SendToDataLayerThread extends Thread {
//        String path = "/data_show";
//        String message;
//
//        public SendToDataLayerThread(String message) {
//            this.message = message;
//        }
//
//        public void run() {
//            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
//
//            for (Node node : nodes.getNodes()) {
//                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
//                if (result.getStatus().isSuccess()) {
//                } else {
//                }
//            }
//            //Gui Image
//
////            for (Iterator<MLearningfeed> it = cards.iterator(); it.hasNext(); ) {
////                MLearningfeed mLearningfeed = it.next();
////                if (mLearningfeed.type == 4 && mLearningfeed.image != null && mLearningfeed.image.getUrl() != null) {
////                    try {
////                        InputStream in = new java.net.URL(mLearningfeed.image.getUrl()).openStream();
////                        Bitmap bitmap = BitmapFactory.decodeStream(in);
////                        Asset asset = createAssetFromBitmap(bitmap);
////                        PutDataMapRequest dataMap = PutDataMapRequest.create("/image");
////                        dataMap.getDataMap().putAsset("imageAssert", asset);
////                        dataMap.getDataMap().putString("filename", mLearningfeed.termID + ".jpg");
////                        //dataMap.getDataMap().putString("time", String.valueOf(System.currentTimeMillis()));
////                        PutDataRequest request = dataMap.asPutDataRequest();
////                        Wearable.DataApi.putDataItem(googleClient, request);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
//
////            for (Node node : nodes.getNodes()) {
////                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, "update".getBytes()).await();
////            }
//            googleClient.disconnect();
//        }
//    }
//
//
//    private static Asset createAssetFromBitmap(Bitmap bitmap) {
//        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
//        return Asset.createFromBytes(byteStream.toByteArray());
//    }
//
//
//    static String convertStreamToString(java.io.InputStream is) {
//        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
//        return s.hasNext() ? s.next() : "";
//    }
//
//}