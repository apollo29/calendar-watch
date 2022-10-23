package com.whatcalendar.activity;

import android.view.View;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.MainActivity;
import com.whatcalendar.view.BatteryLevelView;

/* loaded from: classes.dex */
public class MainActivity$$ViewBinder<T extends MainActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((MainActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((MainActivity$$ViewBinder<T>) ((MainActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        target.mBatteryLevelView = (BatteryLevelView) finder.castView((View) finder.findRequiredView(source, R.id.view_battery_level, "field 'mBatteryLevelView'"), R.id.view_battery_level, "field 'mBatteryLevelView'");
        View view = (View) finder.findRequiredView(source, R.id.button_my_watch, "field 'mMyWatchButton' and method 'myWatchClicked'");
        target.mMyWatchButton = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.MainActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.myWatchClicked();
            }
        });
        target.mChargingLayout = (RelativeLayout) finder.castView((View) finder.findRequiredView(source, R.id.layout_charging, "field 'mChargingLayout'"), R.id.layout_charging, "field 'mChargingLayout'");
        ((View) finder.findRequiredView(source, R.id.button_info, "method 'onInfoClicked'")).setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.MainActivity$$ViewBinder.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onInfoClicked();
            }
        });
    }

    public void unbind(T target) {
        target.mBatteryLevelView = null;
        target.mMyWatchButton = null;
        target.mChargingLayout = null;
    }
}
