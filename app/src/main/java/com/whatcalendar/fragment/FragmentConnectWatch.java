package com.whatcalendar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.whatcalendar.R;
import com.whatcalendar.activity.WelcomeScreenActivity;
import com.whatcalendar.util.GlobalPreferences;

/* loaded from: classes.dex */
public class FragmentConnectWatch extends Fragment {
    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_watch, (ViewGroup) null);
        ((TextView) view.findViewById(R.id.bluetooth_settings_text)).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.fragment.FragmentConnectWatch.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction("android.settings.BLUETOOTH_SETTINGS");
                FragmentConnectWatch.this.startActivity(intentOpenBluetoothSettings);
            }
        });
        ((EditText) view.findViewById(R.id.edit_watch_id)).addTextChangedListener(new TextWatcher() { // from class: com.whatcalendar.fragment.FragmentConnectWatch.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                GlobalPreferences.putTempWatchId(s.toString());
            }
        });
        return view;
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View getView() {
        View view = super.getView();
        if (WelcomeScreenActivity.first_connection) {
            GlobalPreferences.putTempWatchId("");
            view.findViewById(R.id.first_pair_layout).setVisibility(0);
            view.findViewById(R.id.second_pair_layout).setVisibility(8);
        } else {
            getActivity().getWindow().getDecorView().postDelayed(FragmentConnectWatch$$Lambda$1.lambdaFactory$(view), 500L);
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$getView$0(View view) {
        view.findViewById(R.id.first_pair_layout).setVisibility(8);
        view.findViewById(R.id.second_pair_layout).setVisibility(0);
    }
}
