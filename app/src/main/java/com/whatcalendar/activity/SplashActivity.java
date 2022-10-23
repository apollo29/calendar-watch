package com.whatcalendar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import butterknife.ButterKnife;
import com.whatcalendar.R;
import com.whatcalendar.util.GlobalPreferences;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class SplashActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int WELCOME_SCREEN_REQUEST_CODE = 3;

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        startApplication();
    }

    private void startApplication() {
        if (!checkPermission()) {
            requestPermission();
        } else if (checkLocation()) {
            String watchId = GlobalPreferences.getConnectedWatchId();
            if (StringUtils.isEmpty(watchId)) {
                startActivity(new Intent(this, WelcomeScreenActivity.class));
                finish();
                return;
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            startApplication();
        }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.BLUETOOTH_ADMIN") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0 && ContextCompat.checkSelfPermission(this, "android.permission.READ_CALENDAR") == 0;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_CALENDAR", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
    }

    private boolean checkLocation() {
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        try {
            int locationMode = Settings.Secure.getInt(getContentResolver(), "location_mode");
            if (locationMode != 0) {
                return true;
            }
            new AlertDialog.Builder(this).setTitle(R.string.dialog_title_attention).setMessage(R.string.dialog_msg_location).setPositiveButton(17039370, SplashActivity$$Lambda$1.lambdaFactory$(this)).setCancelable(false).show();
            return false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public /* synthetic */ void lambda$checkLocation$0(DialogInterface dialog1, int which) {
        Intent enableLocationIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
        startActivityForResult(enableLocationIntent, 2);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity, android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(getClass().getSimpleName(), "onRequestPermissionsResult");
        boolean granted = true;
        for (int i : grantResults) {
            if (i != 0) {
                granted = false;
            }
        }
        if (granted) {
            startApplication();
            return;
        }
        Intent intent = new Intent(this, LinkWatchResultActivity.class);
        intent.putExtra("state", -1);
        startActivity(intent);
        finish();
    }
}
