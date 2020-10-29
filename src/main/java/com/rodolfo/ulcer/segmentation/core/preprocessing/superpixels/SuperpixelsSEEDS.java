package com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ximgproc;
import org.bytedeco.javacpp.opencv_ximgproc.SuperpixelSEEDS;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SuperpixelsSEEDS extends Superpixels {

    public SuperpixelsSEEDS(Image image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {
        
        super(image, imageEdge, iterations, amount, compactness);
    }

    @Override
    public void createSuperpixels() {
        
        log.info("Segmentando a imagem em superpixel com o método SEEDS");

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

        log.info("Quantidade de superpixels formado na image: '{}'", this.superpixelsAmount);

        this.makeContourImage();
        this.makeSuperpixelsSegmentation();
    }

}