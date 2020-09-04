package com.rodolfo.ulcer.segmentation.services;

import java.io.File;

import com.rodolfo.ulcer.segmentation.models.Image;

import org.bytedeco.javacpp.opencv_core.Mat;

public interface ImageService {
    
    /**
     * Open image
     * @param image
     */
    void open(Image image);

    /**
     * open images
     * @param image
     */
    void openWithLabeled(Image image);

    /**
     * Save images
     * @param image
     */
    void save(Image image);

    /**
     * Save Mat images
     * @param img
     * @param path
     */
    void save(Mat img, File path);
}