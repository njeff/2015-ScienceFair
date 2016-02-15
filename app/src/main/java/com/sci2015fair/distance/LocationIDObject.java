package com.sci2015fair.distance;

import android.location.Location;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mitchell on 12/31/2015.
 */
public class LocationIDObject {
    private Location location;
    private String id;
    private String date;
    private String time;
    private String notes;

    public LocationIDObject(Location location, String id, String date, String time, String notes) {
        this.location = location;
        this.date = date;
        this.time = time;
        this.id = id;
        this.notes= notes;
    }

    public LocationIDObject() {

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public long getLocationTime(String numericType) {
        try {
            if (numericType.equals("UNIX_TIME")) {
                return this.location.getTime();
            } else if (numericType.equals("HH:mm:ss")) {
                return convertToTimeLong(this.location.getTime(), "HH:mm:ss");
            }
        } catch(Exception ex) {
            System.out.println("LOCATIONIDOBJECT: Error in returning time from Location object.  Either time field in Location is null or parameter numericType was not properly specified.");
            Log.d("LOCATIONIDOBJECT", "Error in returning time from Location object.  Either time field in Location is null or parameter numericType was not properly specified.");
        }
        return 0;
    }

    public long getLocationDate(String numericType) {
        try {
            if (numericType.equals("UNIX_TIME")) {
                return this.location.getTime();
            } else if (numericType.equals("MM/dd/yy")) {
                return convertToTimeLong(this.location.getTime(), "MM/dd/yy");
            }
        } catch(Exception ex) {
            System.out.println("LOCATIONIDOBJECT: Error in returning date from Location object.  Either time field in Location is null or parameter numericType was not properly specified.");
            Log.d("LOCATIONIDOBJECT", "Error in returning time from Location object.  Either time field in Location is null or parameter numericType was not properly specified.");
        }
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static long convertToTimeLong(long unixTime, String convertType) {//extracts unix time (number of seconds from midnight Jan. 1, 1970) out of the long passed in (unixTime) and returns it as an long in 24 hour time without the date.  Example includes 23545 (2:35:45) and 162431 (16:24:31)
        Date date = new Date(unixTime);
        DateFormat tf = new SimpleDateFormat(convertType);
        String timeString = tf.format(date);
        if (convertType.equals("HH:mm:ss")) {
            timeString.replaceAll(":", "");
        } else if (convertType.equals("MM/dd/yy")) {
            timeString.replaceAll("/", "");
        }
        long returnedTimeLong = Integer.parseInt(timeString);
        return returnedTimeLong;
    }


}
