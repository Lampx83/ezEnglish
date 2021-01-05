package activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.jquiz.project2.R;

import controlvariable.MyGlobal;
import database.DataBaseHandler;
import database.TermsHandler;
import database.TranslateHandler;
import fragment.ToolsFragment;
import fragment.TranslateFragment;


public class HistoryDetailActivity extends ParentActivity {


    private static final int MENU_INFO = 0;
    private static final int MENU_DELETE = 1;
    private long current_translateid;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTitle(getResources().getString(R.string.detail));
        setContentView(R.layout.activity_historydetail);
        mTranslateFragment = new TranslateFragment();
        current_translateid = getIntent().getLongExtra("translateid", -1);
        mTranslateFragment.current_translateid = current_translateid;
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mTranslateFragment).commit();

        hasTool = true;
        if (hasTool) {
            mToolsFragment = (ToolsFragment) getSupportFragmentManager().findFragmentById(R.id.tool_fragment);

            btnWhiteboard = (ImageButton) findViewById(R.id.btnWhiteboard);
            if (MyGlobal.screen_small) {
                Bitmap bm_drawer_big = BitmapFactory.decodeResource(getResources(), R.drawable.drawer_web);
                btnWhiteboard.setImageBitmap(Bitmap.createScaledBitmap(bm_drawer_big, (int) (0.6f * bm_drawer_big.getWidth()), (int) (0.6f * bm_drawer_big.getHeight()), false));
            } else
                btnWhiteboard.setImageResource(R.drawable.drawer_web);

            move_able = (RelativeLayout) findViewById(R.id.move_able);
            layoutParams = (RelativeLayout.LayoutParams) move_able.getLayoutParams();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItemCompat.setShowAsAction(menu.add(0, MENU_INFO, 0, getString(R.string.more_info)), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItemCompat.setShowAsAction(menu.add(0, MENU_DELETE, 0, getString(R.string.delete)), MenuItemCompat.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_INFO) {
            Intent intent = new Intent(getApplicationContext(), MoreInfoActivity.class);
            intent.putExtra("translateid", getIntent().getLongExtra("translateid", -1));
            startActivity(intent);
            return true;
        } else if (item.getItemId() == MENU_DELETE) {
            new AlertDialog.Builder(context)
                    .setMessage(getString(R.string.are_you_sure_you_want_to_delete))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new TranslateHandler(context).deleteBy(DataBaseHandler.TRANSLATEID + "=?", new String[]{"" + current_translateid});
                            new TermsHandler(context).deleteBy(DataBaseHandler.TRANSLATEID + "=?", new String[]{"" + current_translateid});
                            ((Activity) context).finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        //Update mark
        super.onResume();
    }
}
