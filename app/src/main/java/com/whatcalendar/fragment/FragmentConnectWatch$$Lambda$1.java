package com.whatcalendar.fragment;

import android.view.View;
import java.lang.invoke.LambdaForm;

/* loaded from: classes.dex */
final /* synthetic */ class FragmentConnectWatch$$Lambda$1 implements Runnable {
    private final View arg$1;

    private FragmentConnectWatch$$Lambda$1(View view) {
        this.arg$1 = view;
    }

    private static Runnable get$Lambda(View view) {
        return new FragmentConnectWatch$$Lambda$1(view);
    }

    public static Runnable lambdaFactory$(View view) {
        return new FragmentConnectWatch$$Lambda$1(view);
    }

    @Override // java.lang.Runnable
    @LambdaForm.Hidden
    public void run() {
        FragmentConnectWatch.lambda$getView$0(this.arg$1);
    }
}
