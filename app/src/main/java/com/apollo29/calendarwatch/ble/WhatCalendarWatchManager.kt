package com.apollo29.calendarwatch.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.apollo29.calendarwatch.BuildConfig
import com.apollo29.calendarwatch.model.BatteryInfo
import com.apollo29.calendarwatch.model.DTOEvent
import com.apollo29.calendarwatch.model.DTOResponse
import com.apollo29.calendarwatch.repository.CalendarLoader
import com.apollo29.calendarwatch.repository.Preferences
import com.orhanobut.logger.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import no.nordicsemi.android.ble.ConnectRequest
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.livedata.ObservableBleManager
import org.apache.commons.lang3.time.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.experimental.or

@Singleton
class WhatCalendarWatchManager @Inject constructor(
    @ApplicationContext context: Context,
    val calendarLoader: CalendarLoader
) :
    ObservableBleManager(context) {

    val batteryLevel = MutableLiveData<BatteryInfo>()
    val calibrateState = MutableLiveData<Boolean>()
    val updating = MutableLiveData<Boolean>()

    var forgetDevice = true

    private var device: BluetoothDevice? = null
    private var connectRequest: ConnectRequest? = null

    private val bluetoothManager = context.bluetoothAdapter()
    private val preferences = Preferences(context)

    private var batteryLevelCharacteristic: BluetoothGattCharacteristic? = null
    private var calibrateCharacteristic: BluetoothGattCharacteristic? = null
    private var updateTimeCharacteristic: BluetoothGattCharacteristic? = null
    private var flexibleModeCharacteristic: BluetoothGattCharacteristic? = null
    private var airplaneModeCharacteristic: BluetoothGattCharacteristic? = null
    private var acknowledgmentCharacteristic: BluetoothGattCharacteristic? = null
    private var clearCharacteristic: BluetoothGattCharacteristic? = null
    private var alertsCharacteristic: BluetoothGattCharacteristic? = null
    private var patternCurrentCharacteristic: BluetoothGattCharacteristic? = null
    private var patternTomorrowCharacteristic: BluetoothGattCharacteristic? = null
    private var patternDayAfterTomorrowCharacteristic: BluetoothGattCharacteristic? = null

    private var allDaysPattern = CharArray(288)
    private var currentDayPattern = CharArray(96)
    private var tomorrowPattern = CharArray(96)
    private var dayAfterTomorrowPattern = CharArray(96)
    private var alerts = ArrayList<Byte>()
    private val updatePatternsTask = UpdatePatternsTask()
    private var onTimeChanged = false

    var eventsChangedCallback: EventsChangedCallback = object : EventsChangedCallback() {
        override fun onEventsChanged() {
            updateAllDayPatterns()
        }

        override fun onTimeChanged() {
            onTimeChanged = true
            updateAllDayPatterns()
        }
    }

    override fun log(priority: Int, message: String) {
        if (BuildConfig.DEBUG || priority == Log.ERROR) {
            Logger.log(priority, "WCWM", message, null)
        }
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return WhatCalendarWatchManagerGattCallback()
    }

    private val batteryLevelCallback: BatteryLevelDataCallback =
        object : BatteryLevelDataCallback() {
            override fun onInvalidDataReceived(
                device: BluetoothDevice,
                data: Data
            ) {
                log(Log.WARN, "Invalid data received: $data")
            }

            override fun onStateChanged(device: BluetoothDevice, batteryInfo: BatteryInfo) {
                log(Log.INFO, "data received: $batteryInfo")
                batteryLevel.value = batteryInfo
            }
        }

    // CONNECT/PAIR

    fun connectWatch(device: BluetoothDevice) {
        // Prevent from calling again when called again (screen orientation changed).
        if (this.device == null) {
            this.device = device
            reconnect()
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    @SuppressLint("MissingPermission")
    private fun reconnect() {
        if (device != null) {
            connectRequest = connect(device!!)
                .retry(3, 100)
                .useAutoConnect(false)
                .then {
                    preferences.watchId(it.name)
                    connectRequest = null
                }
            connectRequest!!.enqueue()
        }
    }

    // CALIBRATE

    fun calibrateStart() {
        Logger.d("calibrateStart")
        calibrateState.postValue(true)
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(1),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun calibrateCancel() {
        Logger.d("calibrateCancel")
        calibrateState.postValue(false)
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(0),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun calibrateWatch(hour: Int, minute: Int, minuteA: Int, second: Int, secondA: Int) {
        Logger.d("calibrateWatch")
        writeCharacteristic(
            calibrateCharacteristic,
            byteArrayOf(
                hour.toByte(),
                minute.toByte(),
                minuteA.toByte(),
                second.toByte(),
                secondA.toByte()
            ),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()

        updateTime()
    }

    private fun updateTime() {
        Logger.d("updateTime")
        onTimeChanged = false
        val now = Calendar.getInstance()
        val value = byteArrayOf(
            (now[1] % 100).toByte(),
            (now[2] + 1).toByte(),
            now[5].toByte(),
            now[11].toByte(),
            now[12].toByte(),
            now[13].toByte(),
            0,
            0
        )
        writeCharacteristic(
            updateTimeCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    // endregion

    // Sync

    fun manualSync() {
        reset()
        allDaysPattern[100] = 'a'
        updateAllDayPatterns()
    }

    fun updateAllDayPatterns() {
        updatePatternTask()
    }

    fun updateCurrentDayPattern() {
        val value = updatePattern(
            Calendar.getInstance()[5],
            currentDayPattern
        )
        writeCharacteristic(
            patternCurrentCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun updateTomorrowPattern() {
        val c = Calendar.getInstance()
        c[5] = c[5] + 1
        val value = updatePattern(
            c[5],
            tomorrowPattern
        )
        writeCharacteristic(
            patternTomorrowCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun updateDayAfterTomorrowPattern() {
        val c = Calendar.getInstance()
        c[5] = c[5] + 2
        val value = updatePattern(
            c[5],
            dayAfterTomorrowPattern
        )
        writeCharacteristic(
            patternDayAfterTomorrowCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun acknowledgment() {
        Logger.d("acknowledgment")
        writeCharacteristic(
            acknowledgmentCharacteristic,
            byteArrayOf(1),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    private fun updatePattern(day: Int, pattern: CharArray): ByteArray {
        val value = ByteArray(20)
        value[0] = (pattern[0] - '0').toByte()
        value[0] = (value[0] shl 6).toByte()
        value[0] = (value[0] or day.toByte())
        var i = 1
        while (i < 96) {
            var b: Byte = 0
            for (c in 0..4) {
                val v = pattern[i + c] - '0'
                b = (b + Math.round(Math.pow(3.0, c.toDouble()) * v)).toByte()
            }
            value[i / 5 + 1] = b
            i += 5
        }
        return value
    }

    fun clear(type: Int) {
        Logger.d("acknowledgment")
        writeCharacteristic(
            clearCharacteristic,
            byteArrayOf(type.toByte()),
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    private fun updatePatternTask() {
        Thread(updatePatternsTask).start()
    }

    // endregion

    // Flexible Mode

    fun setFlexibleMode() {
        val value = byteArrayOf(0)
        Logger.d("flexibleMode")
        writeCharacteristic(
            flexibleModeCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun setFixedMode(fixedModeHour: Int) {
        val value = byteArrayOf(1, fixedModeHour.toByte())
        Logger.d("flexibleMode %s", fixedModeHour)
        writeCharacteristic(
            flexibleModeCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    // endregion

    // Airplane Mode

    fun setAirplaneMode(airplane: Boolean) {
        var b: Byte = 1
        val value = ByteArray(1)
        if (!airplane) {
            b = 0
        }
        value[0] = b
        Logger.d("airplaneMode %s", airplane)
        writeCharacteristic(
            airplaneModeCharacteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    // endregion

    // Reset

    fun reset() {
        Arrays.fill(allDaysPattern, '0')
        Arrays.fill(currentDayPattern, '0')
        Arrays.fill(tomorrowPattern, '0')
        Arrays.fill(dayAfterTomorrowPattern, '0')
        alerts = ArrayList<Byte>()
        preferences.pattern(allDaysPattern)
        preferences.alerts(alerts)
    }

    fun forgetDevice() {
        preferences.watchId(null)
        forgetDevice = true
    }

    // endregion

    // Alerts

    fun updateAlerts() {
        Logger.d("updateAlerts")
        var alerts = ByteArray(20)
        for (i in alerts.indices) {
            if (i % 20 == 0) {
                if (i > 0) {
                    writeCharacteristic(
                        alertsCharacteristic,
                        alerts,
                        BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                    ).enqueue()
                }
                alerts = ByteArray(20)
                Arrays.fill(alerts, 0.toByte())
            }
            alerts[i % 20] = alerts[i]
        }
        writeCharacteristic(
            alertsCharacteristic,
            alerts,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).enqueue()
    }

    fun toHex(value: ByteArray): String {
        var hexValue: String
        val sb = StringBuilder()
        for (b in value) {
            val hex = Integer.toHexString(b.toInt())
            hexValue = when (hex.length) {
                0 -> "00"
                1 -> "0$hex"
                2 -> hex
                else -> hex.substring(hex.length - 2, hex.length)
            }
            sb.append(hexValue.uppercase(Locale.getDefault())).append(' ')
        }
        return sb.toString()
    }

    // endregion

    // util

    fun isBluetoothEnabled(): Boolean {
        return bluetoothManager?.isEnabled ?: false
    }

    fun isWatchConnected(): Boolean {
        return isBluetoothEnabled() && device != null
    }

    private fun Context.bluetoothAdapter(): BluetoothAdapter? =
        (this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    /**
     * BluetoothGatt callbacks object.
     */
    private inner class WhatCalendarWatchManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            setNotificationCallback(batteryLevelCharacteristic).with(batteryLevelCallback)
            readCharacteristic(batteryLevelCharacteristic).with(batteryLevelCallback).enqueue()
            enableNotifications(batteryLevelCharacteristic).enqueue()
        }

        public override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            // default service
            val service = gatt.getService(UUID_SERVICE)
            if (service != null) {
                batteryLevelCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_BATTERY_LEVEL)
                calibrateCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_CALIBRATE)
                updateTimeCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_UPDATE_TIME)
                flexibleModeCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_SWITCH_MODE)
                airplaneModeCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_AIRPLANE_MODE)
                acknowledgmentCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_ACKNOWLEDGMENT)
                clearCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_CLEAR)
                alertsCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_ALERTS)

                patternCurrentCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_PATTERN_CURRENT_DAY)
                patternTomorrowCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_PATTERN_TOMORROW)
                patternDayAfterTomorrowCharacteristic =
                    service.getCharacteristic(UUID_CHARACTERISTIC_PATTERN_DAY_AFTER_TOMORROW)
            }
            return batteryLevelCharacteristic != null &&
                    calibrateCharacteristic != null &&
                    updateTimeCharacteristic != null &&
                    flexibleModeCharacteristic != null &&
                    airplaneModeCharacteristic != null &&
                    acknowledgmentCharacteristic != null &&
                    clearCharacteristic != null &&
                    alertsCharacteristic != null &&
                    patternCurrentCharacteristic != null &&
                    patternTomorrowCharacteristic != null &&
                    patternDayAfterTomorrowCharacteristic != null
        }

        override fun onServicesInvalidated() {
            batteryLevelCharacteristic = null
        }
    }

    private inner class UpdatePatternsTask : Runnable {
        override fun run() {
            updating.postValue(true)
            var eventStartMinute: Int
            var eventStartSector: Int
            var eventEndSector: Int
            val response: DTOResponse<List<DTOEvent>> = calendarLoader.loadCalendarEvents()
            var needToUpdate = false
            val mAllDaysNewPattern = CharArray(288)
            val mNewAlerts = ArrayList<Byte>()
            val currentDay = Calendar.getInstance()
            val tomorrow = Calendar.getInstance()
            val dayAfterTomorrow = Calendar.getInstance()
            currentDay[11] = 0
            currentDay[12] = 0
            currentDay[13] = 0
            currentDay[14] = 0
            tomorrow.timeInMillis = currentDay.timeInMillis + DateUtils.MILLIS_PER_DAY
            dayAfterTomorrow.timeInMillis = tomorrow.timeInMillis + DateUtils.MILLIS_PER_DAY
            Arrays.fill(mAllDaysNewPattern, '0')
            Arrays.fill(currentDayPattern, '0')
            Arrays.fill(tomorrowPattern, '0')
            Arrays.fill(dayAfterTomorrowPattern, '0')
            mNewAlerts.add(0.toByte())
            mNewAlerts.add(java.lang.Byte.valueOf((currentDay[1] % 100).toByte()))
            mNewAlerts.add(java.lang.Byte.valueOf((currentDay[2] + 1).toByte()))
            mNewAlerts.add(java.lang.Byte.valueOf(currentDay[5].toByte()))
            var alertIndex = 4
            val sdf = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault())
            val it: Iterator<*> = (response.data as ArrayList<*>?)!!.iterator()
            while (it.hasNext()) {
                val dtoEvent = it.next() as DTOEvent
                if (dtoEvent.endDate >= dtoEvent.startDate) {
                    val eventStartDate = Calendar.getInstance()
                    val eventEndDate = Calendar.getInstance()
                    eventStartDate.timeInMillis = dtoEvent.startDate
                    eventEndDate.timeInMillis = dtoEvent.endDate
                    Logger.d(
                        "event: " + dtoEvent.title + " s: " + sdf.format(eventStartDate.time)
                            .toString() + " e: " + sdf.format(eventEndDate.time)
                    )
                    if (eventStartDate.timeInMillis <= currentDay.timeInMillis) {
                        eventStartSector = 0
                        eventStartMinute = 0
                    } else {
                        eventStartMinute = eventStartDate[11] * 60 + eventStartDate[12]
                        eventStartSector = eventStartMinute / 15
                        if (eventStartDate[6] == tomorrow[6]) {
                            eventStartSector += 96
                            eventStartMinute += 1440
                        }
                        if (eventStartDate[6] == dayAfterTomorrow[6]) {
                            eventStartSector += 192
                            eventStartMinute += 2880
                        }
                    }
                    if (eventEndDate.timeInMillis >= dayAfterTomorrow.timeInMillis + DateUtils.MILLIS_PER_DAY) {
                        eventEndSector = mAllDaysNewPattern.size
                    } else {
                        val eventEndMinute = eventEndDate[11] * 60 + eventEndDate[12]
                        eventEndSector = eventEndMinute / 15
                        if (eventEndMinute % 15 > 0) {
                            eventEndSector++
                        }
                        if (eventEndDate[6] == tomorrow[6]) {
                            eventEndSector += 96
                        }
                        if (eventEndDate[6] == dayAfterTomorrow[6]) {
                            eventEndSector += 192
                        }
                    }
                    Logger.d(
                        "event: " + dtoEvent.title + " s sec: " + eventStartSector.toString() + " e sec: " + eventEndSector
                    )
                    Arrays.fill(mAllDaysNewPattern, eventStartSector, eventEndSector, '1')
                    currentDayPattern =
                        Arrays.copyOfRange(mAllDaysNewPattern, 0, 96)
                    tomorrowPattern =
                        Arrays.copyOfRange(mAllDaysNewPattern, 96, 192)
                    dayAfterTomorrowPattern =
                        Arrays.copyOfRange(mAllDaysNewPattern, 192, 288)
                    val alertStartSector = eventStartSector
                    val alertEndSector = eventEndSector
                    if (preferences.vibrateSwitch()) {
                        for ((_, _, minutes) in dtoEvent.alertsList) {
                            if (alertIndex % 20 == 0) {
                                mNewAlerts.add(0.toByte())
                                mNewAlerts.add(java.lang.Byte.valueOf((currentDay[1] % 100).toByte()))
                                mNewAlerts.add(java.lang.Byte.valueOf((currentDay[2] + 1).toByte()))
                                mNewAlerts.add(java.lang.Byte.valueOf(currentDay[5].toByte()))
                            }
                            var alertStartMinute = eventStartMinute - minutes
                            if (alertStartMinute < 0) {
                                alertStartMinute = 0
                            }
                            mNewAlerts.add(
                                alertIndex,
                                java.lang.Byte.valueOf((alertStartMinute shr 8).toByte())
                            )
                            mNewAlerts.add(
                                alertIndex + 1,
                                java.lang.Byte.valueOf((alertStartMinute and 255).toByte())
                            )
                            mNewAlerts.add(
                                alertIndex + 2,
                                java.lang.Byte.valueOf(alertStartSector.toByte())
                            )
                            mNewAlerts.add(
                                alertIndex + 3,
                                java.lang.Byte.valueOf(alertEndSector.toByte())
                            )
                            alertIndex += 4
                        }
                    }
                }
            }
            if (!mAllDaysNewPattern.contentEquals(allDaysPattern)) {
                needToUpdate = true
            }
            if (alerts.size != mNewAlerts.size) {
                needToUpdate = true
            }
            var i = 4
            while (i < alerts.size && !needToUpdate) {
                if (i % 20 == 0) {
                    i += 4
                }
                if (i < mNewAlerts.size && i < alerts.size && mNewAlerts[i] != alerts[i]) {
                    needToUpdate = true
                }
                i++
            }
            if (onTimeChanged) {
                needToUpdate = true
            }
            if (needToUpdate) {
                allDaysPattern = mAllDaysNewPattern
                alerts = mNewAlerts
                preferences.pattern(allDaysPattern)
                preferences.alerts(alerts)
                Logger.d(
                    "current day pattern: " + Arrays.toString(currentDayPattern)
                )
                Logger.d(
                    "tomorrow pattern: " + Arrays.toString(tomorrowPattern)
                )
                Logger.d(
                    "day after tomorrow pattern: " + Arrays.toString(dayAfterTomorrowPattern)
                )
                Logger.d(
                    "alerts: " + alerts.toString()
                )
                updateTime()
                updateCurrentDayPattern()
                updateTomorrowPattern()
                updateDayAfterTomorrowPattern()
                clear(2)
                updateAlerts()
                acknowledgment()
                return
            }
            Logger.d(
                "No changes in patterns and alerts"
            )
            updateTime()
        }
    }

    abstract class EventsChangedCallback {
        abstract fun onEventsChanged()
        abstract fun onTimeChanged()
    }

    infix fun Byte.shl(that: Int): Int = this.toInt().shl(that)

    companion object {
        // DEFAULT SERVICE
        val UUID_SERVICE =
            UUID.fromString("67E40001-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_BATTERY_LEVEL =
            UUID.fromString("67E4000D-5C68-D803-BF31-F83F2B6585FA")

        val UUID_CHARACTERISTIC_CALIBRATE =
            UUID.fromString("67E40009-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_UPDATE_TIME =
            UUID.fromString("67E40002-5C68-D803-BF31-F83F2B6585FA")

        val UUID_CHARACTERISTIC_SWITCH_MODE =
            UUID.fromString("67E40008-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_AIRPLANE_MODE =
            UUID.fromString("67E40010-5C68-D803-BF31-F83F2B6585FA")

        val UUID_CHARACTERISTIC_PATTERN_CURRENT_DAY =
            UUID.fromString("67E40003-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_PATTERN_TOMORROW =
            UUID.fromString("67E40004-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_PATTERN_DAY_AFTER_TOMORROW =
            UUID.fromString("67E40005-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_CLEAR =
            UUID.fromString("67E40006-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_ALERTS =
            UUID.fromString("67E40007-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_ACKNOWLEDGMENT =
            UUID.fromString("67E4000A-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_UPDATE_REQUEST =
            UUID.fromString("67E4000C-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_TOTAL_ALERTS_COUNT =
            UUID.fromString("67E4000E-5C68-D803-BF31-F83F2B6585FA")
        val UUID_CHARACTERISTIC_REFRESH =
            UUID.fromString("67E4000F-5C68-D803-BF31-F83F2B6585FA")
    }
}