package com.whatcalendar.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.whatcalendar.R;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;
import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class LinkWatchActivity extends AppCompatActivity {
    private static final String TAG = LinkWatchActivity.class.getSimpleName();
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.whatcalendar.activity.LinkWatchActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BackgroundService.ACTION_CONNECTION_STATE_CHANGE.equals(intent.getAction())) {
                int state = intent.getIntExtra("state", 0);
                Log.d(LinkWatchActivity.TAG, "state connection: " + state);
                if (state != 1) {
                    if (state != 2) {
                        LinkWatchActivity.this.stopService(new Intent(context, BackgroundService.class));
                    }
                    Intent startIntent = new Intent(context, LinkWatchResultActivity.class);
                    startIntent.putExtra("state", state);
                    LinkWatchActivity.this.startActivity(startIntent);
                    LinkWatchActivity.this.finish();
                }
            }
        }
    };
    @Bind({R.id.button_pair_watch})
    Button mButtonPair;
    @Bind({R.id.edit_watch_id})
    EditText mEditWatchId;
    @Bind({R.id.progress_pair_watch})
    ProgressBar mProgressPair;
    @Bind({R.id.text_hint_watch_id})
    TextView mTextHintWatchId;
    @Bind({R.id.text_terms})
    TextView mTextTerms;

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_watch);
        ButterKnife.bind(this);
        this.mProgressPair.getIndeterminateDrawable().setColorFilter(-7829368, PorterDuff.Mode.MULTIPLY);
        if (!StringUtils.isEmpty(GlobalPreferences.getTempWatchId()) && !GlobalPreferences.getTempWatchId().equals("")) {
            this.mEditWatchId.setText(GlobalPreferences.getTempWatchId());
        }
    }

    @OnClick({R.id.button_pair_watch})
    public void onPairWatch() {
        if (!isBtEnable()) {
            Intent startIntent = new Intent(this, LinkWatchResultActivity.class);
            startIntent.putExtra("state", 10);
            startActivity(startIntent);
            finish();
            return;
        }
        CharSequence watchId = this.mEditWatchId.getText();
        if (this.mProgressPair.getVisibility() == 0) {
            this.mProgressPair.setVisibility(4);
            this.mTextTerms.setVisibility(0);
            this.mTextHintWatchId.setVisibility(0);
            this.mButtonPair.setVisibility(0);
        } else {
            this.mProgressPair.setVisibility(0);
            this.mTextTerms.setVisibility(4);
            this.mTextHintWatchId.setVisibility(4);
            this.mButtonPair.setVisibility(4);
        }
        GlobalPreferences.putTempWatchId(watchId.toString());
        GlobalPreferences.setPairingMode(true);
        startService(new Intent(this, BackgroundService.class));
        IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_BATTERY_INFO);
        intentFilter.addAction(BackgroundService.ACTION_CONNECTION_STATE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    public boolean isBtEnable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
    }

    @OnClick({R.id.text_terms})
    public void openWebView() {
        startActivity(WebViewActivity.newIntent(this, getString(R.string.info_terms_url), getString(R.string.info_terms)));
    }
}
