package com.sci2015fair.opencv;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.example.jeffrey.camerabackgroundservice.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import libsvm.*;

/**
 * Service to classify photos using OpenCV
 */
public class Classify extends Service {
    private final String TAG = "OCV";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    /**
     * OpenCV Callback
     */
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            //load OpenCV
            System.loadLibrary("opencv_java3");
            if (!OpenCVLoader.initDebug()) {
                Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, getApplicationContext(), mLoaderCallback);
            } else {
                Log.d(TAG, "OpenCV library found inside package. Using it!");
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }

            /*
            File root = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "2015-ScienceFair"); //get root of image directory

            Mat image = new Mat();
            try {
                FileInputStream fis = new FileInputStream(new File(root, "IMG_20151005_141009b.jpg")); //get image
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Utils.bitmapToMat(bitmap, image); //convert to Mat
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // load cascade file from application resources
            InputStream is = getResources().openRawResource(R.raw.haar_leye);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haar_leye.xml");
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(mCascadeFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            CascadeClassifier faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (faceDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                faceDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

            cascadeDir.delete(); //delete temp cascade XML after it is loaded
            Log.d("ROWS", String.format("R: %d    C: %d", image.rows(), image.cols()));

            MatOfRect faceDectections = new MatOfRect(); //array to hold info on detected faces
            faceDetector.detectMultiScale(image, faceDectections); //detect faces

            Log.d("FACE",String.format("Detected %d faces", faceDectections.toArray().length));
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            */


            stopSelf(msg.arg1);
        }
    }


    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "OCV Starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
