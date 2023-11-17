package com.whatcalendar.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.whatcalendar.R;

/* loaded from: classes.dex */
public class FragmentGettingStarted extends Fragment {
    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_getting_started, (ViewGroup) null);
        return view;
    }
}
