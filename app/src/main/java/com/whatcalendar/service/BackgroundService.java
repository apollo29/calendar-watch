package com.whatcalendar.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.view.InputDeviceCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.whatcalendar.R;
import com.whatcalendar.entity.BatteryInfo;
import com.whatcalendar.events.CalendarEventsReceiver;
import com.whatcalendar.events.CalendarLoader;
import com.whatcalendar.events.DTOAlert;
import com.whatcalendar.events.DTOEvent;
import com.whatcalendar.events.DTOResponse;
import com.whatcalendar.events.TimeSettingsReceiver;
import com.whatcalendar.firmware.DfuService;
import com.whatcalendar.firmware.GWatchResponse;
import com.whatcalendar.firmware.UpdateScheme;
import com.whatcalendar.util.FileWritter;
import com.whatcalendar.util.GlobalPreferences;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;
import no.nordicsemi.android.dfu.internal.scanner.BootloaderScanner;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressLint("MissingPermission")
public class BackgroundService extends Service {
    public static final String ACTION_BATTERY_INFO = "action.battery_level";
    public static final String ACTION_CONNECTION_STATE_CHANGE = "action.ConnectionStateChange";
    public static final String ACTION_FIRMWARE_UPDATE_DONE = "action.firmware.update.done";
    public static final String ACTION_FIRMWARE_UPDATE_ERROR = "action.firmware.update.error";
    public static final String ACTION_FIRMWARE_UPDATE_NEED = "action.firmware.update.need";
    public static final String ACTION_FIRMWARE_UPDATE_NO = "action.firmware.update.no";
    public static final String ACTION_FIRMWARE_UPDATE_PROGRESS = "action.firmware.update.progress";
    public static final String ACTION_FIRMWARE_UPDATE_START = "action.firmware.update.start";
    private static final int ALERTS_PACKET_SIZE = 20;
    private static final int MAX_SCAN_TIME = 15000;
    private static final int RSSI_VALUE = -70;
    private static final int SECTORS_COUNT = 96;
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice;
    private String mDeviceId;
    private boolean mFoundDevice;
    private BluetoothGatt mGatt;
    private Timer mTimer;
    private boolean mUpdating;
    private StopScanTimerTask stopScanTimerTask;
    private static final String TAG = BackgroundService.class.getSimpleName();
    private static final UUID UUID_DEVICE_INFORMATION_SERVICE = UUID.fromString("0000180A-0000-1000-8000-00805F9B34FB");
    private static final UUID UUID_CHARACTERISTIC_FIRMWARE = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SERVICE = UUID.fromString("67E40001-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_UPDATE_TIME = UUID.fromString("67E40002-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_PATTERN_CURRENT_DAY = UUID.fromString("67E40003-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_PATTERN_TOMORROW = UUID.fromString("67E40004-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_PATTERN_DAY_AFTER_TOMORROW = UUID.fromString("67E40005-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_CLEAR = UUID.fromString("67E40006-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_ALERTS = UUID.fromString("67E40007-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_SWITCH_MODE = UUID.fromString("67E40008-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_CALIBRATE = UUID.fromString("67E40009-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_ACKNOWLEDGMENT = UUID.fromString("67E4000A-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_UPDATE_REQUEST = UUID.fromString("67E4000C-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_BATTERY_LEVEL = UUID.fromString("67E4000D-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_TOTAL_ALERTS_COUNT = UUID.fromString("67E4000E-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_REFRESH = UUID.fromString("67E4000F-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_CHARACTERISTIC_AIRPLANE_MODE = UUID.fromString("67E40010-5C68-D803-BF31-F83F2B6585FA");
    private static final UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private final IBinder mBinder = new LocalBinder();
    private final UpdatePatternsTask mUpdatePatternsTask = new UpdatePatternsTask();
    private TreeMap<Integer, BluetoothDevice> mDeviceList = new TreeMap<>();
    private boolean forgetDevice = true;
    private char[] mAllDaysPattern = new char[288];
    private char[] mCurrentDayPattern = new char[96];
    private char[] mTomorrowPattern = new char[96];
    private char[] mDayAfterTomorrowPattern = new char[96];
    ArrayList<Byte> mAlerts = new ArrayList<>();
    private boolean onTimeChanged = false;
    private ReconnectTask mReconnectTask = new ReconnectTask();
    private BTCharacteristicWriterQueue mBTCharacteristicWriterQueue = new BTCharacteristicWriterQueue();
    public EventsChangedCallback eventsChangedCallback = new EventsChangedCallback() { // from class: com.whatcalendar.service.BackgroundService.1
        @Override // com.whatcalendar.service.BackgroundService.EventsChangedCallback
        public void onEventsChanged() {
            BackgroundService.this.updateAllDayPatterns();
        }

        @Override // com.whatcalendar.service.BackgroundService.EventsChangedCallback
        public void onTimeChanged() {
            BackgroundService.this.onTimeChanged = true;
            BackgroundService.this.updateAllDayPatterns();
        }
    };
    private final DfuProgressListener mDfuProgressListener = new DfuProgressListenerAdapter() { // from class: com.whatcalendar.service.BackgroundService.2
        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onDeviceConnecting(String deviceAddress) {
            Log.d(BackgroundService.TAG, "DFU onDeviceConnecting");
        }

        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onDfuProcessStarting(String deviceAddress) {
            Log.d(BackgroundService.TAG, "DFU onDfuProcessStarting");
            BackgroundService.this.mBTCharacteristicWriterQueue.clearQueue();
            BackgroundService.this.clearPatterns();
            BackgroundService.this.mAllDaysPattern[100] = 'a';
        }

        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onDfuProcessStarted(String deviceAddress) {
            super.onDfuProcessStarted(deviceAddress);
            Log.d(BackgroundService.TAG, "DFU onDfuProcessStarted");
            BackgroundService.this.mBTCharacteristicWriterQueue.clearQueue();
            BackgroundService.this.clearPatterns();
            BackgroundService.this.mAllDaysPattern[100] = 'a';
        }

        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            super.onProgressChanged(deviceAddress, percent, speed, avgSpeed, currentPart, partsTotal);
            Log.d(BackgroundService.TAG, "onProgressChanged : " + deviceAddress + " " + percent + "% speed: " + speed + " avgSpeed: " + avgSpeed + " part: " + currentPart + "/" + partsTotal);
            LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(new Intent(BackgroundService.ACTION_FIRMWARE_UPDATE_PROGRESS).putExtra("progress", percent));
        }

        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onDfuCompleted(String deviceAddress) {
            super.onDfuCompleted(deviceAddress);
            Log.d(BackgroundService.TAG, "DFU onDfuCompleted");
            BackgroundService.this.updateDone();
        }

        @Override
        // no.nordicsemi.android.dfu.DfuProgressListenerAdapter, no.nordicsemi.android.dfu.DfuProgressListener
        public void onError(String deviceAddress, int error, int errorType, String message) {
            super.onError(deviceAddress, error, errorType, message);
            Log.d(BackgroundService.TAG, "DFU error : " + message);
            BackgroundService.this.updateError();
        }

        /* renamed from: com.whatcalendar.service.BackgroundService$2$1  reason: invalid class name */
        /* loaded from: classes.dex */
        class AnonymousClass1 extends TimerTask {
            AnonymousClass1() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                //BackgroundService.access$3500(BackgroundService.this, BackgroundService.access$3100());
            }
        }

        /* renamed from: com.whatcalendar.service.BackgroundService$2$2  reason: invalid class name and collision with other inner class name */
        /* loaded from: classes.dex */
        class C00022 extends TimerTask {

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (GlobalPreferences.getPairingMode()) {
                    GlobalPreferences.setPairingMode(false);
                    //BackgroundService.access$1900(BackgroundService.this, 0);
                    //BackgroundService.access$3600(BackgroundService.this);
                    //BackgroundService.this.updateDone()[100] = 'a';
                    GlobalPreferences.setFlightModeSwitch(false);
                    GlobalPreferences.setVibrateSwitch(true);
                    GlobalPreferences.setAllDayEventsInfo(true);
                    GlobalPreferences.setSwitchModeSwitch(false);
                    BackgroundService.this.setFlexibleMode();
                }
                BackgroundService.this.updateAllDayPatterns();
            }
        }

        /* renamed from: com.whatcalendar.service.BackgroundService$2$3  reason: invalid class name */
        /* loaded from: classes.dex */
        class AnonymousClass3 extends TimerTask {
            AnonymousClass3() {
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                BackgroundService.this.updateError();
            }
        }
    };
    private BluetoothAdapter.LeScanCallback mScanCallback = BackgroundServiceCallback.lambdaFactory$(this);
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() { // from class: com.whatcalendar.service.BackgroundService.5
        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @SuppressLint("MissingPermission")
        @Override // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(BackgroundService.TAG, "connection state change, status: " + status + ", new state: " + newState);
            if (BackgroundService.this.mAdapter == null || !BackgroundService.this.mAdapter.isEnabled()) {
                BackgroundService.this.mBTCharacteristicWriterQueue.clearQueue();
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(new Intent(BackgroundService.ACTION_CONNECTION_STATE_CHANGE).putExtra("state", 10));
            } else {
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(new Intent(BackgroundService.ACTION_CONNECTION_STATE_CHANGE).putExtra("state", newState));
            }
            if (newState == 2) {
                BackgroundService.this.mGatt.discoverServices();
                BackgroundService.this.forgetDevice = false;
            } else if (newState != 1 && newState == 0) {
                BackgroundService.this.mBTCharacteristicWriterQueue.clearQueue();
                if (BackgroundService.this.mGatt != null) {
                    BackgroundService.this.mGatt.close();
                    BackgroundService.this.mGatt = null;
                }
                BackgroundService.this.mUpdating = false;
                if (!BackgroundService.this.forgetDevice) {
                    new Thread(BackgroundService.this.mReconnectTask).start();
                }
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            try {
                super.onDescriptorWrite(gatt, descriptor, status);
                if (descriptor.getCharacteristic().getUuid().toString().equals(BackgroundService.UUID_CHARACTERISTIC_UPDATE_REQUEST.toString())) {
                    BluetoothGattCharacteristic batteryLevel = BackgroundService.this.mGatt.getService(BackgroundService.UUID_SERVICE).getCharacteristic(BackgroundService.UUID_CHARACTERISTIC_BATTERY_LEVEL);
                    BluetoothGattDescriptor batteryLevelDescriptor = batteryLevel.getDescriptor(BackgroundService.UUID_DESCRIPTOR);
                    batteryLevelDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean stat = BackgroundService.this.mGatt.writeDescriptor(batteryLevelDescriptor);
                    Log.d(BackgroundService.TAG, "write descriptor BatteryLevel " + stat);
                    BackgroundService.this.mGatt.setCharacteristicNotification(batteryLevel, true);
                }
                if (descriptor.getCharacteristic().getUuid().toString().equals(BackgroundService.UUID_CHARACTERISTIC_BATTERY_LEVEL.toString())) {
                    BluetoothGattCharacteristic refresh = BackgroundService.this.mGatt.getService(BackgroundService.UUID_SERVICE).getCharacteristic(BackgroundService.UUID_CHARACTERISTIC_REFRESH);
                    BluetoothGattDescriptor refreshDescriptor = refresh.getDescriptor(BackgroundService.UUID_DESCRIPTOR);
                    refreshDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    boolean stat2 = BackgroundService.this.mGatt.writeDescriptor(refreshDescriptor);
                    Log.d(BackgroundService.TAG, "write descriptor Refresh " + stat2);
                    BackgroundService.this.mGatt.setCharacteristicNotification(refresh, true);
                }
            } catch (Exception e) {
                Log.e(BackgroundService.TAG, "onDescriptorWrite watch Exception \n" + e.getMessage());
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            new Timer().schedule(new TimerTask() { // from class: com.whatcalendar.service.BackgroundService.5.1
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    BackgroundService.this.readCharacteristic(BackgroundService.UUID_CHARACTERISTIC_BATTERY_LEVEL);
                }
            }, TimeUnit.SECONDS.toMillis(1L));
            new Timer().schedule(new TimerTask() { // from class: com.whatcalendar.service.BackgroundService.5.2
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    if (GlobalPreferences.getPairingMode()) {
                        GlobalPreferences.setPairingMode(false);
                        BackgroundService.this.clear(0);
                        BackgroundService.this.clearPatterns();
                        BackgroundService.this.mAllDaysPattern[100] = 'a';
                        GlobalPreferences.setFlightModeSwitch(false);
                        GlobalPreferences.setVibrateSwitch(true);
                        GlobalPreferences.setAllDayEventsInfo(true);
                        GlobalPreferences.setSwitchModeSwitch(false);
                        BackgroundService.this.setFlexibleMode();
                    }
                    BackgroundService.this.updateAllDayPatterns();
                }
            }, TimeUnit.SECONDS.toMillis(2L));
            new Timer().schedule(new TimerTask() { // from class: com.whatcalendar.service.BackgroundService.5.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    BackgroundService.this.updateTime();
                }
            }, TimeUnit.SECONDS.toMillis(5L));
            try {
                BluetoothGattCharacteristic updateRequest = BackgroundService.this.mGatt.getService(BackgroundService.UUID_SERVICE).getCharacteristic(BackgroundService.UUID_CHARACTERISTIC_UPDATE_REQUEST);
                BluetoothGattDescriptor descriptor = updateRequest.getDescriptor(BackgroundService.UUID_DESCRIPTOR);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                boolean stat = BackgroundService.this.mGatt.writeDescriptor(descriptor);
                Log.d(BackgroundService.TAG, "write descriptor updateRequest " + stat);
                BackgroundService.this.mGatt.setCharacteristicNotification(updateRequest, true);
            } catch (Exception e) {
                Log.e(BackgroundService.TAG, "onServicesDiscovered watch Exception \n" + e.getMessage());
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(BackgroundService.TAG, "onCharacteristicChanged");
            if (BackgroundService.UUID_CHARACTERISTIC_REFRESH.equals(characteristic.getUuid())) {
                Log.d(BackgroundService.TAG, "Notify REFRESH!");
                if (!BackgroundService.this.mUpdating) {
                    new Thread(BackgroundService.this.mUpdatePatternsTask).start();
                }
            } else if (BackgroundService.UUID_CHARACTERISTIC_UPDATE_REQUEST.equals(characteristic.getUuid())) {
                Log.d(BackgroundService.TAG, "Notify UPDATE REQUEST! command: " + ((int) characteristic.getValue()[0]));
                if (!BackgroundService.this.mUpdating) {
                    switch (characteristic.getValue()[0]) {
                        case 0:
                            BackgroundService.this.clearPatterns();
                            new Thread(BackgroundService.this.mUpdatePatternsTask).start();
                            return;
                        case 1:
                            BackgroundService.this.updateTime();
                            return;
                        default:
                            return;
                    }
                }
            } else if (BackgroundService.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(characteristic.getUuid())) {
                Log.d(BackgroundService.TAG, "Notify BATTERY LEVEL!");
                gatt.readCharacteristic(characteristic);
            }
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(BackgroundService.TAG, " ==> " + characteristic.getUuid().toString() + " >>> Write status: " + status);
            BackgroundService.this.mBTCharacteristicWriterQueue.writeResult(status);
        }

        @Override // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte b = 0;
            String msg = " ==> " + characteristic.getUuid().toString() + " read status: " + status;
            if (status != 0) {
                Log.d(BackgroundService.TAG, msg);
                return;
            }
            byte[] value = characteristic.getValue();
            Log.d(BackgroundService.TAG, msg + "   read value: " + BackgroundService.toHex(value));
            if (BackgroundService.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(characteristic.getUuid())) {
                byte b2 = value[0];
                if (value.length == 2) {
                    b = value[1];
                }
                BatteryInfo bi = new BatteryInfo(b2, b);
                GlobalPreferences.putBatteryInfo(bi);
                LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(new Intent(BackgroundService.ACTION_BATTERY_INFO).putExtra("info", bi));
            }
            if (BackgroundService.UUID_CHARACTERISTIC_FIRMWARE.equals(characteristic.getUuid())) {
                try {
                    String version = characteristic.getStringValue(0);
                    if (!StringUtils.isEmpty(version)) {
                        Log.d(BackgroundService.TAG, "Current firmware version: " + characteristic.getStringValue(0));
                        BackgroundService.this.checkLastVersion(characteristic.getStringValue(0));
                    } else {
                        Log.e(BackgroundService.TAG, "Can't read firmware version from the Watch");
                    }
                } catch (Exception e) {
                    Log.e(BackgroundService.TAG, e.getMessage());
                }
            }
        }
    };

    /* loaded from: classes.dex */
    public static abstract class EventsChangedCallback {
        public abstract void onEventsChanged();

        public abstract void onTimeChanged();
    }

    /* loaded from: classes.dex */
    class ReconnectTask implements Runnable {
        public boolean stopThread = false;

        ReconnectTask() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BackgroundService.this.mGatt == null) {
                this.stopThread = false;
                if (!BackgroundService.this.isBtAvailable()) {
                    LocalBroadcastManager.getInstance(BackgroundService.this).sendBroadcast(new Intent(BackgroundService.ACTION_CONNECTION_STATE_CHANGE).putExtra("state", 10));
                }
                while (!this.stopThread && !BackgroundService.this.isBtAvailable()) {
                    Log.d(BackgroundService.TAG, "Try to restore BT connection");
                    try {
                        Thread.sleep(BootloaderScanner.TIMEOUT);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (BackgroundService.this.isBtAvailable()) {
                    this.stopThread = true;
                    Log.d(BackgroundService.TAG, "BT adapter is available, reconnect");
                    BackgroundService.this.mDevice = null;
                    BackgroundService.this.startScanning();
                }
            }
            Log.d(BackgroundService.TAG, "Stop reconnect Thread");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BTCharacteristicWriterQueue {
        private final String TAG;
        ArrayDeque<CharacteristicValue> mCharacteristicQueue;
        private CheckWriteStatus mCheckWriteStatus;
        private boolean writeCompleted;

        private BTCharacteristicWriterQueue() {
            this.TAG = BTCharacteristicWriterQueue.class.getSimpleName();
            this.mCharacteristicQueue = new ArrayDeque<>();
            this.writeCompleted = false;
            this.mCheckWriteStatus = null;
        }

        public void clearQueue() {
            Log.d(this.TAG, "clearQueue");
            this.writeCompleted = true;
            this.mCheckWriteStatus = null;
            this.mCharacteristicQueue.clear();
            BackgroundService.this.mUpdating = false;
        }

        public void write(UUID uuid, byte[] value) {
            Log.d(this.TAG, "write");
            CharacteristicValue characteristicValue = new CharacteristicValue(uuid, value);
            this.writeCompleted = false;
            if (this.mCheckWriteStatus == null) {
                this.mCheckWriteStatus = new CheckWriteStatus();
                new Thread(this.mCheckWriteStatus).start();
            }
            this.mCharacteristicQueue.addLast(characteristicValue);
            if (this.mCharacteristicQueue.size() < 2) {
                performWrite();
            }
        }

        public void writeResult(int status) {
            Log.d(this.TAG, "writeResult " + status);
            if (status == 0) {
                this.writeCompleted = true;
                this.mCheckWriteStatus = null;
                this.mCharacteristicQueue.pollFirst();
            }
            performWrite();
        }

        private void performWrite() {
            CharacteristicValue value = this.mCharacteristicQueue.peekFirst();
            if (value == null) {
                BackgroundService.this.mUpdating = false;
                return;
            }
            Log.d(this.TAG, "performWrite");
            BackgroundService.this.writeCharacteristic(value.getUuid(), value.getValue());
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class CharacteristicValue {
            private UUID uuid;
            private byte[] value;

            public CharacteristicValue(UUID uuid, byte[] value) {
                this.uuid = uuid;
                this.value = value;
            }

            public UUID getUuid() {
                return this.uuid;
            }

            public byte[] getValue() {
                return this.value;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class CheckWriteStatus implements Runnable {
            private CheckWriteStatus() {
            }

            @Override // java.lang.Runnable
            public void run() {
                while (!BTCharacteristicWriterQueue.this.writeCompleted && BackgroundService.this.mGatt != null) {
                    try {
                        Thread.sleep(1000L);
                        if (!BTCharacteristicWriterQueue.this.writeCompleted) {
                            BTCharacteristicWriterQueue.this.writeResult(InputDeviceCompat.SOURCE_KEYBOARD);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if (BackgroundService.this.mGatt == null) {
                    BTCharacteristicWriterQueue.this.clearQueue();
                }
                Log.d(BTCharacteristicWriterQueue.this.TAG, "stop CheckWriteStatus thread");
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.d(TAG, "onCreate");
        this.mAllDaysPattern = GlobalPreferences.getPattern();
        this.mAlerts = GlobalPreferences.getAlerts();
        this.mCurrentDayPattern = Arrays.copyOfRange(this.mAllDaysPattern, 0, 96);
        this.mTomorrowPattern = Arrays.copyOfRange(this.mAllDaysPattern, 96, 192);
        this.mDayAfterTomorrowPattern = Arrays.copyOfRange(this.mAllDaysPattern, 192, 288);
        this.mAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        CalendarEventsReceiver.setEventsChangedCallback(this.eventsChangedCallback);
        TimeSettingsReceiver.setEventsChangedCallback(this.eventsChangedCallback);
        if (GlobalPreferences.getConnectedWatchId() != null && !GlobalPreferences.getConnectedWatchId().isEmpty()) {
            this.forgetDevice = false;
        }
        if (!isBtAvailable()) {
            new Thread(this.mReconnectTask).start();
        } else {
            startScanning();
        }
        DfuServiceListenerHelper.registerProgressListener(this, this.mDfuProgressListener);
    }

    public void forgetDevice() {
        this.forgetDevice = true;
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
            this.stopScanTimerTask = null;
        }
        this.mReconnectTask.stopThread = true;
        this.forgetDevice = true;
        stopScanning();
        if (this.mGatt != null) {
            disconnectGatt();
        }
        DfuServiceListenerHelper.registerProgressListener(this, this.mDfuProgressListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startScanning() {
        this.mDeviceId = GlobalPreferences.getConnectedWatchId();
        this.mDeviceList = new TreeMap<>();
        if (this.mDeviceId == null || this.mDeviceId.equals("")) {
            this.mDeviceId = GlobalPreferences.getTempWatchId();
            this.mTimer = new Timer();
            this.stopScanTimerTask = new StopScanTimerTask(this);
            this.mTimer.schedule(this.stopScanTimerTask, 15000L);
        }
        this.mFoundDevice = false;
        this.mAdapter.startLeScan(this.mScanCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopScanning() {
        this.mAdapter.stopLeScan(this.mScanCallback);
        if (this.mFoundDevice) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CONNECTION_STATE_CHANGE).putExtra("state", 1));
        } else {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CONNECTION_STATE_CHANGE).putExtra("state", 0));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectGatt() {
        Log.d(TAG, "connectGatt()");
        if (this.mTimer != null) {
            this.mTimer.cancel();
            this.mTimer = null;
            this.stopScanTimerTask = null;
        }
        if (this.mDevice == null) {
            if (this.mDeviceList.size() > 0) {
                Log.d(TAG, "first rssi: " + this.mDeviceList.firstKey() + " last rssi: " + this.mDeviceList.lastKey());
                this.mDevice = this.mDeviceList.get(this.mDeviceList.lastKey());
                GlobalPreferences.putConnectedWatchId(this.mDevice.getName());
                this.mDevice.connectGatt(getApplicationContext(), false, this.mBluetoothGattCallback);
                return;
            }
            return;
        }
        this.mDevice.connectGatt(getApplicationContext(), false, this.mBluetoothGattCallback);
    }

    private void disconnectGatt() {
        Log.d(TAG, "disconnectGatt()");
        this.mGatt.disconnect();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTime() {
        Calendar now = Calendar.getInstance();
        byte[] value = {(byte) (now.get(1) % 100), (byte) (now.get(2) + 1), (byte) now.get(5), (byte) now.get(11), (byte) now.get(12), (byte) now.get(13), 0, 0};
        this.onTimeChanged = false;
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_UPDATE_TIME, value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCurrentDayPattern() {
        updatePattern(Calendar.getInstance().get(5), this.mCurrentDayPattern, UUID_CHARACTERISTIC_PATTERN_CURRENT_DAY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTomorrowPattern() {
        Calendar c = Calendar.getInstance();
        c.set(5, c.get(5) + 1);
        updatePattern(c.get(5), this.mTomorrowPattern, UUID_CHARACTERISTIC_PATTERN_TOMORROW);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDayAfterTomorrowPattern() {
        Calendar c = Calendar.getInstance();
        c.set(5, c.get(5) + 2);
        updatePattern(c.get(5), this.mDayAfterTomorrowPattern, UUID_CHARACTERISTIC_PATTERN_DAY_AFTER_TOMORROW);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void acknowledgment() {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_ACKNOWLEDGMENT, new byte[]{1});
    }

    private void updatePattern(int day, char[] pattern, UUID uuid) {
        byte[] value = new byte[20];
        value[0] = (byte) (pattern[0] - '0');
        value[0] = (byte) (value[0] << 6);
        value[0] = (byte) (value[0] | day);
        for (int i = 1; i < 96; i += 5) {
            byte b = 0;
            for (int c = 0; c < 5; c++) {
                int v = pattern[i + c] - '0';
                b = (byte) (b + Math.round(Math.pow(3.0d, c) * v));
            }
            value[(i / 5) + 1] = b;
        }
        this.mBTCharacteristicWriterQueue.write(uuid, value);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clear(int type) {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_CLEAR, new byte[]{(byte) type});
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeCharacteristic(UUID uuid, byte[] value) {
        if (this.mGatt != null) {
            try {
                BluetoothGattCharacteristic characteristic = this.mGatt.getService(UUID_SERVICE).getCharacteristic(uuid);
                characteristic.setWriteType(1);
                characteristic.setValue(value);
                Log.d(TAG, " ==> " + uuid.toString() + " >>> Write value: " + toHex(value));
                this.mGatt.writeCharacteristic(characteristic);
            } catch (Exception e) {
                Log.e(TAG, "writeCharacteristic watch Exception \n" + e.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readCharacteristic(UUID uuid) {
        if (this.mGatt != null) {
            try {
                BluetoothGattCharacteristic characteristic = this.mGatt.getService(UUID_SERVICE).getCharacteristic(uuid);
                this.mGatt.readCharacteristic(characteristic);
            } catch (Exception e) {
                Log.e(TAG, "readCharacteristic watch Exception \n" + e.getMessage());
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public void setTime() {
        if (this.mGatt != null) {
            updateTime();
        }
    }

    public void manualSync() {
        clearPatterns();
        this.mAllDaysPattern[100] = 'a';
        updateAllDayPatterns();
    }

    public void updateAllDayPatterns() {
        if (this.mGatt != null) {
            new Thread(this.mUpdatePatternsTask).start();
        }
    }

    public void calibrateStart() {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_CALIBRATE, new byte[]{1});
    }

    public void calibrateCancel() {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_CALIBRATE, new byte[]{0});
    }

    public void calibrateWatch(int hour, int minute, int minuteA, int second, int secondA) {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_CALIBRATE, new byte[]{(byte) hour, (byte) minute, (byte) minuteA, (byte) second, (byte) secondA});
    }

    public void reset() {
        if (this.mGatt != null) {
            clearPatterns();
            clear(0);
            Toast.makeText(getApplicationContext(), getString(R.string.toast_msg_reset_watch), Toast.LENGTH_SHORT).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNeed() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FIRMWARE_UPDATE_NEED));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNo() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FIRMWARE_UPDATE_NO));
    }

    private void updateStart() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FIRMWARE_UPDATE_START));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDone() {
        GlobalPreferences.setFlightModeSwitch(false);
        GlobalPreferences.setVibrateSwitch(true);
        GlobalPreferences.setAllDayEventsInfo(true);
        GlobalPreferences.setSwitchModeSwitch(false);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FIRMWARE_UPDATE_DONE));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateError() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_FIRMWARE_UPDATE_ERROR));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFirmWareFile(File fileUri) {
        if (fileUri == null || this.mGatt == null || this.mDevice == null || this.mDevice.getAddress() == null) {
            updateError();
            return;
        }
        DfuServiceInitiator starter = new DfuServiceInitiator(this.mDevice.getAddress()).setDeviceName(this.mDevice.getName()).setKeepBond(true);
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(false);
        starter.setZip(null, fileUri.getPath());
        starter.setDisableNotification(true);
        starter.start(this, DfuService.class);
    }

    public void updateFirmWare() {
        final GWatchResponse gwResponse = GlobalPreferences.getGWatchResponse();
        if (gwResponse == null || StringUtils.isEmpty(gwResponse.fw_url) || StringUtils.isEmpty(gwResponse.fw_ver)) {
            updateError();
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(UpdateScheme.serverUrl).addConverterFactory(GsonConverterFactory.create()).build();
        UpdateScheme updateScheme = (UpdateScheme) retrofit.create(UpdateScheme.class);
        Call<ResponseBody> downloadCall = updateScheme.downloadFile(gwResponse.fw_url);
        updateStart();
        downloadCall.enqueue(new Callback<ResponseBody>() { // from class: com.whatcalendar.service.BackgroundService.3
            @Override // retrofit2.Callback
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful() && response.body() != null) {
                    Log.d(BackgroundService.TAG, "Firmware file downloaded. Saving");
                    BackgroundService.this.sendFirmWareFile(FileWritter.writeResponseBodyToDisk(response.body(), gwResponse.fw_ver, BackgroundService.this));
                }
            }

            @Override // retrofit2.Callback
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                BackgroundService.this.updateError();
            }
        });
    }

    public void checkLastVersion(final String firmwareVersion) {
        if (!StringUtils.isEmpty(firmwareVersion)) {
            Log.d(TAG, "check for new version");
            GlobalPreferences.putGWatchResponse(null);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(UpdateScheme.serverUrl).addConverterFactory(GsonConverterFactory.create()).build();
            UpdateScheme updateScheme = (UpdateScheme) retrofit.create(UpdateScheme.class);
            Call<GWatchResponse> checkUpdateCall = updateScheme.getFirmwareVersion();
            checkUpdateCall.enqueue(new Callback<GWatchResponse>() { // from class: com.whatcalendar.service.BackgroundService.4
                @Override // retrofit2.Callback
                public void onResponse(Call<GWatchResponse> call, Response<GWatchResponse> response) {
                    if (response != null && response.isSuccessful() && response.body() != null) {
                        Log.d(BackgroundService.TAG, "Last version : " + response.body().fw_ver + " url : " + response.body().fw_url);
                        if (response.body().fw_ver.equals(firmwareVersion)) {
                            Log.d(BackgroundService.TAG, "no firmware updates");
                            BackgroundService.this.updateNo();
                            return;
                        }
                        GlobalPreferences.putGWatchResponse(response.body());
                        BackgroundService.this.updateNeed();
                    }
                }

                @Override // retrofit2.Callback
                public void onFailure(Call<GWatchResponse> call, Throwable t) {
                }
            });
        }
    }

    public void updateBatteryLevel() {
        readCharacteristic(UUID_CHARACTERISTIC_BATTERY_LEVEL);
    }

    public void checkForUpdate() {
        if (this.mGatt != null) {
            try {
                Log.d(TAG, "request to firmware version on the watch");
                BluetoothGattCharacteristic firmwareRevision = this.mGatt.getService(UUID_DEVICE_INFORMATION_SERVICE).getCharacteristic(UUID_CHARACTERISTIC_FIRMWARE);
                this.mGatt.readCharacteristic(firmwareRevision);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }

    public void setAlertsEnabled(boolean enabled) {
        if (enabled) {
            updateAllDayPatterns();
            return;
        }
        this.mAlerts = new ArrayList<>();
        GlobalPreferences.putAlerts(this.mAlerts);
        clear(2);
    }

    public void setFlexibleMode() {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_SWITCH_MODE, new byte[]{0});
    }

    public void setFixedMode(int fixedModeHour) {
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_SWITCH_MODE, new byte[]{1, (byte) fixedModeHour});
    }

    public void setAirplaneMode(boolean airplane) {
        byte b = 1;
        if (airplane) {
            BTCharacteristicWriterQueue bTCharacteristicWriterQueue = this.mBTCharacteristicWriterQueue;
            UUID uuid = UUID_CHARACTERISTIC_AIRPLANE_MODE;
            byte[] bArr = new byte[1];
            if (!airplane) {
                b = 0;
            }
            bArr[0] = b;
            bTCharacteristicWriterQueue.write(uuid, bArr);
        }
    }

    public boolean isConnected() {
        return this.mGatt != null;
    }

    public boolean isBtAvailable() {
        return this.mAdapter != null && this.mAdapter.isEnabled();
    }

    public void restoreConnection() {
        startScanning();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class UpdatePatternsTask implements Runnable {
        UpdatePatternsTask() {
        }

        @Override // java.lang.Runnable
        public void run() {
            int eventStartMinute;
            int eventStartSector;
            int eventEndSector;
            BackgroundService.this.mUpdating = true;
            DTOResponse<ArrayList<DTOEvent>> response = CalendarLoader.loadCalendarEvents(BackgroundService.this);
            if (response.errorCode != 1) {
                Log.d(BackgroundService.TAG, "Can't load any events");
                BackgroundService.this.mUpdating = false;
            } else if (response.data.size() != 0) {
                boolean needToUpdate = false;
                char[] mAllDaysNewPattern = new char[288];
                ArrayList<Byte> mNewAlerts = new ArrayList<>();
                Calendar currentDay = Calendar.getInstance();
                Calendar tomorrow = Calendar.getInstance();
                Calendar dayAfterTomorrow = Calendar.getInstance();
                currentDay.set(11, 0);
                currentDay.set(12, 0);
                currentDay.set(13, 0);
                currentDay.set(14, 0);
                tomorrow.setTimeInMillis(currentDay.getTimeInMillis() + DateUtils.MILLIS_PER_DAY);
                dayAfterTomorrow.setTimeInMillis(tomorrow.getTimeInMillis() + DateUtils.MILLIS_PER_DAY);
                Arrays.fill(mAllDaysNewPattern, '0');
                Arrays.fill(BackgroundService.this.mCurrentDayPattern, '0');
                Arrays.fill(BackgroundService.this.mTomorrowPattern, '0');
                Arrays.fill(BackgroundService.this.mDayAfterTomorrowPattern, '0');
                mNewAlerts.add((byte) 0);
                mNewAlerts.add(Byte.valueOf((byte) (currentDay.get(1) % 100)));
                mNewAlerts.add(Byte.valueOf((byte) (currentDay.get(2) + 1)));
                mNewAlerts.add(Byte.valueOf((byte) currentDay.get(5)));
                int alertIndex = 4;
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault());
                Iterator it = ((ArrayList) response.data).iterator();
                while (it.hasNext()) {
                    DTOEvent dtoEvent = (DTOEvent) it.next();
                    if (dtoEvent.endDate >= dtoEvent.startDate) {
                        Calendar eventStartDate = Calendar.getInstance();
                        Calendar eventEndDate = Calendar.getInstance();
                        eventStartDate.setTimeInMillis(dtoEvent.startDate);
                        eventEndDate.setTimeInMillis(dtoEvent.endDate);
                        Log.d(BackgroundService.TAG, "event: " + dtoEvent.title + " s: " + sdf.format(eventStartDate.getTime()) + " e: " + sdf.format(eventEndDate.getTime()));
                        if (eventStartDate.getTimeInMillis() <= currentDay.getTimeInMillis()) {
                            eventStartSector = 0;
                            eventStartMinute = 0;
                        } else {
                            eventStartMinute = (eventStartDate.get(11) * 60) + eventStartDate.get(12);
                            eventStartSector = eventStartMinute / 15;
                            if (eventStartDate.get(6) == tomorrow.get(6)) {
                                eventStartSector += 96;
                                eventStartMinute += 1440;
                            }
                            if (eventStartDate.get(6) == dayAfterTomorrow.get(6)) {
                                eventStartSector += 192;
                                eventStartMinute += 2880;
                            }
                        }
                        if (eventEndDate.getTimeInMillis() >= dayAfterTomorrow.getTimeInMillis() + DateUtils.MILLIS_PER_DAY) {
                            eventEndSector = mAllDaysNewPattern.length;
                        } else {
                            int eventEndMinute = (eventEndDate.get(11) * 60) + eventEndDate.get(12);
                            eventEndSector = eventEndMinute / 15;
                            if (eventEndMinute % 15 > 0) {
                                eventEndSector++;
                            }
                            if (eventEndDate.get(6) == tomorrow.get(6)) {
                                eventEndSector += 96;
                            }
                            if (eventEndDate.get(6) == dayAfterTomorrow.get(6)) {
                                eventEndSector += 192;
                            }
                        }
                        Log.d(BackgroundService.TAG, "event: " + dtoEvent.title + " s sec: " + eventStartSector + " e sec: " + eventEndSector);
                        Arrays.fill(mAllDaysNewPattern, eventStartSector, eventEndSector, '1');
                        BackgroundService.this.mCurrentDayPattern = Arrays.copyOfRange(mAllDaysNewPattern, 0, 96);
                        BackgroundService.this.mTomorrowPattern = Arrays.copyOfRange(mAllDaysNewPattern, 96, 192);
                        BackgroundService.this.mDayAfterTomorrowPattern = Arrays.copyOfRange(mAllDaysNewPattern, 192, 288);
                        int alertStartSector = eventStartSector;
                        int alertEndSector = eventEndSector;
                        if (GlobalPreferences.getVibrateSwitch()) {
                            for (DTOAlert alert : dtoEvent.alertsList) {
                                if (alertIndex % 20 == 0) {
                                    mNewAlerts.add((byte) 0);
                                    mNewAlerts.add(Byte.valueOf((byte) (currentDay.get(1) % 100)));
                                    mNewAlerts.add(Byte.valueOf((byte) (currentDay.get(2) + 1)));
                                    mNewAlerts.add(Byte.valueOf((byte) currentDay.get(5)));
                                }
                                int alertStartMinute = eventStartMinute - alert.minutes;
                                if (alertStartMinute < 0) {
                                    alertStartMinute = 0;
                                }
                                mNewAlerts.add(alertIndex, Byte.valueOf((byte) (alertStartMinute >> 8)));
                                mNewAlerts.add(alertIndex + 1, Byte.valueOf((byte) (alertStartMinute & 255)));
                                mNewAlerts.add(alertIndex + 2, Byte.valueOf((byte) alertStartSector));
                                mNewAlerts.add(alertIndex + 3, Byte.valueOf((byte) alertEndSector));
                                alertIndex += 4;
                            }
                        }
                    }
                }
                if (!Arrays.equals(mAllDaysNewPattern, BackgroundService.this.mAllDaysPattern)) {
                    needToUpdate = true;
                }
                if (BackgroundService.this.mAlerts.size() != mNewAlerts.size()) {
                    needToUpdate = true;
                }
                int i = 4;
                while (i < BackgroundService.this.mAlerts.size() && !needToUpdate) {
                    if (i % 20 == 0) {
                        i += 4;
                    }
                    if (i < mNewAlerts.size() && i < BackgroundService.this.mAlerts.size() && mNewAlerts.get(i) != BackgroundService.this.mAlerts.get(i)) {
                        needToUpdate = true;
                    }
                    i++;
                }
                if (BackgroundService.this.onTimeChanged) {
                    needToUpdate = true;
                }
                if (needToUpdate) {
                    BackgroundService.this.mAllDaysPattern = mAllDaysNewPattern;
                    BackgroundService.this.mAlerts = mNewAlerts;
                    GlobalPreferences.putPattern(BackgroundService.this.mAllDaysPattern);
                    GlobalPreferences.putAlerts(BackgroundService.this.mAlerts);
                    Log.d(BackgroundService.TAG, "current day pattern: " + Arrays.toString(BackgroundService.this.mCurrentDayPattern));
                    Log.d(BackgroundService.TAG, "tomorrow pattern: " + Arrays.toString(BackgroundService.this.mTomorrowPattern));
                    Log.d(BackgroundService.TAG, "day after tomorrow pattern: " + Arrays.toString(BackgroundService.this.mDayAfterTomorrowPattern));
                    Log.d(BackgroundService.TAG, "alerts: " + BackgroundService.this.mAlerts.toString());
                    BackgroundService.this.updateTime();
                    BackgroundService.this.updateCurrentDayPattern();
                    BackgroundService.this.updateTomorrowPattern();
                    BackgroundService.this.updateDayAfterTomorrowPattern();
                    BackgroundService.this.clear(2);
                    BackgroundService.this.updateAlerts();
                    BackgroundService.this.acknowledgment();
                    return;
                }
                Log.d(BackgroundService.TAG, "No changes in patterns and alerts");
                BackgroundService.this.updateTime();
            }
        }
    }

    /* loaded from: classes.dex */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class StopScanTimerTask extends TimerTask {
        Context mContext;

        public StopScanTimerTask(Context context) {
            this.mContext = context;
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            Log.d("StopScanTimerTask", "run");
            if (BackgroundService.this.mDeviceList.size() > 0) {
                BackgroundService.this.mFoundDevice = true;
            }
            BackgroundService.this.stopScanning();
            BackgroundService.this.connectGatt();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.d(TAG, "rssi: " + rssi + " device: " + device.getName());
        if (this.mDeviceId != null && !this.mDeviceId.equals("") && device != null && device.getName() != null) {
            if (!this.mFoundDevice && this.mDeviceId.toLowerCase().equals(device.getName().toLowerCase())) {
                this.mFoundDevice = true;
                this.mDevice = device;
                GlobalPreferences.putConnectedWatchId(this.mDevice.getName());
                stopScanning();
                connectGatt();
            }
        } else if (!this.mFoundDevice && rssi > RSSI_VALUE && device != null && device.getName() != null && device.getName().toLowerCase().contains("gw_")) {
            this.mDeviceList.put(Integer.valueOf(rssi), device);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearPatterns() {
        Arrays.fill(this.mAllDaysPattern, '0');
        Arrays.fill(this.mCurrentDayPattern, '0');
        Arrays.fill(this.mTomorrowPattern, '0');
        Arrays.fill(this.mDayAfterTomorrowPattern, '0');
        this.mAlerts = new ArrayList<>();
        GlobalPreferences.putPattern(this.mAllDaysPattern);
        GlobalPreferences.putAlerts(this.mAlerts);
    }

    private void scanningFail() {
        Log.d(TAG, "scanning timeout, stop scanning");
        stopScanning();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_CONNECTION_STATE_CHANGE).putExtra("state", 0));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlerts() {
        byte[] alerts = new byte[20];
        for (int i = 0; i < this.mAlerts.size(); i++) {
            if (i % 20 == 0) {
                if (i > 0) {
                    this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_ALERTS, alerts);
                }
                alerts = new byte[20];
                Arrays.fill(alerts, (byte) 0);
            }
            alerts[i % 20] = this.mAlerts.get(i).byteValue();
        }
        this.mBTCharacteristicWriterQueue.write(UUID_CHARACTERISTIC_ALERTS, alerts);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String toHex(byte[] value) {
        String hexValue;
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            String hex = Integer.toHexString(b);
            switch (hex.length()) {
                case 0:
                    hexValue = "00";
                    break;
                case 1:
                    hexValue = "0" + hex;
                    break;
                case 2:
                    hexValue = hex;
                    break;
                default:
                    hexValue = hex.substring(hex.length() - 2, hex.length());
                    break;
            }
            sb.append(hexValue.toUpperCase()).append(' ');
        }
        return sb.toString();
    }
}
