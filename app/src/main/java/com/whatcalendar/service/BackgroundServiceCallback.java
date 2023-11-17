package com.whatcalendar.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/* loaded from: classes.dex */
final class BackgroundServiceCallback implements BluetoothAdapter.LeScanCallback {
    private final BackgroundService arg$1;

    private BackgroundServiceCallback(BackgroundService backgroundService) {
        this.arg$1 = backgroundService;
    }

    private static BluetoothAdapter.LeScanCallback get$Lambda(BackgroundService backgroundService) {
        return new BackgroundServiceCallback(backgroundService);
    }

    public static BluetoothAdapter.LeScanCallback lambdaFactory$(BackgroundService backgroundService) {
        return new BackgroundServiceCallback(backgroundService);
    }

    @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        this.arg$1.lambda$new$0(bluetoothDevice, i, bArr);
    }
}
