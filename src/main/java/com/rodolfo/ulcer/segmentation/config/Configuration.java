package com.rodolfo.ulcer.segmentation.config;

import java.io.File;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Configuration {
    
    private Integer cannyThreshold;
    private Integer sobelSize;
    private Integer kernelFilterSize;
    private Integer inpaintingNeighbor;
    private Integer resampleHeight;
    private Integer resampleWidth;

    private Integer specularReflectionElemntSize;
    private float specularReflectionThreshold;

    private Integer waveletLevel;

    private Integer imageEdgePixelDistance;

    private Integer haralickPixelDistance;

    private String userDirectory;
    private String extension;
    private String imageName;
    private String labeledImageName;
    private String imageWithoutReflectionsName;
    private String superpixelsLabelImageName;
    private String superpixelsInformationalImageName;
    private String svmClassificationImageName;
    private String grabcutSegmentationBinaryImageName;
    private String grabcutSegmentationImageName;
    private String grabcutMaskImageName;
    private String chooseDirectory;
    private String executionTimeFile;
    private String featuresExtractedFile;

    // Superpixels algorithm
    private Integer iterations;
    private Integer amount;
    private Integer compactnessI;
    private Float compactnessF;

    private Integer skeletonKernelErodeSize;
    private Double skeletonAreaErode;

    private Integer grabcutNumberOfIterations;

    private Integer preparationNormalizationDecimalPlaces;

    private String datasourceSEEDSName;
    private String datasourceLSCName;
    private String datasourceSLICName;
    private String minMaxSEEDSName;
    private String minMaxLSCName;
    private String minMaxSLICName;
    private String mlModelSEEDSName;
    private String mlModelLSCName;
    private String mlModelSLICName;

    private File minMax;
    private File datasource;
    private File mlModel;

    public boolean hasImageName() {

        return !this.imageName.equalsIgnoreCase("folder_number");
    }
}