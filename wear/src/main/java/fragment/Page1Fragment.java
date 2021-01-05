
package fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.wearable.view.CircularButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Page1Fragment extends Fragment {


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.translate_layout, container, false);
        context = getActivity();

        // imgIcon = (ImageView) findViewById(R.id.imgIcon);
        rlWave = (LinearLayout) rootView.findViewById(R.id.rlWave);
        // tvText = (TextView) findViewById(R.id.tvText);
        tvTrans = (TextView) rootView.findViewById(R.id.tvTrans);
        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        btnMic = (ImageButton) rootView.findViewById(R.id.btnMic);
        btnSpeak = (CircularButton) rootView.findViewById(R.id.btnSpeak);
        btnPractice = (CircularButton) rootView.findViewById(R.id.btnPractice);

        llSpeak = (LinearLayout) rootView.findViewById(R.id.llSpeak);
        llPractice = (LinearLayout) rootView.findViewById(R.id.llPractice);


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


                /* Get a SensorManager instance */
        sm = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        sm.registerListener(sel, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(sel, magnetometer, SensorManager.SENSOR_DELAY_UI);

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
        return rootView;

    }

    void startListen() {
        isPractice = false;
        if (listening == false) {
            tvTrans.setText("");
            gTranslate = "";
            gText = "";
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startListen();
                }
                return;
            }
        }
    }

    boolean listening = false;
    boolean isPractice = false;


    public String gTranslate = "";

    public void updateView(final String translate) {
        if (!gText.equals("") && !translate.equals("")) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    llPractice.setVisibility(View.VISIBLE);
                    llSpeak.setVisibility(View.VISIBLE);
                    gTranslate = translate;
                    tvTrans.setText(Html.fromHtml(gText + "<br>" + translate));
                    tts(translate, "en");
                    pb.setVisibility(View.INVISIBLE);

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    String history = preferences.getString("history1", "");
                    if (history.length() > 200) {
                        history = history.substring(0, 198);
                    }
                    history = "<br>" + gText + ": " + translate + history;
                    preferences.edit().putString("history1", history).commit();

                }
            });
        }

    }

    String gText = "";

    public void senttoPhone(final String message) {//mLearningfeed chi la de dieu khien
        googleClient = new GoogleApiClient.Builder(context)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                listening = false;

                if (resultCode == ((Activity) context).RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    final String spokenText = result.get(0).toString().trim();
                    if (isPractice) {
                        if (spokenText.trim().toLowerCase().equals(gTranslate.trim().toLowerCase())) {
                            //int score = (int) (data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)[0] * 100);
                            //int score = (int) (data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)[0] * 100);
                            tvTrans.setText(spokenText + " (Correct)");
                        } else {
                            tvTrans.setText(Html.fromHtml(spokenText + " (Wrong, say <b>" + gTranslate + "</b> again)"));
                        }
                    } else {
                        tvTrans.setText("");
                        isInResult = true;
                        pb.setVisibility(View.VISIBLE);
                        gText = spokenText;
                        senttoPhone(spokenText);
                    }
                }
                break;
            }

        }
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
            Toast.makeText(context, "Sorry! Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
        }
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
