package com.example.jeffrey.camerabackgroundservice;

/**
 * Derived from:
 * http://stackoverflow.com/questions/15975988/what-apis-in-android-is-facebook-using-to-create-chat-heads
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.io.IOException;

public class CameraUI extends Service {
    private static final String TAG = "Service";
    private WindowManager windowManager;
    private Camera mCamera;
    private CameraPreview mPreview;

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
                50,
                50,
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
        mCamera.setParameters(cParams);

        mPreview = new CameraPreview(this, mCamera);
        windowManager.addView(mPreview, params);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener(){
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera){

            if(faces.length==1){

            }

            Log.d(TAG, Integer.toString(faces.length));
        }
    };

    //check for camera
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

    //find front facing camera
    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.v("MyActivity", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    //release camera
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
