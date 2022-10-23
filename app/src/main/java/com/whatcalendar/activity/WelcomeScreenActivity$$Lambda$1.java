package com.whatcalendar.activity;

import java.lang.invoke.LambdaForm;

/* loaded from: classes.dex */
final /* synthetic */ class WelcomeScreenActivity$$Lambda$1 implements Runnable {
    private final WelcomeScreenActivity arg$1;

    private WelcomeScreenActivity$$Lambda$1(WelcomeScreenActivity welcomeScreenActivity) {
        this.arg$1 = welcomeScreenActivity;
    }

    private static Runnable get$Lambda(WelcomeScreenActivity welcomeScreenActivity) {
        return new WelcomeScreenActivity$$Lambda$1(welcomeScreenActivity);
    }

    public static Runnable lambdaFactory$(WelcomeScreenActivity welcomeScreenActivity) {
        return new WelcomeScreenActivity$$Lambda$1(welcomeScreenActivity);
    }

    @Override // java.lang.Runnable
    @LambdaForm.Hidden
    public void run() {
        this.arg$1.lambda$showWelcomeWindow$0();
    }
}
