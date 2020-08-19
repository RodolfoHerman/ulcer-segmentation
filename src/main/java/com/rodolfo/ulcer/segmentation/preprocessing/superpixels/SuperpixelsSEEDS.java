package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSEEDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpixelsSEEDS extends Superpixels {

    private static final Logger log = LoggerFactory.getLogger(SuperpixelsSEEDS.class);

    public SuperpixelsSEEDS(Mat image, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentação em superpixels com o método SEEDS");
        
        Mat imgLAB = OpenCV.matImage2LAB(this.getImage());

        final int rows = imgLAB.rows();
        final int cols = imgLAB.rows();
        final int channels = imgLAB.channels();

        SuperpixelSEEDS seeds = opencv_ximgproc.createSuperpixelSEEDS(
            cols, rows, channels, this.getAmount(), this.getCompactenssI()
        );

        seeds.iterate(imgLAB, this.getIterations());
        seeds.getLabels(this.getLabels());
        seeds.getLabelContourMask(this.getContour());
    }
    
}