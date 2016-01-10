package com.sci2015fair.landmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * Created by Jeffrey on 1/2/2016.
 */
public class CalcAttributes {
    private int[][] pt; //x, y points
    private double norm; //distance between bridge of nose and base of nose
    private double mwidth; //mouth width
    double mheight; //mouth height
    double ucurve; //bottom curve of mouth
    double lcurve; //upper curve of mouth
    double lea; //left eyebrow angle
    double rea ; //right eyebrow angle
    double eeor; //eye-eyebrow distance outer right
    double eeir; //eye-eyebrow distance inner right
    double eeol; //eye-eyebrow distance outer left
    double eeil; //eye-eyebrow distance inner left

    CalcAttributes(int[][] _pt) throws Exception {
        if(_pt.length==21){
            this.pt = _pt;
        } else {
            throw new Exception("Length must be 21");
        }
        norm = distance(pt[8][0],pt[8][1],pt[13][0],pt[13][1]); //to normalize distance regardless of image size
        mwidth = distance(pt[16][0],pt[16][1],pt[19][0],pt[19][1])/norm;
        mheight = distance(pt[17][0],pt[17][1],pt[18][0],pt[18][1])/norm;
        ucurve = CalcParabolaVertex(pt[16][0],pt[16][1],pt[17][0],pt[17][1],pt[19][0],pt[19][1]);
        lcurve = CalcParabolaVertex(pt[16][0],pt[16][1],pt[18][0],pt[18][1],pt[19][0],pt[19][1]);
        lea = angle(pt[3][0],pt[3][1],pt[4][0],pt[4][1],pt[5][0],pt[5][1]);
        rea = angle(pt[0][0],pt[0][1],pt[1][0],pt[1][1],pt[2][0],pt[2][1]);
        eeor = distance(pt[0][0],pt[0][1],pt[6][0],pt[6][1])/norm;
        eeir = distance(pt[2][0],pt[2][1],pt[7][0],pt[7][1])/norm;
        eeol = distance(pt[3][0],pt[3][1],pt[9][0],pt[9][1])/norm;
        eeil = distance(pt[5][0],pt[5][1],pt[10][0],pt[10][1])/norm;
    }

    public void writeAttr(String outFile){
        BufferedWriter writer = null;
        File arff = new File(outFile);
        try{
            writer = new BufferedWriter(new FileWriter(arff));
            //arff header
            writer.write("@RELATION face\n\n");
            writer.write("@ATTRIBUTE mouthwidth NUMERIC\n");
            writer.write("@ATTRIBUTE mouthheight NUMERIC\n");
            writer.write("@ATTRIBUTE uppermouthcurve NUMERIC\n");
            writer.write("@ATTRIBUTE lowermouthcurve NUMERIC\n");
            writer.write("@ATTRIBUTE lea NUMERIC\n");
            writer.write("@ATTRIBUTE rea NUMERIC\n");
            writer.write("@ATTRIBUTE eeor NUMERIC\n");
            writer.write("@ATTRIBUTE eeir NUMERIC\n");
            writer.write("@ATTRIBUTE eeol NUMERIC\n");
            writer.write("@ATTRIBUTE eeil NUMERIC\n");
            writer.write("@ATTRIBUTE class {happy, normal, sad, sleepy, surprised}\n\n");
            writer.write("@DATA\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        DecimalFormat df = new DecimalFormat("0.00000000");

        try{
            writer.write(df.format(mwidth) + ", " + df.format(mheight) + ", " + df.format(ucurve) + ", " + df.format(lcurve) + ", " +
                    df.format(lea) + ", " + df.format(rea) + ", " + df.format(eeor) + ", " + df.format(eeir) + ", " + df.format(eeol) + ", " +
                    df.format(eeil) + ", ?\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets distance between two points
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(int x1, int y1, int x2, int y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    /**
     * Gets the A coefficient for a parabola that passes through three points
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @return
     */
    private static double CalcParabolaVertex(int x1, int y1, int x2, int y2, int x3, int y3){
        double denom = (x1 - x2) * (x1 - x3) * (x2 - x3);
        double A     = (x3 * (y2 - y1) + x2 * (y1 - y3) + x1 * (y3 - y2)) / denom;
        //double B     = (x3*x3 * (y1 - y2) + x2*x2 * (y3 - y1) + x1*x1 * (y2 - y3)) / denom;
        //double C     = (x2 * x3 * (x2 - x3) * y1 + x3 * x1 * (x3 - x1) * y2 + x1 * x2 * (x1 - x2) * y3) / denom;
        return A;
    }

    /**
     * Returns angle at x2,y2
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     * @return Angle in radians
     */
    private static double angle(int x1, int y1, int x2, int y2, int x3, int y3){
        double d1 = distance(x2,y2,x3,y3);
        double d2 = distance(x1,y1,x3,y3);
        double d3 = distance(x1,y1,x2,y2);
        return Math.acos((d2*d2 - d1*d1 - d3*d3)/(-2*d1*d3));
    }
}