package com.whatcalendar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityWatchSettingsBinding;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import no.nordicsemi.android.dfu.internal.scanner.BootloaderScanner;

/* loaded from: classes.dex */
public class WatchSettingsActivity extends AppCompatActivity {
    private static final String TAG = WatchSettingsActivity.class.getSimpleName();
    private ActivityWatchSettingsBinding binding;
    private BackgroundService mBackgroundService;

    LinearLayout mButtonCalibrate;
    LinearLayout mButtonReset;
    LinearLayout mButtonSync;
    private Context mContext;
    LinearLayout mFixedButton;
    LinearLayout mFlexibleButton;
    LinearLayout mLayoutWatchSettings;
    Switch mSwitchAirplane;
    Switch mSwitchAllDay;
    Switch mSwitchVibrate;

    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.whatcalendar.activity.WatchSettingsActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            WatchSettingsActivity.this.mBackgroundService = ((BackgroundService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            WatchSettingsActivity.this.mBackgroundService = null;
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.whatcalendar.activity.WatchSettingsActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BackgroundService.ACTION_CONNECTION_STATE_CHANGE.equals(intent.getAction())) {
                int state = intent.getIntExtra("state", 0);
                if (state == 2) {
                    WatchSettingsActivity.this.mSwitchAirplane.setChecked(false);
                }
            }
        }
    };
    private View.OnClickListener onSwitchModeClickListener = new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.3
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (v.getId() == WatchSettingsActivity.this.mFixedButton.getId()) {
                View view = WatchSettingsActivity.this.getLayoutInflater().inflate(R.layout.view_set_time, (ViewGroup) null);
                final NumberPicker pickerHour = view.findViewById(R.id.picker_hour);
                WatchSettingsActivity.setNumberPickerTextColor(pickerHour, WatchSettingsActivity.this.getResources().getColor(R.color.main_text_color));
                WatchSettingsActivity.this.setDividerColor(pickerHour, Color.parseColor("#00000000"));
                pickerHour.setMinValue(0);
                pickerHour.setMaxValue(11);
                pickerHour.setValue(GlobalPreferences.getFixedModeValue());
                pickerHour.setDescendantFocusability(393216);
                final Dialog alert_dialog = new Dialog(WatchSettingsActivity.this.mContext);
                alert_dialog.requestWindowFeature(1);
                alert_dialog.setContentView(R.layout.alert_dialog);
                alert_dialog.findViewById(R.id.alert_dialog_title).setVisibility(View.VISIBLE);
                ((TextView) alert_dialog.findViewById(R.id.alert_dialog_title)).setText(WatchSettingsActivity.this.getString(R.string.dialog_title_set_start_hour));
                ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setVisibility(View.GONE);
                ((LinearLayout) alert_dialog.findViewById(R.id.content)).addView(view);
                alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.3.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        WatchSettingsActivity.this.mBackgroundService.setFixedMode(pickerHour.getValue());
                        WatchSettingsActivity.this.setFixedButtonClicked();
                        GlobalPreferences.setSwitchModeSwitch(true);
                        GlobalPreferences.setFixedModeValue(pickerHour.getValue());
                        alert_dialog.dismiss();
                    }
                });
                alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
                alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.3.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        alert_dialog.dismiss();
                    }
                });
                alert_dialog.show();
            }
            if (v.getId() == WatchSettingsActivity.this.mFlexibleButton.getId() && GlobalPreferences.getSwitchModeSwitch()) {
                WatchSettingsActivity.this.setFlexibleButtonClicked();
                WatchSettingsActivity.this.mBackgroundService.setFlexibleMode();
                GlobalPreferences.setSwitchModeSwitch(false);
            }
        }
    };
    private View.OnTouchListener mAirModeSwitchTouchListener = new View.OnTouchListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.4
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                Log.d(WatchSettingsActivity.TAG, "Touch switch");
                if (!WatchSettingsActivity.this.checkWatchConnection()) {
                    return true;
                }
                final Dialog alert_dialog = new Dialog(WatchSettingsActivity.this.mContext);
                alert_dialog.requestWindowFeature(1);
                alert_dialog.setContentView(R.layout.alert_dialog);
                ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(WatchSettingsActivity.this.getString(R.string.dialog_msg_airplane));
                ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(WatchSettingsActivity.this.getString(R.string.ok));
                alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.4.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        alert_dialog.dismiss();
                    }
                });
                alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
                ((TextView) alert_dialog.findViewById(R.id.cancel_button_text)).setText(WatchSettingsActivity.this.getString(R.string.yes));
                alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.4.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v2) {
                        WatchSettingsActivity.this.mSwitchAirplane.setChecked(true);
                        alert_dialog.dismiss();
                    }
                });
                alert_dialog.show();
                return true;
            }
            return false;
        }
    };
    private View.OnTouchListener mSwitchTouchListener = new View.OnTouchListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.5
        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                Log.d(WatchSettingsActivity.TAG, "Touch switch");
                return !WatchSettingsActivity.this.checkWatchConnection();
            }
            return false;
        }
    };

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_settings);
        binding = ActivityWatchSettingsBinding.inflate(getLayoutInflater());
        this.mContext = this;
        overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.hide_activity_alpha);

        mButtonCalibrate = binding.buttonCalibrateWatch;
        mButtonReset = binding.buttonReset;
        mButtonSync = binding.buttonManualSync;
        mFixedButton = binding.fixedButton;
        mFlexibleButton = binding.flexibleButton;
        mLayoutWatchSettings = binding.layoutWatchSettings;
        mSwitchAirplane = binding.switchAirplane;
        mSwitchAllDay = binding.switchAllday;
        mSwitchVibrate = binding.switchVibrate;

        binding.toolbarIcon.setOnClickListener(view -> closeButtonClick());
        binding.buttonManualSync.setOnClickListener(view -> onSetAllDayPattern());
        binding.buttonCalibrateWatch.setOnClickListener(view -> onCalibrateWatch());
        binding.switchVibrate.setOnCheckedChangeListener((compoundButton, b) -> onVibrateMode(b));
        binding.switchAirplane.setOnCheckedChangeListener((compoundButton, b) -> onAirplaneMode(b));
        binding.switchAllday.setOnCheckedChangeListener((compoundButton, b) -> onAllDayMode(b));
        binding.buttonCalendars.setOnClickListener(view -> onCalendarsClick());
        binding.buttonForget.setOnClickListener(view -> onForget());
        binding.buttonReset.setOnClickListener(view -> onResetWatch());

        bindService(new Intent(this, BackgroundService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        setView();
        IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_CONNECTION_STATE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mFlexibleButton.setOnTouchListener(this.mSwitchTouchListener);
        this.mFixedButton.setOnTouchListener(this.mSwitchTouchListener);
        this.mFlexibleButton.setOnClickListener(this.onSwitchModeClickListener);
        this.mFixedButton.setOnClickListener(this.onSwitchModeClickListener);
        this.mSwitchAllDay.setOnTouchListener(this.mSwitchTouchListener);
        this.mSwitchVibrate.setOnTouchListener(this.mSwitchTouchListener);
        this.mSwitchAirplane.setOnTouchListener(this.mAirModeSwitchTouchListener);
    }

    private void setView() {
        this.mSwitchAllDay.setChecked(GlobalPreferences.getAllDayEventsInfo());
        if (GlobalPreferences.getSwitchModeSwitch()) {
            setFixedButtonClicked();
        } else {
            setFlexibleButtonClicked();
        }
        this.mSwitchVibrate.setChecked(GlobalPreferences.getVibrateSwitch());
        this.mSwitchAirplane.setChecked(GlobalPreferences.getFlightModeSwitch());
    }

    public void setFixedButtonClicked() {
        this.mFixedButton.setBackgroundColor(getResources().getColor(R.color.button_normal));
        ((ImageView) this.mFixedButton.findViewById(R.id.fixed_circle)).setColorFilter(getResources().getColor(R.color.colorAccent));
        this.mFlexibleButton.setBackgroundColor(getResources().getColor(R.color.dark_activity_background));
        ((ImageView) this.mFlexibleButton.findViewById(R.id.flexible_circle)).setColorFilter(getResources().getColor(R.color.colorPressItem));
    }

    public void setFlexibleButtonClicked() {
        this.mFixedButton.setBackgroundColor(getResources().getColor(R.color.dark_activity_background));
        ((ImageView) this.mFixedButton.findViewById(R.id.fixed_circle)).setColorFilter(getResources().getColor(R.color.colorPressItem));
        this.mFlexibleButton.setBackgroundColor(getResources().getColor(R.color.button_normal));
        ((ImageView) this.mFlexibleButton.findViewById(R.id.flexible_circle)).setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    public boolean checkWatchConnection() {
        if (!this.mBackgroundService.isBtAvailable()) {
            Toast.makeText(this.mContext, "Bluetooth is off on the phone.\nPlease, turn it on.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!this.mBackgroundService.isConnected()) {
            Toast.makeText(this.mContext, "Cannot connect to the watch.\nPlease check its operability.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void closeButtonClick() {
        onBackPressed();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.show_activity_alpha, R.anim.push_out_to_bottom);
    }

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        overridePendingTransition(R.anim.show_activity_alpha, R.anim.push_out_to_bottom);
    }

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        overridePendingTransition(R.anim.show_activity_alpha, R.anim.push_out_to_bottom);
        super.onDestroy();
        unbindService(this.mServiceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
    }

    public void onSetAllDayPattern() {
        if (checkWatchConnection()) {
            findViewById(R.id.button_manual_sync).setClickable(false);
            new Timer().schedule(new DisableSyncButton(), BootloaderScanner.TIMEOUT);
            this.mBackgroundService.manualSync();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class DisableSyncButton extends TimerTask {

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            WatchSettingsActivity.this.findViewById(R.id.button_manual_sync).setClickable(true);
        }
    }

    public void onCalibrateWatch() {
        if (checkWatchConnection()) {
            this.mBackgroundService.calibrateStart();
            View view = getLayoutInflater().inflate(R.layout.view_calibrate, (ViewGroup) null);
            Calendar now = Calendar.getInstance();
            final NumberPicker pickerHour = view.findViewById(R.id.picker_hour);
            setNumberPickerTextColor(pickerHour, getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerHour, Color.parseColor("#00000000"));
            pickerHour.setMinValue(0);
            pickerHour.setMaxValue(11);
            pickerHour.setValue(0);
            pickerHour.setValue(now.get(11));
            final NumberPicker pickerMinute = view.findViewById(R.id.picker_minute);
            setNumberPickerTextColor(pickerMinute, getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerMinute, Color.parseColor("#00000000"));
            pickerMinute.setMinValue(0);
            pickerMinute.setMaxValue(59);
            pickerMinute.setValue(now.get(12));
            final NumberPicker pickerSecond = view.findViewById(R.id.picker_second);
            setNumberPickerTextColor(pickerSecond, getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerSecond, Color.parseColor("#00000000"));
            pickerSecond.setMinValue(0);
            pickerSecond.setMaxValue(59);
            final Dialog alert_dialog = new Dialog(this);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            alert_dialog.findViewById(R.id.alert_dialog_title).setVisibility(View.GONE);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setVisibility(View.GONE);
            ((LinearLayout) alert_dialog.findViewById(R.id.content)).addView(view);
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.6
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    WatchSettingsActivity.this.mBackgroundService.calibrateWatch(pickerHour.getValue(), pickerMinute.getValue(), 0, pickerSecond.getValue(), 0);
                    WatchSettingsActivity.this.mBackgroundService.setTime();
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
            alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.7
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    WatchSettingsActivity.this.mBackgroundService.calibrateCancel();
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.show();
        }
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    @SuppressLint("SoonBlockedPrivateApi") Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
                    Log.w("cw", e);
                }
            }
        }
        return false;
    }

    public void setDividerColor(NumberPicker picker, int color) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                    return;
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IllegalAccessException e2) {
                    e2.printStackTrace();
                    return;
                } catch (IllegalArgumentException e3) {
                    e3.printStackTrace();
                    return;
                }
            }
        }
    }

    public void onVibrateMode(boolean checked) {
        GlobalPreferences.setVibrateSwitch(checked);
        if (this.mBackgroundService != null) {
            this.mBackgroundService.setAlertsEnabled(checked);
        }
    }

    public void onAirplaneMode(boolean checked) {
        if (this.mBackgroundService != null) {
            this.mBackgroundService.setAirplaneMode(checked);
        }
        GlobalPreferences.setFlightModeSwitch(checked);
        if (checked) {
            this.mSwitchAirplane.setEnabled(false);
        } else {
            this.mSwitchAirplane.setEnabled(true);
        }
    }

    public void onAllDayMode(boolean checked) {
        GlobalPreferences.setAllDayEventsInfo(checked);
        if (this.mBackgroundService != null) {
            this.mBackgroundService.updateAllDayPatterns();
        }
    }

    public void onCalendarsClick() {
        startActivity(new Intent(this, CalendarsActivity.class));
    }

    public void onForget() {
        final Dialog alert_dialog = new Dialog(this);
        alert_dialog.requestWindowFeature(1);
        alert_dialog.setContentView(R.layout.alert_dialog);
        ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_forget_watch));
        ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(getString(R.string.ok));
        alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.8
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                alert_dialog.dismiss();
            }
        });
        alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
        ((TextView) alert_dialog.findViewById(R.id.cancel_button_text)).setText(getString(R.string.yes));
        alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                GlobalPreferences.putConnectedWatchId(null);
                WatchSettingsActivity.this.mBackgroundService.forgetDevice();
                WatchSettingsActivity.this.stopService(new Intent(WatchSettingsActivity.this.mContext, BackgroundService.class));
                WatchSettingsActivity.this.finish();
                alert_dialog.dismiss();
            }
        });
        alert_dialog.show();
    }

    public void onResetWatch() {
        if (checkWatchConnection()) {
            final Dialog alert_dialog = new Dialog(this);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_reset_watch));
            ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(getString(R.string.ok));
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.10
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
            ((TextView) alert_dialog.findViewById(R.id.cancel_button_text)).setText(getString(R.string.yes));
            alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WatchSettingsActivity.11
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    WatchSettingsActivity.this.mBackgroundService.reset();
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.show();
        }
    }
}
