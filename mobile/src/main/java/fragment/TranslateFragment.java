package fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dropbox.core.v2.files.FileMetadata;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.gson.Gson;
import com.jquiz.project2.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import activity.HistoryDetailActivity;
import controlvariable.MyGlobal;
import controlvariable.MyPref;
import controlvariable.UActivity;
import database.TermsHandler;
import database.TranslateHandler;
import entity.Translate;
import entity_display.MTerms;
import flashcard.Image;
import json.NL.NLSyntax;
import json.NL.Tokens;
import others.DropboxClientFactory;
import others.ImageLoader;
import others.ImageLoaderDropbox;
import others.MyFunc;
import others.PackageManagerUtils;
import others.PermissionUtils;
import others.StringSimilarity;
import others.UploadFileTask;
import ui.Tag;
import ui.TagView;
import waveview.WaveView;

import static android.app.Activity.RESULT_OK;


public class TranslateFragment extends Fragment {
    public long current_translateid = -1;
    private TagView tagGroup;
    private String langto;
    private String langfrom;

    protected static final String TAG = "debug";
    private SpeechRecognizer sr;
    private TextView tvText;
    private TextView tvTrans;
    private boolean translate_done = false;
    private ImageView btnMicro;
    private WaveView waveView;
    public ProgressBar pb;
    private TextToSpeech tts;
    private int mode = 0; //0 means translate ; 1: means repeat

    private boolean listening = false;
    private int mark = 0;
    private TranslateHandler mTranslateHandler;
    private ToggleButton tbLanguage;
    private TermsHandler mTermsHandler;
    public ImageView img_card;
    private String current_text;
    private String mCurrentPhotoName;
    private String translated_text;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private RelativeLayout llTranslate;
    private ImageView imgKeyboard1;
    private ImageView imgKeyboard2;
    //   private ArrayList<Tag> tags;
    private LinearLayout llMenu;
    public static final int REQUEST_TAKE_PHOTO = 1;
    private static final int GALLERY_PERMISSIONS_REQUEST = 2;
    private static final int GALLERY_IMAGE_REQUEST = 3;
    public static final int CAMERA_PERMISSIONS_REQUEST = 4;
    public static final int CAMERA_IMAGE_REQUEST = 5;
    public static final int RECORD_PRACTICE = 6;

    private Button btnTag;
    private Button btnSpeak;
    private Button btnPractice;
    private Button btnCamera;
    private Button btnNote;
    private Button btnMark;
    private Spinner spLangSyntax;


    private Context context;
    public SharedPreferences preferences;

    private static final int TRANSLATE_MODE = 0;
    private static final int VISION_MODE = 1;
    private static final int SYNTAX_MODE = 2;

    private int appType = VISION_MODE;
    private String tap_on;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        context = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        super.onCreate(savedInstanceState);


