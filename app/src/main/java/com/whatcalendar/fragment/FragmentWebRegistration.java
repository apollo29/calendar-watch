package com.whatcalendar.fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.whatcalendar.R;
import com.whatcalendar.activity.WelcomeScreenActivity;

/* loaded from: classes.dex */
public class FragmentWebRegistration extends Fragment {
    ConnectionTask connection = null;
    WebView wv;

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_registartion, (ViewGroup) null);
        this.wv = (WebView) view.findViewById(R.id.registration_web_view);
        this.wv.getSettings().setJavaScriptEnabled(true);
        this.wv.setWebViewClient(new MyWebViewClient());
        this.connection = new ConnectionTask();
        new Thread(this.connection).start();
        return view;
    }

    @Override // android.support.v4.app.Fragment
    public void onDestroy() {
        if (this.connection != null) {
            this.connection.stopConnection = true;
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ConnectionTask implements Runnable {
        public boolean stopConnection = false;

        ConnectionTask() {
        }

        @Override // java.lang.Runnable
        public void run() {
            while (!FragmentWebRegistration.this.isOnline() && !this.stopConnection) {
                try {
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!this.stopConnection) {
                FragmentWebRegistration.this.getActivity().runOnUiThread(new Runnable() { // from class: com.whatcalendar.fragment.FragmentWebRegistration.ConnectionTask.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (FragmentWebRegistration.this.wv != null) {
                            FragmentWebRegistration.this.wv.loadUrl(FragmentWebRegistration.this.getString(R.string.registration_url));
                        }
                    }
                });
            }
        }
    }

    /* loaded from: classes.dex */
    private class MyWebViewClient extends WebViewClient {
        private MyWebViewClient() {
        }

        @Override // android.webkit.WebViewClient
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("MyWebViewClient", "url " + url);
            if (url.toLowerCase().contains(FragmentWebRegistration.this.getString(R.string.registration_success_url).toLowerCase())) {
                ((WelcomeScreenActivity) FragmentWebRegistration.this.getActivity()).onRegistrationSuccess();
                return true;
            }
            view.loadUrl(FragmentWebRegistration.this.getString(R.string.registration_url));
            return true;
        }
    }

    public boolean isOnline() {
        if (getActivity() == null) {
            this.connection.stopConnection = true;
            return true;
        }
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService("connectivity");
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
