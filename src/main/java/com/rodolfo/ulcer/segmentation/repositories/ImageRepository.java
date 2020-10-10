package com.rodolfo.ulcer.segmentation.repositories;

import java.io.File;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;

public class ImageRepository {

    public void open(Image image) {

        Mat matImage = opencv_imgcodecs.imread(image.getDirectory().getImagePath().getAbsolutePath());
        Mat matLabeledImage = opencv_imgcodecs.imread(image.getDirectory().getLabeledImagePath().getAbsolutePath());

        image.setImage(OpenCV.resize(matImage, image.getResampleWidth(), image.getResampleHeight()));
        image.setLabeledImage(OpenCV.resize(matLabeledImage, image.getResampleWidth(), image.getResampleHeight()));
    }

    public void save(Mat img, File path) {

        opencv_imgcodecs.imwrite(path.getAbsolutePath(), img);
    }

}