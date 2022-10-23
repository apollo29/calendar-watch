package com.whatcalendar;

import android.text.TextUtils;
import com.whatcalendar.util.GlobalPreferences;
import java.util.UUID;

/* loaded from: classes.dex */
public class Application extends android.app.Application {
    public boolean firstUpdateChecking = true;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        GlobalPreferences.initialize(this);
        GlobalPreferences.putTempWatchId(null);
        generateUuid();
    }

    private void generateUuid() {
        String uuid = GlobalPreferences.getUuid();
        if (TextUtils.isEmpty(uuid)) {
            String uuid2 = UUID.randomUUID().toString();
            GlobalPreferences.putUuid(uuid2);
        }
    }
}
