package com.sci2015fair.fileoperations;

import android.location.Location;
import android.os.Environment;

import com.sci2015fair.filecontrolcenter.SaveLocations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mitchell on 12/26/2015.
 */
public class LocationGPSLogCSVWriter {
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final File DFLocationGPSLogCSV = SaveLocations.DFLocationGPSLogCSV;
    //CSV file header
    private static final String FILE_HEADER0 = "Id,Date,Time,Unix Time,Latitude,Longitude,Accuracy,Speed,Altitude,Bearing,Data Provider,Notes";
    private static final String FILE_HEADER1 = ",,,(Milliseconds since 1/1/1970 00:00),,,(+-1STD DEV in Meters),Meters/Sec,,(In Degrees),,";

    public static void writeNewEntry(Location currentLocation, String notes) {
        long id = -1;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(DFLocationGPSLogCSV));
            while ((fileReader.readLine()) != null) {
                id++;
            }
            lineTranscriber(id, currentLocation, notes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void lineTranscriber(long id, Location currentLocation, String notes) {
        BufferedWriter fileWriter = null;
        // Create the storage directory if it does not exist
        try {
            fileWriter = new BufferedWriter(new FileWriter(DFLocationGPSLogCSV, true));
            if (id == -1) {
                //Write the CSV file header
                fileWriter.append(FILE_HEADER0.toString());
                //Add a new line separator after the header
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(FILE_HEADER1.toString());
                fileWriter.append(NEW_LINE_SEPARATOR);
                id = 1;
            }
            //Write a new student object list to the CSV file
            fileWriter.append(String.valueOf(id));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(getDate(currentLocation));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(getTime(currentLocation));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getTime()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getLatitude()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getLongitude()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getAccuracy()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getSpeed()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getAltitude()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(currentLocation.getBearing()));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(currentLocation.getProvider());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(notes);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(NEW_LINE_SEPARATOR);


            System.out.println("CSV file was created successfully !!!");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
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
}
