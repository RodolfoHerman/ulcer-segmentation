package com.rodolfo.ulcer.segmentation.preprocessing.light_removal;

import com.rodolfo.ulcer.segmentation.models.Image;

public interface LightRemoval {

    /**
     * Reliza a remoção do reflexo de luz (Specular Reflection)
     * @param image
     */
    void lightRemoval(Image image);

}