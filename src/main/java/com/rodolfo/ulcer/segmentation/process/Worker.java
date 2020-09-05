package com.rodolfo.ulcer.segmentation.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.descriptors.DescriptorFactory;
import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.enums.OperationEnum;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightMaskDetection;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightRemoval;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl.Inpainting;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl.SpecularReflectionDetection;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.Superpixels;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsLSC;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsSEEDS;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsSLIC;
import com.rodolfo.ulcer.segmentation.services.FileService;
import com.rodolfo.ulcer.segmentation.services.ImageService;
import com.rodolfo.ulcer.segmentation.services.impl.FileServiceImpl;
import com.rodolfo.ulcer.segmentation.services.impl.ImageServiceImpl;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_photo;

import javafx.concurrent.Task;

public class Worker extends Task<Void> {

    private static final FileService fileService = new FileServiceImpl();
    private static final ImageService imageService = new ImageServiceImpl();

    private Configuration conf;
    private boolean isWithSRRemoval;
    private Image image;
    private MethodEnum methodEnum;
    private OperationEnum operationEnum;
    private List<File> arffFiles;

    public Worker(Configuration configuration, boolean isWithSRRemoval, Image image, MethodEnum methodEnum, OperationEnum operationEnum) {

        this.conf = configuration;
        this.isWithSRRemoval = isWithSRRemoval;
        this.image = image;
        this.methodEnum = methodEnum;
        this.operationEnum = operationEnum;

        this.updateTitle(this.image.getImageName());
    }

    public Worker(Configuration configuration, List<File> arffFiles, OperationEnum operationEnum) {

        this.conf = configuration;
        this.arffFiles = arffFiles;
        this.operationEnum = operationEnum;

        this.updateTitle("ARFF");
    }

    @Override
    protected Void call() throws Exception {
        
        switch (this.operationEnum) {
            
            case SEGMENTATION:

                this.segmentation();
                
            break;

            case FEATURE_EXTRACTION:

                this.featureExtraction();

            break;

            case CREATE_ARFF_FILE:

                this.createARFFFile();

            break;
        }
        
        return null;
    }

    private void createARFFFile() {

        int maxProcess = 100000;

        for(int index = 0; index < maxProcess; index++) {

            updateProgress(index, maxProcess);
        }

    }

    private void segmentation() {

        Superpixels superpixels = this.createSuperpixelsMethod();

        superpixels.createSuperpixels();

    }

    private void featureExtraction() throws InterruptedException {

        int [] process = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        int maxProcess = process.length;
        int index = 0;

        List<Descriptor> descriptors2Save = new ArrayList<>();

        imageService.openWithLabeled(this.image);
        updateProgress(process[index++], maxProcess);

        if(isWithSRRemoval) {

            this.executeLightRemoval();

        } else {

            this.image.setImageWithoutReflection(this.image.getImage());
        }
        updateProgress(process[index++], maxProcess);
        
        Superpixels superpixels = this.createSuperpixelsMethod();
        updateProgress(process[index++], maxProcess);

        superpixels.createSuperpixels();
        updateProgress(process[index++], maxProcess);

        superpixels.extractRegionLabels();
        updateProgress(process[index++], maxProcess);

        DescriptorFactory dFactoryNonUlcer = new DescriptorFactory(this.image, this.conf, superpixels.getNonUlcerRegion());
        
        dFactoryNonUlcer.processColor();
        updateProgress(process[index++], maxProcess);

        dFactoryNonUlcer.processHaralick();
        updateProgress(process[index++], maxProcess);
        
        dFactoryNonUlcer.processVariationHaralick();
        updateProgress(process[index++], maxProcess);

        dFactoryNonUlcer.processLBPH();
        updateProgress(process[index++], maxProcess);

        dFactoryNonUlcer.processWavelet();
        updateProgress(process[index++], maxProcess);


        DescriptorFactory dFactoryUlcer = new DescriptorFactory(this.image, this.conf, superpixels.getUlcerRegion());
        
        dFactoryUlcer.processColor();
        updateProgress(process[index++], maxProcess);

        dFactoryUlcer.processHaralick();
        updateProgress(process[index++], maxProcess);
        
        dFactoryUlcer.processVariationHaralick();
        updateProgress(process[index++], maxProcess);

        dFactoryUlcer.processLBPH();
        updateProgress(process[index++], maxProcess);

        dFactoryUlcer.processWavelet();
        updateProgress(process[index++], maxProcess);

        dFactoryUlcer.getDescriptors().stream().forEach(descriptors -> {

            descriptors2Save.add(new Descriptor("ULCER", descriptors));
        });
        updateProgress(process[index++], maxProcess);
        
        dFactoryNonUlcer.getDescriptors().stream().forEach(descriptors -> {

            descriptors2Save.add(new Descriptor("NON_ULCER", descriptors));
        });
        updateProgress(process[index++], maxProcess);

        fileService.saveDescritores(descriptors2Save, this.image.getDirectory().getFeaturesExtractedPath());
        updateProgress(process[index++], maxProcess);
        
        imageService.save(superpixels.getContourImage(), this.image.getDirectory().getSuperpixelsLabelsPath());
        updateProgress(process[index++], maxProcess);

        imageService.save(superpixels.getColorInformativePixels(), this.image.getDirectory().getSuperpixelsInformationalPath());
        updateProgress(process[index++], maxProcess);
        
        if(this.isWithSRRemoval) {

            imageService.save(this.image.getImageWithoutReflection(), this.image.getDirectory().getImageWithoutReflectionsPath());
        }
        
        updateProgress(maxProcess, maxProcess);

        Thread.sleep(500l);
    }

    private void executeLightRemoval() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(this.conf.getSpecularReflectionElemntSize(), this.conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(this.image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, this.conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(this.image, this.conf.getKernelFilterSize());
    }

    private Superpixels createSuperpixelsMethod() {

        Superpixels superpixels = null;

        switch (this.methodEnum) {
            
            case LSC:
            
                superpixels = new SuperpixelsLSC(
                    this.image, 
                    this.conf.getImageEdgePixelDistance(), 
                    this.conf.getIterations(),
                    this.conf.getAmount(), 
                    this.conf.getCompactnessF()
                );

            break;
        
            case SLIC:

                superpixels = new SuperpixelsSLIC(
                    this.image, 
                    this.conf.getImageEdgePixelDistance(), 
                    this.conf.getIterations(),
                    this.conf.getAmount(), 
                    this.conf.getCompactnessI()
                );

            break;

            case SEEDS:

                superpixels = new SuperpixelsSEEDS(
                    this.image, 
                    this.conf.getImageEdgePixelDistance(), 
                    this.conf.getIterations(),
                    this.conf.getAmount(), 
                    this.conf.getCompactnessI()
                );

            break;
        }

        return superpixels;
    }

}