package com.rodolfo.ulcer.segmentation.models;

import com.rodolfo.ulcer.segmentation.enums.MethodEnum;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import lombok.Getter;

@Getter
public class ImageStatistic {
 
    private MethodEnum method;
    private Integer amountOfSuperpixels;
    private Double executionTime;
    private Image image;

    private Double falseNegatives;
    private Double falsePositives;
    private Double trueNegatives;
    private Double truePositives;
    
    private Double sensitivity;
    private Double specificity;
    private Double precision;
    private Double accuracy;
    private Double iou;
    
    private boolean labeledImage;
    private Mat overlappingImage;

    private ImageStatistic(Builder builder) {

        this.image = builder.IMAGE;
        this.method = builder.METHOD;
        this.amountOfSuperpixels = builder.AMOUNT_OF_SUPERPIXELS;
        this.executionTime = builder.EXECUTION_TIME;

        this.falseNegatives = builder.falseNegatives;
        this.falsePositives = builder.falsePositives;
        this.trueNegatives = builder.trueNegatives;
        this.truePositives= builder.truePositives;

        this.sensitivity = builder.sensitivity;
        this.specificity = builder.specificity;
        this.precision = builder.precision;
        this.accuracy = builder.accuracy;
        this.iou = builder.iou;

        this.labeledImage = builder.hasLabeledImage;
        this.overlappingImage = builder.overlappingImage;
    }
    
    public static class Builder {

        private final Integer BLACK_PIXEL = 0;
        private final Integer DARK_GRAY_PIXEL = 110;
        private final Integer LIGHT_GRAY_PIXEL = 190;
        private final Integer WHITE_PIXEL = 255;
        
        private final Image IMAGE;
        private final MethodEnum METHOD;
        private final Integer AMOUNT_OF_SUPERPIXELS;
        private final Double EXECUTION_TIME;

        private Double falseNegatives = 0.0;
        private Double falsePositives = 0.0;
        private Double trueNegatives = 0.0;
        private Double truePositives = 0.0;
        private boolean hasLabeledImage = false;

        private Double sensitivity = 0.0;
        private Double specificity = 0.0;
        private Double precision = 0.0;
        private Double accuracy = 0.0;
        private Double iou = 0.0;

        private Mat overlappingImage;

        public Builder(Image image, MethodEnum method, Integer amountOfSuperpixels, Double executionTime) {

            this.IMAGE = image;
            this.METHOD = method;
            this.AMOUNT_OF_SUPERPIXELS = amountOfSuperpixels;
            this.EXECUTION_TIME = executionTime;
        }

        public Builder(
            Image image, 
            MethodEnum method, 
            Integer amountOfSuperpixels, 
            Double executionTime,
            Double falseNegatives,
            Double falsePositives,
            Double trueNegatives,
            Double truePositives
        ) {

            this.IMAGE = image;
            this.METHOD = method;
            this.AMOUNT_OF_SUPERPIXELS = amountOfSuperpixels;
            this.EXECUTION_TIME = executionTime;

            this.falseNegatives = falseNegatives;
            this.falsePositives = falsePositives;
            this.trueNegatives = trueNegatives;
            this.truePositives = truePositives;
        }

        public Builder overlappingImage(Mat finalSegmentation, Mat labeledFilledContourImage) {

            Mat labeledFilledContourImageNot = new Mat();
            overlappingImage = new Mat(finalSegmentation.size(), opencv_core.CV_8UC1, Scalar.WHITE);

            opencv_core.bitwise_not(labeledFilledContourImage, labeledFilledContourImageNot);

            UByteRawIndexer indexLabeled = labeledFilledContourImageNot.createIndexer();
            UByteRawIndexer indexSegmentation = finalSegmentation.createIndexer();
            UByteRawIndexer indexOverlapping = overlappingImage.createIndexer();

            int total = labeledFilledContourImageNot.rows() * labeledFilledContourImageNot.cols();

            for(int index = 0; index < total; index++) {

                if(this.isBlackPixel(indexLabeled.get(index)) && this.isBlackPixel(indexSegmentation.get(index))) {

                    indexOverlapping.put(index, LIGHT_GRAY_PIXEL);
                    this.truePositives++;
                }

                if(!this.isBlackPixel(indexLabeled.get(index)) && !this.isBlackPixel(indexSegmentation.get(index))) {

                    indexOverlapping.put(index, WHITE_PIXEL);
                    this.trueNegatives++;
                }

                if(!this.isBlackPixel(indexLabeled.get(index)) && this.isBlackPixel(indexSegmentation.get(index))) {

                    indexOverlapping.put(index, DARK_GRAY_PIXEL);
                    this.falsePositives++;
                }

                if(this.isBlackPixel(indexLabeled.get(index)) && !this.isBlackPixel(indexSegmentation.get(index))) {

                    indexOverlapping.put(index, BLACK_PIXEL);
                    this.falseNegatives++;
                }
            }
            
            indexLabeled.release();
            indexSegmentation.release();
            indexOverlapping.release();

            return this;
        }

        public Builder hasLabeledImage() {
            
            hasLabeledImage = true;

            return this;
        }

        public Builder sensitivity() {

            if(hasLabeledImage) {

                sensitivity = truePositives/(truePositives + falseNegatives);
            }

            return this;
        }

        public Builder specificity() {

            if(hasLabeledImage) {

                specificity = trueNegatives/(trueNegatives + falsePositives);
            }

            return this;
        }

        public Builder precision() {

            if(hasLabeledImage) {

                precision = truePositives/(truePositives + falsePositives);
            }

            return this;
        }

        public Builder accuracy() {

            if(hasLabeledImage) {

                accuracy = (truePositives + trueNegatives)/(truePositives + trueNegatives + falsePositives + falseNegatives);
            }

            return this;
        }

        public Builder iou() {

            if(hasLabeledImage) {

                iou = truePositives/(truePositives + falsePositives + falseNegatives);
            }

            return this;
        }

        public ImageStatistic build() {

            return new ImageStatistic(this);
        }

        private boolean isBlackPixel(int pixel) {

            return pixel < 200;
        }
    }
}
