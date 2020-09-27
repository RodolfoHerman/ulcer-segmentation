package com.rodolfo.ulcer.segmentation.core.descriptors.color.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.core.descriptors.Process;
import com.rodolfo.ulcer.segmentation.models.Point;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.TermCriteria;

import lombok.Data;

import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

@Data
public class Color implements Process {
    
    private Mat img;
    private Mat img1Row;
    private List<Point> points;
    private int k;
    private int[] centroid;
    private Mat channel1;
    private Mat channel2;
    private Mat channel3;

    public Color(Mat img, List<Point> points, int k) {

        this.img = img;
        this.points = points;
        this.k = k;

        this.createImg1Row();
        this.createSplitMat();
    }

    @Override
    public void process() {
        
        Mat sample = this.img1Row.reshape(1, this.img1Row.rows() * this.img1Row.cols());
        Mat sample32f = new Mat();

        sample.convertTo(sample32f, opencv_core.CV_32F, 1.0/255.0, 0);

        TermCriteria tCriteria = new TermCriteria(TermCriteria.COUNT, 100, 1);

        Mat labels = new Mat();
        Mat centers = new Mat();

        opencv_core.kmeans(sample32f, this.k, labels, tCriteria, 1, opencv_core.KMEANS_RANDOM_CENTERS, centers);

        this.calculateDominantColor(labels, centers);
    }

    private void createImg1Row() {

        this.img1Row = new Mat(1, this.points.size(), this.img.type(), Scalar.BLACK);

        UByteRawIndexer indexImg = this.img.createIndexer();
        UByteRawIndexer indexImg1Row = this.img1Row.createIndexer(); 

        int tempCol = 0;

        for (Point point : this.points) {

            int row = point.getRow();
            int col = point.getCol();

            int[] pixel = new int[this.img.channels()];

            indexImg.get(row, col, pixel);
            indexImg1Row.put(0, tempCol, pixel);

            tempCol++;
        }

        indexImg.release();
        indexImg1Row.release();
    }

    private void createSplitMat() {

        this.channel1 = OpenCV.getMatChannel(this.img1Row, 0);
        this.channel2 = OpenCV.getMatChannel(this.img1Row, 1);
        this.channel3 = OpenCV.getMatChannel(this.img1Row, 2);
    }

    private void calculateDominantColor(Mat labels, Mat centers) {

        centers.convertTo(centers, opencv_core.CV_8UC1, 255.0, 0);
        
        IntRawIndexer indexLabels = labels.createIndexer();
        UByteRawIndexer indexCenters = centers.createIndexer();

        Map<Integer,Integer> amount = new HashMap<>();

        for(int x = 0; x < this.k; x++) {

            amount.put(x,0);
        }

        for(int row = 0; row < labels.rows(); row++) {
            for(int col = 0; col < labels.cols(); col++) {

                int label = indexLabels.get(row, col);

                int aux = amount.get(label) + 1;
                amount.put(label, aux);
            }
        }

        int largestCluster = this.getLargestCluster(amount);
        this.centroid = new int[this.img.channels()];
        
        indexCenters.get(largestCluster, 0, this.centroid);

        indexLabels.release();
        indexCenters.release();
    }

    private int getLargestCluster(Map<Integer,Integer> amount) {

        int largestCluster = 0;
        int label = 0;

        for (Map.Entry<Integer, Integer> entry : amount.entrySet()) {

            if(entry.getValue() > largestCluster) {

                largestCluster = entry.getValue();
                label = entry.getKey();
            }
        }

        return label;
    }
}