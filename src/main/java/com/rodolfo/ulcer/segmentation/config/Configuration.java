package com.rodolfo.ulcer.segmentation.config;

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

    private String userDirectory;
    private String extension;
    private String imageName;
    private String labeledImageName;
    private String imageWithoutReflectionsName;
    private String superpixelsLabelImageName;
    private String svmClassificationImageName;
    private String grabcutSegmentationBinaryImageName;
    private String grabcutSegmentationImageName;
    private String grabcutMaskImageName;
    private String chooseDirectory;
    private String executionTimeFile;
    private String featuresExtractedFile;

    public void updateImageName(String imageName) {

        String newName = this.hasImageName(this.getImageName()) ? this.getImageName() : imageName;

        this.imageName = newName;
    }

    private boolean hasImageName(String imageName) {

        return !imageName.equalsIgnoreCase("folder_number");
    }
}