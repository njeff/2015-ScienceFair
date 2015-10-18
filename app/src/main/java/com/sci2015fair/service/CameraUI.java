package com.sci2015fair.service;

/**
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
import android.view.Gravity;
import android.view.WindowManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CameraUI extends Service {
    private static final String TAG = "Service";
    private WindowManager windowManager;
    private Camera mCamera;
    private CameraPreview mPreview;

    private int cropheight;
    private int cropwidth;
    private int cameraheightres;
    private int camerawidthres;
    int l;
    int t;
    //right, bottom
    int r;
    int b;
    //center coordinates
    int cx;
    int cy;

    public CameraUI() {
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

                l = (1000 + faces[0].rect.left) * camerawidthres / 2000;
                t = (1000 + faces[0].rect.top) * cameraheightres / 2000;
                r = (1000 + faces[0].rect.right) * camerawidthres / 2000;
                b = (1000 + faces[0].rect.bottom) * cameraheightres / 2000;
                cx = faces[0].rect.centerX();
                cy = faces[0].rect.centerY();

                cropwidth = r - l;
                cropheight = b - t;
        //                Log.d(TAG, "LENGTH: " + String.valueOf(croplength));
        //                Log.d(TAG, "WIDTH: " + String.valueOf(cropwidth));
                Log.d(TAG, "L: " + String.valueOf(l) + "/" + camerawidthres);
                Log.d(TAG, "R: " + String.valueOf(r) + "/" + camerawidthres);
                Log.d(TAG, "T: " + String.valueOf(t) + "/" + cameraheightres);
                Log.d(TAG, "B: " + String.valueOf(b) + "/" + cameraheightres);

                if(taken == false && verify == 5){
                    mCamera.takePicture(null, null, mPicture);
                    Log.d(TAG, "GOT PHOTO ---------------------------------------------------------------------------------");
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
            Bitmap originalbitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(SD.hasStorage(true)) {
                SD.saveImage(data);
                cropPicture(originalbitmap, true);
                taken = true;
            } else {
                ContentValues val = new ContentValues();
                Uri uriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, val);

                try {
                    OutputStream os = getContentResolver().openOutputStream(uriTarget);
                    os.write(data);
                    os.flush();
                    os.close();
                    cropPicture(originalbitmap,false);
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
     * @param originalbitmap
     * @param SDv
     */
    public void cropPicture(Bitmap originalbitmap, boolean SDv) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.5f, 0.5f);
        matrix.postRotate(-90);
        Bitmap croppedbitmap = Bitmap.createBitmap(originalbitmap, l, t, cropwidth, cropheight, matrix, true);
        if(SDv){
            SD.saveImage(croppedbitmap);
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
