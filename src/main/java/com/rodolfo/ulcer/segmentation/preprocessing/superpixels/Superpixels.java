package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rodolfo.ulcer.segmentation.models.Point;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import lombok.Data;

@Data
public abstract class Superpixels {
    
    private Integer imageEdge;
    private Integer iterations;
    private Integer amount;
    private Integer compactenssI;
    private Float compactenssF;
    private Mat image;
    private Mat labels = new Mat();
    private Mat contour = new Mat();
    private Mat contourImage;
    private Integer superpixelsAmount;
    private Set<Integer> intLabels;
    private Map<Integer, List<Point>> superpixelsSegmentation;

    public Superpixels(Mat image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {

        this.image = image;
        this.imageEdge = imageEdge;
        this.iterations = iterations;
        this.amount = amount;
        this.compactenssI = compactness;
    }

    public Superpixels(Mat image, Integer imageEdge, Integer iterations, Integer amount, Float compactness) {

        this.image = image;
        this.imageEdge = imageEdge;
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

    protected void makeSuperpixelsSegmentation() {

        this.intLabels = new HashSet<>();
        this.superpixelsSegmentation = new HashMap<>();

        IntRawIndexer index = this.labels.createIndexer();

        for(int row = 0; row < this.labels.rows(); row++) {
            for(int col = 0; col < this.labels.cols(); col++) {

                this.intLabels.add(index.get(row, col));
            }
        }

        this.intLabels.stream().forEach(label -> {

            Mat comp = new Mat(1, 1, opencv_core.CV_32SC1, new Scalar(label.doubleValue()));
            Mat aux  = new Mat(this.labels.size(), opencv_core.CV_8UC1, new Scalar(Double.MAX_VALUE));

            opencv_core.compare(this.labels, comp, aux, opencv_core.CMP_EQ);

            this.superpixelsSegmentation.put(label, this.extractPixelsIndex(aux));
        });

        index.release();
    }

    private List<Point> extractPixelsIndex(Mat aux) {

        UByteRawIndexer index = aux.createIndexer();
        List<Point> points = new ArrayList<>();

        for(int row = this.imageEdge; row < aux.rows(); row++) {
            for(int col = this.imageEdge; col < aux.cols(); col++) {

                if(index.get(row, col) != 0) {

                    points.add(new Point(row, col));
                }
            }
        }

        index.release();

        return points;
    }

}