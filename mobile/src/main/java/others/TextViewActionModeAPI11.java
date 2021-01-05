package others;

import android.content.Context;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.jquiz.project2.R;

import activity.ParentActivity;
import controlvariable.UActivity;

public class TextViewActionModeAPI11 implements ActionMode.Callback {
    TextView tv;
    Context context;

    public TextViewActionModeAPI11(Context context, TextView tv) {
        this.tv = tv;
        this.context = context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Used to put dark icons on light action bar
        mode.setTitle("");
        menu.clear();
        //	menu.add("Copy").setIcon(R.drawable.btn_copy).setTitle("Copy").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.add("English-Chinese").setIcon(R.drawable.english_chinese).setTitle("English-Chinese").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Chinese-English").setIcon(R.drawable.chinese_english).setTitle("Chinese-English").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("English-English").setIcon(R.drawable.english_english).setTitle("English-English").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("Image-Search").setIcon(R.drawable.image_search).setTitle("Image Search").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        //  menu.add("Google").setIcon(R.drawable.btn_google).setTitle("Google").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        String text = tv.getText().toString();
        text = text.substring(tv.getSelectionStart(), tv.getSelectionEnd());

        if (item.getTitle().equals("Copy")) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(android.text.Html.fromHtml(text));
            Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals("English-Chinese")) {
            //String url = "http://m.dictionary.com/?q=" + android.text.Html.fromHtml(text).toString() + "&submit-result-SEARCHD=Search";
            String url = "http://chinesedictionary.mobi/?handler=QueryWorddict&mwdqb=" + android.text.Html.fromHtml(text).toString();
            ((ParentActivity) context).layoutParams.leftMargin = 0; // Open close
            ((ParentActivity) context).move_able.requestLayout();
            ((ParentActivity) context).mToolsFragment.webview.loadUrl(url);
            ((ParentActivity) context).mToolsFragment.etBrowser.setText(url);
            ((ParentActivity) context).setAlphaBtnWhiteboard(1.0f);
            MyFunc.writeUserLog(context, UActivity.EN_CN_DICT, text);
        } else if (item.getTitle().equals("Chinese-English")) {
            //String url = "http://m.dictionary.com/?q=" + android.text.Html.fromHtml(text).toString() + "&submit-result-SEARCHD=Search";
            String url = "http://chinesedictionary.mobi/?handler=QueryWorddict&mwdqb=" + android.text.Html.fromHtml(text).toString();
            ((ParentActivity) context).layoutParams.leftMargin = 0; // Open close
            ((ParentActivity) context).move_able.requestLayout();

            ((ParentActivity) context).mToolsFragment.webview.loadUrl(url);
            ((ParentActivity) context).mToolsFragment.etBrowser.setText(url);
            ((ParentActivity) context).setAlphaBtnWhiteboard(1.0f);
            MyFunc.writeUserLog(context, UActivity.CN_EN_DICT, text);
        } else if (item.getTitle().equals("English-English")) {
            //String url = "http://m.dictionary.com/?q=" + android.text.Html.fromHtml(text).toString() + "&submit-result-SEARCHD=Search";
            String url = "http://www.yourdictionary.com/" + android.text.Html.fromHtml(text).toString();
            ((ParentActivity) context).layoutParams.leftMargin = 0; // Open close
            ((ParentActivity) context).move_able.requestLayout();

            ((ParentActivity) context).mToolsFragment.webview.loadUrl(url);
            ((ParentActivity) context).mToolsFragment.etBrowser.setText(url);
            ((ParentActivity) context).setAlphaBtnWhiteboard(1.0f);
            MyFunc.writeUserLog(context, UActivity.EN_EN_DICT, text);
        } else if (item.getTitle().equals("Image Search")) {
            ((ParentActivity) context).layoutParams.leftMargin = 0; // Open close
            ((ParentActivity) context).move_able.requestLayout();
            String url = "http://www.google.com/search?q=" + android.text.Html.fromHtml(text).toString() + "&tbm=isch";
            ((ParentActivity) context).mToolsFragment.webview.loadUrl(url);
            ((ParentActivity) context).mToolsFragment.etBrowser.setText(url);
            ((ParentActivity) context).setAlphaBtnWhiteboard(1.0f);
            MyFunc.writeUserLog(context, UActivity.IMAGE_SEARCH, text);
        }
        mode.finish();
        return true;
    }

}