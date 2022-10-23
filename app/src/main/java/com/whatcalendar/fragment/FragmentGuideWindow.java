package com.whatcalendar.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.whatcalendar.R;

/* loaded from: classes.dex */
public class FragmentGuideWindow extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    public static final int PAGE_COUNT = 5;
    int pageNumber = 0;
    private static final String TAG = FragmentGuideWindow.class.getSimpleName();
    private static final Integer[] IMAGES = {Integer.valueOf((int) R.drawable.welcome_1), Integer.valueOf((int) R.drawable.welcome_3), Integer.valueOf((int) R.drawable.welcome_4), Integer.valueOf((int) R.drawable.welcome_5), Integer.valueOf((int) R.drawable.welcome_6)};

    public static FragmentGuideWindow newInstance(int page) {
        FragmentGuideWindow pageFragment = new FragmentGuideWindow();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override // android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        Log.d(TAG, "onCreate page: " + this.pageNumber);
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView page: " + this.pageNumber);
        View view = inflater.inflate(R.layout.fragment_welcome_page, (ViewGroup) null);
        ImageView ivPage = (ImageView) view.findViewById(R.id.welcome_image);
        ivPage.setImageResource(IMAGES[this.pageNumber].intValue());
        return view;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        Log.d(TAG, "onDestroy page: " + this.pageNumber);
        super.onDestroy();
    }
}
