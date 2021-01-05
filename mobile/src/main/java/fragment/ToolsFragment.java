package fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.jquiz.project2.R;

import ui.SmoothProgressBar;


public class ToolsFragment extends Fragment {

    public WebView webview;
    public EditText etBrowser;

    private SmoothProgressBar pb;

    private Context context;
    private ImageView btnClear;
    private ImageButton btnBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_tools, container, false);
        context = getActivity();
        etBrowser = (EditText) rootView.findViewById(R.id.etAddress);
        btnBack = (ImageButton) rootView.findViewById(R.id.btnBack);
        pb = (SmoothProgressBar) rootView.findViewById(R.id.pb1);
        pb.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.colors));
        btnClear = (ImageView) rootView.findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etBrowser.setText("");
            }
        });
        webview = (WebView) rootView.findViewById(R.id.webview);

        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        getActivity().registerForContextMenu(webview);

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100)
                    pb.setVisibility(View.VISIBLE);
                else
                    pb.setVisibility(View.INVISIBLE);
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String notuse) {
                etBrowser.setText(notuse);
                return false;
            }
        });
        WebSettings webSettings = webview.getSettings();
        webview.loadUrl("http://www.google.com");
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);

        etBrowser.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String url = etBrowser.getText().toString();
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://www.google.com/search?q=" + android.text.Html.fromHtml(url).toString();
                    }
                    webview.loadUrl(url);
                    etBrowser.setText(url);
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etBrowser.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.goBack();
            }
        });
        return rootView;
    }
}
