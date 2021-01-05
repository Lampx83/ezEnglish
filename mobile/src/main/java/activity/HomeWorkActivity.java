package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.v2.files.FileMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jquiz.project2.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import controlvariable.MyGlobal;
import controlvariable.MyPref;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import entity_display.MTranslate;
import others.DropboxClientFactory;
import others.MyFunc;
import others.UploadFileTask;

public class HomeWorkActivity extends ParentActivity {
    private MediaPlayer player;
    LinkedHashSet<String> seletedItems;
    boolean bl[];
    CharSequence[] items;
    private ArrayList<TextView> listTextView = new ArrayList<TextView>();
    private ArrayList<Button> listBtnChooseWords = new ArrayList<Button>();
    private ArrayList<Button> listBtnRecord = new ArrayList<Button>();
    private ArrayList<Button> listBtnPlay = new ArrayList<Button>();
    private ArrayList<SeekBar> listSeekBar = new ArrayList<SeekBar>();
    private ArrayList<ImageView> listImCloud = new ArrayList<ImageView>();

    TermsHandler mTermsHandler;
    TranslateHandler mTranslateHandler;
    public static final int REQUEST_RECORD_SOUND = 2;
    private int stateRecord = 2; //2 stop
    private final Handler handler = new Handler();
    int hw = 0;
    int numberOfHomework;
    LinearLayout llHomework;
    String[] arrContext;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acitivity_homework);
        setTitle(getString(R.string.homework));
        mTermsHandler = new TermsHandler(context);
        mTranslateHandler = new TranslateHandler(context);
        llHomework = (LinearLayout) findViewById(R.id.llHomework);

        arrContext = getResources().getStringArray(R.array.arrContext);
        numberOfHomework = arrContext.length - 1 + 2;  //Home work =1 va home work =6 la Pretest va postest

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams paramsMathParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i <= numberOfHomework - 1; i++) {

            LinearLayout llMain = new LinearLayout(context);
            llMain.setOrientation(LinearLayout.VERTICAL);

            //Textview
            AppCompatTextView textView = new AppCompatTextView(context);
            textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
            llMain.addView(textView, params);

            //Button
            LinearLayout llButton = new LinearLayout(context);

            AppCompatButton btnChooseWords = new AppCompatButton(context);
            btnChooseWords.setText(getString(R.string.select_words));
            llButton.addView(btnChooseWords, params);

            AppCompatButton btnRecord = new AppCompatButton(context);
            btnRecord.setText(getString(R.string.record));
            llButton.addView(btnRecord, params);

            AppCompatButton btnPlay = new AppCompatButton(context);
            btnPlay.setText(getString(R.string.play));
            btnPlay.setVisibility(View.INVISIBLE);
            llButton.addView(btnPlay, params);

            AppCompatImageView imCloud = new AppCompatImageView(context);
            imCloud.setImageResource(R.drawable.cloud1);
            imCloud.setVisibility(View.INVISIBLE);
            llButton.addView(imCloud, params);

            llMain.addView(llButton, paramsMathParent);

            //Seekbar
            AppCompatSeekBar seekBar = new AppCompatSeekBar(context);
            seekBar.setVisibility(View.INVISIBLE);
            llMain.addView(seekBar, paramsMathParent);
            //All
            llHomework.addView(llMain, paramsMathParent);

            listTextView.add(textView);
            listBtnChooseWords.add(btnChooseWords);
            listBtnRecord.add(btnRecord);
            listBtnPlay.add(btnPlay);
            listSeekBar.add(seekBar);
            listImCloud.add(imCloud);

            show_hide(i);
            show_words(i);
            final int finalI = i;
            listBtnRecord.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    record(finalI);
                    listImCloud.get(finalI).setVisibility(View.INVISIBLE);
                }
            });
            listBtnPlay.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play(finalI);
                }
            });
            listImCloud.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    upload(finalI);
                }
            });
            listBtnChooseWords.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseword(finalI);
                }
            });
        }
    }

    public void show_words(int homework) {
        List<MTranslate> list;
        if (homework != 0 && homework != numberOfHomework - 1)
            list = mTranslateHandler.getAllBy("where " + DataBaseHandler.TRANSLATE_CONTEXT + "='" + arrContext[homework - 1] + "'" + " and " + DataBaseHandler.TRANSLATE_USERID + "= '" + MyGlobal.user_id + "'", "");
        else
            list = mTranslateHandler.getAllBy("where " + DataBaseHandler.TRANSLATE_CONTEXT + "='" + arrContext[arrContext.length - 1] + "'" + " and " + DataBaseHandler.TRANSLATE_USERID + "= '" + MyGlobal.user_id + "'", "");

        LinkedHashSet<String> lhs = new LinkedHashSet<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).langfrom.equals(MyGlobal.lang2))
                lhs.add(list.get(i).text);
            else
                lhs.add(list.get(i).result);
        }

        String pref_homework_words = preferences.getString(MyPref.pref_homework_words + homework, "");
        seletedItems = new Gson().fromJson(pref_homework_words, new TypeToken<LinkedHashSet<String>>() {
        }.getType());
        if (seletedItems == null)
            seletedItems = new LinkedHashSet();
        items = new CharSequence[lhs.size()];
        bl = new boolean[lhs.size()];
        Iterator<String> itr = lhs.iterator();
        int i = 0;
        while (itr.hasNext()) {
            String s = itr.next();
            items[i++] = s;
            if (!seletedItems.contains(s))
                seletedItems.remove(s);
        }
        show_selected_words(homework);
    }

    public void show_selected_words(int homework) {
        String str = "Selected words: ";
        for (int i = 0; i < items.length; i++) {
            if (seletedItems.contains(items[i])) {
                bl[i] = true;
                str = str + items[i] + ", ";
            } else
                bl[i] = false;
        }

        String strHW = null;
        if (homework != 0 && homework != numberOfHomework - 1)
            strHW = getString(R.string.homework) + " \"" + arrContext[homework - 1] + "\":";
        else if (homework == 0)
            strHW = getString(R.string.pretest)+":";
        else if (homework == numberOfHomework - 1)
            strHW = getString(R.string.posttest)+":";


        if (seletedItems.size() != 0) {
            str = str.substring(0, str.length() - 2);
            listTextView.get(homework).setText(Html.fromHtml(strHW + "<br><small><i>" + str + "<i><small>"));
        } else {
            listTextView.get(homework).setText(strHW);
        }
        if (seletedItems.size() < 5) {
            listBtnRecord.get(homework).setEnabled(false);
        } else {
            listBtnRecord.get(homework).setEnabled(true);
        }
    }

    public void chooseword(final int homework) {
        show_words(homework);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMultiChoiceItems(items, bl, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            seletedItems.add((String) items[indexSelected]);
                        } else {
                            seletedItems.remove((String) items[indexSelected]);
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        preferences.edit().putString(MyPref.pref_homework_words + homework, new Gson().toJson(seletedItems)).commit();
                        show_selected_words(homework);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

    public void upload(final int homework) {
        String pref_homework_words = preferences.getString(MyPref.pref_homework_words + homework, "");
        LinkedHashSet seletedItems = new Gson().fromJson(pref_homework_words, new TypeToken<LinkedHashSet<String>>() {
        }.getType());

        Iterator<String> itr = seletedItems.iterator();
        int i = 0;
        String words = "";
        while (itr.hasNext()) {
            words = words + "___" + itr.next();
        }

        listImCloud.get(homework).setVisibility(View.INVISIBLE);
        Toast.makeText(context, "Uploading...", Toast.LENGTH_SHORT).show();
        new UploadFileTask(context, DropboxClientFactory.getClient(), MyGlobal.record_folder + "hw" + (homework + 1) + ".3gp", 2, "hw" + (homework + 1) + words + ".3gp", new UploadFileTask.Callback() {
            @Override
            public void onUploadComplete(FileMetadata result) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                preferences.edit().putBoolean(MyPref.pref_homework_uploaded + homework, true).commit();
                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
                show_hide(homework);
            }

            @Override
            public void onError(Exception e) {

            }
        }).execute();



    }

    public void record(int homework) {
        hw = homework;
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, REQUEST_RECORD_SOUND);
    }

    public void play(int homework) {
        hw = homework;
        stopAll();
        if (stateRecord == 2) { // Start playing

            listSeekBar.get(homework).setVisibility(View.VISIBLE);
            listBtnPlay.get(homework).setText(getString(R.string.stop));

            stateRecord = 3;
            player = new MediaPlayer();
            try {
                player.setDataSource(MyGlobal.record_folder + "hw" + (homework + 1) + ".3gp");
                player.prepare();
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
            player.start();
            startPlayProgressUpdater();
            listSeekBar.get(homework).setMax(player.getDuration());
        } else if (stateRecord == 3) { // Stop playing
            stopAll();
        }
    }

    public void stopAll() {
        for (int i = 0; i <= numberOfHomework - 1; i++)
            listBtnPlay.get(i).setText(getString(R.string.play));

        if (player != null && player.isPlaying())
            player.stop();
    }

    public void show_hide(int homework) {
        if (preferences.getBoolean(MyPref.pref_homework_done + homework, false)) {
            listBtnPlay.get(homework).setVisibility(View.VISIBLE);
            listImCloud.get(homework).setVisibility(View.VISIBLE);
            if (preferences.getBoolean(MyPref.pref_homework_uploaded + homework, false)) {
                listImCloud.get(homework).setImageResource(R.drawable.cloud);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_RECORD_SOUND && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                String filePath = getAudioFilePathFromUri(uri);
                new MyFunc(context).copy(new File(filePath), new File(MyGlobal.record_folder + "hw" + (hw + 1) + ".3gp"));
                getContentResolver().delete(uri, null, null);
                (new File(filePath)).delete();

                preferences.edit().putBoolean(MyPref.pref_homework_done + hw, true).commit();
                show_hide(hw);

                upload(hw);


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    public void startPlayProgressUpdater() {
        if (player.isPlaying()) {
            Runnable notification = new Runnable() {
                @Override
                public void run() {
                    startPlayProgressUpdater();
                }
            };
            handler.postDelayed(notification, 100);
            listSeekBar.get(hw).setProgress(player.getCurrentPosition());
        } else {
            player.pause();
            for (int i = 0; i <= numberOfHomework - 1; i++) {
                listBtnPlay.get(i).setText(getString(R.string.play));
                listSeekBar.get(i).setProgress(0);
                listSeekBar.get(i).setVisibility(View.INVISIBLE);
            }
            stateRecord = 2;
        }
    }
}
