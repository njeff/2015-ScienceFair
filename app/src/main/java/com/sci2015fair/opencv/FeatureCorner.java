package com.sci2015fair.opencv;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jeffrey on 12/13/2015.
 */
public class FeatureCorner {
    void edgeCorners(Bitmap bitmap){
        Mat image = new Mat();
        Utils.bitmapToMat(bitmap, image);

        Size ksize = new Size(0,0);
        ksize.height = 7;
        ksize.width = 7;

        Mat edges = new Mat();
        Core.normalize(image,image,0,255, Core.NORM_MINMAX, CvType.CV_8UC3); //normalize image
        Imgproc.GaussianBlur(image, image, ksize, 3, 1); //reduce noise
        Imgproc.bilateralFilter(image, edges, 20, 22, 10);

        for(int i = 0; i<image.width(); i++){
            for(int j = 0; j<image.height(); j++){
                double data[] = image.get(j, i);
                data[0] = data[0]*data[0]/128;
                image.put(j, i, data);
            }
        }
        Imgproc.Canny(image, image, 10, 100); //edge detection
        image.convertTo(image, CvType.CV_32F); //was used for gabor kernel

        edges = image;

        boolean l = true;
        boolean r = true;

        for(int i = 0; i<edges.width(); i++){
            for(int j = 0; j<edges.height(); j++){
                double[] pix = edges.get(j,i); //from the left
                if(pix[0] > 130 && l){
                    System.out.println(j + " " + i);
                    l = false;
                    Imgproc.rectangle(edges, new Point(i, j), new Point(i + 2, j + 2),
                            new Scalar(0, 255, 0));
                }

                int rightpix = edges.width()-i-1;
                pix = edges.get(j,rightpix); //from the right
                if(pix[0] > 120 && r){
                    System.out.println(j + " " + rightpix);
                    r = false;
                    Imgproc.rectangle(edges, new Point(rightpix, j), new Point(rightpix + 2, j + 2),
                            new Scalar(0, 255, 0));
                }
            }
            if(!l&&!r) break;
        }
    }
}
