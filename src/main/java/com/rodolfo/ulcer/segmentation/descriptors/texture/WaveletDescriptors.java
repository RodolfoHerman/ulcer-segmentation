package com.rodolfo.ulcer.segmentation.descriptors.texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.models.Point;
import com.rodolfo.ulcer.segmentation.utils.Util;
import com.rodolfo.ulcer.segmentation.utils.statistic.HistogramStatistic;

import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveletDescriptors {
    
    private static final Logger log = LoggerFactory.getLogger(WaveletDescriptors.class);

    private Mat wT;
    private Mat wTNorm;
    private Map<Float,Integer> wThistogram;
    private Map<Float,Integer> wTNormhistogram;
    private List<Point> points;

    public WaveletDescriptors(Mat wT, Mat wTNorm, List<Point> points) {

        this.wT = wT;
        this.wTNorm = wTNorm;
        this.points = points;

        this.createHistograms();
    }

    private void createHistograms() {

        log.info("Criando os histogramas para os descritores wavelet");

        List<Float> wTValues  = new ArrayList<>();
        List<Float> wTNormValues  = new ArrayList<>();

        FloatRawIndexer wTIndex = this.wT.createIndexer();
        FloatRawIndexer wTNormIndex = this.wTNorm.createIndexer();

        this.points.stream().forEach(point -> {

            int row = point.getRow();
            int col = point.getCol();

            wTValues.add(wTIndex.get(row, col));
            wTNormValues.add(wTNormIndex.get(row, col));
        });

        wTIndex.release();
        wTNormIndex.release();

        this.wThistogram = Util.createHistogram(wTValues);
        this.wTNormhistogram = Util.createHistogram(wTNormValues);
    }

    public Double energy() {

        return HistogramStatistic.energy(wThistogram);
    }

    public Double entropy() {

        return HistogramStatistic.entropy(wTNormhistogram);
    }

}