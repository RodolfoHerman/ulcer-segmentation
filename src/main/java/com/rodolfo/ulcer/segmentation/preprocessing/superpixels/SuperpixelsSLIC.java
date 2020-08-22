package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpixelsSLIC extends Superpixels {

    private static final Logger log = LoggerFactory.getLogger(SuperpixelsSLIC.class);

    public SuperpixelsSLIC(Mat image, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentação em superpixels com o método SLIC");

        Mat imgLAB = OpenCV.matImage2LAB(this.getImage());

        SuperpixelSLIC slic = opencv_ximgproc.createSuperpixelSLIC(
            imgLAB, opencv_ximgproc.SLIC, this.getAmount(), this.getCompactenssI()
        );

        slic.iterate(this.getIterations());
        slic.enforceLabelConnectivity(this.getAmount() - 1);
        slic.getLabels(this.getLabels());
        slic.getLabelContourMask(this.getContour());
        this.setSuperpixelsAmount(slic.getNumberOfSuperpixels());
        slic.deallocate();

        this.makeContourImage();
    }

}