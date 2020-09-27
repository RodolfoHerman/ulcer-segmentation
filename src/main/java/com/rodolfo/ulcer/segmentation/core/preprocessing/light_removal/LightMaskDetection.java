package com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal;

import org.bytedeco.javacpp.opencv_core.Mat;

public interface LightMaskDetection {

    /**
     * Criar máscara binária que aponta os reflexos de luz
     * @param src
     * @return Mat - Máscara binária
     */
    Mat lightMask(Mat src);
}