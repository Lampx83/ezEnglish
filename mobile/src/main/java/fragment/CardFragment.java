package fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.dropbox.core.v2.files.FileMetadata;
import com.jquiz.project2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import activity.ViewCardActivity;
import controlvariable.MyGlobal;
import controlvariable.UActivity;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import entity.Translate;
import entity_display.MTerms;
import flashcard.Image;
import others.DropboxClientFactory;
import others.ImageLoader;
import others.ImageLoaderDropbox;
import others.MyFunc;
import others.UploadFileTask;
import pacard.AnimationFactory;
import pacard.AnimationFactory.FlipDirection;

public class CardFragment extends Fragment {
    public static final String ARG_PAGE = "page";
    private static final int REQ_CODE_SPEECH_INPUT = 0;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private int mPageNumber;
    private TextView tvDef;
    private TextView tvTerm;
    private Context context;
    private ImageView img_card;
    private ProgressBar pb;
    private Button btnWrong;
    private Button btnCorrect;
    private ViewAnimator viewAnimator;
    private TextView tvResult;
    private ImageView btnFlip1;
    private ImageView btnFlip2;
    private RelativeLayout rlDef;
    private MTerms mTerms;
    private TermsHandler mTermsHandler;
    private TranslateHandler mTranslateHandler;
    private LinearLayout llMenu;
    private Button btnSpeak;
    private Button btnPractice;
    private Button btnCamera;
    private Button btnNote;
    private Button btnMark;


