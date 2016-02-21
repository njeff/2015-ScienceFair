package com.sci2015fair.distance;

import com.sci2015fair.filecontrolcenter.SaveLocations;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.sci2015fair.fileoperations.LocationGPSLogCSVReader.readLocationCSVFile;

/**
 * Calculates the approximate distance traveled per day
 *
 */
public class DistanceCalculator extends WakefulBroadcastReceiver{
    private static String TAG = "Distance";
    /*
    if current point is within 10m of last point, do not add to distance traveled
    else add distance between this point and last point that counted towards distance traveled
    calculate total distance for the day
    find difference between distance for previous day to check of pattern
    graph movement delta
     - will likely need to save in new text file
     - how to prevent writing distance day all over again each time this is run (how to check for existing data)
     - or run when graph is brought up (will create very laggy UX)
    */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        dayDistance();
    }

    public void SetAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent("distance.START_ALARM");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        // adjust time here
        // update distance every (12) hours
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pi);
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, DistanceCalculator.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    private static void dayDistance() {
        //extremely inefficient to reparse file each time
        Log.d(TAG, "Calculating Distance!");
        ArrayList<LocationIDObject> allLocationData = new ArrayList<>(readLocationCSVFile(SaveLocations.DFLocationGPSLogCSV));
        File tdl = SaveLocations.TotalDistanceLog;
        SaveLocations.dataFolder.mkdirs(); //make sure directories exist
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tdl, false)); //add header
            bw.write("Date,Distance,Delta,Points,FractionCluster");
            bw.write("\n");

            Location lastl = allLocationData.get(0).getLocation();
            double lastDT = 0; //previous day's distance travelled
            double dayTotal = 0; //current day's distance travlled
            double delta = 0; //difference between today and yesterday
            String pdate = allLocationData.get(0).getDate(); //previousDate

            DecimalFormat df = new DecimalFormat("0.000");
            int maxClusterSize = 0;
            int clusterCount = 0; //count how long a user is in once place for the day
            int dayCount = 1;
            for(int i = 1; i<allLocationData.size(); i++){ //go through all the points
                String cD = allLocationData.get(i).getDate(); //current date
                Location c = allLocationData.get(i).getLocation();
                double distance = c.distanceTo(lastl); //in meters

                if(cD.equals(pdate)&&(i!=allLocationData.size()-1)){ //if the same day (and not the last day)
                    if(distance>30){ //if big enough movement (prevent clusters from adding up into large movement)
                        if(clusterCount>maxClusterSize){
                            maxClusterSize = clusterCount;
                        }
                        clusterCount = 0;
                        dayTotal += distance;
                        lastl = c; //update last point with significant movement
                    } else {
                        clusterCount++;
                    }
                    dayCount++;
                } else { //if day changed
                    dayTotal += distance; //add any last distance
                    delta = dayTotal-lastDT; //change between the day that just ended and the day before that

                    double notMoving = (double)maxClusterSize/(double)dayCount;

                    //save data here:
                    bw.write(pdate + ",");
                    bw.write(df.format(dayTotal) + ",");
                    bw.write(df.format(delta) + ",");
                    bw.write(Integer.toString(dayCount) + ",");
                    bw.write(df.format(notMoving) + "\n");

                    lastDT = dayTotal; //save last amount
                    dayTotal = 0;
                    maxClusterSize = 0;
                    dayCount = 1;
                    lastl = c; //force location transition
                }
                pdate = cD; //keep date moving
                bw.flush();
            }

            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG,"Done!");
    }
}