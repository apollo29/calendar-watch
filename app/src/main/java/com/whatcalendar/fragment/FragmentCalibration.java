package com.whatcalendar.fragment;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.whatcalendar.R;

import java.lang.reflect.Field;
import java.util.Calendar;

/* loaded from: classes.dex */
public class FragmentCalibration extends Fragment {
    NumberPicker pickerHour;
    NumberPicker pickerMinute;
    NumberPicker pickerSecond;

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibration, (ViewGroup) null);
        Calendar now = Calendar.getInstance();
        this.pickerHour = (NumberPicker) view.findViewById(R.id.picker_hour);
        setNumberPickerTextColor(this.pickerHour, getResources().getColor(R.color.main_text_color));
        setDividerColor(this.pickerHour, Color.parseColor("#00000000"));
        this.pickerHour.setMinValue(0);
        this.pickerHour.setMaxValue(11);
        this.pickerHour.setValue(0);
        this.pickerHour.setValue(now.get(11));
        this.pickerMinute = (NumberPicker) view.findViewById(R.id.picker_minute);
        setNumberPickerTextColor(this.pickerMinute, getResources().getColor(R.color.main_text_color));
        setDividerColor(this.pickerMinute, Color.parseColor("#00000000"));
        this.pickerMinute.setMinValue(0);
        this.pickerMinute.setMaxValue(59);
        this.pickerMinute.setValue(now.get(12));
        this.pickerSecond = (NumberPicker) view.findViewById(R.id.picker_second);
        setNumberPickerTextColor(this.pickerSecond, getResources().getColor(R.color.main_text_color));
        setDividerColor(this.pickerSecond, Color.parseColor("#00000000"));
        this.pickerSecond.setMinValue(0);
        this.pickerSecond.setMaxValue(59);
        return view;
    }

    public int[] getPuckerValue() {
        return new int[]{this.pickerHour.getValue(), this.pickerMinute.getValue(), this.pickerSecond.getValue()};
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

    private void setDividerColor(NumberPicker picker, int color) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                    return;
                } catch (Resources.NotFoundException | IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
