package com.whatcalendar.firmware;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.whatcalendar.R;
import com.whatcalendar.activity.InfoActivity;
import com.whatcalendar.activity.WelcomeScreenActivity;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;
import java.lang.reflect.Field;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public abstract class UpdateBroadcastReceiver extends BroadcastReceiver {
    private BackgroundService mBackgroundService;
    private Context mContext;
    Dialog progressDialog;
    TextView progressText;
    private boolean done = false;
    private BroadcastReceiver updateBroadcastReceiver = this;

    public abstract void callback(String str);

    public UpdateBroadcastReceiver(Context context, BackgroundService service) {
        this.mContext = context;
        this.mBackgroundService = service;
    }

    private String getString(int id) {
        return this.mContext.getString(id);
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context11, Intent intent) {
        if (!((Activity) this.mContext).isDestroyed()) {
            callback(intent.getAction());
            if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_NEED)) {
                final Dialog alert_dialog = new Dialog(this.mContext);
                alert_dialog.requestWindowFeature(1);
                alert_dialog.setContentView(R.layout.alert_dialog);
                ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_update_can_be));
                ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(getString(R.string.f0no));
                alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        LocalBroadcastManager.getInstance(UpdateBroadcastReceiver.this.mContext).unregisterReceiver(UpdateBroadcastReceiver.this.updateBroadcastReceiver);
                        alert_dialog.dismiss();
                    }
                });
                alert_dialog.findViewById(R.id.cancel_button).setVisibility(0);
                ((TextView) alert_dialog.findViewById(R.id.cancel_button_text)).setText(getString(R.string.yes));
                alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.2
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        alert_dialog.dismiss();
                        if (!UpdateBroadcastReceiver.this.checkBatteryLevel() || !UpdateBroadcastReceiver.this.isOnline()) {
                            LocalBroadcastManager.getInstance(UpdateBroadcastReceiver.this.mContext).unregisterReceiver(UpdateBroadcastReceiver.this.updateBroadcastReceiver);
                        } else {
                            UpdateBroadcastReceiver.this.mBackgroundService.updateFirmWare();
                        }
                    }
                });
                alert_dialog.setCancelable(false);
                alert_dialog.show();
            } else if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_NO)) {
                LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.updateBroadcastReceiver);
                if (this.mContext instanceof InfoActivity) {
                    final Dialog alert_dialog2 = new Dialog(this.mContext);
                    alert_dialog2.requestWindowFeature(1);
                    alert_dialog2.setContentView(R.layout.alert_dialog);
                    ((TextView) alert_dialog2.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_update_no));
                    ((TextView) alert_dialog2.findViewById(R.id.ok_button_text)).setText(getString(R.string.close));
                    alert_dialog2.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.3
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            alert_dialog2.dismiss();
                        }
                    });
                    alert_dialog2.findViewById(R.id.cancel_button).setVisibility(8);
                    alert_dialog2.setCancelable(false);
                    alert_dialog2.show();
                }
            } else if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_START)) {
                Dialog alert_dialog3 = new Dialog(this.mContext);
                alert_dialog3.requestWindowFeature(1);
                alert_dialog3.setContentView(R.layout.alert_dialog);
                alert_dialog3.setCancelable(false);
                this.progressText = (TextView) alert_dialog3.findViewById(R.id.alert_dialog_text);
                ((TextView) alert_dialog3.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_update_progress) + StringUtils.LF + "0 %");
                alert_dialog3.findViewById(R.id.alert_dialog_buttons_layout).setVisibility(8);
                alert_dialog3.findViewById(R.id.ok_button).setVisibility(8);
                alert_dialog3.findViewById(R.id.cancel_button).setVisibility(8);
                alert_dialog3.show();
                this.progressDialog = alert_dialog3;
            } else if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_PROGRESS)) {
                if (this.progressText != null) {
                    int progress = intent.getIntExtra("progress", 0);
                    this.progressText.setText(getString(R.string.dialog_msg_update_progress) + StringUtils.LF + progress + " %");
                }
            } else if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_DONE)) {
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                    this.progressText = null;
                }
                this.done = true;
                final Dialog alert_dialog4 = new Dialog(this.mContext);
                this.progressDialog = alert_dialog4;
                alert_dialog4.requestWindowFeature(1);
                alert_dialog4.setContentView(R.layout.alert_dialog);
                ((TextView) alert_dialog4.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_update_done));
                ((TextView) alert_dialog4.findViewById(R.id.ok_button_text)).setText(getString(R.string.ok));
                alert_dialog4.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.4
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        alert_dialog4.dismiss();
                    }
                });
                alert_dialog4.findViewById(R.id.cancel_button).setVisibility(8);
                alert_dialog4.setCancelable(false);
                alert_dialog4.show();
            } else if (intent.getAction().equals(BackgroundService.ACTION_BATTERY_INFO) && this.done) {
                LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.updateBroadcastReceiver);
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                }
                onCalibrateWatch();
            } else if (intent.getAction().equals(BackgroundService.ACTION_FIRMWARE_UPDATE_ERROR)) {
                LocalBroadcastManager.getInstance(this.mContext).unregisterReceiver(this.updateBroadcastReceiver);
                if (this.progressDialog != null) {
                    this.progressDialog.dismiss();
                    this.progressText = null;
                }
                final Dialog alert_dialog5 = new Dialog(this.mContext);
                alert_dialog5.requestWindowFeature(1);
                alert_dialog5.setContentView(R.layout.alert_dialog);
                ((TextView) alert_dialog5.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_update_error));
                ((TextView) alert_dialog5.findViewById(R.id.ok_button_text)).setText(getString(R.string.close));
                alert_dialog5.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.5
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        alert_dialog5.dismiss();
                    }
                });
                alert_dialog5.findViewById(R.id.cancel_button).setVisibility(8);
                alert_dialog5.setCancelable(false);
                alert_dialog5.show();
            }
        }
    }

    boolean checkBatteryLevel() {
        if (GlobalPreferences.getBatteryInfo().getLevel() >= 70) {
            return true;
        }
        final Dialog alert_dialog = new Dialog(this.mContext);
        alert_dialog.requestWindowFeature(1);
        alert_dialog.setContentView(R.layout.alert_dialog);
        ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_low_battery));
        ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(getString(R.string.close));
        alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.6
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                alert_dialog.dismiss();
            }
        });
        alert_dialog.findViewById(R.id.cancel_button).setVisibility(8);
        alert_dialog.show();
        return false;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!result) {
            final Dialog alert_dialog = new Dialog(this.mContext);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.internet_connection_alert));
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.7
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.show();
        }
        return result;
    }

    void onCalibrateWatch() {
        if (!(this.mContext instanceof WelcomeScreenActivity)) {
            this.mBackgroundService.calibrateStart();
            View view = ((Activity) this.mContext).getLayoutInflater().inflate(R.layout.view_calibrate, (ViewGroup) null);
            Calendar now = Calendar.getInstance();
            final NumberPicker pickerHour = (NumberPicker) ButterKnife.findById(view, (int) R.id.picker_hour);
            setNumberPickerTextColor(pickerHour, ((Activity) this.mContext).getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerHour, Color.parseColor("#00000000"));
            pickerHour.setMinValue(0);
            pickerHour.setMaxValue(11);
            pickerHour.setValue(0);
            pickerHour.setValue(now.get(11));
            final NumberPicker pickerMinute = (NumberPicker) ButterKnife.findById(view, (int) R.id.picker_minute);
            setNumberPickerTextColor(pickerMinute, ((Activity) this.mContext).getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerMinute, Color.parseColor("#00000000"));
            pickerMinute.setMinValue(0);
            pickerMinute.setMaxValue(59);
            pickerMinute.setValue(now.get(12));
            final NumberPicker pickerSecond = (NumberPicker) ButterKnife.findById(view, (int) R.id.picker_second);
            setNumberPickerTextColor(pickerSecond, ((Activity) this.mContext).getResources().getColor(R.color.main_text_color));
            setDividerColor(pickerSecond, Color.parseColor("#00000000"));
            pickerSecond.setMinValue(0);
            pickerSecond.setMaxValue(59);
            final Dialog alert_dialog = new Dialog(this.mContext);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            alert_dialog.findViewById(R.id.alert_dialog_title).setVisibility(8);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setVisibility(8);
            ((LinearLayout) alert_dialog.findViewById(R.id.content)).addView(view);
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.8
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    UpdateBroadcastReceiver.this.mBackgroundService.calibrateWatch(pickerHour.getValue(), pickerMinute.getValue(), 0, pickerSecond.getValue(), 0);
                    UpdateBroadcastReceiver.this.mBackgroundService.setTime();
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.findViewById(R.id.cancel_button).setVisibility(0);
            alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.firmware.UpdateBroadcastReceiver.9
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    UpdateBroadcastReceiver.this.mBackgroundService.calibrateCancel();
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
                    Field selectorWheelPaintField = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (IllegalAccessException e) {
                    Log.w("setNumberPickerTextColor", e);
                } catch (IllegalArgumentException e2) {
                    Log.w("setNumberPickerTextColor", e2);
                } catch (NoSuchFieldException e3) {
                    Log.w("setNumberPickerTextColor", e3);
                }
            }
        }
        return false;
    }

    private void setDividerColor(NumberPicker picker, int color) {
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
}
