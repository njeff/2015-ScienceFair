package com.sci2015fair.service;

/**
 * The service that is automatically called when the phone is unlocked
 * Takes a photo of the user
 *
 * Derived from:
 * http://www.piwai.info/chatheads-basics/
 */
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import com.sci2015fair.csvlog.ConsoleLogCSVWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraService extends Service {
    private static final String TAG = "Service";
    private WindowManager windowManager;
    private Camera mCamera;
    private CameraPreview mPreview;

    private int cropheight, cropwidth, cameraheightres, camerawidthres;
    int leftBound;
    int topBound;
    //right, bottom
    int rightBound;
    int bottomBound;
    //center coordinates
    int cx;
    int cy;

    public CameraService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                16,
                16,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;

        if(checkCameraHardware(this)) {
            releaseCamera();
            try {
                mCamera = Camera.open(findFrontFacingCamera());
                mCamera.setFaceDetectionListener(faceDetectionListener);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        //enable autofocus
        Camera.Parameters cParams = mCamera.getParameters();
        cParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        cParams.setJpegQuality(100);
        List<Camera.Size> sizes = cParams.getSupportedPictureSizes();

        // Iterate through all available resolutions and choose one.
        /*
        for (Camera.Size size : sizes) {
            Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
        }
        */
        cameraheightres = sizes.get(0).height;
        camerawidthres = sizes.get(0).width;
        cParams.setPictureSize(camerawidthres, cameraheightres); //use largest resolution possible
        mCamera.setParameters(cParams);

        mPreview = new CameraPreview(this, mCamera);
        windowManager.addView(mPreview, params);

        TimerTask timestop = new TimerTask() {
            @Override
            public void run() {
                stopSelf();
            }
        };

        Timer stopTimer = new Timer();
        stopTimer.schedule(timestop, 10000); //kill service after 10 seconds if no face is found
    }

    private boolean taken = false; //whether or not photo has been taken
    private int verify = 0; //make sure the face has been detected for long enough

    //face detection listener
    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener(){
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera){
            if(taken == true){
                Log.d(TAG, "taken!");
                stopSelf();
            }

            if(faces.length==1){
                verify++;
                //translates .rect's coordinates from a scale of -1000 to 1000 on both axis to the camera's native resolution with (0,0) on the upper left corner
                leftBound = (1000 + faces[0].rect.left) * camerawidthres / 2000;
                topBound = (1000 + faces[0].rect.top) * cameraheightres / 2000;
                rightBound = (1000 + faces[0].rect.right) * camerawidthres / 2000;
                bottomBound = (1000 + faces[0].rect.bottom) * cameraheightres / 2000;
                cx = faces[0].rect.centerX();
                cy = faces[0].rect.centerY();

                cropwidth = rightBound - leftBound;
                cropheight = bottomBound - topBound;
                Log.d(TAG, "LENGTH: " + String.valueOf(cropheight));
                Log.d(TAG, "WIDTH: " + String.valueOf(cropwidth));
                Log.d(TAG, "Left Bound: " + String.valueOf(leftBound) + "/" + camerawidthres);
                Log.d(TAG, "Right Bound: " + String.valueOf(rightBound) + "/" + camerawidthres);
                Log.d(TAG, "Top Bound: " + String.valueOf(topBound) + "/" + cameraheightres);
                Log.d(TAG, "Bottom : " + String.valueOf(bottomBound) + "/" + cameraheightres);
                ConsoleLogCSVWriter.writeCsvFile("AutoCamera", String.valueOf(leftBound) + "/" + camerawidthres);
                ConsoleLogCSVWriter.writeCsvFile("AutoCamera", String.valueOf(rightBound) + "/" + camerawidthres);
                ConsoleLogCSVWriter.writeCsvFile("AutoCamera", String.valueOf(topBound) + "/" + cameraheightres);
                ConsoleLogCSVWriter.writeCsvFile("AutoCamera", String.valueOf(bottomBound) + "/" + cameraheightres);

                if(taken == false && verify == 5){
                    mCamera.takePicture(null, null, mPicture);

                    Log.d(TAG, "Taking Picture...");
                    ConsoleLogCSVWriter.writeCsvFile("AutoCamera","Taking Picture...");

                }
            }
            else {
                verify = 0;
            }
            Log.d(TAG, Integer.toString(faces.length));
        }
    };

    //http://developer.android.com/guide/topics/media/camera.html
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            //http://android-er.blogspot.com/2011/01/save-camera-image-using-mediastore.html
            //save to photos folder on phone
            Bitmap rawBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//create bitmap of image from datastream
            Matrix matrix = new Matrix();
//            matrix.postScale(0.5f, 0.5f);
//            matrix.postRotate(-90);
            Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();
            Log.d(TAG, "Rotation: " + rotation + " Degrees");
            matrix.postRotate(rotation);

            Bitmap rotatedBitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.getWidth(), rawBitmap.getHeight(), matrix, true);
            if(SD.hasStorage(true)) {
                SD.saveImage(data);
                cropPicture(rotatedBitmap, true);//run method to generate cropped picture
                taken = true;
            } else {
                ContentValues val = new ContentValues();
                Uri uriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, val);

                try {
                    OutputStream os = getContentResolver().openOutputStream(uriTarget);
                    os.write(data);
                    os.flush();
                    os.close();
                    cropPicture(rotatedBitmap, false);
                    taken = true; //set flag - photo has been taken
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }
            }
        }
    };

    /**
     * Crop image to face
     * @param rotatedBitmap
     * @param SDv
     */
    public void cropPicture(Bitmap rotatedBitmap, boolean SDv) {

        Bitmap croppedbitmap = Bitmap.createBitmap(rotatedBitmap, leftBound, topBound, cropheight, cropwidth);
        if(SDv){
            SD.saveImage(croppedbitmap, cropwidth, cropheight);
        } else {
            ContentValues val = new ContentValues();
            Uri seconduriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, val);
            MediaStore.Images.Media.insertImage(getContentResolver(), croppedbitmap, "A", null);
        }
    }

    /**
     * Check for camera
     * @param context
     * @return
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Log.d(TAG,"camera found");
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Find front facing camera
     * @return
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.v(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    /**
     * Release camera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "destroy");
        super.onDestroy();
        if (mPreview != null) windowManager.removeView(mPreview); //remove camera preview
        releaseCamera();
    }

}
