package com.rodolfo.ulcer.segmentation.utils;

import java.util.Locale;

import com.rodolfo.ulcer.segmentation.models.ImageStatistic;

public class ImageStatisticUtil {

    private final ImageStatistic imageStatistic;
    private StringBuilder statistics;

    public ImageStatisticUtil(ImageStatistic imageStatistic) {

        this.imageStatistic = imageStatistic;
        this.statistics = new StringBuilder();
    }

    public ImageStatisticUtil csvFormatter() {

        if(this.imageStatistic.isLabeledImage()) {

            this.statistics
            .append("img;method;tp;tn;fp;fn;sensitivity;specifictity;precision;accuracy;iou;execution_time;amount_superpixels")
            .append(System.lineSeparator())
            .append(this.imageStatistic.getImage().getImageName())
            .append(";")
            .append(this.imageStatistic.getMethod().name())
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getTruePositives()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getTrueNegatives()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getFalsePositives()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getFalseNegatives()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getSensitivity()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getSpecificity()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getPrecision()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getAccuracy()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getIou()))
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getExecutionTime()))
            .append(";")
            .append(this.imageStatistic.getAmountOfSuperpixels());

        } else {

            this.statistics
            .append("img;method;execution_time;amount_superpixels")
            .append(System.lineSeparator())
            .append(this.imageStatistic.getImage().getImageName())
            .append(";")
            .append(this.imageStatistic.getMethod().name())
            .append(";")
            .append(this.numberFormatter(this.imageStatistic.getExecutionTime()))
            .append(";")
            .append(this.imageStatistic.getAmountOfSuperpixels());
        }

        return this;
    }

    public ImageStatisticUtil userViewFormatter() {

        if(this.imageStatistic.isLabeledImage()) {

            this.statistics
                .append("******************************************")
                .append(System.lineSeparator())
                .append("********** Method: ").append(this.imageStatistic.getMethod().name()).append(" ****************")
                .append(System.lineSeparator())
                .append("******************************************")
                .append(System.lineSeparator())
                .append(this.textFormatter("IMAGE NAME", this.imageStatistic.getImage().getImageName()))
                .append(this.textFormatter("SUPERPIXELS", this.imageStatistic.getAmountOfSuperpixels()))
                .append(this.textFormatter("EXECUTION TIME", this.imageStatistic.getExecutionTime()))
                .append("**********").append(System.lineSeparator())
                .append(this.textFormatter("TRUE POSITIVES", this.imageStatistic.getTruePositives()))
                .append(this.textFormatter("TRUE NEGATIVES", this.imageStatistic.getTrueNegatives()))
                .append(this.textFormatter("FALSE POSITIVES", this.imageStatistic.getFalsePositives()))
                .append(this.textFormatter("FALSE NEGATIVES", this.imageStatistic.getFalseNegatives()))
                .append("**********").append(System.lineSeparator())
                .append(this.textFormatter("SENSITIVITY", this.imageStatistic.getSensitivity()))
                .append(this.textFormatter("SPECIFICITY", this.imageStatistic.getSpecificity()))
                .append(this.textFormatter("PRECISION", this.imageStatistic.getPrecision()))
                .append(this.textFormatter("ACCURACY", this.imageStatistic.getAccuracy()))
                .append(this.textFormatter("INTER. UNION", this.imageStatistic.getAccuracy()));

        } else {

            this.statistics
                .append("******************************************")
                .append(System.lineSeparator())
                .append("******* Method: ").append(this.imageStatistic.getMethod().name()).append(" ****************")
                .append(System.lineSeparator())
                .append("******************************************")
                .append(System.lineSeparator())
                .append(this.textFormatter("IMAGE NAME", this.imageStatistic.getImage().getImageName()))
                .append(this.textFormatter("AMOUNT SUPERPIXELS", this.imageStatistic.getAmountOfSuperpixels()))
                .append(this.textFormatter("EXECUTION TIME", this.imageStatistic.getExecutionTime()));
        }

        return this;
    }

    @Override
    public String toString() {

        return this.statistics.toString();
    }

    private String textFormatter(String title, String value) {

        return (new StringBuilder()).append(title).append("\t : ").append(value).append(System.lineSeparator()).toString();
    }

    private String textFormatter(String title, Double value) {

        return (new StringBuilder()).append(title).append("\t : ").append(this.numberFormatter(value)).append(System.lineSeparator()).toString();
    }

    private String textFormatter(String title, Integer value) {

        return (new StringBuilder()).append(title).append("\t : ").append(value).append(System.lineSeparator()).toString();
    }

    private String numberFormatter(Double value) {

        return String.format(new Locale("en", "US"), "%.2f", value);
    }
}
