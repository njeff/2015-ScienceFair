package com.sci2015fair.fileoperations;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles saving photos
 *
 * Created by Jeffrey on 9/26/2015.
 */
public class SavePictures {
    static final String TAG = "SD";
    //http://stackoverflow.com/questions/902089/how-to-tell-if-the-sdcard-is-mounted-in-android
    static public boolean hasStorage(boolean requireWriteAccess) {
        //TODO: After fix the bug,  add "if (VERBOSE)" before logging errors.
        String state = Environment.getExternalStorageState();
        Log.v(TAG, "storage state is " + state);

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                Log.v(TAG, "storage writable is " + writable);
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Create a temporary file to see whether a volume is really writeable.
     * It's important not to put it in the root directory which may have a
     * limit on the number of files.
     * @return
     */
    private static boolean checkFsWritable() {
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }

    /**
     * Save JPEG
     * @param data
     * @param process if the picture was processed
     * @return Picture file
     */
    static public File saveImage(byte[] data, boolean process, Context context){
        File pictureFile;
        if(process){
            pictureFile = getOutputMediaFile(MEDIA_TYPE_PROCIMAGE);
        } else {
            pictureFile = getOutputMediaFile(MEDIA_TYPE_UNPROCIMAGE);
        }

        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: " );
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        Intent intent =//rescan MTP directory; this code is still not working to our knowledge (2015.11.14-15), need to figure out how to utilize the sendBroadcast() method.
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(pictureFile));
        MediaScannerConnection.scanFile(context, new String[]{pictureFile.getAbsolutePath()}, null, null);
        return pictureFile;
    }

    /**
     * Save bitmap
     * @param image
     * @param process if the picture was processed
     * @return Picture file
     */
    static public File saveImage(Bitmap image, boolean process, Context context){
        File pictureFile;
        if(process){
            pictureFile = getOutputMediaFile(MEDIA_TYPE_PROCIMAGE);
        } else {
            pictureFile = getOutputMediaFile(MEDIA_TYPE_UNPROCIMAGE);
        }
        if (pictureFile == null){
            Log.d(TAG, "Error creating media file, check storage permissions: " );
            return null;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        MediaScannerConnection.scanFile(context, new String[]{pictureFile.getAbsolutePath()}, null, null);
        return pictureFile;
    }

//    public static Bitmap combineImages(Bitmap image, int cropwidth, int cropheight) {
//        Bitmap dotlayer = new Bitmap(cropwidth, cropheight);
//        Canvas canvas = new Canvas(croppedbitmap);                 //draw a canvas in defined bmp
//        Paint paint = new Paint();                          //define paint and paint color
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        //paint.setStrokeWidth(0.5f);
//        paint.setAntiAlias(true);
//        canvas.drawCircle(50, 50, 3, paint);
//    }

    public static final int MEDIA_TYPE_UNPROCIMAGE = 1;
    public static final int MEDIA_TYPE_PROCIMAGE = 2;

    /**
     * Generates the file objects for writing out images
     * @param type
     * @return
     */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "2015-ScienceFair");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_UNPROCIMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_PROCIMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + "b.jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
}