    public static CardFragment create(int pageNumber) { // 0 show term //1 show def //2 show both
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);

    }

    int i = 0;
    OnClickListener flipListen = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AnimationFactory.flipTransition(viewAnimator, FlipDirection.LEFT_RIGHT);
            //  updateSpeaker();
        }
    };
    private String mCurrentPhotoName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        mTranslateHandler = new TranslateHandler(context);
        mTerms = ((ViewCardActivity) context).list_terms.get(mPageNumber);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_card, container, false);
        btnFlip1 = (ImageView) rootView.findViewById(R.id.btnFlip1);
        rlDef = (RelativeLayout) rootView.findViewById(R.id.rlDef);
        btnFlip2 = (ImageView) rootView.findViewById(R.id.btnFlip2);
        viewAnimator = (ViewAnimator) rootView.findViewById(R.id.viewFlipper);
        tvResult = (TextView) rootView.findViewById(R.id.tvResult);
        mTermsHandler = new TermsHandler(context);
        llMenu = (LinearLayout) rootView.findViewById(R.id.llMenu);
        tvResult.setTextSize(2f * MyGlobal.font_size * tvResult.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        if (MyGlobal.showTermFirst) {
            i = 1;
            viewAnimator.setDisplayedChild(1);
        }
        viewAnimator.setOnClickListener(flipListen);
        btnFlip1.setOnClickListener(flipListen);
        btnFlip2.setOnClickListener(flipListen);

        btnMark = (Button) rootView.findViewById(R.id.btnMark);
        btnSpeak = (Button) rootView.findViewById(R.id.btnSpeak);
        btnPractice = (Button) rootView.findViewById(R.id.btnPractice);
        btnCamera = (Button) rootView.findViewById(R.id.btnCamera);
        btnNote = (Button) rootView.findViewById(R.id.btnNote);
        Translate mTranslate = mTranslateHandler.getByID(mTerms.translateID);
        if (mTranslate.tts <= 0)
            btnSpeak.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.speaker, 0, 0);
        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewCardActivity) context).tts(mTerms.getDefinition(), mTerms.langto);
            }
        });
        if (mTranslate.score < 0)
            btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice, 0, 0);
        btnPractice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPractice();
            }
        });
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
        if (mTranslate.note == null || mTranslate.note.equals(""))
            btnNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.note, 0, 0);
        btnNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyFunc.writeUserLog(context, UActivity.ADD_NOTE);
                final EditText input = new AppCompatEditText(context);
                final Translate mTranslate = mTranslateHandler.getByID(mTerms.translateID);
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
        if (mTerms.mark == 1)
            btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_done, 0, 0);
        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTerms.mark == 0) {
                    MyFunc.writeUserLog(context, UActivity.MARK_BOOKMARK);
                    mTerms.mark = 1;
                    btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark_done, 0, 0);
                } else {
                    mTerms.mark = 0;
                    btnMark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.mark, 0, 0);
                    MyFunc.writeUserLog(context, UActivity.MARK_NONE);
                    mTermsHandler.deleteBy(DataBaseHandler.CARD_ID + "=?", new String[]{mTerms.getItemID()});
                }
            }
        });

        // Terms
        tvTerm = (TextView) rootView.findViewById(R.id.tvTerm);
        img_card = (ImageView) rootView.findViewById(R.id.img_card);
        pb = (ProgressBar) rootView.findViewById(R.id.pb);

        btnWrong = (Button) rootView.findViewById(R.id.btnWrong);
        btnCorrect = (Button) rootView.findViewById(R.id.btnCorrect);
        btnWrong.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                choose(2, true);
            }
        });
        btnCorrect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                choose(1, true);
            }
        });

        tvTerm.setTextSize(1.4f * MyGlobal.font_size * tvTerm.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);

        tvTerm.setText(mTerms.getTerm());
        new MyFunc(context).setToolforTextView(tvTerm);

        tvDef = (TextView) rootView.findViewById(R.id.tvDef);
        tvDef.setTextSize(1.4f * MyGlobal.font_size * tvDef.getTextSize() / MyGlobal.scaledDensity * MyGlobal.ScreenScale);
        new MyFunc(context).setToolforTextView(tvDef);
        // Image
        if (mTerms.getImage() != null && mTerms.getImage().getUrl() != null) {
            final String url = mTerms.getImage().getUrl();
            preShowImage();
            if (url.startsWith("img")) {
                Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + url, rlDef);
                img_card.setImageBitmap(bitmap);
            } else if (url.startsWith("dropbox")) {
                pb.setVisibility(View.VISIBLE);
                new ImageLoaderDropbox(context, R.drawable.img_holder, pb, DropboxClientFactory.getClient()).DisplayImage(mTranslate.user_id + "___" + mTranslate.text + ".jpg", img_card);
            } else {
                pb.setVisibility(View.VISIBLE);
                new ImageLoader(context, R.drawable.img_holder, pb).DisplayImage(mTerms.getImage().getUrl(), img_card);
            }
            img_card.setVisibility(View.VISIBLE);
            img_card.setPadding(2 * MyGlobal.fivedp, 0, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp);
        } else {
            btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera, 0, 0);
            RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rParam.addRule(RelativeLayout.CENTER_IN_PARENT);
            tvDef.setLayoutParams(rParam);
        }

        // Definitation
        if (mTerms.getDefinition().trim().equals("")) {
            tvDef.setVisibility(View.GONE);
            img_card.setPadding(2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp, 2 * MyGlobal.fivedp);
        } else
            tvDef.setText(mTerms.getDefinition());


        if (mTerms.getDefinition() != null && !mTerms.getDefinition().equals("")) {
            tvDef.setVisibility(View.VISIBLE);
        }

        choose(mTerms.status, false);


        return rootView;
    }

    public void preShowImage() {
        RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvDef.setLayoutParams(rParam);
        rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rParam.addRule(RelativeLayout.BELOW, tvDef.getId());
        rParam.addRule(RelativeLayout.ABOVE, llMenu.getId());
        img_card.setLayoutParams(rParam);
        img_card.setVisibility(View.VISIBLE);
    }


    private void startPractice() {
        MyFunc.writeUserLog(context, UActivity.MAIN_PRACTICE);


        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, mTerms.langto);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, mTerms.langto);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, mTerms.langto);
        // intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

    }


    private void choose(int status, boolean isClick) {
        if (isClick)
            MyFunc.writeUserLog(context, UActivity.REVIEW_FLASHCARD);
        if (status == 1) {
            btnCorrect.setVisibility(View.GONE);
            btnWrong.setVisibility(View.GONE);
        } else if (status == 2) {
            btnCorrect.setVisibility(View.GONE);
            btnWrong.setVisibility(View.GONE);
        }


        if (status == 1) { // Neu Known
            tvResult.setText(getResources().getString(R.string.got_it));
            ((ViewCardActivity) context).pager_tab_strip.changecolor(mPageNumber, context.getResources().getColor(R.color.mau_xanhlo));
            mTerms.status = 1; // lam roi va lam dung
            if (isClick)
                new MyFunc(context).answer(true, mTerms.getItemID(), 4);
        } else if (status == 2) {
            tvResult.setText(getResources().getString(R.string.missed));
            ((ViewCardActivity) context).pager_tab_strip.changecolor(mPageNumber, context.getResources().getColor(R.color.mau_wrong));
            tvResult.setBackgroundColor(getResources().getColor(R.color.mau_wrong));
            mTerms.status = 2; // lam roi va lam dung
            if (isClick)
                new MyFunc(context).answer(false, mTerms.getItemID(), 4);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQ_CODE_SPEECH_INPUT) {
                if (data != null) {
                    String show = "";
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    String spokenText = result.get(0);
                    if (spokenText.toLowerCase().equals(mTerms.getDefinition().toLowerCase())) { //Noi dung
                        int score = (int) (data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)[0] * 100);
                        show = spokenText + " (" + getResources().getString(R.string.correct) + ", " + getResources().getString(R.string.score) + " = " + score + "/100)";
                        Translate translate = mTranslateHandler.getByID(mTerms.translateID);
                        if (score > translate.score) {
                            translate.score = score;
                            translate.practice_times++;
                            mTranslateHandler.update(translate);
                        }
                        btnPractice.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.practice_done, 0, 0);

                    } else {

                        Translate translate = mTranslateHandler.getByID(mTerms.translateID);
                        if (translate.score < 0) { //Update
                            translate.score = -3;
                            translate.practice_times++;
                            mTranslateHandler.update(translate);
                        }
                        show = spokenText + "(" + getString(R.string.wrong) + ")";
                    }

                    Toast.makeText(context, show, Toast.LENGTH_LONG).show();


                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                MyFunc.writeUserLog(context, UActivity.ADD_IMAGE_DONE);
                Bitmap bitmap = MyFunc.decodePic(MyGlobal.image_folder + mCurrentPhotoName, rlDef);
                btnCamera.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.camera_done, 0, 0);
                img_card.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams rParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tvDef.setLayoutParams(rParam);
                img_card.setImageBitmap(bitmap);


                //insert Flashcard
                Image img = new Image();
                img.setUrl(mCurrentPhotoName);
                mTerms.setImage(img);
                mTermsHandler.update(mTerms);

                Translate translate = mTranslateHandler.getByID(mTerms.translateID);
                translate.image = mCurrentPhotoName;
                mTranslateHandler.update(translate);

                //Upload image to Dropbox

                File file = new File(Environment.getExternalStorageDirectory().toString(), "temp.jpg");
                try {
                    OutputStream outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    new UploadFileTask(context, DropboxClientFactory.getClient(), file.getAbsolutePath(), 1, mTerms.getTerm(), new UploadFileTask.Callback() {
                        @Override
                        public void onUploadComplete(FileMetadata result) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }).execute();

                } catch (IOException e) {

                }
            }
        }
    }


}