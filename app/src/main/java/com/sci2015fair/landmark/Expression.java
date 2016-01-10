package com.sci2015fair.landmark;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.sci2015fair.filecontrolcenter.SaveLocations;
import com.sci2015fair.fileoperations.ExpressionCSV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import wlsvm.WLSVM;

/**
 * Created by Jeffrey on 12/31/2015.
 */
public class Expression {
    private String TAG = "Express";
    private Classifier cls;

    /**
     * Allows for classification of .arff
     * @param res Resource to the training data
     * @param context
     */
    Expression(int res, Context context){
        Classifier cModel = (Classifier)new NaiveBayes();

        InputStream ins = context.getResources().openRawResource(res);
        File trainDir = context.getDir("model", Context.MODE_PRIVATE);
        File training = new File(trainDir, "complete.arff");
        //unclassified arff generation will guarantee /Data exists
        File model = SaveLocations.model;
        try {
            //if (!model.exists()) { //if there is not pre-trained model
                FileOutputStream fos = new FileOutputStream(training); //load training data
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = ins.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                ins.close();
                fos.close();

                Instances i = new Instances(new BufferedReader(
                        new FileReader(training.getAbsolutePath())));
                i.setClassIndex(i.numAttributes() - 1); //set class
                cModel.buildClassifier(i); //create classifier
                weka.core.SerializationHelper.write(SaveLocations.model.getAbsolutePath(), cModel); //save model
            //} else {
            //    cModel = (Classifier) weka.core.SerializationHelper.read(SaveLocations.model.getAbsolutePath()); //load model
            //    Log.i(TAG, "Training data file exists.");
            //}
            Log.d(TAG,"Model Loaded Successfully");
        } catch (Exception e){
            e.printStackTrace();
        }
        cls = cModel;
    }

    /**
     * Runs classifier
     * @param inFile Path to .arff to be classifed
     * @param outFile Path to classified .arff
     */
    public void getExpression(String inFile, String outFile){
        try {
            Instances unlabeled = new Instances( //load .arff
                    new BufferedReader(
                            new FileReader(inFile)));
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

            // label instance
            double dist[] = cls.distributionForInstance(unlabeled.instance(0));
            double predict = cls.classifyInstance(unlabeled.instance(0));
            String preClass = unlabeled.classAttribute().value((int)predict);
            Log.d(TAG, preClass);

            for(int i = 0; i<dist.length; i++){
                String label = unlabeled.classAttribute().value(i);
                Log.d(TAG,label + ": " + dist[i]);
            }

            ExpressionCSV.writeLog(preClass,dist); //save emotions to log file
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
