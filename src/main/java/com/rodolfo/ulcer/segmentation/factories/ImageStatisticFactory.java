package com.rodolfo.ulcer.segmentation.factories;

import java.util.List;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.models.ImageStatistic;
import com.rodolfo.ulcer.segmentation.services.FileService;
import com.rodolfo.ulcer.segmentation.services.impl.FileServiceImpl;

public class ImageStatisticFactory {

    private final Integer POSITION_CONTENT_HEADER = 0;
    private final Integer POSITION_CONTENT_DATA = 1;

    private final Integer POSITION_EXECUTION_TIME = 2;
    private final Integer POSITION_AMOUNT_SUPERPIXELS = 3;
    private final Integer POSITION_TRUE_POSITIVES = 4;
    private final Integer POSITION_TRUE_NEGATIVES = 5;
    private final Integer POSITION_FALSE_POSITIVES = 6;
    private final Integer POSITION_FALSE_NEGATIVES = 7;

    private final FileService FILE_SERVICE = new FileServiceImpl();

    private final List<Image> images;

    private List<ImageStatistic> imagesStatistics;

    public ImageStatisticFactory(List<Image> images) {

        this.images = images;
    }

    public ImageStatisticFactory createImageStatisticsSvm() {

        imagesStatistics = this.images.stream().map(image -> {

            List<String> content = this.FILE_SERVICE.getFileContent(image.getDirectory().getStatisticsSvmCsvPath());

            if(this.hasLabeledImage(content.get(this.POSITION_CONTENT_HEADER))) {
                
                return new ImageStatistic
                    .Builder(
                        image, 
                        MethodEnum.SVM,
                        this.getAmountOfSuperpixels(content.get(this.POSITION_CONTENT_DATA)),
                        this.getExecutionTime(content.get(this.POSITION_CONTENT_DATA)),
                        this.getFalseNegatives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getFalsePositives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getTrueNegatives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getTruePositives(content.get(this.POSITION_CONTENT_DATA))
                    ).hasLabeledImage()
                    .sensitivity()
                    .specificity()
                    .precision()
                    .accuracy()
                    .iou()
                    .build();

            } else {

                return new ImageStatistic
                    .Builder(
                        image, 
                        MethodEnum.SVM,
                        this.getAmountOfSuperpixels(content.get(this.POSITION_CONTENT_DATA)),
                        this.getExecutionTime(content.get(this.POSITION_CONTENT_DATA))
                    ).build();
            }
        }).collect(Collectors.toList());

        return this;
    }

    public ImageStatisticFactory createImageStatisticsGrab() {

        imagesStatistics = this.images.stream().map(image -> {

            List<String> content = this.FILE_SERVICE.getFileContent(image.getDirectory().getStatisticsGrabCsvPath());

            if(this.hasLabeledImage(content.get(this.POSITION_CONTENT_HEADER))) {
                
                return new ImageStatistic
                    .Builder(
                        image, 
                        MethodEnum.GRAB,
                        this.getAmountOfSuperpixels(content.get(this.POSITION_CONTENT_DATA)),
                        this.getExecutionTime(content.get(this.POSITION_CONTENT_DATA)),
                        this.getFalseNegatives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getFalsePositives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getTrueNegatives(content.get(this.POSITION_CONTENT_DATA)),
                        this.getTruePositives(content.get(this.POSITION_CONTENT_DATA))
                    ).hasLabeledImage()
                    .sensitivity()
                    .specificity()
                    .precision()
                    .accuracy()
                    .iou()
                    .build();

            } else {

                return new ImageStatistic
                    .Builder(
                        image, 
                        MethodEnum.GRAB,
                        this.getAmountOfSuperpixels(content.get(this.POSITION_CONTENT_DATA)),
                        this.getExecutionTime(content.get(this.POSITION_CONTENT_DATA))
                    ).build();
            }
        }).collect(Collectors.toList());

        return this;
    }

    public List<ImageStatistic> getImagesStatistics() {

        return this.imagesStatistics;
    }

    private Integer getAmountOfSuperpixels(String data) {

        return Integer.valueOf(data.split(";")[this.POSITION_AMOUNT_SUPERPIXELS]);
    }

    private Double getExecutionTime(String data) {

        return Double.valueOf(data.split(";")[this.POSITION_EXECUTION_TIME]);
    }

    private Double getFalseNegatives(String data) {

        return Double.valueOf(data.split(";")[this.POSITION_FALSE_NEGATIVES]);
    }

    private Double getFalsePositives(String data) {

        return Double.valueOf(data.split(";")[this.POSITION_FALSE_POSITIVES]);
    }

    private Double getTrueNegatives(String data) {

        return Double.valueOf(data.split(";")[this.POSITION_TRUE_NEGATIVES]);
    }

    private Double getTruePositives(String data) {

        return Double.valueOf(data.split(";")[this.POSITION_TRUE_POSITIVES]);
    }

    private boolean hasLabeledImage(String header) {

        return header.split(";").length > 4;
    }
}
