package com.whatcalendar.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import com.whatcalendar.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.commons.lang3.time.DateUtils;

/* loaded from: classes.dex */
public class NotificationReceiver extends BroadcastReceiver {
    public static final String EXTRA_TIME = "extra_time";
    static final String TAG = NotificationReceiver.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        long time = intent.getLongExtra(EXTRA_TIME, 0L);
        Log.d(TAG, "onReceive time: " + time);
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        int[] value = {now.get(1) % 100, now.get(2) + 1, now.get(5), now.get(11), now.get(12), now.get(13), 0, 0};
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String current_time = "Current time: " + df.format(now.getTime());
        String current_time2 = current_time + "\nValue:";
        for (int i = 0; i < value.length; i++) {
            current_time2 = current_time2 + " " + value[i];
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentTitle("Calendar Watch send time").setAutoCancel(true).setStyle(new NotificationCompat.BigTextStyle().bigText(current_time2)).setSmallIcon(R.mipmap.ic_launcher).setColor(ContextCompat.getColor(context, R.color.main_text_color)).setPriority(0).setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)).setContentText(current_time2);
        int id = (int) (time % DateUtils.MILLIS_PER_HOUR);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, builder.build());
    }
}
