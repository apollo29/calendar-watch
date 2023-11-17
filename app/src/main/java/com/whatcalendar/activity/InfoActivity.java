package com.whatcalendar.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityInfoBinding;
import com.whatcalendar.firmware.UpdateBroadcastReceiver;
import com.whatcalendar.service.BackgroundService;

/* loaded from: classes.dex */
public class InfoActivity extends AppCompatActivity {
    private BackgroundService mBackgroundService;
    private Context mContext;
    private ActivityInfoBinding binding;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.whatcalendar.activity.InfoActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            InfoActivity.this.mBackgroundService = ((BackgroundService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            InfoActivity.this.mBackgroundService = null;
        }
    };
    //@BindView(R.id.button_update)
    //RelativeLayout mUpdateButton;

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_info);
        this.mContext = this;
        binding.buttonUpdate.setOnClickListener(view -> onCheckUpdate());
        binding.buttonInfoPrivacy.setOnClickListener(view -> onPrivacyClicked());
        binding.buttonInfoWhatWatchCom.setOnClickListener(view -> onWhatWatchComClicked());
        binding.buttonInfoTerm.setOnClickListener(view -> onTermClicked());
        binding.buttonInfoHelp.setOnClickListener(view -> onHelpClicked());
        binding.buttonInfoFaq.setOnClickListener(view -> onFaqClicked());
        binding.buttonInfoWelcome.setOnClickListener(view -> onWelcomeClicked());
        binding.toolbarIcon.setOnClickListener(view -> onCloseButtonClicked());

        try {
            String appVersion = getPackageManager().getPackageInfo(getPackageName(), 1).versionName;
            binding.textAppVersion.setText(appVersion);
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        bindService(new Intent(this, BackgroundService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(this.mServiceConnection);
    }

    public void onCheckUpdate() {
        if (checkWatchConnection() && isOnline()) {
            IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_FIRMWARE_UPDATE_NEED);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_NO);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_START);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_DONE);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_ERROR);
            intentFilter.addAction(BackgroundService.ACTION_FIRMWARE_UPDATE_PROGRESS);
            intentFilter.addAction(BackgroundService.ACTION_BATTERY_INFO);
            LocalBroadcastManager.getInstance(this.mContext).registerReceiver(new UpdateBroadcastReceiver(this, this.mBackgroundService) { // from class: com.whatcalendar.activity.InfoActivity.2
                @Override // com.whatcalendar.firmware.UpdateBroadcastReceiver
                public void callback(String action) {
                    InfoActivity.this.binding.buttonUpdate.setClickable(true);
                }
            }, intentFilter);
            this.binding.buttonUpdate.setClickable(false);
            this.mBackgroundService.checkForUpdate();
        }
    }

    private boolean checkWatchConnection() {
        if (!this.mBackgroundService.isBtAvailable()) {
            Toast.makeText(this.mContext, "Bluetooth is off on the phone.\nPlease, turn it on.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!this.mBackgroundService.isConnected()) {
            Toast.makeText(this.mContext, "Cannot connect to the watch.\nPlease check its operability.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean result = netInfo != null && netInfo.isConnectedOrConnecting();
        if (!result) {
            final Dialog alert_dialog = new Dialog(this.mContext);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.internet_connection_alert));
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.InfoActivity.3
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.show();
        }
        return result;
    }

    public void onPrivacyClicked() {
        openWebView(getString(R.string.info_privacy_url), getString(R.string.info_privacy));
    }

    public void onWhatWatchComClicked() {
        openWebView(getString(R.string.info_what_watch_com_url), getString(R.string.info_what_watch_com));
    }

    public void onTermClicked() {
        openWebView(getString(R.string.info_terms_url), getString(R.string.info_terms));
    }

    public void onHelpClicked() {
        openWebView(getString(R.string.info_help_url), getString(R.string.info_help));
    }

    public void onFaqClicked() {
        openWebView(getString(R.string.info_faq_url), "FAQ");
    }

    public void onWelcomeClicked() {
        Intent intent = new Intent(this, WelcomeScreenActivity.class);
        intent.putExtra(WelcomeScreenActivity.ONLY_GUIDE, true);
        startActivity(intent);
    }

    public void onCloseButtonClicked() {
        onBackPressed();
    }

    private void openWebView(String url, String page_name) {
        startActivity(WebViewActivity.newIntent(this, url, page_name));
    }
}
