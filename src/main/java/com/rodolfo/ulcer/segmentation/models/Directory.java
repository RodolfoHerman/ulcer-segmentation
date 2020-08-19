package com.rodolfo.ulcer.segmentation.models;

import java.io.File;

import com.rodolfo.ulcer.segmentation.config.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Directory {

    // Path images
    private File dirPath;
    private File imagePath;
    private File imageWithoutReflectionsPath;
    private File labeledImagePath;
    private File superpixelsLabesPath;
    private File svmClassificatioPath;
    private File grabCutSegmentationBinaryPath;
    private File grabCutSegmentationPath;
    private File grabCutMaskPath;

    // Path files
    private File executionTimePath;
    private File featuresExtractedPath;

    public void updatePaths(File dirPath, Configuration configuration) {

        this.dirPath = dirPath;

        this.imagePath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getExtension()));
        this.labeledImagePath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getLabeledImageName()).concat(configuration.getExtension()));
        this.imageWithoutReflectionsPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getImageWithoutReflectionsName()).concat(configuration.getExtension()));
        this.superpixelsLabesPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getSuperpixelsLabelImageName()).concat(configuration.getExtension()));
        this.svmClassificatioPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getSvmClassificationImageName()).concat(configuration.getExtension()));
        this.grabCutSegmentationBinaryPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getGrabcutSegmentationBinaryImageName()).concat(configuration.getExtension()));
        this.grabCutSegmentationPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getGrabcutSegmentationImageName()).concat(configuration.getExtension()));
        this.grabCutMaskPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getImageName()).concat(configuration.getGrabcutMaskImageName()).concat(configuration.getExtension()));
        
        this.executionTimePath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getExecutionTimeFile()));
        this.featuresExtractedPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getFeaturesExtractedFile()));
    }
}