package com.whatcalendar.util;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import okhttp3.ResponseBody;

/* loaded from: classes.dex */
public class FileWritter {
    private static final String TAG = FileWritter.class.getSimpleName();

    public static File writeResponseBodyToDisk(ResponseBody body, String name, Context context) {
        Throwable th;
        try {
            File file = new File(context.getExternalFilesDir(null) + File.separator + name + ".zip");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                OutputStream outputStream2 = new FileOutputStream(file);
                while (true) {
                    try {
                        int read = inputStream.read(fileReader);
                        if (read == -1) {
                            break;
                        }
                        outputStream2.write(fileReader, 0, read);
                        fileSizeDownloaded += read;
                        Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    } catch (IOException e) {
                        outputStream = outputStream2;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream == null) {
                            return null;
                        }
                        outputStream.close();
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        outputStream = outputStream2;
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        throw th;
                    }
                }
                outputStream2.flush();
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream2 == null) {
                    return file;
                }
                outputStream2.close();
                return file;
            } catch (IOException e2) {
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (IOException e3) {
            return null;
        }
    }
}
