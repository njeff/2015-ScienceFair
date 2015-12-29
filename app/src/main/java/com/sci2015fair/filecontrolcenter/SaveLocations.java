package com.sci2015fair.filecontrolcenter;

import android.os.Environment;

import java.io.File;

/**
 * Created by Mitchell on 12/24/2015.
 */
public class SaveLocations {
    public static final String appFolderName = "2015-ScienceFair";
    public static final File mainExternalFolder = new File(Environment.getExternalStorageDirectory(), appFolderName);
    public static final File dataFolder = new File(Environment.getExternalStorageDirectory(), appFolderName + "/Data");
    public static final File pictureFolder = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "2015-ScienceFair");
    public static final File DFConsoleLogCSV = new File(Environment.getExternalStorageDirectory(), appFolderName + "/Data/consolelog.csv");
    public static final File DFLocationGPSLogCSV = new File(Environment.getExternalStorageDirectory(), appFolderName + "/Data/locationgpslog.csv");
    public static final File DFPedometerTrackingLogTXT = new File(Environment.getExternalStorageDirectory(), appFolderName + "/Data/pedometertrackinglog.txt");
}
