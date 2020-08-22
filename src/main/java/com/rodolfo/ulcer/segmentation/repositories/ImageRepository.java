package com.rodolfo.ulcer.segmentation.repositories;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;

public class ImageRepository {
    
    public void open(Image image) {

        Mat matImage = opencv_imgcodecs.imread(image.getDirectory().getImagePath().getAbsolutePath());

        image.setImage(OpenCV.resize(matImage, image.getResampleWidth(), image.getResampleHeight()));
    }

    public void openWithLabeled(Image image) {

        Mat matImage = opencv_imgcodecs.imread(image.getDirectory().getImagePath().getAbsolutePath());
        Mat matLabeledImage = opencv_imgcodecs.imread(image.getDirectory().getLabeledImagePath().getAbsolutePath());

        image.setImage(OpenCV.resize(matImage, image.getResampleWidth(), image.getResampleHeight()));
        image.setLabeledImage(OpenCV.resize(matLabeledImage, image.getResampleWidth(), image.getResampleHeight()));
    }

}