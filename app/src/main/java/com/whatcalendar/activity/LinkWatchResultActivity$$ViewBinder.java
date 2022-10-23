package com.whatcalendar.activity;

import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.LinkWatchResultActivity;

/* loaded from: classes.dex */
public class LinkWatchResultActivity$$ViewBinder<T extends LinkWatchResultActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((LinkWatchResultActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((LinkWatchResultActivity$$ViewBinder<T>) ((LinkWatchResultActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.result_hint, "field 'mTextResultHint'");
        target.mTextResultHint = (TextView) finder.castView(view, R.id.result_hint, "field 'mTextResultHint'");
        View view2 = (View) finder.findRequiredView(source, R.id.result_text, "field 'mTextResult'");
        target.mTextResult = (TextView) finder.castView(view2, R.id.result_text, "field 'mTextResult'");
        View view3 = (View) finder.findRequiredView(source, R.id.result_button, "method 'onOk'");
        view3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.LinkWatchResultActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onOk();
            }
        });
    }

    public void unbind(T target) {
        target.mTextResultHint = null;
        target.mTextResult = null;
    }
}
