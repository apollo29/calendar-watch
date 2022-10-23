package com.whatcalendar.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.LinkWatchActivity;

/* loaded from: classes.dex */
public class LinkWatchActivity$$ViewBinder<T extends LinkWatchActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((LinkWatchActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((LinkWatchActivity$$ViewBinder<T>) ((LinkWatchActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.edit_watch_id, "field 'mEditWatchId'");
        target.mEditWatchId = (EditText) finder.castView(view, R.id.edit_watch_id, "field 'mEditWatchId'");
        View view2 = (View) finder.findRequiredView(source, R.id.progress_pair_watch, "field 'mProgressPair'");
        target.mProgressPair = (ProgressBar) finder.castView(view2, R.id.progress_pair_watch, "field 'mProgressPair'");
        View view3 = (View) finder.findRequiredView(source, R.id.text_terms, "field 'mTextTerms' and method 'openWebView'");
        target.mTextTerms = (TextView) finder.castView(view3, R.id.text_terms, "field 'mTextTerms'");
        view3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.LinkWatchActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.openWebView();
            }
        });
        View view4 = (View) finder.findRequiredView(source, R.id.text_hint_watch_id, "field 'mTextHintWatchId'");
        target.mTextHintWatchId = (TextView) finder.castView(view4, R.id.text_hint_watch_id, "field 'mTextHintWatchId'");
        View view5 = (View) finder.findRequiredView(source, R.id.button_pair_watch, "field 'mButtonPair' and method 'onPairWatch'");
        target.mButtonPair = (Button) finder.castView(view5, R.id.button_pair_watch, "field 'mButtonPair'");
        view5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.LinkWatchActivity$$ViewBinder.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onPairWatch();
            }
        });
    }

    public void unbind(T target) {
        target.mEditWatchId = null;
        target.mProgressPair = null;
        target.mTextTerms = null;
        target.mTextHintWatchId = null;
        target.mButtonPair = null;
    }
}
