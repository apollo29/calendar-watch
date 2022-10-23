package com.whatcalendar.activity;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;
import com.whatcalendar.R;
import com.whatcalendar.activity.WelcomeScreenActivity;
import com.whatcalendar.view.LockedViewPager;
import me.relex.circleindicator.CircleIndicator;

/* loaded from: classes.dex */
public class WelcomeScreenActivity$$ViewBinder<T extends WelcomeScreenActivity> implements ButterKnife.ViewBinder<T> {
    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void bind(ButterKnife.Finder finder, Object obj, Object obj2) {
        bind(finder, (ButterKnife.Finder) ((WelcomeScreenActivity) obj), obj2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // butterknife.ButterKnife.ViewBinder
    public /* bridge */ /* synthetic */ void unbind(Object obj) {
        unbind((WelcomeScreenActivity$$ViewBinder<T>) ((WelcomeScreenActivity) obj));
    }

    public void bind(ButterKnife.Finder finder, final T target, Object source) {
        View view = (View) finder.findRequiredView(source, R.id.progress_pair_watch, "field 'mProgressPair'");
        target.mProgressPair = (ProgressBar) finder.castView(view, R.id.progress_pair_watch, "field 'mProgressPair'");
        View view2 = (View) finder.findRequiredView(source, R.id.welcome_viewPager, "field 'mWelcomeViewPager'");
        target.mWelcomeViewPager = (ViewPager) finder.castView(view2, R.id.welcome_viewPager, "field 'mWelcomeViewPager'");
        View view3 = (View) finder.findRequiredView(source, R.id.next_button, "field 'mNextButton'");
        target.mNextButton = (RelativeLayout) finder.castView(view3, R.id.next_button, "field 'mNextButton'");
        View view4 = (View) finder.findRequiredView(source, R.id.next_button_text, "field 'mNextButtonText'");
        target.mNextButtonText = (TextView) finder.castView(view4, R.id.next_button_text, "field 'mNextButtonText'");
        View view5 = (View) finder.findRequiredView(source, R.id.pair_watch_button_text, "field 'mPairWatchButtonText'");
        target.mPairWatchButtonText = (TextView) finder.castView(view5, R.id.pair_watch_button_text, "field 'mPairWatchButtonText'");
        View view6 = (View) finder.findRequiredView(source, R.id.try_again_button_text, "field 'mTryAgainButtonText'");
        target.mTryAgainButtonText = (TextView) finder.castView(view6, R.id.try_again_button_text, "field 'mTryAgainButtonText'");
        View view7 = (View) finder.findRequiredView(source, R.id.starting_viewPager, "field 'mStartingViewPager'");
        target.mStartingViewPager = (LockedViewPager) finder.castView(view7, R.id.starting_viewPager, "field 'mStartingViewPager'");
        View view8 = (View) finder.findRequiredView(source, R.id.fragment_welcome_window_layout, "field 'mWelcomeWindowLayout'");
        target.mWelcomeWindowLayout = (LinearLayout) finder.castView(view8, R.id.fragment_welcome_window_layout, "field 'mWelcomeWindowLayout'");
        View view9 = (View) finder.findRequiredView(source, R.id.indicator, "field 'mWelcomeFragmentIndicator'");
        target.mWelcomeFragmentIndicator = (CircleIndicator) finder.castView(view9, R.id.indicator, "field 'mWelcomeFragmentIndicator'");
        View view10 = (View) finder.findRequiredView(source, R.id.fragment_guide_layout, "field 'mFragmentGuideLayout'");
        target.mFragmentGuideLayout = (RelativeLayout) finder.castView(view10, R.id.fragment_guide_layout, "field 'mFragmentGuideLayout'");
        View view11 = (View) finder.findRequiredView(source, R.id.image_x_sign, "method 'onXbuttonClicked'");
        view11.setOnClickListener(new DebouncingOnClickListener() { // from class: com.whatcalendar.activity.WelcomeScreenActivity$$ViewBinder.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onXbuttonClicked();
            }
        });
    }

    public void unbind(T target) {
        target.mProgressPair = null;
        target.mWelcomeViewPager = null;
        target.mNextButton = null;
        target.mNextButtonText = null;
        target.mPairWatchButtonText = null;
        target.mTryAgainButtonText = null;
        target.mStartingViewPager = null;
        target.mWelcomeWindowLayout = null;
        target.mWelcomeFragmentIndicator = null;
        target.mFragmentGuideLayout = null;
    }
}
