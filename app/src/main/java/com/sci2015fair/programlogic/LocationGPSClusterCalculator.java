package com.sci2015fair.programlogic;

import com.sci2015fair.filecontrolcenter.SaveLocations;
import android.location.Location;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.sci2015fair.fileoperations.LocationGPSLogCSVReader.readLocationCSVFile;

/**
 * Created by Mitchell on 12/29/2015.
 */
public class LocationGPSClusterCalculator {

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

    public static void dayDistance() {
        //extremely inefficient to reparse file each time
        ArrayList<LocationIDObject> allLocationData = new ArrayList<>(readLocationCSVFile(SaveLocations.DFLocationGPSLogCSV));

        Location lastl = allLocationData.get(0).getLocation();
        double lastDT = 0;
        double dayTotal = 0;
        double delta = 0;
        String date = allLocationData.get(0).getDate();
        for(int i = 1; i<allLocationData.size(); i++){
            String cD = allLocationData.get(i).getDate();
            Location c = allLocationData.get(i).getLocation();
            double distance = c.distanceTo(lastl); //in meters

            if(cD.equals(date)){ //if the same day
                if(distance>10){ //if big enough movement (prevent clusters from adding up into large movement)
                    dayTotal += distance;
                    lastl = c; //update last point with significant movement
                }

            } else { //if day changed
                dayTotal += distance; //add any last distance
                delta = lastDT-dayTotal; //change between the day that just ended and the day before that
                lastDT = dayTotal; //save last amount
                dayTotal = 0;

                //save data here:

                lastl = c; //force location transition
            }
            date = cD; //keep date moving
        }
    }
}
