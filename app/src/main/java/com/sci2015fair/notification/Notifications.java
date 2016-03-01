package com.sci2015fair.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sci2015fair.R;
import com.sci2015fair.activity.MainActivity;
import com.sci2015fair.activity.SurveyActivity;
import com.sci2015fair.broadcastreceiver.AlarmReceiver;

import java.util.Calendar;

public class Notifications extends Service {
    public Notifications() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        notifyUser(getApplicationContext());
    //    scheduleServiceUpdates(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notifyUser(getApplicationContext());
        return 0;
    }

    protected void notifyUser(Context context) {

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.notification_icon)
                        .setContentTitle("App")
                        .setContentText("Survey required today.");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, SurveyActivity.class);
//        Intent intent = new Intent(this, .class);
//        this.startActivity(intent);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

       // createAlarm(context);
    }

    public void createAlarm(Context context) {
        Intent myIntent = new Intent(context, Notifications.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, myIntent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000 , pendingIntent);  //set repeating every 24 hours
    }

}
