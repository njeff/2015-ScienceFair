package com.sci2015fair.broadcastreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sci2015fair.fileoperations.ConsoleLogCSVWriter;
import com.sci2015fair.notification.Notifications;
import com.sci2015fair.service.CameraService;
import com.sci2015fair.service.CheckFilesPresentService;
import com.sci2015fair.service.LocationGPSLogPersistentService;

import java.util.Calendar;

public class SystemStartupServBroadcastReceiverHub extends BroadcastReceiver {
    public SystemStartupServBroadcastReceiverHub() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        System.out.println(intent.getAction());
//        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {//has the phone's boot sequence completed?
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {//has the phone's boot sequence completed?
//        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||//has the phone's boot sequence completed?
//                intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {//have we logged in?
            Log.d("YEAH", "");
//            ConsoleLogCSVWriter.writeCsvFile("AutoCamera", String.valueOf(bottomBound) + "/" + cameraheightres);
            Intent checkFiles = new Intent(context, CheckFilesPresentService.class);//create intent
            context.startService(checkFiles);

//            Intent notServ = new Intent(context, Notifications.class);//create intent
//            context.startService(notServ);

            Intent takePicture = new Intent(context, CameraService.class);//create intent
            context.startService(takePicture);//start services that run on starting up the app
//            throw new UnsupportedOperationException("Not yet implemented");
//
//
//
//
//
////            Intent camServ = new Intent(context, com.sci2015fair.notification.Notifications.class);//create intent
            Intent LocServ = new Intent(context, LocationGPSLogPersistentService.class);//create intent
            context.startService(LocServ);//start CameraService

//
////            camServ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////            context.startActivity(camServ);
        }
    }

    /**
     * Schedule the next update
     *
     * @param context
     *            the current application context
     */

    private void scheduleServiceUpdates(final Context context) {
        // create intent for our alarm receiver (or update it if it exists)
        Log.d("YEAH", "sashfkjdshgjajwjgiweuiogfjbnfdklgrhthlknbdfjkth9q2irjknlwgbjisejtopremhkotuy90e4kgldfhioerip[wemtokrduhoprdtw4BOOTBOOTBOOTBOOTBOOTBOOTBOOT");
        final Intent intent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pending = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // compute first call time 1 minute from now
        Calendar calendar = Calendar.getInstance();
        // calendar.add(Calendar.MINUTE, 10);
        calendar.add(Calendar.MILLISECOND, 10000);
        long trigger = calendar.getTimeInMillis();

        long delay = 24 * 60 * 60 * 1000;//set delay between each notification call: 24 Hours





        Intent myIntent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        // Set alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar initialFiringTime = Calendar.getInstance();//create calendar object for the initial time at which the notification will fire for the first time, getInstance() specifies the locale/time zone
        initialFiringTime.set(Calendar.HOUR_OF_DAY, 16);
        initialFiringTime.set(Calendar.MINUTE, 52);
        initialFiringTime.set(Calendar.SECOND, 0);
        initialFiringTime.set(Calendar.MILLISECOND, 0);
        Calendar currentTime = Calendar.getInstance();

        long initFirTime = initialFiringTime.getTimeInMillis();
        long presentTime = currentTime.getTimeInMillis();

        if(initFirTime >= presentTime) // you can add buffer time too here to ignore some small differences in milliseconds
        {
            //set from today
            alarmManager.setRepeating(AlarmManager.RTC,
                    initFirTime , AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        }
        else{
            //set from next day
            // you might consider using calendar.add() for adding one day to the current day
            initialFiringTime.add(Calendar.DAY_OF_MONTH, 1);
            initFirTime = initialFiringTime.getTimeInMillis();

            alarmManager.setRepeating(AlarmManager.RTC,
                    initFirTime , AlarmManager.INTERVAL_DAY,
                    pendingIntent);

        }
    }

}
