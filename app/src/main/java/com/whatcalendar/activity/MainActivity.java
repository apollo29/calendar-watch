package com.whatcalendar.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.whatcalendar.R;
import com.whatcalendar.entity.BatteryInfo;
import com.whatcalendar.firmware.UpdateBroadcastReceiver;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;
import com.whatcalendar.view.BatteryLevelView;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BackgroundService mBackgroundService;
    @Bind({R.id.view_battery_level})
    BatteryLevelView mBatteryLevelView;
    @Bind({R.id.layout_charging})
    RelativeLayout mChargingLayout;
    private Context mContext;
    @Bind({R.id.button_my_watch})
    View mMyWatchButton;
    private boolean firstLaunch = true;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.whatcalendar.activity.MainActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int state;
            boolean z = true;
            if (BackgroundService.ACTION_BATTERY_INFO.equals(intent.getAction())) {
                MainActivity.this.updateBatteryView(true);
                if (MainActivity.this.mBackgroundService != null && MainActivity.this.mBackgroundService.isConnected() && MainActivity.this.firstLaunch) {
                    MainActivity.this.checkForUpdate();
                }
            } else if (BackgroundService.ACTION_CONNECTION_STATE_CHANGE.equals(intent.getAction()) && (state = intent.getIntExtra("state", 0)) != 1) {
                if (state == 2) {
                    GlobalPreferences.setFlightModeSwitch(false);
                }
                if (GlobalPreferences.getFlightModeSwitch()) {
                    state = 10;
                }
                MainActivity mainActivity = MainActivity.this;
                if (state == 0) {
                    z = false;
                }
                mainActivity.updateBatteryView(z);
            }
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.whatcalendar.activity.MainActivity.2
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.this.mBackgroundService = ((BackgroundService.LocalBinder) service).getService();
            if (!GlobalPreferences.getFlightModeSwitch()) {
                MainActivity.this.updateBatteryView(MainActivity.this.mBackgroundService.isConnected());
            }
            MainActivity.this.mBackgroundService.updateBatteryLevel();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.this.mBackgroundService = null;
        }
    };

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        this.mContext = this;
        updateBatteryView(GlobalPreferences.getFlightModeSwitch());
        if (isWatchLinked()) {
            startService(new Intent(this, BackgroundService.class));
            bindService(new Intent(this, BackgroundService.class), this.mServiceConnection, 1);
        }
        IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_BATTERY_INFO);
        intentFilter.addAction(BackgroundService.ACTION_CONNECTION_STATE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mMyWatchButton.setOnTouchListener(new UpSwipeListener() { // from class: com.whatcalendar.activity.MainActivity.3
            @Override // com.whatcalendar.activity.MainActivity.UpSwipeListener
            public void upSwipeAction() {
                MainActivity.this.myWatchClicked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public abstract class UpSwipeListener implements View.OnTouchListener {
        int downY;
        int upY;

        public abstract void upSwipeAction();

        UpSwipeListener() {
            MainActivity.this = this$0;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                this.downY = (int) event.getY();
                return true;
            } else if (event.getAction() == 1) {
                this.upY = (int) event.getY();
                Log.d(MainActivity.TAG, "down: " + this.downY + " up: " + this.upY);
                if (this.downY - this.upY <= 100 && Math.abs(this.downY - this.upY) >= 5) {
                    return true;
                }
                upSwipeAction();
                return true;
            } else {
                return false;
            }
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (StringUtils.isEmpty(GlobalPreferences.getConnectedWatchId())) {
            startActivity(new Intent(this, WelcomeScreenActivity.class));
            finish();
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        unbindService(this.mServiceConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
    }

    private boolean isWatchLinked() {
        return StringUtils.isNotEmpty(GlobalPreferences.getConnectedWatchId());
    }

    @OnClick({R.id.button_my_watch})
    public void myWatchClicked() {
        startActivity(new Intent(this, WatchSettingsActivity.class));
    }

    @OnClick({R.id.button_info})
    public void onInfoClicked() {
        startActivity(new Intent(this, InfoActivity.class));
    }

    public void updateBatteryView(boolean connected) {
        boolean z = true;
        if (!connected) {
            this.mBatteryLevelView.setViewType(1);
            this.mBatteryLevelView.setBatteryLevel(0, false);
            this.mChargingLayout.setVisibility(8);
            return;
        }
        BatteryInfo bi = GlobalPreferences.getBatteryInfo();
        if (bi.getChargingStatus() == 1) {
            this.mBatteryLevelView.setViewType(2);
            this.mChargingLayout.setVisibility(0);
        } else {
            this.mBatteryLevelView.setViewType(0);
            this.mChargingLayout.setVisibility(8);
        }
        BatteryLevelView batteryLevelView = this.mBatteryLevelView;
        int level = bi.getLevel();
        if (this.mBackgroundService == null || !this.mBackgroundService.isBtAvailable() || GlobalPreferences.getFlightModeSwitch()) {
            z = false;
        }
        batteryLevelView.setBatteryLevel(level, z);
    }

    public void checkForUpdate() {
        this.firstLaunch = false;
        if (this.mBackgroundService != null && isOnline()) {
            IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_FIRMWARE_UPDATE_NEED);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_NO);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_START);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_DONE);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_ERROR);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_PROGRESS);
            intentFilter.addAction(BackgroundService.ACTION_BATTERY_INFO);
            LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new UpdateBroadcastReceiver(this, this.mBackgroundService) { // from class: com.whatcalendar.activity.MainActivity.4
                @Override // com.whatcalendar.firmware.UpdateBroadcastReceiver
                public void callback(String action) {
                }
            }, intentFilter);
            this.mBackgroundService.checkForUpdate();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
