package com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightRemoval;

import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Inpainting implements LightRemoval {

    private final Mat inpaintingMask;
    private final int inpaintingMethod;
    private final int inpaintingNeighbor;

    private static final Logger log = LoggerFactory.getLogger(LightRemoval.class);

    public Inpainting(Mat inpaintingMask, int inpaintingMethod, int inpaintingNeighbor) {

        this.inpaintingMask = inpaintingMask;
        this.inpaintingMethod = inpaintingMethod;
        this.inpaintingNeighbor = inpaintingNeighbor;
    }

    @Override
    public void lightRemoval(Image image, int kernelFilterSize) {
        
        log.info("Reconstrução das regiões de reflexo com o método Inpainting");

        Mat dst = new Mat();

        opencv_photo.inpaint(image.getImage(), inpaintingMask, dst, this.inpaintingNeighbor, this.inpaintingMethod);
        opencv_imgproc.medianBlur(dst, dst, kernelFilterSize);

        image.setImageWithoutReflection(dst);
    }

}