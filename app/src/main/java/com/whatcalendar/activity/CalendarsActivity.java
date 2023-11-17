package com.whatcalendar.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.whatcalendar.R;
import com.whatcalendar.databinding.ActivityCalendarsBinding;
import com.whatcalendar.events.DTOCalendar;
import com.whatcalendar.service.BackgroundService;
import com.whatcalendar.util.GlobalPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* loaded from: classes.dex */
public class CalendarsActivity extends AppCompatActivity {
    private static final String TAG = CalendarsActivity.class.getSimpleName();
    private BackgroundService mBackgroundService;
    private ActivityCalendarsBinding binding;
    ArrayList<DTOCalendar> calendars = new ArrayList<>();
    private ServiceConnection mServiceConnection = new ServiceConnection() { // from class: com.whatcalendar.activity.CalendarsActivity.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            CalendarsActivity.this.mBackgroundService = ((BackgroundService.LocalBinder) service).getService();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            CalendarsActivity.this.mBackgroundService = null;
        }
    };

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.BaseFragmentActivityGingerbread, android.app.Activity
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendars);
        binding = ActivityCalendarsBinding.inflate(getLayoutInflater());
        bindService(new Intent(this, BackgroundService.class), this.mServiceConnection, Context.BIND_AUTO_CREATE);
        binding.recyclerView.setAdapter(new CalendarsAdapter());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.toolbarIcon.setOnClickListener(view -> onCloseButtonClicked());
        loadCalendars();
    }

    @Override
    // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.mBackgroundService != null) {
            this.mBackgroundService.updateAllDayPatterns();
        }
        unbindService(this.mServiceConnection);
    }

    private void loadCalendars() {
        this.calendars.clear();
        Uri.Builder eventsUriBuilder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        Uri contentCalendars = eventsUriBuilder.build();
        String[] PROJECTION = {"_id", "calendar_displayName", "account_name"};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursorCalendars = contentResolver.query(contentCalendars, PROJECTION, null, null, null);
        cursorCalendars.moveToFirst();
        int calendarsCount = cursorCalendars.getCount();
        for (int i = 0; i < calendarsCount; i++) {
            DTOCalendar calendar = new DTOCalendar();
            calendar.id = cursorCalendars.getString(0);
            calendar.title = cursorCalendars.getString(1);
            calendar.account = cursorCalendars.getString(2);
            Log.d(TAG, "calendar ID: " + calendar.id + " title: " + calendar.title + " account: " + calendar.account);
            this.calendars.add(calendar);
            cursorCalendars.moveToNext();
        }
        Collections.sort(this.calendars, new Comparator<DTOCalendar>() { // from class: com.whatcalendar.activity.CalendarsActivity.2
            public int compare(DTOCalendar lhs, DTOCalendar rhs) {
                return lhs.account.compareTo(rhs.account);
            }
        });
        binding.recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void onCloseButtonClicked() {
        onBackPressed();
    }

    /* loaded from: classes.dex */
    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout calAccLayout;
        TextView calAccName;
        Switch calSwitch;
        TextView calTitle;
        View divider;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ViewHolder(View itemView) {
            super(itemView);
            this.calTitle = (TextView) itemView.findViewById(R.id.calendar_title);
            this.calSwitch = (Switch) itemView.findViewById(R.id.calendar_switch);
            this.calAccLayout = (RelativeLayout) itemView.findViewById(R.id.account_background);
            this.calAccName = (TextView) itemView.findViewById(R.id.account_name);
            this.divider = itemView.findViewById(R.id.divider);
        }
    }

    /* loaded from: classes.dex */
    public class CalendarsAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = CalendarsActivity.this.getLayoutInflater().inflate(R.layout.view_calendar_item, parent, false);
            return new ViewHolder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            final DTOCalendar calendar = CalendarsActivity.this.calendars.get(position);
            holder.calTitle.setText(calendar.title);
            if (position == 0 || !calendar.account.equals(CalendarsActivity.this.calendars.get(position - 1).account)) {
                holder.calAccLayout.setVisibility(View.VISIBLE);
                holder.calAccName.setText(calendar.account);
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.calAccLayout.setVisibility(View.GONE);
                holder.divider.setVisibility(View.VISIBLE);
            }
            boolean ckecked = GlobalPreferences.getCalendarEnabled(calendar.id);
            Log.d(CalendarsActivity.TAG, "get " + calendar.id + " " + ckecked);
            holder.calSwitch.setOnCheckedChangeListener(null);
            holder.calSwitch.setChecked(ckecked);
            holder.calSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.whatcalendar.activity.CalendarsActivity.CalendarsAdapter.1
                @Override // android.widget.CompoundButton.OnCheckedChangeListener
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(CalendarsActivity.TAG, "set " + calendar.id + " " + isChecked);
                    GlobalPreferences.putCalendarEnabled(calendar.id, isChecked);
                }
            });
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            return CalendarsActivity.this.calendars.size();
        }
    }
}
