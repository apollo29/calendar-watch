package com.whatcalendar.activity;

import android.view.View;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.InfoActivity;

/* loaded from: classes.dex */
public class InfoActivity$$ViewBinder<T extends InfoActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((InfoActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((InfoActivity$$ViewBinder<T>) ((InfoActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.button_update, "field 'mUpdateButton' and method 'onCheckUpdate'");
        target.mUpdateButton = (RelativeLayout) finder.castView(view, R.id.button_update, "field 'mUpdateButton'");
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onCheckUpdate();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_privacy, "method 'onPrivacyClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onPrivacyClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_what_watch_com, "method 'onWhatWatchComClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onWhatWatchComClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_term, "method 'onTermClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onTermClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_help, "method 'onHelpClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onHelpClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_faq, "method 'onFaqClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onFaqClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.button_info_welcome, "method 'onWelcomeClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onWelcomeClicked();
            }
        });
        ((View) finder.findRequiredView(source, R.id.toolbar_icon, "method 'onCloseButtonClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.InfoActivity$$ViewBinder.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onCloseButtonClicked();
            }
        });
    }

    public void unbind(T target) {
        target.mUpdateButton = null;
    }
}
