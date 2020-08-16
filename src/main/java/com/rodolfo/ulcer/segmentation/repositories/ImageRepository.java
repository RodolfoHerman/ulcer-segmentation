package com.rodolfo.ulcer.segmentation.repositories;

import com.rodolfo.ulcer.segmentation.models.Image;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;

public class ImageRepository {
    
    public void open(Image image) {

        Mat matImage = opencv_imgcodecs.imread(image.getDirectory().getImagePath().getAbsolutePath());

        image.setImage(matImage);
    }

    public void openWithLabeled(Image image) {

        Mat matImage = opencv_imgcodecs.imread(image.getDirectory().getImagePath().getAbsolutePath());
        Mat matLabeledImage = opencv_imgcodecs.imread(image.getDirectory().getLabeledImagePath().getAbsolutePath());

        image.setImage(matImage);
        image.setLabeledImage(matLabeledImage);
    }

}