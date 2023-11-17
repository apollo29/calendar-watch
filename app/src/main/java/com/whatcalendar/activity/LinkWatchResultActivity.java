package com.whatcalendar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityLinkWatchResultBinding;

/* loaded from: classes.dex */
public class LinkWatchResultActivity extends AppCompatActivity {
    private ActivityLinkWatchResultBinding binding;
    TextView mTextResult;
    TextView mTextResultHint;
    int state;

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLinkWatchResultBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_link_watch_result);

        mTextResult = binding.resultText;
        mTextResultHint = binding.resultHint;

        binding.resultButton.setOnClickListener(view -> onOk());

        this.state = getIntent().getIntExtra("state", 0);
        if (this.state == 0) {
            this.mTextResult.setText(getString(R.string.error));
            this.mTextResultHint.setText(getString(R.string.error_hint));
        } else if (this.state == 2) {
            this.mTextResult.setText(getString(R.string.successful));
            this.mTextResultHint.setText(getString(R.string.successful_hint));
        } else if (this.state == -1) {
            this.mTextResult.setText(getString(R.string.error));
            this.mTextResultHint.setText(getString(R.string.permission_hint));
        } else {
            this.mTextResult.setText(getString(R.string.error));
            this.mTextResultHint.setText(getString(R.string.error_no_bt_hint));
        }
    }

    public void onOk() {
        if (this.state == -1) {
            startActivity(new Intent(this, SplashActivity.class));
        } else if (this.state != 2) {
            startActivity(new Intent(this, LinkWatchActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
