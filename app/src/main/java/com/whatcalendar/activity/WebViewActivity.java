package com.whatcalendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.whatcalendar.R;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class WebViewActivity extends AppCompatActivity {
    public static final String EXTRA_PAGE_NAME = "page_name";
    private static final String EXTRA_URL = "_url";
    @Bind({R.id.web_view})
    WebView mWebView;

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        String page_name = getIntent().getStringExtra(EXTRA_PAGE_NAME);
        if (!StringUtils.isEmpty(page_name)) {
            setTitle(page_name);
        }
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null && url.toLowerCase().endsWith(".pdf")) {
            url = "https://docs.google.com/gview?embedded=true&url=" + url;
        }
        this.mWebView.getSettings().setJavaScriptEnabled(true);
        this.mWebView.setWebViewClient(new MyWebViewClient());
        this.mWebView.loadUrl(url);
    }

    public static Intent newIntent(Context context, String url, String page_name) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_PAGE_NAME, page_name);
        return intent;
    }

    /* loaded from: classes.dex */
    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
            WebViewActivity.this = r1;
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
