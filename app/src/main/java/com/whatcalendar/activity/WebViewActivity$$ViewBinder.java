package com.whatcalendar.activity;

import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import com.whatcalendar.R;
import com.whatcalendar.activity.WebViewActivity;

/* loaded from: classes.dex */
public class WebViewActivity$$ViewBinder<T extends WebViewActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((WebViewActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((WebViewActivity$$ViewBinder<T>) ((WebViewActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.web_view, "field 'mWebView'");
        target.mWebView = (WebView) finder.castView(view, R.id.web_view, "field 'mWebView'");
    }

    public void unbind(T target) {
        target.mWebView = null;
    }
}
