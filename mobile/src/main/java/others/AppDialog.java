
package others;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import android.widget.ImageView;

import com.jquiz.project2.R;

import controlvariable.UActivity;
import database.TermsHandler;
import entity.MItem;
import entity.Term;
import entity_display.MTerms;

public class AppDialog {
    private final SharedPreferences.Editor preferences_edit;
    private final SharedPreferences preferences;
    private final Context context;

    static AlertDialog alert;

    public AppDialog(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences_edit = preferences.edit();
    }


    public void mark_click_dialog(final MItem item, final ImageView imMark) {
        CharSequence[] items = new CharSequence[3];
        int mark = 0;

        TermsHandler mTermsHandler = new TermsHandler(context);
        Term term = mTermsHandler.getByID(Long.parseLong(item.getItemID()));
        mark = term.mark;

        items[0] = "None";
        items[1] = "Bookmark";
        items[2] = "Mastered";
        // items[3] = "Unuseful";

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setSingleChoiceItems(items, mark, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {

                changeMark(item, position);
                item.mark = position;
                if (position == 1) {
                    MyFunc.writeUserLog(context, UActivity.MARK_BOOKMARK);
                    imMark.setImageResource(R.drawable.mark_done);
                } else {
                    imMark.setImageResource(R.drawable.mark);
                    MyFunc.writeUserLog(context, UActivity.MARK_NONE);
                }
                alert.dismiss();
            }

        });
        alert = builder.create();
        alert.show();
    }

    public void changeMark(MItem item, int position) {

        TermsHandler mTermsHandler = new TermsHandler(context);
        MTerms term = mTermsHandler.getByID(Long.parseLong(item.getItemID()));
        term.mark = position;
        term.bookmarkTime = (int) (System.currentTimeMillis() / 1000);
        mTermsHandler.update(term);


    }
    public void showDialogExitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setCancelable(true);
        builder.setTitle("Quit");
        builder.setMessage("Are you sure want to quit");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((Activity) context).finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


}
