package com.sci2015fair.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppStartupServiceHub extends Service {
    public AppStartupServiceHub() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        Intent checkFiles = new Intent(this, CheckFilesPresentService.class);//create intent
//        this.startService(checkFiles);//start services that run on starting up the app
//        Intent takePicture = new Intent(this, CameraService.class);//create intent
//        this.startService(takePicture);//start services that run on starting up the app
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
