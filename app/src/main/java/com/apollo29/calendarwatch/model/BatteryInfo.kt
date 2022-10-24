package com.apollo29.calendarwatch.model

import java.io.Serializable

class BatteryInfo(val chargingStatus: Int, val level: Int) : Serializable {

    override fun toString(): String {
        return "BatteryInfo(chargingStatus=$chargingStatus, level=$level)"
    }

    companion object {
        const val CHARGING = 1
        const val FULL_CHARGE = 2
        const val NOT_CHARGING = 0
    }
}