package com.rodolfo.ulcer.segmentation.opencv;

import java.util.Arrays;
import java.util.List;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

public class OpenCV {
    
    public static Mat matImage2HSV(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2HSV);

        return dst;
    }

    public static Mat matImage2LAB(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2Lab);

        return dst;
    }

    public static List<Double> neighborhoodFloatMat_8(int row, int col, FloatRawIndexer index) {

        //  Vizinhaça 8
        // | 1-X | 2-O | 3-X |
        // | 4-O |     | 5-O |
        // | 6-X | 7-O | 8-X |
        Float pixel1 = index.get(row - 1 , col - 1);
        Float pixel2 = index.get(row - 1 , col);
        Float pixel3 = index.get(row - 1 , col + 1);
        Float pixel4 = index.get(row, col - 1);
        Float pixel5 = index.get(row, col + 1);
        Float pixel6 = index.get(row + 1 , col - 1);
        Float pixel7 = index.get(row + 1 , col);
        Float pixel8 = index.get(row + 1 , col + 1);

        return Arrays.asList(
            pixel1.doubleValue(),
            pixel2.doubleValue(),
            pixel3.doubleValue(),
            pixel4.doubleValue(),
            pixel5.doubleValue(),
            pixel6.doubleValue(),
            pixel7.doubleValue(),
            pixel8.doubleValue()
        );
    }

    public static float[] getMinMaxIntensityFloatMat(Mat src) {
        
        FloatRawIndexer indice = src.createIndexer();

        float maiorFloat = Integer.MIN_VALUE;
        float menorFloat = Integer.MAX_VALUE;

        for(int row = 3; row < src.rows() - 3; row++) {
            for(int col = 3; col < src.cols() - 3; col++){

                float aux = indice.get(row,col);

                if(maiorFloat < aux) {

                    maiorFloat = aux;
                }

                if(menorFloat > aux) {

                    menorFloat = aux;
                }
            }
        }

        indice.release();

        return new float[]{menorFloat, maiorFloat};
    }

    public static Mat dilateByEllipse(Mat src, int elementSize) {
        
        Mat dst = new Mat();

        Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, new Size(elementSize, elementSize));
        opencv_imgproc.dilate(src, dst, kernel);

        return dst;
    }

    public static Mat findAndFillContoursCV_8UC1(Mat src) {

        Mat dst = src.clone();

        src.convertTo(dst, opencv_core.CV_8UC1);

        MatVector contornos = new MatVector();

        opencv_imgproc.findContours(dst, contornos, new Mat(), opencv_imgproc.RETR_TREE, opencv_imgproc.CHAIN_APPROX_SIMPLE);
        opencv_imgproc.fillPoly(dst, contornos, Scalar.WHITE);

        return dst;
    }
}