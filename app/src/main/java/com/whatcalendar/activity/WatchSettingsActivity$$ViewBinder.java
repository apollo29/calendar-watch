package com.whatcalendar.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.WatchSettingsActivity;

/* loaded from: classes.dex */
public class WatchSettingsActivity$$ViewBinder<T extends WatchSettingsActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((WatchSettingsActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((WatchSettingsActivity$$ViewBinder<T>) ((WatchSettingsActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.flexible_button, "field 'mFlexibleButton'");
        target.mFlexibleButton = (LinearLayout) finder.castView(view, R.id.flexible_button, "field 'mFlexibleButton'");
        View view2 = (View) finder.findRequiredView(source, R.id.fixed_button, "field 'mFixedButton'");
        target.mFixedButton = (LinearLayout) finder.castView(view2, R.id.fixed_button, "field 'mFixedButton'");
        View view3 = (View) finder.findRequiredView(source, R.id.switch_allday, "field 'mSwitchAllDay' and method 'onAllDayMode'");
        target.mSwitchAllDay = (Switch) finder.castView(view3, R.id.switch_allday, "field 'mSwitchAllDay'");
        ((CompoundButton) view3).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.1
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton p0, boolean p1) {
                target.onAllDayMode(p1);
            }
        });
        View view4 = (View) finder.findRequiredView(source, R.id.switch_vibrate, "field 'mSwitchVibrate' and method 'onVibrateMode'");
        target.mSwitchVibrate = (Switch) finder.castView(view4, R.id.switch_vibrate, "field 'mSwitchVibrate'");
        ((CompoundButton) view4).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton p0, boolean p1) {
                target.onVibrateMode(p1);
            }
        });
        View view5 = (View) finder.findRequiredView(source, R.id.switch_airplane, "field 'mSwitchAirplane' and method 'onAirplaneMode'");
        target.mSwitchAirplane = (Switch) finder.castView(view5, R.id.switch_airplane, "field 'mSwitchAirplane'");
        ((CompoundButton) view5).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.3
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton p0, boolean p1) {
                target.onAirplaneMode(p1);
            }
        });
        View view6 = (View) finder.findRequiredView(source, R.id.button_manual_sync, "field 'mButtonSync' and method 'onSetAllDayPattern'");
        target.mButtonSync = (LinearLayout) finder.castView(view6, R.id.button_manual_sync, "field 'mButtonSync'");
        view6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onSetAllDayPattern();
            }
        });
        View view7 = (View) finder.findRequiredView(source, R.id.button_reset, "field 'mButtonReset' and method 'onResetWatch'");
        target.mButtonReset = (LinearLayout) finder.castView(view7, R.id.button_reset, "field 'mButtonReset'");
        view7.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onResetWatch();
            }
        });
        View view8 = (View) finder.findRequiredView(source, R.id.button_calibrate_watch, "field 'mButtonCalibrate' and method 'onCalibrateWatch'");
        target.mButtonCalibrate = (LinearLayout) finder.castView(view8, R.id.button_calibrate_watch, "field 'mButtonCalibrate'");
        view8.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onCalibrateWatch();
            }
        });
        View view9 = (View) finder.findRequiredView(source, R.id.layout_watch_settings, "field 'mLayoutWatchSettings'");
        target.mLayoutWatchSettings = (LinearLayout) finder.castView(view9, R.id.layout_watch_settings, "field 'mLayoutWatchSettings'");
        View view10 = (View) finder.findRequiredView(source, R.id.toolbar_icon, "method 'closeButtonClick'");
        view10.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.closeButtonClick();
            }
        });
        View view11 = (View) finder.findRequiredView(source, R.id.button_calendars, "method 'onCalendarsClick'");
        view11.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onCalendarsClick();
            }
        });
        View view12 = (View) finder.findRequiredView(source, R.id.button_forget, "method 'onForget'");
        view12.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity$$ViewBinder.9
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onForget();
            }
        });
    }

    public void unbind(T target) {
        target.mFlexibleButton = null;
        target.mFixedButton = null;
        target.mSwitchAllDay = null;
        target.mSwitchVibrate = null;
        target.mSwitchAirplane = null;
        target.mButtonSync = null;
        target.mButtonReset = null;
        target.mButtonCalibrate = null;
        target.mLayoutWatchSettings = null;
    }
}
