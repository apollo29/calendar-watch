package com.whatcalendar.entity;

import java.io.Serializable;

/* loaded from: classes.dex */
public class BatteryInfo implements Serializable {
    public static final int CHARGING = 1;
    public static final int FULL_CHARGE = 2;
    public static final int NOT_CHARGING = 0;
    private int mChargingStatus;
    private int mLevel;

    public BatteryInfo(int chargingStatus, int level) {
        this.mChargingStatus = chargingStatus;
        this.mLevel = level;
    }

    public int getChargingStatus() {
        return this.mChargingStatus;
    }

    public int getLevel() {
        return this.mLevel;
    }
}
