package com.rodolfo.ulcer.segmentation.process;

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

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_photo;

import javafx.concurrent.Task;

public class Worker extends Task<Void> {

    private final Configuration conf;
    private final Image image;
    private final MethodEnum methodEnum;
    private final OperationEnum operationEnum;

    public Worker(Configuration configuration, Image image, MethodEnum methodEnum, OperationEnum operationEnum) {

        this.conf = configuration;
        this.image = image;
        this.methodEnum = methodEnum;
        this.operationEnum = operationEnum;

        this.updateTitle("");
    }

    @Override
    protected Void call() throws Exception {
        
        this.executeLightRemoval();
        
        switch (this.operationEnum) {
            
            case SEGMENTATION:

                this.segmentation();
                
            break;

            case FEATURE_EXTRACTION:

                this.featureExtraction();

            break;
        }
        
        return null;
    }

    private void segmentation() {

        Superpixels superpixels = this.createSuperpixelsMethod();

        superpixels.createSuperpixels();

    }

    private void featureExtraction() {

        List<Descriptor> nonUlcerDescriptors = new ArrayList<>();
        List<Descriptor> ulcerDescriptors = new ArrayList<>();
        
        Superpixels superpixels = this.createSuperpixelsMethod();

        superpixels.createSuperpixels();
        superpixels.extractRegionLabels();

        DescriptorFactory dFactoryNonUlcer = new DescriptorFactory(this.image, this.conf, superpixels.getNonUlcerRegion());
        dFactoryNonUlcer.process();
        DescriptorFactory dFactoryUlcer = new DescriptorFactory(this.image, this.conf, superpixels.getUlcerRegion());
        dFactoryUlcer.process();

        dFactoryNonUlcer.getDescriptors().stream().forEach(descriptors -> {

            nonUlcerDescriptors.add(new Descriptor("NON_ULCER", descriptors));
        });

        dFactoryUlcer.getDescriptors().stream().forEach(descriptors -> {

            ulcerDescriptors.add(new Descriptor("ULCER", descriptors));
        });

    }

    private void executeLightRemoval() {

        LightMaskDetection lightMaskDetection = new SpecularReflectionDetection(this.conf.getSpecularReflectionElemntSize(), this.conf.getSpecularReflectionThreshold());

        Mat mask = lightMaskDetection.lightMask(this.image.getImage());

        LightRemoval lightRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, this.conf.getInpaintingNeighbor());
        lightRemoval.lightRemoval(image, conf.getKernelFilterSize());
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