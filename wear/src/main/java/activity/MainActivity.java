package activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.wearable.view.CircularButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.jquiz.project2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import others.MyFunc;

public class MainActivity extends ParentActivity {
    private SpeechRecognizer sr;
    private Sensor accelerometer;
    private Sensor magnetometer;
    // TextView tvText;
    TextView tvTrans;
    final String TAG = "debug";
    //ImageView imgIcon;
    //WaveView wave_view;
    ProgressBar pb;
    Context context;
    SensorManager sm = null;
    LinearLayout rlWave;
    ImageButton btnMic;
    CircularButton btnSpeak;
    CircularButton btnPractice;

    LinearLayout llSpeak;
    LinearLayout llPractice;

    SensorEventListener sel = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = event.values;
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientationData[] = new float[3];
                    SensorManager.getOrientation(R, orientationData);
                    //int azimuth = (int) (20 * orientationData[0]);
                    int pitch = (int) (20 * orientationData[1]);
                    // int roll = (int) (20 * orientationData[2]);
                    if (isInResult) {
                        float[] values = event.values;
                        if (pitch > 0) {
                            invert(false);
                        } else {
                            invert(true);
                        }
                    }
                    // Log.d(TAG, "azimuth = " + azimuth + "pitch = " + pitch + "roll = " + roll);
                }
            }
        }

    };
    float[] mGravity = null;
    float[] mGeomagnetic = null;
    boolean isFront = true;
    boolean isInResult = false;

    public void invert(boolean isFront) {

        // if (this.isFront != isFront) {
        this.isFront = isFront;
        if (isFront) {
            tvTrans.setRotation(0);
            rlWave.setVisibility(View.VISIBLE);
            tvTrans.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        } else {
            tvTrans.setRotation(180);
            tvTrans.setTextAppearance(context, android.R.style.TextAppearance_Large);
            rlWave.setVisibility(View.GONE);
        }
        //  }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAmbientEnabled();
        context = this;
        setContentView(R.layout.translate_layout);
        // imgIcon = (ImageView) findViewById(R.id.imgIcon);
        rlWave = (LinearLayout) findViewById(R.id.rlWave);
        // tvText = (TextView) findViewById(R.id.tvText);
        tvTrans = (TextView) findViewById(R.id.tvTrans);
        pb = (ProgressBar) findViewById(R.id.pb);
        btnMic = (ImageButton) findViewById(R.id.btnMic);
        btnSpeak = (CircularButton) findViewById(R.id.btnSpeak);
        btnPractice = (CircularButton) findViewById(R.id.btnPractice);

        llSpeak = (LinearLayout) findViewById(R.id.llSpeak);
        llPractice = (LinearLayout) findViewById(R.id.llPractice);

        //  wave_view = (WaveView) findViewById(R.id.wave_view);
//        wave_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
//                } else {
//                   startListen();
//                }
//            }
//        });

        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                } else {
                    startListen();
                }
            }
        });
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts(gTranslate, "en");
            }
        });

        btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPractice();
            }
        });

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

                /* Get a SensorManager instance */
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(sel, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(sel, magnetometer, SensorManager.SENSOR_DELAY_UI);

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
    }

    void startListen() {
        isPractice = false;
        if (listening == false) {
            tvTrans.setText("");
            gTranslate = "";
            pb.setVisibility(View.INVISIBLE);
            llPractice.setVisibility(View.GONE);
            llSpeak.setVisibility(View.GONE);
            //imgIcon.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            String language = "zh_TW";
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            // intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            //intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "straight talk please");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            // sr.startListening(intent);
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            isInResult = false;
            listening = true;
        }
    }

    void startPractice() {
        isPractice = true;
        if (listening == false) {

            tvTrans.setText("");
            pb.setVisibility(View.INVISIBLE);
            //imgIcon.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            String language = "en";
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: " + gTranslate);

            // intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            //sr.startListening(intent);
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            isInResult = false;
            listening = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startListen();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    boolean listening = false;
    boolean isPractice = false;

    @Override
    protected void onDestroy() {
        sr.destroy();
        super.onDestroy();
    }

    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
            //  wave_view.changeHeightMode(1);
            tvTrans.setText("Listening...");
        }

        @Override
        public void onBeginningOfSpeech() {
            //  wave_view.changeHeightMode(1);
            Log.d(TAG, "onBeginningOfSpeech");
            tvTrans.setText("Listening...");
            //  wave_view.setWaveHz(WaveView.LARGE);

        }

        public void onRmsChanged(float rmsdB) {
            // Log.d(TAG, "onRmsChanged: " + rmsdB);
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");

        }

        public void onEndOfSpeech() {
            listening = false;
            Log.d(TAG, "onEndofSpeech");
            //wave_view.setWaveHz(WaveView.LITTLE);
            // wave_view.setVisibility(View.INVISIBLE);
            // wave_view.changeHeightMode(0);
            tvTrans.setText("");

        }

        public void onError(int error) {
            if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                tvTrans.setText("Please try speaking again!");
                //  wave_view.setWaveHz(WaveView.LITTLE);
                // wave_view.changeHeightMode(0);
                listening = false;
            } else if (error == SpeechRecognizer.ERROR_SERVER) {
                tvTrans.setText("Error from server");
                // wave_view.setWaveHz(WaveView.LITTLE);
                // wave_view.changeHeightMode(0);
                listening = false;
            } else {
                tvTrans.setText("Error: " + error);
            }
        }

        public void onResults(Bundle results) {
            listening = false;
            // wave_view.changeHeightMode(0);
            tvTrans.setText("");
            isInResult = true;
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            int score = (int) (results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[0] * 100);
            for (int i = 0; i < data.size(); i++) {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            final String spokenText = data.get(0).toString().trim();
            // tvText.setText(spokenText);
            tvTrans.setText("");
            pb.setVisibility(View.VISIBLE);
            senttoPhone(spokenText);
            // usewifi(spokenText);


        }


        public void onPartialResults(Bundle results) {
            if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
                List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                //tvText.setText(TextUtils.join(" · ", heard));
            }
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

    public void usewifi(final String spokenText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String message = new MyFunc(context).translate(spokenText);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTrans.setText(message);
                            tts(message, "en");
                            pb.setVisibility(View.INVISIBLE);
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String gTranslate = "";

    public void updateView(final String translate) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llPractice.setVisibility(View.VISIBLE);
                llSpeak.setVisibility(View.VISIBLE);
                gTranslate = translate;
                tvTrans.setText(translate);
                tts(translate, "en");
                pb.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void senttoPhone(final String message) {//mLearningfeed chi la de dieu khien
        googleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new SendToDataLayerThread("/message_path", message).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        //        tvText.setText("Connection error!");
                    }
                })
                .build();
        googleClient.connect();
    }

    public class SendToDataLayerThread extends Thread {
        String path;
        String message;

        // Constructor to send a message to the data layer
        public SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleClient).await();
            if (nodes.getNodes().size() == 0) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //tvText.setText("Connection error!");
                    }
                });
            } else {
                for (Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleClient, node.getId(), path, message.getBytes()).await();
                    if (result.getStatus().isSuccess()) {

                    } else {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //   tvText.setText("Connection error!");
                            }
                        });
                    }
                }
            }
            if (null != googleClient && googleClient.isConnected()) {
                googleClient.disconnect();
            }
        }
    }

    GoogleApiClient googleClient;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                listening = false;

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    final String spokenText = result.get(0).toString().trim();
                    if (isPractice) {
                        if (spokenText.trim().toLowerCase().equals(gTranslate.trim().toLowerCase())) {
                            //int score = (int) (data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)[0] * 100);
                            tvTrans.setText(spokenText + " (Correct)");
                        } else {
                            tvTrans.setText(Html.fromHtml(spokenText + " (Wrong, say <b>" + gTranslate + "</b> again)"));
                        }
                    } else {
                        tvTrans.setText("");
                        isInResult = true;
                        pb.setVisibility(View.VISIBLE);
                        senttoPhone(spokenText);
                    }
                }
                break;
            }

        }
    }

