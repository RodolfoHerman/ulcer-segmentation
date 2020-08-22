package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;

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
    private Mat contourImage;
    private Integer superpixelsAmount;

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

    protected void makeContourImage() {

        Mat mask = new Mat();
        contourImage = this.image.clone();
        MatVector channels = new MatVector();

        opencv_core.split(contourImage, channels);
        
        Mat channel_1 = channels.get(0);
        Mat channel_2 = channels.get(1);
        Mat channel_3 = channels.get(2);
        
        opencv_core.bitwise_not(OpenCV.dilateByCross(this.contour, 3), mask);

        opencv_core.bitwise_and(channel_1, mask, channel_1);
        opencv_core.bitwise_and(channel_2, mask, channel_2);
        opencv_core.bitwise_and(channel_3, mask, channel_3);

        opencv_core.merge(channels, contourImage);
    }

}