package com.rodolfo.ulcer.segmentation.core.descriptors.color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.utils.Util;
import com.rodolfo.ulcer.segmentation.utils.statistic.HistogramStatistic;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

public class ColorDescriptors {

    private Mat img;
    private Map<Float,Integer> histogram;
    private Float frequency1;
    private Float frequency2;
    private Integer intensity1;
    private Integer intensity2;

    public ColorDescriptors(Mat img) {

        this.img = img;

        this.createHistogram();
        this.twoHighestPeaksHistograms();
    }

    private void createHistogram() {

        Mat imgF = this.img.clone();
        List<Float> values = new ArrayList<>();

        imgF.convertTo(imgF, opencv_core.CV_32F);

        FloatRawIndexer index = imgF.createIndexer();

        for(int row = 0; row < imgF.rows(); row++) {
            for(int col = 0; col < imgF.cols(); col++) {

                values.add(index.get(row, col));
            }
        }

        index.release();

        this.histogram = Util.createHistogram(values);
    }

    private void twoHighestPeaksHistograms() {

        Mat hist = new Mat();

        opencv_imgproc.calcHist(this.img, 1, new int[]{0}, new Mat(), hist, 1, new int[]{256}, new float[]{0f, 256f}, true, false);
        // apply moving average filter of size 3
        opencv_imgproc.medianBlur(hist, hist, 3);
        
        float[] histData = new float[(int)(hist.total() * hist.channels())];

        FloatRawIndexer index = hist.createIndexer();

        index.get(0, 0, histData);

        index.release();
        
        this.intensity1 = 0;
        this.frequency1 = -1f;

        this.intensity2 = 0;
        this.frequency2 = -1f;

        boolean newPeak1 = false;
        boolean newPeak2 = false;

        for(int x = 1; x < histData.length; x++) {

            // peak 1 rise
            while(x < (histData.length - 1) && this.frequency1 <= histData[x]) {

                // check if peak 1 is greater than 2 in the new formation
                if(newPeak2 && this.frequency2 < this.frequency1) {

                    this.frequency2 = this.frequency1;
                    this.intensity2 = this.intensity1;
                    newPeak2 = false;
                }

                this.frequency1 = histData[x];
                this.intensity1 = x;
                x++;
            }

            // peak 1 descent
            while(x < (histData.length - 1) && histData[x] <= histData[x -1]) {

                x++;
            }

            // peak 2 rise
            while(x < (histData.length - 1) && this.frequency2 <= histData[x]) {

                // check if peak 2 is greater than 1 in the new formation
                if(newPeak1 && this.frequency1 < this.frequency2) {

                    this.frequency1 = this.frequency2;
                    this.intensity1 = this.intensity2;
                    newPeak1 = false;
                }

                this.frequency2 = histData[x];
                this.intensity2 = x;
                x++;
            }

            // peak 2 descent
            while(x < (histData.length - 1) && histData[x] <= histData[x -1]) {

                x++;
            }

            newPeak1 = true;
            newPeak2 = true;
        }

        if(this.frequency2 <= 0f) {

            this.frequency2 = this.frequency1;
            this.intensity2 = this.intensity1;
        }
    }

    public Double mean() {

        return HistogramStatistic.mean(this.histogram);
    }

    public Double variance() {

        return HistogramStatistic.variance(this.histogram);
    }

    public Double asymmetry() {

        return HistogramStatistic.asymmetry(this.histogram);
    }

    public Double frequency1() {

        return this.frequency1.doubleValue();
    }

    public Double intensity1() {

        return this.intensity1.doubleValue();
    }

    public Double frequency2() {

        return this.frequency2.doubleValue();
    }

    public Double intensity2() {

        return this.intensity2.doubleValue();
    }

}