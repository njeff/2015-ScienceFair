package com.sci2015fair.fileoperations;

import android.util.Log;

import com.sci2015fair.filecontrolcenter.SaveLocations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by Jeffrey on 1/9/2016.
 */
public class ExpressionCSV {
    private static String header = "ID,Date,Time,Predicted,Happy,Neutral,Sad,Sleepy,Surprised,Fear,Anger";
    private static String TAG = "ExpCSV";

    /**
     * Append a line to the expression log file
     * @param predicted
     * @param prob
     */
    public static void writeLog(String predicted, double[] prob){
        long id = -1;
        try {
            if(!SaveLocations.expressionCSV.exists()) { //if there is no file
                SaveLocations.dataFolder.mkdirs(); //make sure directories exist
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(SaveLocations.expressionCSV, true)); //add header
                    bw.append(header);
                    bw.append("\n");
                    bw.flush();
                    bw.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            BufferedReader fileReader = new BufferedReader(new FileReader(SaveLocations.expressionCSV));
            while ((fileReader.readLine()) != null) { //count number of lines to get id
                id++;
            }
            fileReader.close();

            ConsoleLogObject clo = new ConsoleLogObject(0,"","");
            BufferedWriter bw = null;
            try{
                bw = new BufferedWriter(new FileWriter(SaveLocations.expressionCSV, true));
                bw.append(String.valueOf(id) + ",");
                bw.append(clo.getDate() + ",");
                bw.append(clo.getTime() + ",");
                bw.append(predicted);
                DecimalFormat df = new DecimalFormat("0.00000000");
                for(int i = 0; i<prob.length; i++){
                    bw.append("," + df.format(prob[i]));
                }
                bw.append("\n");
                bw.flush();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG,"Added expression entry.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
