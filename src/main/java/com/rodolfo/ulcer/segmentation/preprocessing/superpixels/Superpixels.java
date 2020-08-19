package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import org.bytedeco.javacpp.opencv_core.Mat;

import lombok.Data;

@Data
public abstract class Superpixels {
    
    private Integer iterations;
    private Integer amount;
    private Integer compactenssI;
    private Float compactenssF;
    private Mat image;
    private Mat labels = new Mat();
    private Mat contour = new Mat();

    public Superpixels(Mat image, Integer iterations, Integer amount, Integer compactness) {

        this.image = image;
        this.iterations = iterations;
        this.amount = amount;
        this.compactenssI = compactness;
    }

    public Superpixels(Mat image, Integer iterations, Integer amount, Float compactness) {

        this.image = image;
        this.iterations = iterations;
        this.amount = amount;
        this.compactenssF = compactness;
    }

    abstract public void createSuperpixels();

}