package com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSEEDS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuperpixelsSEEDS extends Superpixels {

    private static final Logger log = LoggerFactory.getLogger(SuperpixelsSEEDS.class);

    public SuperpixelsSEEDS(Image image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, imageEdge, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentação em superpixels com o método SEEDS");
        
        Mat imgLAB = OpenCV.matImage2LAB(this.image.getImageWithoutReflection());

        final int width = imgLAB.arrayWidth();
        final int height = imgLAB.arrayHeight();
        final int channels = 3; //this.getImage().getImageWithoutReflection().channels();

        SuperpixelSEEDS seeds = opencv_ximgproc.createSuperpixelSEEDS(
            width, height, channels, this.amount, this.compactenssI
        );

        seeds.iterate(imgLAB, this.iterations);
        seeds.getLabels(this.labels);
        seeds.getLabelContourMask(this.contour);
        this.superpixelsAmount = seeds.getNumberOfSuperpixels();
        seeds.deallocate();

        this.makeContourImage();
        this.makeSuperpixelsSegmentation();
    }

}