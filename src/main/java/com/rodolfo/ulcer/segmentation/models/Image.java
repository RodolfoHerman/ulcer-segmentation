package com.rodolfo.ulcer.segmentation.models;

import org.bytedeco.javacpp.opencv_core.Mat;

import lombok.Data;

@Data
public class Image {
    
    private Mat image;
    private Mat labeledImage;
    private Mat imageWithoutReflection;
    private Directory directory;

    public Image() {}

    public Image(Directory directory) {

        this.directory = directory;
    }
}