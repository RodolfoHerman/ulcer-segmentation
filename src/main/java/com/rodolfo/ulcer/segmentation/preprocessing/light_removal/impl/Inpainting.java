package com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightRemoval;

import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacpp.opencv_core.Mat;

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
    public void lightRemoval(Image image) {
        
        Mat dst = new Mat();

        opencv_photo.inpaint(image.getImage(), inpaintingMask, dst, this.inpaintingNeighbor, this.inpaintingMethod);

        image.setImageWithoutReflection(dst);
    }

}