package com.whatcalendar.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.lang.invoke.LambdaForm;

/* loaded from: classes.dex */
final /* synthetic */ class BackgroundService$$Lambda$1 implements BluetoothAdapter.LeScanCallback {
    private final BackgroundService arg$1;

    private BackgroundService$$Lambda$1(BackgroundService backgroundService) {
        this.arg$1 = backgroundService;
    }

    private static BluetoothAdapter.LeScanCallback get$Lambda(BackgroundService backgroundService) {
        return new BackgroundService$$Lambda$1(backgroundService);
    }

    public static BluetoothAdapter.LeScanCallback lambdaFactory$(BackgroundService backgroundService) {
        return new BackgroundService$$Lambda$1(backgroundService);
    }

    @Override // android.bluetooth.BluetoothAdapter.LeScanCallback
    @LambdaForm.Hidden
    public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
        this.arg$1.lambda$new$0(bluetoothDevice, i, bArr);
    }
}
