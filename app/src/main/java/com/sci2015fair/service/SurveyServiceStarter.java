package com.sci2015fair.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sci2015fair.activity.ItemFragment;

/**
 * Created by Mitchell on 12/13/2015.
 */

public class SurveyServiceStarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        System.out.println(intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||//has the phone's boot sequence completed?
                intent.getAction().equals(Intent.ACTION_USER_PRESENT))//have we logged in?
        {
            Intent camServ = new Intent(context,ItemFragment.class);//create intent
            context.startService(camServ);//start CameraService
        }
    }
}
