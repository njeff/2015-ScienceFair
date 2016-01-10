package com.sci2015fair.landmark;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.PointF;
import android.util.Log;

/**
 * Created by Jeffrey on 12/13/2015.
 */
public class FeatureCorner {
    private final String TAG = "FCorner";
    PointF pt1 = new PointF(0,0);
    PointF pt2 = new PointF(0,0);
    PointF pt3 = new PointF(0,0);
    PointF pt4 = new PointF(0,0);

    /**
     * Returns detected points
     * @param i
     * @return
     */
    public PointF getPoint(int i){
        switch(i){
            case 1:
                return pt1;
            case 2:
                return pt2;
            case 3:
                return pt3;
            case 4:
                return pt4;
            default:
                return pt1;
        }
    }

    /**
     * Get the far left and right corners of features
     * Doesn't work.
     * @param bitmap
     */
    public void edgeCorners(Bitmap bitmap){
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);

        Size ksize = new Size(0,0);
        ksize.height = 7;
        ksize.width = 7;

        Log.d(TAG, "Channels: " + image.channels());
        Log.d(TAG, "ImgType: " + image.type());
        Mat edges = new Mat();
        Core.normalize(image, image, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC4); //normalize image

        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2GRAY, 1); //change the number of channels
        Log.d(TAG, "ImgTypeAfter: " + image.type());
        Imgproc.GaussianBlur(image, image, ksize, 3, 1); //reduce noise
        Imgproc.bilateralFilter(image, edges, 20, 22, 10); //reduce even more noise and preserve edges

        image = edges;

        for(int i = 0; i<image.width(); i++){ //increase separation between dark and light
            for(int j = 0; j<image.height(); j++){
                double data[] = image.get(j, i);
                //data[0] = data[0]*data[0]/128;
                image.put(j, i, data);
            }
        }

        Mat temp = image.clone();
        temp.convertTo(temp, CvType.CV_8U);
        Imgproc.Canny(temp, temp, 10, 100);

        //image.convertTo(image, CvType.CV_32F); //was used for gabor kernel
        //edges = image;
        //Mat gabor = Imgproc.getGaborKernel(ksize,3,Math.PI/4,6.0,0.7,0,CvType.CV_32F);
        //Imgproc.filter2D(image, image, -1, gabor); //apply gabor filter

        //Imgproc.Sobel(edges, edges, CvType.CV_32F, 1, 0); //derivative of image

        //all left/right descriptors are relative to the person in the photo
        double leftX = image.width()/2;
        double leftY = image.height()/2;
        double rightX = image.width()/2;
        double rightY = image.height()/2;

        MatOfPoint arr = new MatOfPoint();
        Imgproc.goodFeaturesToTrack(image, arr, 45, 0.01, 0);
        for(int i =0; i<arr.rows(); i++){
            double pt[] = arr.get(i, 0);
            //Imgproc.rectangle(image, new Point(pt[0], pt[1]), new Point(pt[0] + 2, pt[1] + 2),
            //            new Scalar(0, 255, 0));
            if(Math.abs(pt[1]-image.height()/2)/image.height() < 0.4){ //make sure point is within the middle of the image
                if(pt[0] > leftX){ //get the leftmost and rightmost points (very likely to be the corners of the eye/mouth)
                    leftX = pt[0];
                    leftY = pt[1];
                }
                if(pt[0] < rightX){
                    rightX = pt[0];
                    rightY = pt[1];
                }
            }
        }

        double midX = (rightX+leftX)/2;
        double midY = (rightY+leftY)/2;

        boolean nfound = true;

        Log.d(TAG, leftX + ", " + leftY);
        pt1.x = (float)leftX;
        pt1.y = (float)leftY;
        Log.d(TAG, rightX + ", " + rightY);
        pt2.x = (float)rightX;
        pt2.y = (float)rightY;

        Bitmap mapmap = Bitmap.createBitmap(bitmap,0,0,image.width(),image.height());
        //Utils.matToBitmap(image, mapmap);
    }
}
