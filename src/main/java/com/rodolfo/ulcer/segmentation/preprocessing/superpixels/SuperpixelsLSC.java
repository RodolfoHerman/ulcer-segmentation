package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelLSC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpixelsLSC extends Superpixels {

    private static final Logger log = LoggerFactory.getLogger(SuperpixelsLSC.class);

    public SuperpixelsLSC(Mat image, Integer iterations, Integer amount, Float compactness) {

        super(image, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentação em superpixels com o método LSC");
        
        Mat imgLAB = OpenCV.matImage2LAB(this.getImage());

        SuperpixelLSC lsc = opencv_ximgproc.createSuperpixelLSC(
            imgLAB, this.getAmount(), this.getCompactenssF()
        );

        lsc.iterate(this.getIterations());
        lsc.getLabels(this.getLabels());
        lsc.getLabelContourMask(this.getContour());
    }
    
}