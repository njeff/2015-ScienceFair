package com.sci2015fair.filecontrolcenter;

import android.os.Environment;

import java.io.File;

/**
 * Created by Mitchell on 12/24/2015.
 */
public class SaveLocations {
    public static final String appFolderName = "2015-ScienceFair";
    public static final File mainExternalFolder = new File(Environment.getExternalStorageDirectory(), appFolderName);
    public static final File dataFolder = new File(mainExternalFolder, "/Data");
    public static final File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "2015-ScienceFair");
    public static final File DFSurveyLogCSV = new File(dataFolder, "surveylog.csv");
    public static final File DFConsoleLogCSV = new File(dataFolder, "consolelog.csv");
    public static final File DFLocationGPSLogCSV = new File(dataFolder, "locationgpslog.csv");
    public static final File DFPedometerTrackingLogTXT = new File(dataFolder, "pedometertrackinglog.txt");
    public static final File expressionCSV = new File(dataFolder, "expression.csv");
    public static final File model = new File(dataFolder, "m.model");
    public static final File tempARFF = new File(dataFolder, "temp.arff");
}
