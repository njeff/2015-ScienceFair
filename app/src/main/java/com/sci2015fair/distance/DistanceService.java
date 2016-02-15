package com.sci2015fair.distance;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Code from:
 * http://stackoverflow.com/questions/4459058/alarm-manager-example
 */
public class DistanceService extends Service{
    DistanceCalculator dc = new DistanceCalculator();
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        dc.SetAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        dc.SetAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}