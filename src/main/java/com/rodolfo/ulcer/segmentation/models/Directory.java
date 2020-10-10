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
    private File superpixelsLabelsPath;
    private File superpixelsInformationalPath;
    private File svmClassificatioPath;
    private File grabCutSegmentationBinaryPath;
    private File grabCutSegmentationPath;
    private File grabCutMaskPath;
    private File skeletonWithBranchsPath;
    private File skeletonWithoutBranchsPath;
    private File labeledResampleImagePath;

    // Path files
    private File executionTimePath;
    private File featuresExtractedPath;

    public void updatePaths(File dirPath, String imageName, Configuration configuration) {

        this.dirPath = dirPath;

        this.imagePath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getExtension()));
        this.labeledImagePath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getLabeledImageName()).concat(configuration.getExtension()));
        this.imageWithoutReflectionsPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getImageWithoutReflectionsName()).concat(configuration.getExtension()));
        this.superpixelsLabelsPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getSuperpixelsLabelImageName()).concat(configuration.getExtension()));
        this.superpixelsInformationalPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getSuperpixelsInformationalImageName()).concat(configuration.getExtension()));
        this.svmClassificatioPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getSvmClassificationImageName()).concat(configuration.getExtension()));
        this.grabCutSegmentationBinaryPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getGrabcutSegmentationBinaryImageName()).concat(configuration.getExtension()));
        this.grabCutSegmentationPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getGrabcutSegmentationImageName()).concat(configuration.getExtension()));
        this.grabCutMaskPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getGrabcutMaskImageName()).concat(configuration.getExtension()));
        this.skeletonWithBranchsPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getSkeletonWithBranchsName()).concat(configuration.getExtension()));
        this.skeletonWithoutBranchsPath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getSkeletonWithoutBranchsName()).concat(configuration.getExtension()));
        this.labeledResampleImagePath = new File(dirPath.getAbsolutePath().concat("\\").concat(imageName).concat(configuration.getLabeledResampleImageName()).concat(configuration.getExtension()));
        
        this.executionTimePath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getExecutionTimeFile()));
        this.featuresExtractedPath = new File(dirPath.getAbsolutePath().concat("\\").concat(configuration.getFeaturesExtractedFile()));
    }

    public boolean hasLabeledImagePath() {

        return !this.labeledImagePath.getAbsolutePath().equals(this.imagePath.getAbsolutePath());
    }
}