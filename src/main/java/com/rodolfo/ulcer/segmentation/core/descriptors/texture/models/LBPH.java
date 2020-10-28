package com.rodolfo.ulcer.segmentation.core.descriptors.texture.models;

import java.util.ArrayList;
import java.util.List;

import com.rodolfo.ulcer.segmentation.core.descriptors.Process;
import com.rodolfo.ulcer.segmentation.models.Point;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

import lombok.Data;

@Data
public class LBPH implements Process {

    private Mat img;
    private List<Point> points;
    private List<Float> values;
    
    public LBPH(Mat img, List<Point> points) {

        this.img = img;
        this.points = points;
    }
    
    @Override
    public void process() {

        Mat dst = this.img.clone();
        this.values = new ArrayList<>();

        dst.convertTo(dst, opencv_core.CV_32FC1);

        FloatRawIndexer index = dst.createIndexer();

        this.points.stream().forEach(point -> {

            int row = point.getRow();
            int col = point.getCol();

            float pixel = index.get(row, col);

            float sum = 0f;

            if(pixel < index.get(row - 1, col - 1)) sum += 128f;
            if(pixel < index.get(row - 1, col))     sum += 64f;
            if(pixel < index.get(row - 1, col + 1)) sum += 32f;
            if(pixel < index.get(row, col + 1))     sum += 16f;
            if(pixel < index.get(row + 1, col + 1)) sum += 8f;
            if(pixel < index.get(row + 1, col))     sum += 4f;
            if(pixel < index.get(row + 1, col - 1)) sum += 2f;
            if(pixel < index.get(row, col - 1))     sum += 1f;

            values.add(sum);
        });

        index.release();
    }
    
}