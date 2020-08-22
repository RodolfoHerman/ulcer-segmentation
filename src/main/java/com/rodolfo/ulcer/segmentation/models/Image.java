package com.rodolfo.ulcer.segmentation.models;

import org.bytedeco.javacpp.opencv_core.Mat;

import lombok.Data;

@Data
public class Image {
    
    private Mat image;
    private Mat labeledImage;
    private Mat imageWithoutReflection;
    private int resampleWidth;
    private int resampleHeight;
    private Directory directory;

    public Image() {}

    public Image(Directory directory, int resampleWidth, int resampleHeight) {

        this.directory = directory;
        this.resampleWidth = resampleWidth;
        this.resampleHeight = resampleHeight;
    }
}