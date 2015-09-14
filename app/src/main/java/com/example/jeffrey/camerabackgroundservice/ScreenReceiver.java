package com.example.jeffrey.camerabackgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jeffrey on 9/6/2015.
 * Starts the camera service when the phone is unlocked
 */
public class ScreenReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent){
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals(Intent.ACTION_USER_PRESENT))
        {
            Intent intent1 = new Intent(context,CameraUI.class);
            context.startService(intent1);
        }
    }
}
