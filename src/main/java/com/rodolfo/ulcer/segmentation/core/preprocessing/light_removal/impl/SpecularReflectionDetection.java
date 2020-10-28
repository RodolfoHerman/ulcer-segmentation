package com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl;

import java.util.List;

import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightMaskDetection;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import lombok.extern.slf4j.Slf4j;

import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

/**
 * Article implementation: Detection of Specular Reflection and Segmentation of
 * Cervix Region in Uterine Cervix Images for Cervical Cancer Screening
 */
@Slf4j
public class SpecularReflectionDetection implements LightMaskDetection {
    
    private final int elementSize;
    private final float threshold;

    public SpecularReflectionDetection(int elementSize, float threshold) {

        this.elementSize = elementSize;
        this.threshold = threshold;
    }
    
    @Override
    public Mat lightMask(Mat src) {
        
        log.info("Identificação das regiões com reflexos de luz na imagem");

        return this.thresholdMask(this.stdFilter(this.featureImage(src)));
    }

    private Mat thresholdMask(Mat src) {

        Mat threshold = new Mat(src.size(), opencv_core.CV_8UC1, Scalar.BLACK);

        float[] minMax = OpenCV.getMinMaxIntensityFloatMat(src);

        float limiar = minMax[1] * this.threshold;

        UByteRawIndexer thresholdIndice = threshold.createIndexer();
        FloatRawIndexer srcIndice = src.createIndexer();

        for (int row = 0; row < src.rows(); row++) {
            for(int col = 0; col < src.cols(); col++) {

                float pixel1 = srcIndice.get(row,col);
                
                if(pixel1 >= limiar) {

                    thresholdIndice.put(row, col, 255);
                }
            }
        }

        thresholdIndice.release();
        srcIndice.release();

        Mat filled = OpenCV.findAndFillContoursCV_8UC1(threshold);
        
        return OpenCV.dilateByEllipse(filled, this.elementSize);
    }
    
    private Mat stdFilter(Mat src) {

        Mat filtered = new Mat(src.size(), opencv_core.CV_32FC1);

        FloatRawIndexer featureIndice = src.createIndexer();
        FloatRawIndexer filteredIndice = filtered.createIndexer();

        for (int row = 3; row < filtered.rows() - 3; row++) {
            for(int col = 3; col < filtered.cols() - 3; col++) {

                List<Double> pixels = OpenCV.neighborhoodFloatMat_8(row, col, featureIndice);
                Double media = pixels.stream().mapToDouble(d -> d).average().orElse(0.0);
                Double sum = pixels.stream().mapToDouble(d -> Math.pow((d - media), 2)).sum();

                float newPixel = (float) Math.sqrt((sum.doubleValue()/((3.0 * 3.0) - 1.0)));

                filteredIndice.put(row,col, newPixel);
            }
        }

        featureIndice.release();
        filteredIndice.release();

        return filtered;
    }    

    private Mat featureImage(Mat src) {

        Mat lChannel = this.channelNormalizer(OpenCV.matImage2LAB(src), 0);
        Mat sChannel = this.channelNormalizer(OpenCV.matImage2HSV(src), 1);
        Mat gChannel = this.channelNormalizer(src, 1);

        Mat feature = new Mat(src.size(), opencv_core.CV_32FC1);

        FloatRawIndexer lChannelIndice = lChannel.createIndexer();
        FloatRawIndexer sChannelIndice = sChannel.createIndexer();
        FloatRawIndexer gChannelIndice = gChannel.createIndexer();
        FloatRawIndexer indiceFeature = feature.createIndexer();

        for (int row = 0; row < feature.rows(); row++) {
            for(int col = 0; col < feature.cols(); col++) {

                Float pixelL = lChannelIndice.get(row,col);
                Float pixelS = sChannelIndice.get(row,col);
                Float pixelG = gChannelIndice.get(row,col);

                float pixelF = (float) Math.pow((1.0 - pixelS.doubleValue()) * pixelL.doubleValue() * pixelG.doubleValue(), 3);

                indiceFeature.put(row,col, (pixelF * 255f));
            }
        }

        lChannelIndice.release();
        sChannelIndice.release();
        gChannelIndice.release();
        indiceFeature.release();

        return feature;
    }

    private Mat channelNormalizer(Mat src, int channel) {

        Mat dst = new Mat(src.size(), opencv_core.CV_32FC1);

        FloatRawIndexer indiceDst = dst.createIndexer();
        UByteRawIndexer indiceSrc = src.createIndexer();

        int[] pixel = new int[src.channels()];

        for (int row = 0; row < dst.rows(); row++) {
            for(int col = 0; col < dst.cols(); col++) {

                indiceSrc.get(row, col, pixel);
                indiceDst.put(row, col, (pixel[channel]/255f));
            }
        }

        indiceDst.release();
        indiceSrc.release();

        return dst;
    }

}