//    class RetrieveFeedTask extends AsyncTask<String, Void, String> {
//
//        private Exception exception;
//
//        protected String doInBackground(String... urls) {
//            try {
//                String message = "你好你好你好";
//                String language = "zh-cn|en";
//                URL website = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20151028T141023Z.241b0968e802421c.762b90e59c1a641ef5be6625d14f10aaa28780aa&lang=zh-en&text=%E8%AC%9D%E8%AC%9D%E4%BD%A0%EF%BC%8C%E6%98%A8%E5%A4%A9%E6%88%91%E4%BE%86%E5%88%B0%E4%BD%A0%E7%9A%84%E5%AE%B6");
//
//                HttpsURLConnection urlConnection = (HttpsURLConnection) website.openConnection();
//                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                StringWriter writer = new StringWriter();
//                // String content = convertStreamToString(in);
//
//
//                IOUtils.copy(in, writer, "UTF-8");
//
//                String content = writer.toString();
//                MyPojo myPojo = new Gson().fromJson(content, MyPojo.class);
//
//                return myPojo.getResponseData().getTranslatedText();
//
//
//            } catch (Exception e) {
//                this.exception = e;
//                return "error";
//            }
//
//        }
//
//        protected void onPostExecute(String feed) {
//            Toast.makeText(getApplicationContext(), feed, Toast.LENGTH_SHORT).show();
//        }
//    }


    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }


    private final int REQ_CODE_SPEECH_INPUT = 100;

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
    }


    public boolean inApp = false;

    @Override
    protected void onPause() {
        inApp = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        inApp = true;
        super.onResume();
    }

    private TextToSpeech tts;
    private HashMap<String, String> map = new HashMap<String, String>();

    public void tts(String word, String locale) {
        if (locale != null) {
            Locale l1 = new Locale(locale);
            int result = tts.setLanguage(l1);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + System.currentTimeMillis());
                tts.speak(word, TextToSpeech.QUEUE_FLUSH, map);

            }
        }
    }
}
