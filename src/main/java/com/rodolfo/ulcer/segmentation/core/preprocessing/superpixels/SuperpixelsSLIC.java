package com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSLIC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuperpixelsSLIC extends Superpixels {

    public SuperpixelsSLIC(Image image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, imageEdge, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {

        log.info("Segmentando a imagem em superpixel com o método SLIC");

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

        log.info("Quantidade de superpixels formado na image: '{}'", this.superpixelsAmount);

        this.makeContourImage();
        this.makeSuperpixelsSegmentation();
    }

}