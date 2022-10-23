package com.whatcalendar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.whatcalendar.R;

/* loaded from: classes.dex */
public abstract class FragmentWelcomeWindow extends Fragment {
    public abstract void onOkClicked();

    @Override // android.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome_window, (ViewGroup) null);
        view.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() { // from class: com.whatcalendar.fragment.FragmentWelcomeWindow.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                FragmentWelcomeWindow.this.onOkClicked();
            }
        });
        return view;
    }
}
