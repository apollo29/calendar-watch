package com.whatcalendar.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityWelcomeScreenBinding;
import com.whatcalendar.fragment.FragmentCalibration;
import com.whatcalendar.fragment.FragmentConnectWatch;
import com.whatcalendar.fragment.FragmentGettingStarted;
import com.whatcalendar.fragment.FragmentGuideWindow;
import com.whatcalendar.fragment.FragmentResult;
import com.whatcalendar.fragment.FragmentWebRegistration;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;
import com.whatcalendar.view.LockedViewPager;

import me.relex.circleindicator.CircleIndicator;

/* loaded from: classes.dex */
public class WelcomeScreenActivity extends AppCompatActivity {
    private ActivityWelcomeScreenBinding binding;

    public static final int ANIMATION_DURATION = 400;
    private static final int LOCATION_REQUEST_CODE = 2;
    public static final String ONLY_GUIDE = "only_guide";
    public static final int STATE_CALIBRATED = 5;
    private static final String TAG = WelcomeScreenActivity.class.getSimpleName();
    public static int connection_state = 0;
    public static boolean first_connection = true;
    private BackgroundService mBackgroundService;
    RelativeLayout mFragmentGuideLayout;
    FragmentTransaction mFragmentTransaction;
    //FragmentWelcomeWindow mFragmentWelcomeWindow;
    RelativeLayout mNextButton;
    TextView mNextButtonText;
    TextView mPairWatchButtonText;
    ProgressBar mProgressPair;
    PagerAdapter mStartingPageAdapter;
    LockedViewPager mStartingViewPager;
    TextView mTryAgainButtonText;
    CircleIndicator mWelcomeFragmentIndicator;
    PagerAdapter mWelcomePagerAdapter;
    ViewPager mWelcomeViewPager;
    LinearLayout mWelcomeWindowLayout;
    Context mContext = null;
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            WelcomeScreenActivity.this.mBackgroundService = ((BackgroundService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            WelcomeScreenActivity.this.mBackgroundService = null;
        }
    };
    private View.OnClickListener onNextButtonClickListener = new View.OnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.3
        @Override // android.view.View.OnClickListener
        public void onClick(View v) {
            if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 0) {
                WelcomeScreenActivity.this.showConnectWatchWindow();
            } else if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 1) {
                WelcomeScreenActivity.this.showConnectWatchWindow();
            } else if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 2) {
                WelcomeScreenActivity.this.checkAgreeAndStartPairing();
            } else if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 3) {
                if (WelcomeScreenActivity.connection_state != 2) {
                    WelcomeScreenActivity.this.showConnectWatchWindow();
                } else {
                    WelcomeScreenActivity.this.showCalibrateWindow();
                }
            } else if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 4) {
                WelcomeScreenActivity.this.showFinishWindow();
            } else if (WelcomeScreenActivity.this.mStartingViewPager.getCurrentItem() == 5) {
                if (GlobalPreferences.getFirstShowWelcome()) {
                    WelcomeScreenActivity.this.showGuideWindow();
                } else {
                    WelcomeScreenActivity.this.onXbuttonClicked();
                }
            }
        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.15
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BackgroundService.ACTION_CONNECTION_STATE_CHANGE.equals(intent.getAction())) {
                int state = intent.getIntExtra("state", 0);
                Log.d(WelcomeScreenActivity.TAG, "state connection: " + state);
                if (state != 1) {
                    if (state != 2) {
                        try {
                            WelcomeScreenActivity.this.unbindService(WelcomeScreenActivity.this.mServiceConnection);
                        } catch (Exception e) {
                        }
                    }
                    WelcomeScreenActivity.this.stopPairingWatch(state);
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        binding = ActivityWelcomeScreenBinding.inflate(getLayoutInflater());

        first_connection = true;
        this.mContext = this;

        mFragmentGuideLayout = binding.fragmentGuideLayout;
        mNextButton = binding.nextButton;
        mNextButtonText = binding.nextButtonText;
        mPairWatchButtonText = binding.pairWatchButtonText;
        mProgressPair = binding.progressPairWatch;
        mStartingViewPager = binding.startingViewPager;
        mTryAgainButtonText = binding.tryAgainButtonText;
        mWelcomeFragmentIndicator = binding.indicator;
        mWelcomeViewPager = binding.welcomeViewPager;
        mWelcomeWindowLayout = binding.fragmentWelcomeWindowLayout;

        binding.imageXSign.setOnClickListener(view -> onXbuttonClicked());

        initViews();
    }

    @SuppressLint("ValidFragment")
    private void initViews() {
        this.mWelcomePagerAdapter = new WelcomeFragmentPageAdapter(getSupportFragmentManager());
        this.mWelcomeViewPager.setAdapter(this.mWelcomePagerAdapter);
        this.mWelcomeFragmentIndicator.setViewPager(this.mWelcomeViewPager);
        if (getIntent().getBooleanExtra(ONLY_GUIDE, false)) {
            this.mFragmentGuideLayout.setAlpha(1.0f);
            this.mFragmentGuideLayout.setVisibility(View.VISIBLE);
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        findViewById(R.id.fragment_starting_layout).setTranslationY(metrics.heightPixels);
        this.mProgressPair.getIndeterminateDrawable().setColorFilter(-7829368, PorterDuff.Mode.MULTIPLY);
        this.mStartingPageAdapter = new StartingFragmentPageAdapter(getSupportFragmentManager());
        this.mStartingViewPager.setAdapter(this.mStartingPageAdapter);
        this.mNextButton.setOnClickListener(this.onNextButtonClickListener);
        /*
        this.mFragmentWelcomeWindow = new FragmentWelcomeWindow() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.2
            @Override // com.whatcalendar.fragment.FragmentWelcomeWindow
            public void onOkClicked() {
                WelcomeScreenActivity.this.hideWelcomeWindow();
            }
        };
        if (GlobalPreferences.getFirstShowWelcome()) {
            showWelcomeWindow();
            return;
        }
         */
        findViewById(R.id.logo_layout).setTranslationY(0.0f);
        findViewById(R.id.text_logo).setAlpha(0.0f);
        findViewById(R.id.main_background).setAlpha(1.0f);
        findViewById(R.id.image_logo).setScaleX(0.7f);
        findViewById(R.id.image_logo).setScaleY(0.7f);
        findViewById(R.id.fragment_starting_layout).setTranslationY(0.0f);
        this.mWelcomeWindowLayout.setVisibility(View.GONE);
        this.mStartingViewPager.setCurrentItem(2, false);
        this.mNextButtonText.setVisibility(View.INVISIBLE);
        this.mPairWatchButtonText.setVisibility(View.VISIBLE);
        this.mPairWatchButtonText.setAlpha(1.0f);
    }

    public void onRegistrationSuccess() {
        showConnectWatchWindow();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showGuideWindow() {
        GlobalPreferences.setFirstShowWelcome(false);
        this.mFragmentGuideLayout.animate().alpha(1.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.4
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                WelcomeScreenActivity.this.mFragmentGuideLayout.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFinishWindow() {
        int[] values = ((StartingFragmentPageAdapter) this.mStartingPageAdapter).getPickersValue();
        if (values != null) {
            this.mBackgroundService.calibrateWatch(values[0], values[1], 0, values[2], 0);
            connection_state = 5;
            this.mStartingViewPager.setCurrentItem(5, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showCalibrateWindow() {
        this.mBackgroundService.calibrateStart();
        this.mStartingViewPager.setCurrentItem(4, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopPairingWatch(int state) {
        connection_state = state;
        first_connection = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
        if (state == 2) {
            showSuccessfulWindow();
        } else {
            showErrorWindow();
        }
    }

    private void showSuccessfulWindow() {
        this.mStartingViewPager.setCurrentItem(3, true);
        this.mNextButtonText.animate().alpha(1.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.5
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                WelcomeScreenActivity.this.mProgressPair.setVisibility(View.INVISIBLE);
                WelcomeScreenActivity.this.mNextButtonText.setVisibility(View.VISIBLE);
                WelcomeScreenActivity.this.mNextButton.setEnabled(true);
                super.onAnimationStart(animation);
            }
        });
    }

    private void showErrorWindow() {
        this.mStartingViewPager.setCurrentItem(3, true);
        this.mTryAgainButtonText.animate().alpha(1.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.6
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                WelcomeScreenActivity.this.mProgressPair.setVisibility(View.INVISIBLE);
                WelcomeScreenActivity.this.mTryAgainButtonText.setVisibility(View.VISIBLE);
                WelcomeScreenActivity.this.mNextButton.setEnabled(true);
                super.onAnimationStart(animation);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkAgreeAndStartPairing() {
        if (!isBtEnable()) {
            showAlertMessage(getString(R.string.bluetooth_connection_alert));
        } else if (checkLocation()) {
            showTermsAlertMessage();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startPairingWatch() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        this.mPairWatchButtonText.animate().alpha(0.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.7
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                WelcomeScreenActivity.this.mNextButton.setEnabled(false);
            }

            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                WelcomeScreenActivity.this.mPairWatchButtonText.setVisibility(View.INVISIBLE);
                WelcomeScreenActivity.this.mProgressPair.setVisibility(View.VISIBLE);
            }
        });
        GlobalPreferences.setPairingMode(true);
        bindService(new Intent(this, BackgroundService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        IntentFilter intentFilter = new IntentFilter(BackgroundService.ACTION_BATTERY_INFO);
        intentFilter.addAction(BackgroundService.ACTION_CONNECTION_STATE_CHANGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: showWebRegistrationWindow */
    public void showWebRegistrationWindow() {
        if (!isOnline()) {
            showAlertMessage(getString(R.string.internet_connection_alert));
            return;
        }
        this.mNextButton.setVisibility(View.GONE);
        this.mStartingViewPager.setCurrentItem(1, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConnectWatchWindow() {
        this.mNextButton.setVisibility(View.VISIBLE);
        getWindow().setSoftInputMode(32);
        this.mStartingViewPager.setCurrentItem(2, true);
        this.mTryAgainButtonText.animate().alpha(0.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.8
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                WelcomeScreenActivity.this.mTryAgainButtonText.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        this.mNextButtonText.animate().alpha(0.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.9
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                WelcomeScreenActivity.this.mNextButtonText.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        this.mPairWatchButtonText.animate().alpha(1.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.10
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                WelcomeScreenActivity.this.mPairWatchButtonText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showTermsAlertMessage() {
        if (!first_connection || !GlobalPreferences.getFirstShowWelcome()) {
            startPairingWatch();
            return;
        }
        final Dialog alert_dialog = new Dialog(this);
        alert_dialog.requestWindowFeature(1);
        alert_dialog.setContentView(R.layout.alert_dialog);
        ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) alert_dialog.findViewById(R.id.ok_button_text)).setText(getString(R.string.agree));
        alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.11
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                WelcomeScreenActivity.this.startPairingWatch();
                alert_dialog.dismiss();
            }
        });
        alert_dialog.findViewById(R.id.cancel_button).setVisibility(View.VISIBLE);
        alert_dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.12
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                alert_dialog.dismiss();
            }
        });
        alert_dialog.show();
    }

    private void showAlertMessage(String message) {
        final Dialog alert_dialog = new Dialog(this);
        alert_dialog.requestWindowFeature(1);
        alert_dialog.setContentView(R.layout.alert_dialog);
        ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(message);
        alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.13
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                alert_dialog.dismiss();
            }
        });
        alert_dialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showStartingWindow() {
        findViewById(R.id.logo_layout).animate().translationY(0.0f).setDuration(400L);
        findViewById(R.id.text_logo).animate().alpha(0.0f).setDuration(400L);
        findViewById(R.id.main_background).animate().alpha(1.0f).setDuration(400L);
        findViewById(R.id.image_logo).animate().scaleX(0.7f).scaleY(0.7f).setDuration(400L);
        findViewById(R.id.fragment_starting_layout).animate().translationY(0.0f).setDuration(400L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /*
    public void hideWelcomeWindow() {
        this.mWelcomeWindowLayout.animate().translationY(getResources().getDimension(R.dimen.welcome_window_size)).setDuration(400L).setListener(new AnimatorListenerAdapter() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.14
            @Override
            // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                WelcomeScreenActivity.this.mFragmentTransaction = WelcomeScreenActivity.this.getFragmentManager().beginTransaction();
                WelcomeScreenActivity.this.mFragmentTransaction.remove(WelcomeScreenActivity.this.mFragmentWelcomeWindow);
                WelcomeScreenActivity.this.mFragmentTransaction.commit();
                WelcomeScreenActivity.this.showStartingWindow();
                super.onAnimationEnd(animation);
            }
        });
        findViewById(R.id.main_background).animate().alpha(0.9f).setDuration(400L);
    }

    private void showWelcomeWindow() {
        this.mFragmentTransaction = getFragmentManager().beginTransaction();
        this.mFragmentTransaction.add(R.id.fragment_welcome_window_layout, this.mFragmentWelcomeWindow);
        this.mFragmentTransaction.commit();
        getWindow().getDecorView().postDelayed(() -> {
            this.mWelcomeWindowLayout.animate().translationY(0.0f).setDuration(400L);
            this.mWelcomeWindowLayout.animate().alpha(1.0f).setDuration(400L);
            findViewById(R.id.main_background).setBackgroundColor(getResources().getColor(R.color.main_background));
        }, 1500L);
    }

     */

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onXbuttonClicked() {
        if (getIntent().getBooleanExtra(ONLY_GUIDE, false)) {
            finish();
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        GlobalPreferences.setFirstShowWelcome(false);
        finish();
    }

    /* loaded from: classes.dex */
    private class StartingFragmentPageAdapter extends FragmentStatePagerAdapter {
        private FragmentCalibration mFragmentCalibration = null;

        public int[] getPickersValue() {
            if (this.mFragmentCalibration != null) {
                return this.mFragmentCalibration.getPuckerValue();
            }
            return null;
        }

        public StartingFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter
        public Fragment getItem(int position) {
            if (position == 0) {
                return new FragmentGettingStarted();
            } else if (position == 1) {
                return new FragmentWebRegistration();
            } else if (position == 2) {
                return new FragmentConnectWatch();
            } else if (position == 3) {
                return new FragmentResult();
            } else if (position == 4) {
                Fragment fragment = new FragmentCalibration();
                this.mFragmentCalibration = (FragmentCalibration) fragment;
                return fragment;
            } else if (position != 5) {
                return null;
            } else {
                return new FragmentResult();
            }
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return 6;
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isBtEnable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /* loaded from: classes.dex */
    private class WelcomeFragmentPageAdapter extends FragmentStatePagerAdapter {
        public WelcomeFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override // android.support.v4.app.FragmentStatePagerAdapter
        public Fragment getItem(int position) {
            return FragmentGuideWindow.newInstance(position);
        }

        @Override // android.support.v4.view.PagerAdapter
        public int getCount() {
            return 5;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        try {
            unbindService(this.mServiceConnection);
        } catch (Exception e) {
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.mBroadcastReceiver);
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
            final Dialog alert_dialog = new Dialog(this);
            alert_dialog.requestWindowFeature(1);
            alert_dialog.setContentView(R.layout.alert_dialog);
            ((TextView) alert_dialog.findViewById(R.id.alert_dialog_text)).setText(getString(R.string.dialog_msg_location));
            alert_dialog.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity.16
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    Intent enableLocationIntent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                    ((Activity) WelcomeScreenActivity.this.mContext).startActivityForResult(enableLocationIntent, 2);
                    alert_dialog.dismiss();
                }
            });
            alert_dialog.show();
            return false;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
