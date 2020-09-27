package com.rodolfo.ulcer.segmentation.core.descriptors.texture;

import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.utils.Util;
import com.rodolfo.ulcer.segmentation.utils.statistic.HistogramStatistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LBPHDescriptors {
    
    private static final Logger log = LoggerFactory.getLogger(LBPHDescriptors.class);

    List<Float> values;
    Map<Float,Integer> histogram;

    public LBPHDescriptors(List<Float> values) {

        this.values = values;

        this.createHistogram();
    }

    private void createHistogram() {

        log.info("Criando o histograma para os descritores LBPH");

        this.histogram = Util.createHistogram(this.values);
    }

    public Double mean() {

        return HistogramStatistic.mean(this.histogram);
    }

    public Double variance() {

        return HistogramStatistic.variance(this.histogram);
    }

    public Double entropy() {

        return HistogramStatistic.entropy(this.histogram);
    }

    public Double energy() {

        return HistogramStatistic.energy(this.histogram);
    }
    
}