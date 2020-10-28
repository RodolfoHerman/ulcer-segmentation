package com.rodolfo.ulcer.segmentation.utils.statistic;

import java.util.Map;

public class HistogramStatistic {
    
    private HistogramStatistic() {}

    public static Double mean(Map<Float,Integer> histogram) {

        Double mean = 0.0;
        Double total = 0.0;

        int val;
        
        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();

            mean += entry.getKey() * val;

            total += val;
        }

        return (total == 0 ? 0 : mean/total);
    }

    public static Double variance(Map<Float,Integer> histogram) {
        return variance(histogram, mean(histogram));
    }

    public static Double variance(Map<Float,Integer> histogram, Double mean) {
        
        Double variance = 0.0;
        Double total = -1.0;

        int val;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();

            variance += Math.pow((entry.getKey() - mean), 2) * val;

            total += val;
        }

        return (total == 0 ? 0 : variance/total);
    }

    public static Double standardDeviation(Map<Float,Integer> histogram) {
        return standardDeviation(histogram, mean(histogram));
    }
    
    public static Double standardDeviation(Map<Float,Integer> histogram, Double mean) {
        
        Double standardDeviation = 0.0;
        Double total = 0.0;

        Double diff;
        int val;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();

            diff = (double) entry.getKey() - mean;

            standardDeviation += diff * diff * val;

            total += val;
        }

        return (total == 0 ? 0 : Math.sqrt(standardDeviation/total));
    }

    public static Double entropy(Map<Float,Integer> histogram) {
        
        Double entropy = 0.0;
        Double total = 0.0;

        Double probability;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            total += entry.getValue();
        }

        if(total != 0) {

            for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

                probability = entry.getValue()/(total * 1.0);

                if(probability != 0) {

                    entropy += (probability * (Math.log10(probability)/Math.log10(2)));
                }
            }
        }

        return (-1.0) * entropy;
    }

    public static Double energy(Map<Float,Integer> histogram) {
        return energy(histogram, 0f);
    }

    public static Double energy(Map<Float,Integer> histogram, Float intensity) {
        
        Double energy = 0.0;

        int val;
        Float norm;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();
            norm = entry.getKey() - intensity;

            energy += (norm * norm) * val;
        }

        return Math.sqrt(energy);
    }

    public static Double asymmetry(Map<Float,Integer> histogram) {
        return asymmetry(histogram, mean(histogram), standardDeviation(histogram));
    }

    public static Double asymmetry(Map<Float,Integer> histogram, Double mean, Double standarDeviation) {
        
        Double asymmetry = 0.0;
        standarDeviation = standarDeviation == 0.0 ? 1.0 : standarDeviation;
        
        int val;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();
            Double div = histogram.size() == 0 ? 1.0 : (histogram.size()*1.0);

            asymmetry += (Math.pow((entry.getKey() - mean) * val, 3)/div);
        }


        return asymmetry/Math.pow(standarDeviation, 3);
    }

    public static Double kurtose(Map<Float,Integer> histogram) {
        return kurtose(histogram, mean(histogram));
    }

    public static Double kurtose(Map<Float,Integer> histogram, Double mean) {

        Double numerator   = 0.0;
        Double denominator = 0.0;

        int val;

        for (Map.Entry<Float,Integer> entry : histogram.entrySet()) {

            val = entry.getValue();

            numerator   += Math.pow((entry.getKey() - mean) * val, 4);
            denominator += Math.pow((entry.getKey() - mean) * val, 2);
        }

        return denominator == 0.0 ? 0.0 : histogram.size() * (numerator/Math.pow(denominator, 2));
    }

}