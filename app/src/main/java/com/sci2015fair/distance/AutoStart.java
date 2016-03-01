package com.sci2015fair.distance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * http://stackoverflow.com/questions/4459058/alarm-manager-example
 */
public class AutoStart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent i = new Intent(context, DistanceService.class);
            context.startService(i);
        }
    }
}