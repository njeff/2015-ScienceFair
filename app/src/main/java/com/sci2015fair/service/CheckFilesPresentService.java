package com.sci2015fair.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.sci2015fair.filecontrolcenter.SaveLocations;
import com.sci2015fair.fileoperations.ScanMediaService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CheckFilesPresentService extends Service {
    private static final File mainExternalFolder = SaveLocations.mainExternalFolder;
    private static final File dataFolder = SaveLocations.dataFolder;
    private static final File pictureFolder = SaveLocations.pictureFolder;
    private static final File DFSurveyLogCSV = SaveLocations.DFSurveyLogCSV;
    private static final File DFConsoleLogCSV = SaveLocations.DFConsoleLogCSV;
    private static final File DFLocationGPSLogCSV = SaveLocations.DFLocationGPSLogCSV;
    private static final File DFPedometerTrackingLogTXT = SaveLocations.DFPedometerTrackingLogTXT;

    private static ArrayList<String> toBeScanned = new ArrayList<>();

    public CheckFilesPresentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return null;
    }

    @Override
    public void onCreate() {//everything starts here when service is started
        super.onCreate();
        checkAllFolders(this);
        stopSelf();
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("fagdgegwegads", "");
//        checkAllFolders(this);
//        return 0;
//    }

    public void checkAllFolders(Context context) {
        checkMainFolder(context);
        checkPicturesFolder(context);

        Intent intent = new Intent(this, ScanMediaService.class);
        intent.putExtra("Scan Files", toBeScanned);
        startService(intent);
    }

    public void checkMainFolder(Context context) {
        if (mainExternalFolder.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + mainExternalFolder.getPath() + "\" folder found.");
        } else {
            dataFolder.mkdirs();
            Log.d("CheckFilesPresentServ", "\"" + mainExternalFolder.getPath() + "\" folder not found.  Folder has been created.");
        }
        checkMFContents(context);
    }

    public void checkMFContents(final Context context) {
        checkDataFolder();
//        MediaScannerConnection.scanFile(context, new String[]{mainExternalFolder.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//                Log.i("ExternalStorage", "Scanned " + path + ":");
//                Log.i("ExternalStorage", "-> uri=" + uri);
//                MediaScannerConnection.scanFile(context, new String[]{pictureFolder.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                    @Override
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                        stopSelf();
//                    }
//                });
//
//            }
//        });
    }

    public static void checkDataFolder() {
        if (dataFolder.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + dataFolder.getPath() + "\" folder found.");
        } else {
            dataFolder.mkdirs();
            Log.d("CheckFilesPresentServ", "\"" + dataFolder.getPath() + "\" folder not found.  Folder has been created.");
        }
        checkDFContents();
    }

    public static void checkPicturesFolder(Context context) {
        if (pictureFolder.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + pictureFolder.getPath() + "\" folder found.");
        } else {
            pictureFolder.mkdirs();
            Log.d("CheckFilesPresentServ", "\"" + pictureFolder.getPath() + "\" folder not found.  Folder has been created.");
        }

    }

    public static void checkDFContents() {
        checkDFSurveyLogCSV();
        checkDFConsoleLogCSVFile();
        checkDFLocationGPSLogCSV();
        checkDFPedometerTrackingLogTXT();
    }

    public static void checkDFSurveyLogCSV() {
        if (DFSurveyLogCSV.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + DFSurveyLogCSV.getPath() + "\" file found.");
        } else {
            try {
                DFSurveyLogCSV.createNewFile();
                Log.d("CheckFilesPresentServ", "\"" + DFSurveyLogCSV.getPath() + "\" file not found.  File has been created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toBeScanned.add(DFSurveyLogCSV.getAbsolutePath());
    }

    public static void checkDFConsoleLogCSVFile() {
        if (DFConsoleLogCSV.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + DFConsoleLogCSV.getPath() + "\" file found.");
        } else {
            try {
                DFConsoleLogCSV.createNewFile();
                Log.d("CheckFilesPresentServ", "\"" + DFConsoleLogCSV.getPath() + "\" file not found.  File has been created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toBeScanned.add(DFConsoleLogCSV.getAbsolutePath());
    }

    public static void checkDFLocationGPSLogCSV() {
        if (DFLocationGPSLogCSV.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + DFLocationGPSLogCSV.getPath() + "\" file found.");
        } else {
            try {
                DFLocationGPSLogCSV.createNewFile();
                Log.d("CheckFilesPresentServ", "\"" + DFLocationGPSLogCSV.getPath() + "\" file not found.  File has been created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toBeScanned.add(DFLocationGPSLogCSV.getAbsolutePath());
    }

    public static void checkDFPedometerTrackingLogTXT() {
        if (DFPedometerTrackingLogTXT.exists()) {
            Log.d("CheckFilesPresentServ", "\"" + DFPedometerTrackingLogTXT.getPath() + "\" file found.");
        } else {
            try {
                DFPedometerTrackingLogTXT.createNewFile();
                Log.d("CheckFilesPresentServ", "\"" + DFPedometerTrackingLogTXT.getPath() + "\" file not found.  File has been created.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        toBeScanned.add(DFPedometerTrackingLogTXT.getAbsolutePath());
    }

    @Override
    public void onDestroy(){
        Log.d("CheckFilesPresentServ", "All files checked and accounted for.  Service shutting down.");
        super.onDestroy();
    }
}
