package com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelLSC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuperpixelsLSC extends Superpixels {

    public SuperpixelsLSC(Image image, Integer imageEdge, Integer iterations, Integer amount, Float compactness) {

        super(image, imageEdge, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {
        
        log.info("Segmentando a imagem em superpixel com o método LSC");

        Mat imgLAB = OpenCV.matImage2LAB(this.image.getImageWithoutReflection());

        SuperpixelLSC lsc = opencv_ximgproc.createSuperpixelLSC(
            imgLAB, this.amount, this.compactenssF
        );

        lsc.iterate(this.iterations);
        lsc.enforceLabelConnectivity(this.amount - 1);
        lsc.getLabels(this.labels);
        lsc.getLabelContourMask(this.contour);
        this.superpixelsAmount = lsc.getNumberOfSuperpixels();
        lsc.deallocate();
        
        this.makeContourImage();
        this.makeSuperpixelsSegmentation();
    }

}