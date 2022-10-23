package com.whatcalendar.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.whatcalendar.R;
import com.whatcalendar.activity.WelcomeScreenActivity;

/* loaded from: classes.dex */
public class FragmentResult extends Fragment {
    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, (ViewGroup) null);
        return view;
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View getView() {
        View view = super.getView();
        if (WelcomeScreenActivity.connection_state == 2) {
            ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.successful));
            ((TextView) view.findViewById(R.id.tv_text)).setText(getString(R.string.successful_text));
        } else if (WelcomeScreenActivity.connection_state == 5) {
            ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.successful));
            ((TextView) view.findViewById(R.id.tv_text)).setText(getString(R.string.successful_calibrated_text));
        } else {
            ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.error));
            ((TextView) view.findViewById(R.id.tv_text)).setText(getString(R.string.error_hint));
        }
        return view;
    }
}
