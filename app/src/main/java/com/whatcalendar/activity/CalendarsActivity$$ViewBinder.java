package com.whatcalendar.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.CalendarsActivity;

/* loaded from: classes.dex */
public class CalendarsActivity$$ViewBinder<T extends CalendarsActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((CalendarsActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((CalendarsActivity$$ViewBinder<T>) ((CalendarsActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.recycler_view, "field 'recyclerView'");
        target.recyclerView = (RecyclerView) finder.castView(view, R.id.recycler_view, "field 'recyclerView'");
        View view2 = (View) finder.findRequiredView(source, R.id.toolbar_icon, "method 'onCloseButtonClicked'");
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.CalendarsActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onCloseButtonClicked();
            }
        });
    }

    public void unbind(T target) {
        target.recyclerView = null;
    }
}
