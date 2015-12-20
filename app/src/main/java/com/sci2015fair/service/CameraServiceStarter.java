package com.sci2015fair.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sci2015fair.activity.ItemFragment;
import com.sci2015fair.activity.MainActivity;

/**
 * Created by Jeffrey on 9/6/2015.
 * Starts the camera service (CameraService) as soon as the phone is turned on and unlocked.
 * Activated by call in AndroidManifest.
 *
 * 12/13/2015 - renamed from ScreenReceiver to CameraServiceStarter by Mitchell Wu.
 */

public class CameraServiceStarter extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||//has the phone's boot sequence completed?
                intent.getAction().equals(Intent.ACTION_USER_PRESENT))//have we logged in?
        {
//            Intent camServ = new Intent(context, com.sci2015fair.service.Notifications.class);//create intent
            Intent camServ = new Intent(context, CameraService.class);//create intent
            context.startService(camServ);//start CameraService

//            camServ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(camServ);
        }
    }
}
