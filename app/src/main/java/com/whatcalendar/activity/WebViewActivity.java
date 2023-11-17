package com.whatcalendar.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityWebViewBinding;

import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class WebViewActivity extends AppCompatActivity {
    private ActivityWebViewBinding binding;
    public static final String EXTRA_PAGE_NAME = "page_name";
    private static final String EXTRA_URL = "_url";
    WebView mWebView;

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());

        mWebView = binding.webView;

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
        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
