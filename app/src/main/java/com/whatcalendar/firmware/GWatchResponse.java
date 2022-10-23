package com.whatcalendar.firmware;

/* loaded from: classes.dex */
public class GWatchResponse {
    public String fw_url;
    public String fw_ver;

    public GWatchResponse(String vers, String url) {
        this.fw_ver = vers;
        this.fw_url = url;
    }

    public String getFw_ver() {
        return this.fw_ver;
    }

    public void setFw_ver(String fw_ver) {
        this.fw_ver = fw_ver;
    }

    public String getFw_url() {
        return this.fw_url;
    }

    public void setFw_url(String fw_url) {
        this.fw_url = fw_url;
    }
}
