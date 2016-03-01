package com.sci2015fair.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SystemStartupServBroadcastReceiverHub extends BroadcastReceiver {
    public SystemStartupServBroadcastReceiverHub() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {//has the phone's boot sequence completed?
            Log.d("YEAH", "BOOT");

            Intent checkFiles = new Intent(context, CheckFilesPresentService.class);//create intent
            context.startService(checkFiles);//start services that run on starting up the app

            Intent takePicture = new Intent(context, CameraService.class);//create intent
            context.startService(takePicture);

            Intent LocServ = new Intent(context, LocationGPSLogPersistentService.class);//create intent
            context.startService(LocServ);
        }
    }
}
