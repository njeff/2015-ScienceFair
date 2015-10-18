package com.sci2015fair.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CameraService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.jeffrey.camerabackgroundservice.action.FOO";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.jeffrey.camerabackgroundservice.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.jeffrey.camerabackgroundservice.extra.PARAM2";

    private Camera mCamera;
    private CameraPreview mPreview;
    private String TAG = "cameraBackground";
    private static Activity act;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2, Activity activity) {
        act = activity;
        Intent intent = new Intent(context, CameraService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public CameraService() {
        super("CameraService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
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
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);

        SurfaceView view = new SurfaceView(getApplicationContext());
        view.setVisibility(View.VISIBLE);

        /*
        try {
            mCamera.setPreviewDisplay(view.getHolder());
            mCamera.startPreview();
            mCamera.startFaceDetection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        mPreview = new CameraPreview(act.getApplicationContext(), mCamera);
        //FrameLayout preview = (FrameLayout) act.findViewById(R.id.camera_preview);
        //preview.addView(mPreview);

        while(true){

        }
    }


    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener(){
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera){
            Log.d(TAG, Integer.toString(faces.length));
        }
    };

    //check for camera
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            Log.d(TAG, "camera found");
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
