package com.whatcalendar.activity;

import android.content.DialogInterface;
import java.lang.invoke.LambdaForm;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final /* synthetic */ class SplashActivity$$Lambda$1 implements DialogInterface.OnClickListener {
    private final SplashActivity arg$1;

    private SplashActivity$$Lambda$1(SplashActivity splashActivity) {
        this.arg$1 = splashActivity;
    }

    private static DialogInterface.OnClickListener get$Lambda(SplashActivity splashActivity) {
        return new SplashActivity$$Lambda$1(splashActivity);
    }

    public static DialogInterface.OnClickListener lambdaFactory$(SplashActivity splashActivity) {
        return new SplashActivity$$Lambda$1(splashActivity);
    }

    @Override // android.content.DialogInterface.OnClickListener
    @LambdaForm.Hidden
    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$checkLocation$0(dialogInterface, i);
    }
}
