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

import com.sci2015fair.R;
import com.sci2015fair.service.SD;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Service to classify photos using OpenCV
 */
public class Classify extends Service {
    private final String TAG = "OCV";
    private ServiceHandler mServiceHandler;
    private String imgPath = null;

    public native int[] runLandmarks(int cols, int rows, int[] bbox, byte[] data, String path);
    static {
        System.loadLibrary("clandmark");
    }
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

            Mat image = new Mat();
            File originalImage = new File(imgPath);
            try {
                FileInputStream fis = new FileInputStream(originalImage); //get image
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                Utils.bitmapToMat(bitmap, image); //convert to Mat
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // load cascade file from application resources
            // needs to write resource to file to get a valid path
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
            if(!mCascadeFile.exists()){
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
            } else {
                Log.i(TAG, "Cascade file exists.");
            }

            CascadeClassifier faceDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (faceDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                faceDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
            //mCascadeFile.delete(); //delete after it is loaded

            Log.d(TAG, String.format("Rows: %d   Cols: %d", image.rows(), image.cols()));

            MatOfRect faceDetections = new MatOfRect(); //array to hold info on detected faces
            faceDetector.detectMultiScale(image, faceDetections); //detect faces

            Log.d(TAG, String.format("Detected %d faces", faceDetections.toArray().length));

            Mat temp = image.clone();
            Imgproc.cvtColor(image,temp,Imgproc.COLOR_BGR2GRAY);
            temp.convertTo(temp, CvType.CV_8U); //convert to grayscale
            byte[] data = new byte[temp.rows()*temp.cols()]; //save into continuous array
            for(int y = 0; y<temp.rows(); y++){
                for(int x = 0; x<temp.cols(); x++){
                    data[y*temp.cols()+x] = (byte)temp.get(y,x)[0];
                }
            }

            //load flandmark model
            InputStream ins = getResources().openRawResource(R.raw.frontalaflw);
            File landDir = getDir("flandmark", Context.MODE_PRIVATE);
            File land = new File(landDir, "flandmark_model.xml");
            if(!land.exists()){
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(land);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = ins.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    ins.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Landmark file exists.");
            }

            String landPath = land.getAbsolutePath(); //path to model
            int[] out; //data from clandmark

            for(Rect rect : faceDetections.toArray()){
                rect.height *= 1.15; //increase height slightly to compensate for smaller window
                int[] bbox = new int[8];
                bbox[0] = rect.x;
                bbox[1] = rect.y;
                bbox[2] = rect.x+rect.width;
                bbox[3] = rect.y;
                bbox[4] = rect.x+rect.width;
                bbox[5] = rect.y+rect.height;
                bbox[6] = rect.x;
                bbox[7] = rect.y+rect.height;

                for(int i = 0; i<8; i+=2){ //print out bounding box
                    Log.d(TAG, bbox[i] + ", " + bbox[i+1]);
                }
                Imgproc.rectangle(image, new Point(bbox[0], bbox[1]), new Point(bbox[4], bbox[5]),
                        new Scalar(255, 0, 0)); //draw bounding box

                out = runLandmarks(image.width(),image.height(),bbox,data,landPath); //call clandmark code
                for(int i = 0; i<out.length; i+=2){ //display points
                    Log.d(TAG, out[i] + ", " + out[i + 1]);
                    Imgproc.rectangle(image, new Point(out[i], out[i+1]), new Point(out[i] + 2, out[i+1] + 2),
                            new Scalar(0, 255, 0));
                }
                break; //only analyze the first face
            }
            Bitmap bmp = Bitmap.createBitmap(image.width(),image.height(),Bitmap.Config.ARGB_8888); //convert back
            Utils.matToBitmap(image, bmp);
            SD.saveImage(bmp,true);
            //land.delete(); //delete after it is loaded
            // originalImage.delete();//remove old image
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
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
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "OCV Starting", Toast.LENGTH_SHORT).show();

        if (intent.hasExtra("filepath")) {
            imgPath = intent.getStringExtra("filepath");
        }

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
