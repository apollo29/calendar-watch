package com.whatcalendar.firmware;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.whatcalendar.activity.MainActivity;

/* loaded from: classes.dex */
public class NotificationActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isTaskRoot()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(268435456);
            intent.putExtras(getIntent().getExtras());
            startActivity(intent);
        }
        finish();
    }
}
