package com.rodolfo.ulcer.segmentation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.models.ImageStatistic;
import com.rodolfo.ulcer.segmentation.utils.statistic.HistogramStatistic;

public class ImageStatisticUtil {

    private final String HEADER_TOTAL = "img;method;execution_time;amount_superpixels;tp;tn;fp;fn;sensitivity;specifictity;precision;accuracy;iou";
    private final String HEADER_RESUME = "img;method;execution_time;amount_superpixels";
    
    private final Integer AMOUNT_SUPERPIXELS = 0;
    private final Integer EXECUTION_TIME = 1;
    private final Integer TRUE_POSITIVES = 2;
    private final Integer TRUE_NEGATIVES = 3;
    private final Integer FALSE_POSITIVES = 4;
    private final Integer FALSE_NEGATIVES = 5;
    private final Integer SENSITIVITY = 6;
    private final Integer SPECIFICITY = 7;
    private final Integer PRECISION = 8;
    private final Integer ACCURACY = 9;
    private final Integer IOU = 10;

    private List<ImageStatistic> imagesStatistics;
    private StringBuilder statistics;

    public ImageStatisticUtil(List<ImageStatistic> imagesStatistics) {

        this.imagesStatistics = imagesStatistics;
        this.statistics = new StringBuilder();
    }

    @Override
    public String toString() {

        return this.statistics.toString();
    }

