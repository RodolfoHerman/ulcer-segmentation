package com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal;

import com.rodolfo.ulcer.segmentation.models.Image;

public interface LightRemoval {

    /**
     * Reliza a remoção do reflexo de luz (Specular Reflection)
     * @param image
     * @param kernelFilterSize
     */
    void lightRemoval(Image image, int kernelFilterSize);

}