        tagGroup = (TagView) rootView.findViewById(R.id.tag_group);
        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, final int position) {
                final EditText input = new AppCompatEditText(context);
                input.setText(tag.text);
                new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.type_text)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = input.getText().toString().trim();
                        if (!text.equals("") && !tagGroup.mTags.get(position).text.equals(text)) {
                            ((TextView) tagGroup.findViewById(1234 * position)).setText(text);
                            tagGroup.mTags.get(position).text = text;
                            updateEditTag();

                            Translate translate = mTranslateHandler.getByID(current_translateid);
                            translate.edit++;
                            mTranslateHandler.update(translate);
                        }
                    }
                }).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp).show();
            }
        });

        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        updateEditTag();
                        Translate translate = mTranslateHandler.getByID(current_translateid);
                        translate.delete++;
                        mTranslateHandler.update(translate);
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();


            }
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, final int position) {
                if (tag.text.toString().trim().endsWith("+")) {
                    final EditText input = new AppCompatEditText(context);
                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.type_text)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String text = input.getText().toString().trim();
                            if (!text.equals("")) {
                                Tag tag = new Tag(text.trim());

                                tag.isDeletable = true;
                                tagGroup.mTags.add(position, tag);
                                tagGroup.drawTags();
                                updateEditTag();
                                Translate translate = mTranslateHandler.getByID(current_translateid);
                                translate.add++;
                                mTranslateHandler.update(translate);
                            }
                        }
                    }).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp).show();
                }

            }
        });


        mTranslateHandler = new TranslateHandler(context);
        mTermsHandler = new TermsHandler(context);

        llMenu = (LinearLayout) rootView.findViewById(R.id.llMenu);

        btnMark = (Button) rootView.findViewById(R.id.btnMark);
        btnTag = (Button) rootView.findViewById(R.id.btnTag);
        btnSpeak = (Button) rootView.findViewById(R.id.btnSpeak);
        btnPractice = (Button) rootView.findViewById(R.id.btnPractice);
        btnCamera = (Button) rootView.findViewById(R.id.btnCamera);
        btnNote = (Button) rootView.findViewById(R.id.btnNote);


        ArrayAdapter adapter_langSyntax = ArrayAdapter.createFromResource(context, R.array.langSyntax, R.layout.spinner_item);
        spLangSyntax = (Spinner) rootView.findViewById(R.id.spLangSyntax);

        spLangSyntax.setAdapter(adapter_langSyntax);

        imgKeyboard1 = (ImageView) rootView.findViewById(R.id.imgKeyboard1);
        imgKeyboard2 = (ImageView) rootView.findViewById(R.id.imgKeyboard2);

        btnTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTag(1);
            }
        });

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = 0;
                if (appType == VISION_MODE) {
                    translated_text = "";
                    for (Tag tag : tagGroup.mTags) {
                        if (!tag.text.endsWith("+"))
                            translated_text = translated_text + tag.text + ", ";
                    }
                }

                tts(translated_text, langto);
            }
        });


        btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (new MyFunc(context).isRecordPermissionGranted(true, RECORD_PRACTICE))
                    if (!listening) {
                        mode = 1;
                        startPractice();
                    }
            }
        });
        img_card = (ImageView) rootView.findViewById(R.id.img_card);


        btnMicro = (ImageView) rootView.findViewById(R.id.btnMicro);
        tvText = (TextView) rootView.findViewById(R.id.tvText);
        tvTrans = (TextView) rootView.findViewById(R.id.tvTrans);
        new MyFunc(context).setToolforTextView(tvTrans);
        new MyFunc(context).setToolforTextView(tvText);

        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        waveView = (WaveView) rootView.findViewById(R.id.waveView);

        llTranslate = ((RelativeLayout) rootView.findViewById(R.id.llTranslate));


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photoFile = MyFunc.createImageFile("img");
                    mCurrentPhotoName = photoFile.getName();
                    // Continue only if the File was successfully created
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                } catch (IOException ex) {
                    ex.printStackTrace();

                }
            }
        });
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current_translateid != -1) {
                    int current_mark = mark;

                    Translate translate = mTranslateHandler.getByID(current_translateid);
                    translate.mark = current_mark;
                    mark = new MyFunc(context).changeMarkTranslate(translate, btnMark, true);
                    mTranslateHandler.update(translate);
                }
            }
        });

        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });


        final UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener() {

            @Override
            public void onStart(String utteranceId) {


            }

            @Override
            public void onError(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                if (preferences.getBoolean(MyPref.pref_practicemode, false) && mode == 1) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getResources().getString(R.string.please_repeat), Toast.LENGTH_SHORT).show();
                        }
                    });
                    startPractice();
                }
            }
        };
        tts.setOnUtteranceProgressListener(mUtteranceProgressListener);
        imgKeyboard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startType();
            }
        });


        View.OnClickListener startMain = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (appType == TRANSLATE_MODE) {
                    startSpeak();
                } else if (appType == VISION_MODE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder
                            .setMessage(R.string.dialog_select_prompt)
                            .setPositiveButton(R.string.dialog_select_gallery, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startGalleryChooser();
                                }
                            })
                            .setNegativeButton(R.string.dialog_select_camera, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startCamera();
                                }
                            });
                    builder.create().show();
                } else if (appType == SYNTAX_MODE) {
                    startType();
                }
            }
        };
        waveView.setOnClickListener(startMain);
        btnMicro.setOnClickListener(startMain);
        tvText.setOnClickListener(startMain);


        imgKeyboard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (translate_done) {
                    MyFunc.writeUserLog(context, UActivity.START_TO_EDIT);
                    final EditText input = new AppCompatEditText(context);
                    input.setText(tvTrans.getText());
                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.edit)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String text = input.getText().toString().trim();
                            translated_text = text;
                            if (!text.equals("")) {
                                tvTrans.setText(text);
                                //Update translate
                                Translate translate = mTranslateHandler.getByID(current_translateid);
                                translate.result = text;
                                mTranslateHandler.update(translate);
                                //update if mark

                                MTerms term = mTermsHandler.getByTranslateID(current_translateid);
                                if (term != null) {
                                    term.setDefinition(text);
                                    mTermsHandler.update(term);
                                }
                            }
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), null).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp).show();
                }
            }
        });

        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyFunc.writeUserLog(context, UActivity.ADD_NOTE);
                final EditText input = new AppCompatEditText(context);
                final Translate mTranslate = mTranslateHandler.getByID(current_translateid);
                input.setText(mTranslate.note);
                new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.type_text)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = input.getText().toString().trim();
                        mTranslate.note = text;
                        mTranslateHandler.update(mTranslate);
                        if (text.equals(""))
                            btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note, 0, 0);
                        else
                            btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note_done, 0, 0);
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), null).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp).show();
            }

        });
        sr = SpeechRecognizer.createSpeechRecognizer(context);
        sr.setRecognitionListener(new listener());

        //Config
        tbLanguage = (ToggleButton) rootView.findViewById(R.id.tbLanguage);
        langfrom = preferences.getString(MyPref.pref_langfrom, MyGlobal.lang1);
        langto = preferences.getString(MyPref.pref_langto, MyGlobal.lang2);

        if (langto.equals(MyGlobal.lang2))
            tbLanguage.setChecked(false);
        else
            tbLanguage.setChecked(true);

        tbLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (tbLanguage.isChecked()) {
                    preferences.edit().putString(MyPref.pref_langfrom, MyGlobal.lang2).commit();
                    preferences.edit().putString(MyPref.pref_langto, MyGlobal.lang1).commit();
                    langfrom = MyGlobal.lang2;
                    langto = MyGlobal.lang1;
                } else {
                    preferences.edit().putString(MyPref.pref_langfrom, MyGlobal.lang1).commit();
                    preferences.edit().putString(MyPref.pref_langto, MyGlobal.lang2).commit();
                    langfrom = MyGlobal.lang1;
                    langto = MyGlobal.lang2;
                }
                preferences.edit().commit();
            }
        });

        if (!MyGlobal.user_name.equals("User_" + MyGlobal.user_id.substring(0, 3)))
            tvTrans.setText(Html.fromHtml(getResources().getString(R.string.hi) + ", " + MyGlobal.user_name + "<br><small>" + getResources().getString(R.string.tap_on_the_microphone_and_speak) + "<small>"));


        if (context instanceof HistoryDetailActivity) {
            imgKeyboard1.setVisibility(View.INVISIBLE);
            imgKeyboard2.setVisibility(View.VISIBLE);
            translate_done = true;
            tbLanguage.setVisibility(View.GONE);
            waveView.setOnClickListener(null);
            Translate mTranslate = mTranslateHandler.getByID(current_translateid);
            tvText.setText(mTranslate.text);
            tvTrans.setText(mTranslate.result);
            btnMicro.setVisibility(View.GONE);
            langto = mTranslate.langto;
            llMenu.setVisibility(View.VISIBLE);
            translated_text = mTranslate.result;
            current_text = mTranslate.text;


            //Mark
            if (mTermsHandler.checkTranslateIdExist(current_translateid))
                mark = 1;
            else
                mark = 0;

            if (mark == 0)
                btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark, 0, 0);
            //Note
            if (mTranslate.note == null || mTranslate.note.equals(""))
                btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note, 0, 0);
            //Image
            if (mTranslate.image == null)
                btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera, 0, 0);
            else {
                preShowImage();
                mCurrentPhotoName = mTranslate.image;
                if (mCurrentPhotoName.startsWith("img")) {
                    Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + mCurrentPhotoName, llTranslate);
                    img_card.setImageBitmap(bitmap);
                } else if (mCurrentPhotoName.startsWith("dropbox")) {
                    pb.setVisibility(View.VISIBLE);
                    new ImageLoaderDropbox(context, R.drawable.img_holder, pb, DropboxClientFactory.getClient()).DisplayImage(mTranslate.user_id + "___" + mTranslate.text + ".jpg", img_card);
                } else if (mCurrentPhotoName.startsWith("content")) {
//                    Uri uri = Uri.parse(mCurrentPhotoName);
//                    Bitmap bitmap = null;
//                    try {
//
//                        bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri), 1200);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    img_card.setImageBitmap(bitmap);
                } else {
                    pb.setVisibility(View.VISIBLE);
                    new ImageLoader(context, R.drawable.img_holder, pb).DisplayImage(mCurrentPhotoName, img_card);
                }
                img_card.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvTrans.setLayoutParams(rParam);
            }


            //Practice
            if (mTranslate.score < 0)
                btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice, 0, 0);
            //Text to speech
            if (mTranslate.tts <= 0)
                btnSpeak.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker, 0, 0);

            if (appType == VISION_MODE) {
                tvTrans.setVisibility(View.GONE);
                String[] arr = translated_text.split(", ");
                tagGroup.mTags = new ArrayList<>();
                for (String s : arr) {
                    if (!s.trim().equals("")) {
                        Tag tag = new Tag(s.trim());
                        tag.isDeletable = true;
                        tagGroup.mTags.add(tag);
                    }
                }
                Tag tag = new Tag("Add new +");
                tag.layoutColor = Color.GRAY;
                tagGroup.mTags.add(tag);
                tagGroup.drawTags();

                tagGroup.drawTags();
            }


        } else {
//            if (!preferences.getBoolean(MyPref.pref_translate, true)) { //Translate or not
//                llTranslate.setVisibility(View.GONE);
//                imgKeyboard1.setVisibility(View.GONE);
//            }
        }

        tap_on = getString(R.string.tap_on_the_microphone_and_speak);

        if (appType == VISION_MODE) {
            img_card = (ImageView) rootView.findViewById(R.id.img_card2);
            tvText.setVisibility(View.GONE);
            btnMicro.setImageResource(R.drawable.ic_camera);
            imgKeyboard1.setVisibility(View.GONE);
            imgKeyboard2.setVisibility(View.GONE);
            tbLanguage.setVisibility(View.GONE);
            btnPractice.setVisibility(View.GONE);
            btnCamera.setVisibility(View.GONE);
            tap_on = getString(R.string.tap_on_the_camera_to_start);
        } else if (appType == SYNTAX_MODE) {
            tbLanguage.setVisibility(View.GONE);
            btnCamera.setVisibility(View.GONE);
            imgKeyboard1.setImageResource(R.drawable.ic_voice);
            imgKeyboard1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSpeak();
                }
            });
            tap_on = getString(R.string.tap_on_the_pen_to_start);
        }
        tvTrans.setText(tap_on);

        return rootView;
    }

    private void startSpeak() {
        if (new MyFunc(context).isRecordPermissionGranted(true, RECORD_PRACTICE)) {
            if (!listening) {
                listening = true;
                MyFunc.writeUserLog(context, UActivity.START_TO_SPEAK);
                startNewTranslate();
                waveView.changeHeightMode(1);
                tvText.setText(getResources().getString(R.string.listening) + "...");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langfrom);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langfrom);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, langfrom);
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
                // startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

                sr.startListening(intent);
            }
            startNewTranslate();
        }
    }

    private void startType() {
        MyFunc.writeUserLog(context, UActivity.START_TO_TYPE);
        startNewTranslate();
        final EditText input = new AppCompatEditText(context);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.type_text)).setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String text = input.getText().toString().trim();
                if (!text.equals("")) {
                    current_text = text;
                    tvText.setText(text);
                    tvTrans.setText("");
                    mode = 0;
                    current_translateid = -1;
                    if (preferences.getBoolean(MyPref.pref_translate, true)) {
                        if (appType == TRANSLATE_MODE)
                            translate(text, 2);
                        else if (appType == SYNTAX_MODE)
                            nl(text, 2);
                    } else
                        noTranslate(text, 2);
                } else {
                    tvTrans.setText(Html.fromHtml(getResources().getString(R.string.hi) + ", " + MyGlobal.user_name + "<br><small>" + tap_on + "<small>"));
                    btnMicro.setVisibility(View.VISIBLE);
                    imgKeyboard1.setVisibility(View.VISIBLE);
                }
            }
        }).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tvTrans.setText(Html.fromHtml(getResources().getString(R.string.hi) + ", " + MyGlobal.user_name + "<br><small>" + tap_on + "<small>"));
                btnMicro.setVisibility(View.VISIBLE);
                imgKeyboard1.setVisibility(View.VISIBLE);
            }
        }).setView(input, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp);


        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                tvTrans.setText(Html.fromHtml(getResources().getString(R.string.hi) + ", " + MyGlobal.user_name + "<br><small>" + getResources().getString(R.string.tap_on_the_microphone_and_speak) + "<small>"));
                btnMicro.setVisibility(View.VISIBLE);
                imgKeyboard1.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();
    }

    public void noTranslate(String spokenText, int device) {
        try {
            new MyFunc(context).addTranslate(spokenText, device, langfrom, langto, mCurrentPhotoName, preferences.getString(MyPref.pref_context, ""), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSpeak.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker, 0, 0);
        btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice, 0, 0);
        btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera, 0, 0);
        btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note, 0, 0);
        btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark, 0, 0);
        llMenu.setVisibility(View.VISIBLE);
        // imgKeyboard2.setVisibility(View.VISIBLE);
        imgKeyboard1.setVisibility(View.VISIBLE);
        translate_done = true;
    }

    public void startNewTranslate() {
        imgKeyboard1.setVisibility(View.INVISIBLE);
        imgKeyboard2.setVisibility(View.INVISIBLE);

        translate_done = false;
        btnTag.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tag, 0, 0);
        btnSpeak.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_speaker, 0, 0);
        btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice, 0, 0);
        btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera, 0, 0);
        btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note, 0, 0);
        mCurrentPhotoName = null;
        RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        current_text = "";
        tvTrans.setLayoutParams(rParam);
        mark = 0;
        if (!preferences.getBoolean(MyPref.pref_autobookmark, MyGlobal.AUTOBOOKMARK))
            btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark, 0, 0);
        else
            btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_done, 0, 0);

        current_translateid = -1;
        tvText.setText("");
        mode = 0;
        tvTrans.setText("");
        llMenu.setVisibility(View.INVISIBLE);
        btnMicro.setVisibility(View.INVISIBLE);

        if (!preferences.getBoolean(MyPref.pref_translate, true)) {
            imgKeyboard1.setVisibility(View.GONE);
        }
    }

    private void startPractice() {
        MyFunc.writeUserLog(context, UActivity.MAIN_PRACTICE);
        imgKeyboard1.setVisibility(View.INVISIBLE);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listening = true;
                waveView.changeHeightMode(1);
                tvText.setText("");
                pb.setVisibility(View.INVISIBLE);
                btnMicro.setVisibility(View.INVISIBLE);
                tvText.setText(getResources().getString(R.string.listening) + "...");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, langto);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langto);
                intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, langto);
                // intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
                sr.startListening(intent);
                //startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

            }
        });

    }

    private HashMap<String, String> map = new HashMap<String, String>();

    public void tts(String word, String locale) {
        if (locale != null) {
            Locale l1 = new Locale(locale);
            int result = tts.setLanguage(l1);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                //    if (!tts.isSpeaking()) {
                btnSpeak.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker_done, 0, 0);
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "" + System.currentTimeMillis());

                tts.speak(word, TextToSpeech.QUEUE_FLUSH, map);
                if (current_translateid != -1) {
                    Translate translate = mTranslateHandler.getByID(current_translateid);
                    translate.tts++;
                    mTranslateHandler.update(translate);
                }
                //   }
            } else {
                // Toast.makeText(context, "TTS error", Toast.LENGTH_SHORT).show();
                if (preferences.getBoolean(MyPref.pref_practicemode, false) && mode == 1) {
                    startPractice();
                }
            }
        }
    }


    @Override
    public void onDestroyView() {
        new UploadFileTask(context, DropboxClientFactory.getClient(), "/Translate/", 0, "", new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {

            }

            @Override
            public void onError(Exception e) {

            }
        }).execute();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        sr.destroy();
        super.onDestroy();
    }

    public void preShowImage() {
        RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvTrans.setLayoutParams(rParam);
        rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rParam.addRule(RelativeLayout.BELOW, tvTrans.getId());
        rParam.addRule(RelativeLayout.ABOVE, llMenu.getId());
        img_card.setLayoutParams(rParam);
        img_card.setVisibility(View.VISIBLE);
    }

    public void showImage(Bitmap bitmap) {
        preShowImage();
        img_card.setImageBitmap(bitmap);
        //save Bitmap to sd
        try {
            File storageDir = new File(MyGlobal.image_folder);
            File image = File.createTempFile("img" + (System.currentTimeMillis() / 1000L), ".jpg", storageDir);
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            out.close();

            updateImagetoDB(image.getName());

            //Upload image
            new UploadFileTask(context, DropboxClientFactory.getClient(), image.getAbsolutePath(), 1, current_text, new UploadFileTask.Callback() {
                @Override
                public void onUploadComplete(FileMetadata result) {

                }

                @Override
                public void onError(Exception e) {

                }
            }).execute();


            btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera_done, 0, 0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateImagetoDB(String path) {
        if (mark == 1) {
            //insert Flashcard
            MTerms mTerms = mTermsHandler.getByTranslateID(current_translateid);
            Image img = new Image();
            img.setUrl(path);
            mTerms.setImage(img);
            mTermsHandler.update(mTerms);
        }
        Translate translate = mTranslateHandler.getByID(current_translateid);
        File file = new File(MyGlobal.image_folder + translate.image);
        if (file.exists()) {
            file.delete();
        }
        translate.image = path;
        mTranslateHandler.update(translate);
    }


    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            listening = true;
            Log.d(TAG, "onReadyForSpeech");
            waveView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBeginningOfSpeech() {
            MyFunc.writeUserLog(context, UActivity.BEGIN_SPEECH);
            listening = true;
            Log.d(TAG, "onBeginningOfSpeech");
            waveView.setWaveHz(waveView.LARGE);

        }

        public void onRmsChanged(float rmsdB) {
            // Log.d(TAG, "onRmsChanged: " + rmsdB);
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            MyFunc.writeUserLog(context, UActivity.END_OF_SPEECH);
            imgKeyboard1.setVisibility(View.VISIBLE);
            Log.d(TAG, "onEndofSpeech");
            waveView.setWaveHz(waveView.LITTLE);
            // waveView.setVisibility(View.INVISIBLE);
            waveView.changeHeightMode(0);

//            if (mode == 0)
//                tvText.setText(getString(R.string.recognizing) + "...");

            listening = false;
        }

        public void onError(int error) {
            Log.d(TAG, "onError " + error);

            if (listening == false) {
                MyFunc.writeUserLog(context, UActivity.SPPECH_ERROR + error);
                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {

                    waveView.setWaveHz(waveView.LITTLE);
                    waveView.changeHeightMode(0);

                    if (mode == 0) {
                        tvText.setText(getResources().getString(R.string.please_try_speaking_again));
                        llMenu.setVisibility(View.INVISIBLE);
                    } else {
                        tvText.setText(getResources().getString(R.string.wrong) + "!");
                    }
                    tvTrans.setText("");
                    mode = 0;
                } else if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    if (tvText.getText().toString().equals(getString(R.string.recognizing) + "...")) {
                        tvText.setText(getResources().getString(R.string.no_matches_found));
                        imgKeyboard1.setVisibility(View.VISIBLE);
                    } else if (tvText.getText().toString().equals(getString(R.string.listening) + "...")) {
                        tvText.setText(getResources().getString(R.string.no_matches_found));
                        imgKeyboard1.setVisibility(View.VISIBLE);
                    }

                } else if (error == SpeechRecognizer.ERROR_SERVER || error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) {

                    waveView.setWaveHz(waveView.LITTLE);
                    waveView.changeHeightMode(0);

                    if (mode == 0) {
                        tvText.setText(getResources().getString(R.string.please_try_speaking_again));
                        llMenu.setVisibility(View.INVISIBLE);
                    } else {
                        tvText.setText(getResources().getString(R.string.wrong) + "!");
                    }
                    tvTrans.setText("");
                    mode = 0;
                }

                listening = false;
            }

        }

        public void onResults(Bundle results) {
            MyFunc.writeUserLog(context, UActivity.RESULT_SPEECH);

            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//          ArrayList conf = results.getStringArrayList(SpeechRecognizer.CONFIDENCE_SCORES);

            final String spokenText = data.get(0).toString().trim();

            if (mode == 0) { //Speak to Translate
                current_text = spokenText;
                if (langfrom.equals(MyGlobal.lang2)) {
                    int score = (int) (results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[0] * 100);
                    tvText.setText(spokenText + " (" + getResources().getString(R.string.score) + " = " + score + "/100)");

                } else
                    tvText.setText(spokenText);
                tvTrans.setText("");

                if (appType == TRANSLATE_MODE) {

                    if (preferences.getBoolean(MyPref.pref_translate, true)) { //Translate or not
                        translate(spokenText, 1);
                    } else {
                        //MyFunc.writeUserLog(context, UActivity.PRACTICE_SPEECH);
                        noTranslate(spokenText, 1);
                        if (langfrom.equals(MyGlobal.lang2)) {
                            String similar = "";
                            for (int i = 1; i < data.size(); i++) {
                                Log.d(TAG, "result " + data.get(i));
                                similar += "<br>~ " + data.get(i);
                            }
                            tvText.setText(Html.fromHtml(tvText.getText() + "<small><small><font color='#FFDCE7'>" + similar + "</font></small></small>"));
                        }
                    }
                } else if (appType == SYNTAX_MODE)
                    nl(spokenText, 2);
            } else { //Speak to Practice
                if (spokenText.toLowerCase().equals(translated_text.toLowerCase())) { //Noi dung
                    int score = (int) (results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)[0] * 100);
                    tvText.setText(spokenText + " (" + getResources().getString(R.string.correct) + ", " + getResources().getString(R.string.score) + " = " + score + "/100)");
                    Translate translate = mTranslateHandler.getByID(current_translateid);
                    if (score > translate.score) {
                        translate.score = score;
                        translate.practice_times++;
                        mTranslateHandler.update(translate);
                    }
                    btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice_done, 0, 0);
                    //  tvTrans.setText(Html.fromHtml(translated_text + "<br><small>(Tap here to practice again)</small>"));
                } else {
                    String comment = "Good";
                    if (translated_text.length() > 10) {
                        int score = new StringSimilarity().similarity(spokenText, translated_text);
                        if (score > 90)
                            comment = "Good";
                        else if (score > 70)
                            comment = "Average";
                        else if (score > 50)
                            comment = "Below Average";
                        else
                            comment = "Failing";
                        tvText.setText(spokenText + " (" + comment + ", " + getResources().getString(R.string.score) + " = " + score + "/100)");
                    } else
                        tvText.setText(spokenText + " (" + getResources().getString(R.string.wrong) + ") ");


                    Translate translate = mTranslateHandler.getByID(current_translateid);
                    if (translate.score < 0) { //Update
                        translate.score = -3;
                        translate.practice_times++;
                        mTranslateHandler.update(translate);
                    }
                    // tvTrans.setText(Html.fromHtml(translated_text + "<br><small>(Tap here to practice again)</small>"));
                }
            }
        }

        public void onPartialResults(Bundle results) {
//            if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
//                List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                Log.d(TAG, TextUtils.join(" · ", heard));
//                tvText.setText(TextUtils.join(" · ", heard));
//            }
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }

    private void translate(final String spokenText, final int device) {
        imgKeyboard1.setVisibility(View.INVISIBLE);
        MyFunc.writeUserLog(context, UActivity.TRANSLATE);
        pb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    translated_text = new MyFunc(context).addTranslate(spokenText, device, langfrom, langto, mCurrentPhotoName, preferences.getString(MyPref.pref_context, ""), true);
                    if (spokenText.equals(current_text))
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                                tvTrans.setText(Html.fromHtml(translated_text));
                                translate_done = true;
                                imgKeyboard1.setVisibility(View.VISIBLE);
                                imgKeyboard2.setVisibility(View.VISIBLE);
                                if (preferences.getBoolean(MyPref.pref_autoread, true)) { //Text to speech
                                    MyFunc.writeUserLog(context, UActivity.MAIN_TTS);
                                    mode = 1;
                                    tts(translated_text, langto);
                                } else {
                                    if (preferences.getBoolean(MyPref.pref_practicemode, false)) {
                                        startPractice();
                                    }
                                }
                                if (preferences.getBoolean(MyPref.pref_autobookmark, MyGlobal.AUTOBOOKMARK)) {
                                    mark = 0;
                                    Translate translate = mTranslateHandler.getByID(current_translateid);
                                    translate.mark = mark;
                                    mark = new MyFunc(context).changeMarkTranslate(translate, btnMark, false);
                                }
                                llMenu.setVisibility(View.VISIBLE);
                            }
                        });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    ArrayList<Tokens> tokens;

    private void nl(final String spokenText, final int device) {
        translated_text = spokenText;
        tvText.setText(spokenText);
        imgKeyboard1.setVisibility(View.INVISIBLE);
        MyFunc.writeUserLog(context, UActivity.TRANSLATE);
        pb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    langfrom = new MyFunc(context).getLangCodeSyntax(spLangSyntax.getSelectedItemPosition());
                    String json = new MyFunc(context).addNL(spokenText, 4, langfrom, langto, mCurrentPhotoName, preferences.getString(MyPref.pref_context, ""), true);
                    NLSyntax nlSyntax = new Gson().fromJson(json, NLSyntax.class);
                    tokens = nlSyntax.tokens;
                    showTag(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showTag(final int type) {
        tagGroup.mTags.clear();
        Tag tag;
        for (Tokens t : tokens) {
            if (type == 0)
                tag = new Tag("<small><small>     </small></small><br>" + t.text.content + "<br><small><small>     </small></small>");
            else
                tag = new Tag("<small><small>" + t.dependencyEdge.label.toLowerCase() + "</small></small><br>" + t.text.content + "<br><small><small>" + t.partOfSpeech.tag.toLowerCase() + "</small></small>");

            tag.isDeletable = false;
            tag.tokens = t;

            if (type == 1 && t.dependencyEdge.label.toLowerCase().equals("root"))
                tag.layoutColor = Color.RED;
            tagGroup.mTags.add(tag);
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tagGroup.drawTags();
                btnMicro.setVisibility(View.INVISIBLE);
                imgKeyboard1.setVisibility(View.VISIBLE);
                llMenu.setVisibility(View.VISIBLE);
                btnMicro.setVisibility(View.INVISIBLE);
                btnMicro.setVisibility(View.GONE);
                tvTrans.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);
                if (type == 1)
                    btnTag.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tag_done, 0, 0);

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(context, result.get(0), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                MyFunc.writeUserLog(context, UActivity.ADD_IMAGE_DONE);
                Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + mCurrentPhotoName, llTranslate);
                btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera_done, 0, 0);
                img_card.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvTrans.setLayoutParams(rParam);
                img_card.setImageBitmap(bitmap);


                updateImagetoDB(mCurrentPhotoName);

                //Upload image to Dropbox

                File file = new File(Environment.getExternalStorageDirectory().toString(), "temp.jpg");
                try {
                    OutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    new UploadFileTask(context, DropboxClientFactory.getClient(), file.getAbsolutePath(), 1, current_text, new UploadFileTask.Callback() {
                        @Override
                        public void onUploadComplete(FileMetadata result) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }).execute();

                } catch (IOException e) {
                    Log.d(TAG, "ERROR");
                }
            } else if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
                uploadImage(data.getData());
                //  mCurrentPhotoName = data.getData().toString();
            } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK) {
                //  Uri photoUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", getCameraFile());

                Uri photoUri = Uri.fromFile(new File(MyGlobal.image_folder + mCurrentPhotoName));
                uploadImage(photoUri);
            }
        }

    }


    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                Bitmap bitmap = scaleBitmapDown(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri), 1200);
                //
                saveBitmap(MyGlobal.image_folder + mCurrentPhotoName, bitmap);


                new UploadFileTask(context, DropboxClientFactory.getClient(), MyGlobal.image_folder + mCurrentPhotoName, 1, mCurrentPhotoName, new UploadFileTask.Callback() {
                    @Override
                    public void onUploadComplete(FileMetadata result) {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                }).execute();


                callCloudVision(bitmap);
                img_card.setImageBitmap(bitmap);


            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(context, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(context, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }


    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //tvTrans.setVisibility(View.VISIBLE);
        //tvTrans.setText(R.string.loading_message);

        pb.setVisibility(View.VISIBLE);

        tagGroup.mTags = new ArrayList<>();
        tagGroup.drawTags();

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer =
                            new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                                /**
                                 * We override this so we can inject important identifying fields into the HTTP
                                 * headers. This enables use of a restricted cloud platform API key.
                                 */
                                @Override
                                protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                                        throws IOException {
                                    super.initializeVisionRequest(visionRequest);

                                    String packageName = context.getPackageName();
                                    visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                                    String sig = PackageManagerUtils.getSignature(context.getPackageManager(), packageName);

                                    visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                                }
                            };

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("LABEL_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {

                tagGroup.mTags.clear();
                Tag tag;
                String[] arr = result.split(",");
                String translated_text = "";
                for (String s : arr) {
                    tag = new Tag(s);
                    tag.isDeletable = true;
                    tagGroup.mTags.add(tag);
                    translated_text = translated_text + s + ", ";
                }

                tag = new Tag("Add new +");
                tag.layoutColor = Color.GRAY;
                tagGroup.mTags.add(tag);
                tagGroup.drawTags();


                btnMicro.setVisibility(View.INVISIBLE);
                img_card.setImageBitmap(bitmap);
                llMenu.setVisibility(View.VISIBLE);
                btnMicro.setVisibility(View.INVISIBLE);
                btnMicro.setVisibility(View.GONE);
                tvTrans.setVisibility(View.INVISIBLE);
                pb.setVisibility(View.INVISIBLE);

                //Dua vao DB
                try {
                    new MyFunc(context).addCloudVision("Photo", 3, langfrom, langto, mCurrentPhotoName, preferences.getString(MyPref.pref_context, ""), translated_text);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.execute();
    }


    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static final String CLOUD_VISION_API_KEY = "AIzaSyAfhjYAgUUyubTbza7bxBEtgmhbHudzeXE";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";


    public void startGalleryChooser() {

        startNewTranslate();
        if (PermissionUtils.requestPermission(getActivity(), GALLERY_PERMISSIONS_REQUEST, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            File photoFile = null;
            try {
                photoFile = MyFunc.createImageFile("img");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCurrentPhotoName = photoFile.getName();

            startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST);
        }
    }


    public void startCamera() {
        startNewTranslate();
        if (PermissionUtils.requestPermission(
                getActivity(),
                CAMERA_PERMISSIONS_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //Uri photoUri = FileProvider.getUriForFile(getActivity(), context.getPackageName() + ".provider", getCameraFile());
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            File photoFile = null;
            try {
                photoFile = MyFunc.createImageFile("img");
                mCurrentPhotoName = photoFile.getName();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Continue only if the File was successfully created


            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
        }
    }

    public File getCameraFile() {
        File dir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                // message += String.format(Locale.US, "%s (%.3f),",label.getDescription(),label.getScore());
                message += label.getDescription() + ",";
            }
        } else {
            message += "nothing,";
        }


        return message.substring(0, message.length() - 3);
    }


    private void saveBitmap(String filename, Bitmap bmp) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEditTag() {

        String translated_text = "";
        for (Tag tag : tagGroup.mTags) {
            if (!tag.text.endsWith("+"))
                translated_text = translated_text + tag.text + ", ";
        }

        Translate translate = mTranslateHandler.getByID(current_translateid);
        translate.result = translated_text;
        mTranslateHandler.update(translate);
        //update if mark

        MTerms term = mTermsHandler.getByTranslateID(current_translateid);
        if (term != null) {
            term.setDefinition(translated_text);
            mTermsHandler.update(term);
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA_PERMISSIONS_REQUEST:
//                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSIONS_REQUEST, grantResults)) {
//                    startCamera();
//                }
//                break;
//            case GALLERY_PERMISSIONS_REQUEST:
//                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSIONS_REQUEST, grantResults)) {
//                    startGalleryChooser();
//                }
//                break;
//            case RECORD_PRACTICE:
//                if (PermissionUtils.permissionGranted(requestCode, RECORD_PRACTICE, grantResults)) {
//                    if (!listening) {
//                        mode = 1;
//                        startPractice();
//                    }
//                }
//                break;
//        }
//    }


}
