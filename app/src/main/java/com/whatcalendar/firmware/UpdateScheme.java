package com.whatcalendar.firmware;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/* loaded from: classes.dex */
public interface UpdateScheme {
    public static final String serverUrl = "https://www.backwhatwatch.com:443/";

    @GET
    Call<ResponseBody> downloadFile(@Url String str);

    @GET("/gwatch/watch_fw.json")
    Call<GWatchResponse> getFirmwareVersion();
}
