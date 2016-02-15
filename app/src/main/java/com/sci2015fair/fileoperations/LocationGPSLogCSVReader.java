package com.sci2015fair.fileoperations;

import android.location.Location;
import android.util.Log;

import com.sci2015fair.distance.LocationIDObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Mitchell on 12/30/2015.
 */
public class LocationGPSLogCSVReader {
    private static final String COMMA_DELIMITER = ",";
    public static ArrayList<LocationIDObject> readLocationCSVFile(File fileToRead) {
        String line;
        ArrayList<LocationIDObject> returnedLocationArray = new ArrayList<>();
        BufferedReader fileReader = null;
        try {
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(fileToRead));

            //Read the CSV file header to skip it
            fileReader.readLine();
            fileReader.readLine();

            //Read the file line by line starting from the second line
            while ((line = fileReader.readLine()) != null) {
                //Get all tokens available in line
                String[] tokens = line.split(COMMA_DELIMITER);
                Log.d("TOKENS", Arrays.toString(tokens));
                LocationIDObject cachedLocationID = new LocationIDObject();
                if (tokens.length > 0) {
                    //Create a new student object and fill his data
                    cachedLocationID.setId(tokens[0]);
                    cachedLocationID.setDate(tokens[1]);
                    cachedLocationID.setTime(tokens[2]);
                    Location location = new Location(tokens[10]);
                    location.setTime(Long.parseLong(tokens[3]));
                    location.setLatitude(Double.parseDouble(tokens[4]));
                    location.setLongitude(Double.parseDouble(tokens[5]));
                    location.setAccuracy(Float.parseFloat(tokens[6]));
                    location.setSpeed(Float.parseFloat(tokens[7]));
                    location.setAltitude(Double.parseDouble(tokens[8]));
                    location.setBearing(Float.parseFloat(tokens[9]));
                    location.setProvider(tokens[10]);
                    cachedLocationID.setLocation(location);
                    cachedLocationID.setNotes(tokens[11]);
                }
                returnedLocationArray.add(cachedLocationID);
            }

        }
        catch (Exception e) {
            System.out.println("Error in CsvFileReader !!!");
            e.printStackTrace();
        } finally {
            try {
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while closing fileReader !!!");
                e.printStackTrace();
            }
        }
        return returnedLocationArray;
    }

    public static String getDate(Location currentLocation) {
        DateFormat tf = new SimpleDateFormat("MM/dd/yy");
        Date dateobj = new Date(currentLocation.getTime());
        String date = tf.format(dateobj);
        return date;
    }

    public static String getTime(Location currentLocation) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date dateobj = new Date(currentLocation.getTime());
        String time = df.format(dateobj);
        return time;
    }

    public static int convertToTimeInt(long unixTime) {//extracts the military time out of Date objects and returns it as an int.
        Date date = new Date(unixTime);
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        String timeString = tf.format(date);
        timeString.replaceAll(":", "");
        int returnedTimeInt = Integer.parseInt(timeString);
        return returnedTimeInt;
    }
}
