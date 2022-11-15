package com.apollo29.calendarwatch.ui.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollo29.calendarwatch.ble.WhatCalendarWatchManager
import com.apollo29.calendarwatch.repository.Preferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsDialogViewModel @Inject constructor(@ApplicationContext context: Context) :
    ViewModel() {
    val manager = WhatCalendarWatchManager(context)
    val preferences = Preferences(context)

    val switchModeValue = MutableLiveData(preferences.switchModeSwitch())

    fun switchMode(value: Boolean, hour: Int?) {
        switchModeValue.postValue(value)
        preferences.switchModeSwitch(value)
        if (value && hour != null) {
            preferences.fixedModeValue(hour)
            manager.setFixedMode(hour)
        } else {
            manager.setFlexibleMode()
        }
    }

    fun fixedModeValue(): Int {
        return preferences.fixedModeValue()
    }

    fun airplaneMode(value: Boolean) {
        preferences.flightModeSwitch(value)
        manager.setAirplaneMode(value)
    }

    fun airplaneModeValue(): Boolean {
        return preferences.flightModeSwitch()
    }

    fun reset() {
        manager.reset()
    }

    fun unpair() {
        manager.forgetDevice()
    }

    /*
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
     */
}