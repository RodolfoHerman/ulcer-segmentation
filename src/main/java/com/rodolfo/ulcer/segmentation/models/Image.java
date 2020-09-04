package com.rodolfo.ulcer.segmentation.models;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;
import com.rodolfo.ulcer.segmentation.utils.Util;

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

    public Mat matImage2BGR() {

        return this.imageWithoutReflection.clone();
    }

    public Mat matImage2LAB() {

        return OpenCV.matImage2LAB(this.imageWithoutReflection);
    }

    public Mat matImage2LUV() {

        return OpenCV.matImage2LUV(this.imageWithoutReflection);
    }

    public Mat matImage2GRAY() {

        return OpenCV.matImage2GRAY(this.imageWithoutReflection);
    }

    public Mat matImage2BGRNorm() {

        return OpenCV.matImage2BGRNorm(this.imageWithoutReflection);
    }

    public String getImageName() {

        return Util.extractFileNameFromPath(this.directory.getImagePath().getAbsolutePath());
    }
}