package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpixelsSLIC extends Superpixels {

    private static final Logger log = LoggerFactory.getLogger(SuperpixelsSLIC.class);

    public SuperpixelsSLIC(Image image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, imageEdge, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentação em superpixels com o método SLIC");

        Mat imgLAB = OpenCV.matImage2LAB(this.image.getImageWithoutReflection());

        SuperpixelSLIC slic = opencv_ximgproc.createSuperpixelSLIC(
            imgLAB, opencv_ximgproc.SLIC, this.amount, this.compactenssI
        );

        slic.iterate(this.iterations);
        slic.enforceLabelConnectivity(this.amount - 1);
        slic.getLabels(this.labels);
        slic.getLabelContourMask(this.contour);
        this.superpixelsAmount = slic.getNumberOfSuperpixels();
        slic.deallocate();

        this.makeContourImage();
        this.makeSuperpixelsSegmentation();
    }

}