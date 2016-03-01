package com.sci2015fair.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sci2015fair.notification.Notifications;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        context.startService(new Intent(context, Notifications.class));
        Log.d("YEAH", "ashfkjdshgjajwjgiweuiogfjbnfdklgrhthlknbdfjkth9q2irjknlwgbjisejtopremhkotuy90e4kgldfhioerip[wemtokrduhoprdtw4BOOTBOOTBOOTBOOTBOOTBOOTBOOT");

    }



}
