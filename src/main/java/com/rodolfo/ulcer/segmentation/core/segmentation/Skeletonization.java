package com.rodolfo.ulcer.segmentation.core.segmentation;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import lombok.Getter;
import lombok.Setter;

/**
 * Article implementation:
 * A Fast Parallel Algorithm for Thinning Digital Patterns (Skeleton Zhang-Suen)
 * Adapted from:
 * https://github.com/krishraghuram/Zhang-Suen-Skeletonization/blob/master/skeletonization.hpp
 */
@Getter
@Setter
public class Skeletonization implements Process {

    private Configuration conf;
    private Mat skeletonWithBranchs;
    private Mat skeletonWithoutBranchs;

    public Skeletonization(Configuration conf, Mat outlineFilled) {

        this.conf = conf;
        this.skeletonWithBranchs = outlineFilled.clone();
    }

    @Override
    public void process() {

        Mat value = new Mat(this.skeletonWithBranchs.size(), opencv_core.CV_8UC1, Scalar.WHITE);
        Double area = this.calculateArea(this.skeletonWithBranchs) * this.conf.getSkeletonAreaErode();

        opencv_core.divide(this.skeletonWithBranchs, value, this.skeletonWithBranchs);

        do {
            
            erode(this.skeletonWithBranchs, 0);
            erode(this.skeletonWithBranchs, 1);

        } while (this.calculateArea(this.skeletonWithBranchs) > area);

        opencv_core.multiply(this.skeletonWithBranchs, value, this.skeletonWithBranchs);
        this.skeletonWithoutBranchs = OpenCV.erodeByCross(this.skeletonWithBranchs, this.conf.getSkeletonKernelErodeSize());
    }

    public void createSkeletonImageView() {

        opencv_core.bitwise_not(this.skeletonWithBranchs, this.skeletonWithBranchs);
        this.skeletonWithBranchs = OpenCV.erodeByCross(this.skeletonWithBranchs, 3);
    }

    private int verify(boolean condition) {

        return condition ? 1 : 0;
    }

    private void erode(Mat img, int iter) {

        Mat aux = Mat.ones(img.size(), opencv_core.CV_8UC1).asMat();

        UByteRawIndexer imgIndex = img.createIndexer();
        UByteRawIndexer auxIndex = aux.createIndexer();

        for(int row = this.conf.getImageEdgePixelDistance(); row < img.rows() - this.conf.getImageEdgePixelDistance(); row++) {
            for(int col = this.conf.getImageEdgePixelDistance(); col < img.cols()- this.conf.getImageEdgePixelDistance(); col++) {

                int p2 = imgIndex.get(row-1, col);
                int p3 = imgIndex.get(row-1, col+1);
                int p4 = imgIndex.get(row, col+1);
                int p5 = imgIndex.get(row+1, col+1);
                int p6 = imgIndex.get(row+1, col);
                int p7 = imgIndex.get(row+1, col-1);
                int p8 = imgIndex.get(row, col-1);
                int p9 = imgIndex.get(row-1, col-1);

                int a = this.verify((p2 == 0 && p3 == 1)) + this.verify((p3 == 0 && p4 == 1)) +
                        this.verify((p4 == 0 && p5 == 1)) + this.verify((p5 == 0 && p6 == 1)) +
                        this.verify((p6 == 0 && p7 == 1)) + this.verify((p7 == 0 && p8 == 1)) +
                        this.verify((p8 == 0 && p9 == 1)) + this.verify((p9 == 0 && p2 == 1));

                int b = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;

                int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
                int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

                if ((a == 1) && (b >= 2 && b <= 6) && m1 == 0 && m2 == 0) {
                    auxIndex.put(row, col, 0);
                }
                    
            }
        }

        imgIndex.release();
        auxIndex.release();

        opencv_core.bitwise_and(aux, img, img);
    }

    private Double calculateArea(Mat img) {

        UByteRawIndexer index = img.createIndexer();

        int total = img.cols() * img.rows();
        double resp = 0.0; 

        for(int x = 0; x < total; x++) {

            if(index.get(x) == 1 || index.get(x) == 255) {

                resp += 1;
            }
        }

        index.release();

        return resp;
    }

}
