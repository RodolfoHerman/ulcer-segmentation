package com.rodolfo.ulcer.segmentation.core.descriptors.texture.models;

import java.util.List;

import com.rodolfo.ulcer.segmentation.core.descriptors.Process;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.enums.GlcmDegreeEnum;
import com.rodolfo.ulcer.segmentation.models.Point;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import lombok.Data;

@Data
public class HaralickGlcm implements Process {

    private Mat img;
    private Mat glcm;
    private List<Point> points;
    private GlcmDegreeEnum gde;
    private int distance;

    public HaralickGlcm(Mat img, List<Point> points, GlcmDegreeEnum gde, int distance) {

        this.img = img;
        this.points = points;
        this.gde = gde;
        this.distance = distance;
    }

    public void process() {

        int maxIntensity = 255; //OpenCV.getMinMaxIntensityFromSuperpixel(this.img, this.points, this.distance)[1];

        this.glcm = Mat.zeros(maxIntensity+1, maxIntensity+1, opencv_core.CV_32FC1).asMat();

        UByteRawIndexer imgIndex  = this.img.createIndexer();
        FloatRawIndexer glcmIndex = this.glcm.createIndexer();

        this.points.stream().forEach(point -> {

            int val_1 = imgIndex.get(point.getRow(), point.getCol());
            int val_2 = this.getVal(imgIndex, point);

            float val_3 = glcmIndex.get(val_1, val_2) + 1f;

            glcmIndex.put(val_1, val_2, val_3);
        });

        imgIndex.release();
        glcmIndex.release();
    }

    private int getVal(UByteRawIndexer index, Point point) {
        
        if(this.gde.toString().equals("DEGREE_0")) {

            return index.get(point.getRow(), point.getCol() + this.distance);
        }

        if(this.gde.toString().equals("DEGREE_45")) {

            return index.get(point.getRow() - this.distance, point.getCol() + this.distance);
        }

        if(this.gde.toString().equals("DEGREE_90")) {

            return index.get(point.getRow() - this.distance, point.getCol());
        }

        return index.get(point.getRow() - this.distance, point.getCol() - this.distance);
    }
}