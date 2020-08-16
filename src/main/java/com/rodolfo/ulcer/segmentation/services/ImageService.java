package com.rodolfo.ulcer.segmentation.services;

import com.rodolfo.ulcer.segmentation.models.Image;

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
}