    public ImageStatisticUtil csvFormatter() {
        
        String data = this.imagesStatistics.stream()
            .map(imageStatistic -> {

                StringBuilder temp = new StringBuilder();

                if(imageStatistic.isLabeledImage()) { 
                    
                    temp.append(imageStatistic.getImage().getImageName())
                        .append(";")
                        .append(imageStatistic.getMethod().name())
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getExecutionTime()))
                        .append(";")
                        .append(imageStatistic.getAmountOfSuperpixels())
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getTruePositives()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getTrueNegatives()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getFalsePositives()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getFalseNegatives()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getSensitivity()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getSpecificity()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getPrecision()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getAccuracy()))
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getIou()));

                    return temp.toString();

                } else {

                    temp.append(imageStatistic.getImage().getImageName())
                        .append(";")
                        .append(imageStatistic.getMethod().name())
                        .append(";")
                        .append(this.numberFormatter(imageStatistic.getExecutionTime()))
                        .append(";")
                        .append(imageStatistic.getAmountOfSuperpixels());

                    return temp.toString();
                }
        }).collect(Collectors.joining(System.lineSeparator()));

        this.statistics.append(data);

        return this;
    }

    public ImageStatisticUtil userViewFormatter() {

        String data = this.imagesStatistics.stream()
            .map(imageStatistic -> {

                StringBuilder temp = new StringBuilder();

                if(imageStatistic.isLabeledImage()) {

                    temp.append(imageStatistic.getImage().getImageName())
                        .append(" ")
                        .append(imageStatistic.getAmountOfSuperpixels())
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getExecutionTime()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getTruePositives()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getTrueNegatives()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getFalsePositives()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getFalseNegatives()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getSensitivity()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getSpecificity()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getPrecision()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getAccuracy()))
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getIou()));

                    return temp.toString();

                } else {

                    temp.append(imageStatistic.getImage().getImageName())
                        .append(" ")
                        .append(imageStatistic.getAmountOfSuperpixels())
                        .append(" ")
                        .append(this.numberFormatter(imageStatistic.getExecutionTime()));

                    return temp.toString();
                }
        }).collect(Collectors.joining(System.lineSeparator()));

        this.statistics.append(data)
            .append(System.lineSeparator())
            .append(System.lineSeparator());

        return this;
    }

    public ImageStatisticUtil statisticsMeanAndStd() {

        if(!this.imagesStatistics.isEmpty()) {

            ImageStatistic imageStatistic = this.imagesStatistics.get(0);
            Map<Integer,List<Float>> values = this.extractMetricsValues();

            this.setMeanValues(values, imageStatistic.isLabeledImage());
            this.setStdValues(values, imageStatistic.isLabeledImage());
        }

        return this;
    }

    public ImageStatisticUtil statisticsCsvHeader() {

        if(!this.imagesStatistics.isEmpty()) {

            ImageStatistic imageStatistic = this.imagesStatistics.get(0);

            if(imageStatistic.isLabeledImage()) {

                this.statistics.append(this.HEADER_TOTAL).append(System.lineSeparator());

            } else {

                this.statistics.append(this.HEADER_RESUME).append(System.lineSeparator());
            }
        }

        return this;
    }

    public ImageStatisticUtil methodHeader() {

        StringBuilder temp = new StringBuilder();

        if(!this.imagesStatistics.isEmpty()) {

            ImageStatistic imageStatistic = this.imagesStatistics.get(0);
    
            temp.append("******************************************")
                .append(System.lineSeparator())
                .append("******* Method: ").append(imageStatistic.getMethod().name()).append(" ****************")
                .append(System.lineSeparator())
                .append("******************************************")
                .append(System.lineSeparator());
        }

        temp.append(this.statistics.toString());

        this.statistics = temp;

        return this;
    }

    public ImageStatisticUtil createTab() {

        List<List<String>> dataList = new ArrayList<>();

        for (String linha : Arrays.asList(this.statistics.toString().split(System.lineSeparator()))) {
            dataList.add(Arrays.asList(linha.split(" ")));
        }

        List<Integer> largestColumnWordSize = this.createWordSizeList(dataList);
        StringBuilder tab = new StringBuilder();

        for(List<String> line : dataList) {

            for(int x = 0; x < line.size(); x++) {

                tab.append(line.get(x) + createWhiteSpaces(getNumberOfSpaces(largestColumnWordSize.get(x), line.get(x))));
            }

            tab.append(System.lineSeparator());
        }

        tab.append(System.lineSeparator());

        this.statistics = tab;

        return this;
    }

    public static String getStatisticsMerged(List<ImageStatistic> svmImageStatistic, List<ImageStatistic> grabImageStatistic) {
        
        String svmStatistics = (new ImageStatisticUtil(svmImageStatistic))
            .statisticsViewHeader()
            .userViewFormatter()
            .statisticsMeanAndStd()
            .createTab()
            .methodHeader()
            .toString();

        String grabtatistics = (new ImageStatisticUtil(grabImageStatistic))
            .statisticsViewHeader()
            .userViewFormatter()
            .statisticsMeanAndStd()
            .createTab()
            .methodHeader()
            .toString();

        return svmStatistics
            .concat(System.lineSeparator())
            .concat(System.lineSeparator())
            .concat(grabtatistics);
    }

    private void setMeanValues(Map<Integer,List<Float>> values, boolean isLabeledImage) {
        
        if(isLabeledImage) {

            this.statistics.append("MEAN:")
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.AMOUNT_SUPERPIXELS)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.EXECUTION_TIME)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.TRUE_POSITIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.TRUE_NEGATIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.FALSE_POSITIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.FALSE_NEGATIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.SENSITIVITY)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.SPECIFICITY)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.PRECISION)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.ACCURACY)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.IOU)))
                .append(System.lineSeparator());

        } else {

            this.statistics.append("MEAN:")
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.AMOUNT_SUPERPIXELS)))
                .append(" ")
                .append(this.numberFormatter(this.getMeanValue(values, this.EXECUTION_TIME)))
                .append(System.lineSeparator());
        }
    }

    private void setStdValues(Map<Integer,List<Float>> values, boolean isLabeledImage) {

        if(isLabeledImage) {

            this.statistics.append("STD:")
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.AMOUNT_SUPERPIXELS)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.EXECUTION_TIME)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.TRUE_POSITIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.TRUE_NEGATIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.FALSE_POSITIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.FALSE_NEGATIVES)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.SENSITIVITY)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.SPECIFICITY)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.PRECISION)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.ACCURACY)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.IOU)))
                .append(System.lineSeparator());

        } else {

            this.statistics.append("STD:")
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.AMOUNT_SUPERPIXELS)))
                .append(" ")
                .append(this.numberFormatter(this.getStdValue(values, this.EXECUTION_TIME)))
                .append(System.lineSeparator());
        }
    }

    private Double getMeanValue(Map<Integer,List<Float>> values, Integer valueType) {

        return HistogramStatistic.mean(Util.createHistogram(values.get(valueType)));
    }

    private Double getStdValue(Map<Integer,List<Float>> values, Integer valueType) {

        return HistogramStatistic.standardDeviation(Util.createHistogram(values.get(valueType)));
    }

    private ImageStatisticUtil statisticsViewHeader() {

        StringBuilder temp = new StringBuilder();

        if(!this.imagesStatistics.isEmpty()) {

            ImageStatistic imageStatistic = this.imagesStatistics.get(0);

            if(imageStatistic.isLabeledImage()) {

                temp.append("IMAGE_NAME")
                    .append(" ")
                    .append("SUPERPIXELS")
                    .append(" ")
                    .append("EXECUTION_TIME")
                    .append(" ")
                    .append("TRUE_POSITIVES")
                    .append(" ")
                    .append("TRUE_NEGATIVES")
                    .append(" ")
                    .append("FALSE_POSITIVES")
                    .append(" ")
                    .append("FALSE_NEGATIVES")
                    .append(" ")
                    .append("SENSITIVITY")
                    .append(" ")
                    .append("SPECIFICITY")
                    .append(" ")
                    .append("PRECISION")
                    .append(" ")
                    .append("ACCURACY")
                    .append(" ")
                    .append("INTERSECTION_UNION")
                    .append(System.lineSeparator());

            } else {

                temp.append("IMAGE_NAME")
                    .append(" ")
                    .append("SUPERPIXELS")
                    .append(" ")
                    .append("EXECUTION_TIME")
                    .append(System.lineSeparator());
            }
        }

        this.statistics.append(temp.toString());

        return this;
    }

    private List<Integer> createWordSizeList(List<List<String>> dataList) {

        List<Integer> largestColumnWordSize = new ArrayList<>();

        for(int x = 0; x < dataList.size(); x++) {
            for(int y = 0; y < dataList.get(x).size(); y++) {

                if(x == 0) {

                    largestColumnWordSize.add(dataList.get(x).get(y).length());

                } else if(dataList.get(x).get(y).length() > largestColumnWordSize.get(y)) {

                    largestColumnWordSize.set(y, dataList.get(x).get(y).length());
                }
            }
        }

        return largestColumnWordSize;
    }

    private String createWhiteSpaces(int amountOfSpaces) {
        
        StringBuilder spaces = new StringBuilder();

        for(int x = 0; x < amountOfSpaces; x++) {

            spaces.append(" ");
        }

        return spaces.toString();
    }

    private static Integer getNumberOfSpaces(int largerSize, String input) {
        
        return largerSize - input.length() + 2;
    }

    private String numberFormatter(Double value) {

        return String.format(new Locale("en", "US"), "%.2f", value);
    }

    private Map<Integer,List<Float>> extractMetricsValues() {

        Map<Integer,List<Float>> values = new HashMap<>();

        List<Float> amountSuperpixels = new ArrayList<>();
        List<Float> executionTime = new ArrayList<>();
        List<Float> truePositives = new ArrayList<>();
        List<Float> trueNegatives = new ArrayList<>();
        List<Float> falsePositives = new ArrayList<>();
        List<Float> falseNegatives = new ArrayList<>();
        List<Float> sensitivity = new ArrayList<>();
        List<Float> specificity = new ArrayList<>();
        List<Float> precision = new ArrayList<>();
        List<Float> accuracy = new ArrayList<>();
        List<Float> intersection = new ArrayList<>();

        this.imagesStatistics.forEach(statistic -> {

            amountSuperpixels.add(statistic.getAmountOfSuperpixels().floatValue());
            executionTime.add(statistic.getExecutionTime().floatValue());
            truePositives.add(statistic.getTruePositives().floatValue());
            trueNegatives.add(statistic.getTrueNegatives().floatValue());
            falsePositives.add(statistic.getFalsePositives().floatValue());
            falseNegatives.add(statistic.getFalseNegatives().floatValue());
            sensitivity.add(statistic.getSensitivity().floatValue());
            specificity.add(statistic.getSpecificity().floatValue());
            precision.add(statistic.getPrecision().floatValue());
            accuracy.add(statistic.getAccuracy().floatValue());
            intersection.add(statistic.getIou().floatValue());
        });

        values.put(this.AMOUNT_SUPERPIXELS, amountSuperpixels);
        values.put(this.EXECUTION_TIME, executionTime);
        values.put(this.TRUE_POSITIVES, truePositives);
        values.put(this.TRUE_NEGATIVES, trueNegatives);
        values.put(this.FALSE_POSITIVES, falsePositives);
        values.put(this.FALSE_NEGATIVES, falseNegatives);
        values.put(this.SENSITIVITY, sensitivity);
        values.put(this.SPECIFICITY, specificity);
        values.put(this.PRECISION, precision);
        values.put(this.ACCURACY, accuracy);
        values.put(this.IOU, intersection);

        return values;
    }
}
