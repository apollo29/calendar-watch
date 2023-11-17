package com.whatcalendar.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.whatcalendar.R;

public abstract class FragmentWelcomeWindow extends Fragment {
    public abstract void onOkClicked();

    public FragmentWelcomeWindow() {
        // constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
