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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.WindowManager;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import android.view.Display;

import com.sci2015fair.fileoperations.CameraSavePicturesService;
import com.sci2015fair.fileoperations.ConsoleLogCSVWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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
    public void onCreate(){//everything starts here when service is started
        super.onCreate();

        ////////////////////////////////////////////////////////////////////////////////////////////
        //create 16x16 window for camera feed to show onscreen wen service is started
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);//get system's high-level window manager for our display to be shown on all screens regardless of applications running or things displayed

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                16,//set length
                16,//set height
                WindowManager.LayoutParams.TYPE_PHONE,//for phone-factor devices
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,//not clickable by user
                PixelFormat.TRANSLUCENT);//?

        params.gravity = Gravity.TOP | Gravity.LEFT;//sets origin orientation of WindowManager
        params.x = 0;//x position for window (measured from top left)
        params.y = 0;//y position for window


        if(checkCameraHardware(this)) {
            releaseCamera();
            try {
                mCamera = Camera.open(findFrontFacingCamera());
                mCamera.setFaceDetectionListener(faceDetectionListener);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////
        //enable autofocus
        Camera.Parameters cParams = mCamera.getParameters();
        cParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        cParams.setJpegQuality(100);
        List<Camera.Size> sizes = cParams.getSupportedPictureSizes();

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
            int checkswitch = 0;
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

                if(taken == false && verify == 3){
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
            if(CameraSavePicturesService.hasStorage(true)) {
                CameraSavePicturesService.saveImage(data, getApplicationContext());
                cropPicture(rotatedBitmap, true);//run method to generate cropped picture
                taken = true;
            } else {
                ContentValues val = new ContentValues(); //save to android mediastore
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

    public void cropPicture(Bitmap rotatedbitmap, boolean SDv) {
        Matrix matrix = new Matrix();
        matrix.setRotate(-90);
        matrix.postScale(0.75f, 0.75f);
        Bitmap croppedbitmap = Bitmap.createBitmap(rotatedbitmap, leftBound, topBound, cropheight, cropwidth, matrix, true);

        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(croppedbitmap).build();
        SparseArray<Face> faces = detector.detect(frame);

        croppedbitmap = convertToMutable(croppedbitmap);
        Canvas can = new Canvas(croppedbitmap);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        Log.d(TAG, "Number of Faces: " + faces.size());
        /*
        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            Log.d(TAG,"LE: "+face.getIsLeftEyeOpenProbability()+" RE: "+face.getIsRightEyeOpenProbability()+" SP: "+face.getIsSmilingProbability());


            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                paint.setARGB(255, 50, 50, 255);
                can.drawCircle(cx, cy, 2, paint);
            }
            double yaxis = face.getEulerY();

            float righteyeX = face.getLandmarks().get(0).getPosition().x;
            float lefteyeX = face.getLandmarks().get(1).getPosition().x;
            float dist = (righteyeX-lefteyeX)/(float)Math.cos(yaxis); //scale if face is rotated
            float righteyeY = face.getLandmarks().get(0).getPosition().y;
            float lefteyeY = face.getLandmarks().get(1).getPosition().y;
            can.drawRect(righteyeX-dist/3,righteyeY+dist/4,righteyeX+dist/3,righteyeY-dist/4,paint); //box right eye
            can.drawRect(lefteyeX-dist/3,lefteyeY+dist/4,lefteyeX+dist/3,lefteyeY-dist/4,paint); //box left eye

            float nosebaseX = face.getLandmarks().get(2).getPosition().x;
            float nosebaseY = face.getLandmarks().get(2).getPosition().y;
            float mouthtop = nosebaseY-dist/4;
            can.drawRect(nosebaseX-dist/2,mouthtop,nosebaseX+dist/2,mouthtop-dist*3/5,paint); //box mouth
        }
        */
        detector.release();

        if(SDv){
            CameraSavePicturesService.saveImage(croppedbitmap, cropwidth, cropheight, getApplicationContext());
        } else {
            ContentValues val = new ContentValues();
            Uri seconduriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, val);
            MediaStore.Images.Media.insertImage(getContentResolver(), croppedbitmap, "A", null);//insert image into directory in a file format, saving it

        }
    }

    /**
     * Check for front-facing camera.
     * @param context
     * @return boolean if front-facing camera exists
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            // this device has a camera
            Log.d(TAG,"Camera Found");
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Find front facing camera
     * @return camera id of front facing camera
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
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     * From: http://stackoverflow.com/questions/4349075/bitmapfactory-decoderesource-returns-a-mutable-bitmap-in-android-2-2-and-an-immu
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but mutable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
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
        //startService(new Intent(getApplicationContext(), Classify.class)); //launch OpenCV classifier
        Log.d(TAG, "destroy");
        super.onDestroy();
        if (mPreview != null) windowManager.removeView(mPreview); //remove camera preview
        releaseCamera();
    }
}