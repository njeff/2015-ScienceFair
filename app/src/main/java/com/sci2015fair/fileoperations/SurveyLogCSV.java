package com.sci2015fair.fileoperations;

import android.location.Location;

import com.sci2015fair.filecontrolcenter.SaveLocations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mitchell on 2/21/2016.
 */
public class SurveyLogCSV {
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final File DFSurveyLogCSV = SaveLocations.DFSurveyLogCSV;
    //CSV file header
    private static final String FILE_HEADER0 = "Id,Timestamp Date,Time,Entry for Day,General Scale 1-7,Hours of Sleep,Mood Sleepy,Mood Sad,Mood Neutral,Mood Happy,Mood Surprised";
    //private static final String FILE_HEADER0 = "Id,Date,Time,Unix Time,Latitude,Longitude,Accuracy,Speed,Altitude,Bearing,Data Provider,Notes";

    public static void writeNewEntry(String entryForDay, String generalScale, String hoursOfSleep, String mSleep, String mSad, String mNeutral, String mHappy, String mSurprised) {
        BufferedWriter fileWriter = null;
        long id = idCounter();
        // Create the storage directory if it does not exist
        try {
            fileWriter = new BufferedWriter(new FileWriter(DFSurveyLogCSV, true));
            if (id == 0) {
                //Write the CSV file header
                fileWriter.append(FILE_HEADER0.toString());
                //Add a new line separator after the header
                fileWriter.append(NEW_LINE_SEPARATOR);
                id = 1;
            }
            //Write a new student object list to the CSV file
            fileWriter.append(String.valueOf(id));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(getDate());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(getTime());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(entryForDay);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(generalScale);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(hoursOfSleep);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(mSleep);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(mSad);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(mNeutral);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(mHappy);
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(mSurprised);
            fileWriter.append(NEW_LINE_SEPARATOR);


            System.out.println("New entry written in SurveyLogCSV.");
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

    public static long idCounter() {
        long id = 0;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(DFSurveyLogCSV));
            while ((fileReader.readLine()) != null) {
                id++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String getDate() {
        DateFormat df = new SimpleDateFormat("MM/dd/yy");
        Calendar cal = Calendar.getInstance();
        String time = df.format(cal.getTime());
        return time;
    }

    public static String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String time = df.format(cal.getTime());
        return time;
    }
}
