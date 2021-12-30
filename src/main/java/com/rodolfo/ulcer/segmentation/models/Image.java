package com.rodolfo.ulcer.segmentation.models;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;
import com.rodolfo.ulcer.segmentation.utils.Util;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import lombok.Data;

@Data
public class Image {
    
    private Mat image;
    private Mat labeledImage;
    private Mat labeledFilledContourImage;
    private Mat imageWithoutReflection;

    private Mat grabCutHumanMask;
    private Mat grabCutOverlappingImage;
    private Mat finalUlcerSegmentation;
    private Mat finalBinarySegmentation;
    private Mat skeletonWithBranchs;
    private Mat skeletonWithoutBranchs;
    private Mat mlCLassifiedImage;
    private Mat mlOverlappingImage;

    private Mat superpixelsContourImage;
    private Mat superpixelsColorInformativeImage;

    private int resampleWidth;
    private int resampleHeight;
    private Directory directory;
    private ImageStatistic svmImageStatistic;
    private ImageStatistic grabImageStatistic;

    private String imageNameFromString;

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

    public Size getSize() {

        return this.image.size();
    }

    public Integer getType() {

        return this.image.type();
    }

    public Mat getLabeledFilledContourImage() {

        if(this.labeledFilledContourImage == null) {

            Mat gray = OpenCV.matImage2GRAY(this.labeledImage);
            this.labeledFilledContourImage = OpenCV.findLargerOutlineAndFill(gray);
            opencv_core.bitwise_not(this.labeledFilledContourImage, this.labeledFilledContourImage);
        }
        
        return this.labeledFilledContourImage;
    }
}