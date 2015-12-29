package com.sci2015fair.fileoperations;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ScanMediaService extends Service {

    private static ArrayList<String> toBeScanned = new ArrayList<>();
    public ScanMediaService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {//everything starts here when service is started
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub

        toBeScanned = (ArrayList<String>) (intent.getStringArrayListExtra("Scan Files")).clone();
        Log.d("READY", "greiuhgregehiujktgrefghkefauihkertoigtjiodfbjioth");
        Log.d("READY", toBeScanned.toString());
        scanFiles(this, toBeScanned);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        Log.d("CheckFilesPresentServ", "All files checked and accounted for.  Service shutting down.");
        super.onDestroy();
    }



    public void scanFiles(Context context, ArrayList<String> toBeScanned) {
        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);
        final int[] countToShutdown = {1};
        boolean trigger = true;
        Log.d("READY", Arrays.toString(toBeScannedStr));

//        for (String scanIt : toBeScannedStr) {
//            Log.d("READY", scanIt);
//            MediaScannerConnection.scanFile(context, new String[]{scanIt}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                @Override
//                public void onScanCompleted(String path, Uri uri) {
//                    System.out.println("SCAN COMPLETED: " + path);
//                    countToShutdown[0]++;
//                }
//            });
//        }
        for (String scanString : toBeScannedStr) {
            File scanIt = new File(scanString);
            Uri contentUri = Uri.fromFile(scanIt);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Log.d("READY", "Scanning " + scanString);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        }


//        MediaScannerConnection.scanFile(context, toBeScannedStr, null, new MediaScannerConnection.OnScanCompletedListener() {
//            @Override
//            public void onScanCompleted(String path, Uri uri) {
//                System.out.println("SCAN COMPLETED: " + path);
//                countToShutdown[0]++;
//            }
//        });
//        Log.d("READY", "Scanning...");
//        while (trigger) {
//            if (countToShutdown[0] == toBeScanned.size()) {
                stopSelf();
//
//                trigger = false;
//            }
//            Log.d("Ready", Integer.toString(countToShutdown[0]));
//        }

    }
}
