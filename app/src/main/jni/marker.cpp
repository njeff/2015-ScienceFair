//
// Created by Jeffrey on 12/24/2015.
// based on the example clandmark code (https://github.com/uricamic/flandmark)
//

#include "marker.h"

/**
 * Runs the Clandmark facial landmarking library
 * image is written in one continuous array, row after row starting from the top
 */
extern "C" {
    using namespace clandmark;
    using namespace std;

    JNIEXPORT jintArray JNICALL Java_com_sci2015fair_landmark_Classify_runLandmarks(JNIEnv *env, jobject thiz, jint cols, jint rows, jintArray bbox, jbyteArray data, jstring inpath){

        const char* path = env->GetStringUTFChars(inpath,NULL); //path to the flandmark_model.xml
        jbyte* data2 = env->GetByteArrayElements(data,0); //load image data
        jint* bbox2 = env->GetIntArrayElements(bbox,0); //load bounding box data

        cimg_library::CImg<unsigned char> * resultImage = new cimg_library::CImg<unsigned char>(cols, rows);

        for (int y = 0; y < rows; ++y) //load image from datastream to CImg
            for (int x = 0; x < cols; ++x)
                (*resultImage)(x, y) = data2[y*cols + x];

        Flandmark *flandmark = Flandmark::getInstanceOf((path));
        CFeaturePool *featurePool = new CFeaturePool(flandmark->getBaseWindowSize()[0], flandmark->getBaseWindowSize()[1]);
        featurePool->addFeaturesToPool(
            new CSparseLBPFeatures(
                featurePool->getWidth(),
                featurePool->getHeight(),
                featurePool->getPyramidLevels(),
                featurePool->getCumulativeWidths()
            )
        );
        flandmark->setNFfeaturesPool(featurePool);
        flandmark->detect_optimized(resultImage, bbox2); //detect landmarks
        delete resultImage;

        fl_double_t *landmarks;
        landmarks = flandmark->getLandmarks();

        //return results
        int size = 2*(flandmark->getLandmarksCount());
        jintArray results = env->NewIntArray(size);

        jint fill[128];
        for(int i = 0; i<size; i++){
            fill[i] = (int)landmarks[i];
        }
        env->SetIntArrayRegion(results, 0, size, fill);
        LOGD("JNI","Clandmark done");
        return results;
    }
}