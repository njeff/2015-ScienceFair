package com.sci2015fair.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CheckIfPersistentServicesRunning extends Service {
    public CheckIfPersistentServicesRunning() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void checkLocationTrackingService() {

    }

    public static void checkPedometerTrackingService() {

    }

    public static void checkNotificationService() {

    }

}
