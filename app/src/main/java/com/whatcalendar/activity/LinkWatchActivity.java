package com.whatcalendar.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityLinkWatchBinding;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;

import org.apache.commons.lang3.StringUtils;

/* loaded from: classes.dex */
public class LinkWatchActivity extends AppCompatActivity {
    private static final String TAG = LinkWatchActivity.class.getSimpleName();
    private ActivityLinkWatchBinding binding;
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
    Button mButtonPair;
    EditText mEditWatchId;
    ProgressBar mProgressPair;
    TextView mTextHintWatchId;
    TextView mTextTerms;

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_watch);
        binding = ActivityLinkWatchBinding.inflate(getLayoutInflater());
        mButtonPair = binding.buttonPairWatch;
        mEditWatchId = binding.editWatchId;
        mProgressPair = binding.progressPairWatch;
        mTextHintWatchId = binding.textHintWatchId;
        mTextTerms = binding.textTerms;
        binding.buttonPairWatch.setOnClickListener(view -> onPairWatch());
        binding.textTerms.setOnClickListener(view -> openWebView());
        this.mProgressPair.getIndeterminateDrawable().setColorFilter(-7829368, PorterDuff.Mode.MULTIPLY);
        if (!StringUtils.isEmpty(GlobalPreferences.getTempWatchId()) && !GlobalPreferences.getTempWatchId().equals("")) {
            this.mEditWatchId.setText(GlobalPreferences.getTempWatchId());
        }
    }

    public void onPairWatch() {
        if (!isBtEnable()) {
            Intent startIntent = new Intent(this, LinkWatchResultActivity.class);
            startIntent.putExtra("state", 10);
            startActivity(startIntent);
            finish();
            return;
        }
        CharSequence watchId = this.mEditWatchId.getText();
        if (this.mProgressPair.getVisibility() == View.VISIBLE) {
            this.mProgressPair.setVisibility(View.INVISIBLE);
            this.mTextTerms.setVisibility(View.VISIBLE);
            this.mTextHintWatchId.setVisibility(View.VISIBLE);
            this.mButtonPair.setVisibility(View.VISIBLE);
        } else {
            this.mProgressPair.setVisibility(View.VISIBLE);
            this.mTextTerms.setVisibility(View.INVISIBLE);
            this.mTextHintWatchId.setVisibility(View.INVISIBLE);
            this.mButtonPair.setVisibility(View.INVISIBLE);
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

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
    }

    public void openWebView() {
        startActivity(WebViewActivity.newIntent(this, getString(R.string.info_terms_url), getString(R.string.info_terms)));
    }
}
