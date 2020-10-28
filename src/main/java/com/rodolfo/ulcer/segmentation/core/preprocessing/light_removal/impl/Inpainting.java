package com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl;

import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightRemoval;
import com.rodolfo.ulcer.segmentation.models.Image;

import org.bytedeco.javacpp.opencv_core.Mat;

import lombok.extern.slf4j.Slf4j;

import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_photo;

@Slf4j
public class Inpainting implements LightRemoval {

    private final Mat inpaintingMask;
    private final int inpaintingMethod;
    private final int inpaintingNeighbor;

    public Inpainting(Mat inpaintingMask, int inpaintingMethod, int inpaintingNeighbor) {

        this.inpaintingMask = inpaintingMask;
        this.inpaintingMethod = inpaintingMethod;
        this.inpaintingNeighbor = inpaintingNeighbor;
    }

    @Override
    public void lightRemoval(Image image, int kernelFilterSize) {

        log.info("Remoção dos reflexos de luz com o método INPAINTING");

        Mat dst = new Mat();

        opencv_photo.inpaint(image.getImage(), inpaintingMask, dst, this.inpaintingNeighbor, this.inpaintingMethod);
        opencv_imgproc.medianBlur(dst, dst, kernelFilterSize);

        image.setImageWithoutReflection(dst);
    }

}