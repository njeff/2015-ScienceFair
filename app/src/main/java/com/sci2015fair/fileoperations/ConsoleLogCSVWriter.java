package com.sci2015fair.fileoperations;

import android.os.Environment;

import com.sci2015fair.filecontrolcenter.SaveLocations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Mitchell on 11/1/2015.
 */
public class ConsoleLogCSVWriter {
    //Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final File DFConsoleLogCSV = SaveLocations.DFConsoleLogCSV;
    //CSV file header
    private static final String FILE_HEADER = "id,date,time,outputmessagecategory,mainoutputstring";

    public static void writeCsvFile(String outputmessagecategory, String mainoutputstring) {
        long id = 0;
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(DFConsoleLogCSV));
            while ((fileReader.readLine()) != null) {
                id++;
            }
            transcriber(id, outputmessagecategory, mainoutputstring);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void transcriber(long id, String outputmessagecategory, String mainoutputstring) {

        //Create new students objects
        ConsoleLogObject logLine = new ConsoleLogObject(id, outputmessagecategory, mainoutputstring);

        //Create a new list of student objects



        BufferedWriter fileWriter = null;

        // Create the storage directory if it does not exist


        try {
            fileWriter = new BufferedWriter(new FileWriter(DFConsoleLogCSV, true));
            if (id == 0) {
                //Write the CSV file header
                fileWriter.append(FILE_HEADER.toString());

                //Add a new line separator after the header
                fileWriter.append(NEW_LINE_SEPARATOR);
                id = 1;
            }
            //Write a new student object list to the CSV file
            fileWriter.append(String.valueOf(id));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(logLine.getDate());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(logLine.getTime());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(logLine.getOutputMessageCategory());
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(logLine.getMainOutputString());
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

}
