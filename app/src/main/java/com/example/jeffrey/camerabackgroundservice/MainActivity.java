package com.example.jeffrey.camerabackgroundservice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

import static com.example.jeffrey.camerabackgroundservice.CameraService.startActionFoo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Camera mCamera;
    private CameraPreview mPreview;
    private TextView faceCount;
    private TextView confidence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        faceCount = (TextView) findViewById(R.id.face_count);
        confidence = (TextView) findViewById(R.id.confidence);
        /*
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

        SurfaceView view = new SurfaceView(this);

        try {
            mCamera.setPreviewDisplay(view.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        //setup camera preview

        //mPreview = new CameraPreview(this, mCamera);
        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);
        startService(new Intent(getApplicationContext(), CameraUI.class));
    }



    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener(){
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera){

            faceCount.setText(Integer.toString(faces.length));
            if(faces.length>=1){
                confidence.setText(Integer.toString(faces[0].rect.left)+", "+Integer.toString(faces[0].rect.right));
            }

            Log.d(TAG, Integer.toString(faces.length));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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

    //release camera when done
    @Override
    public void onPause()
    {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), CameraUI.class));
    }
}
