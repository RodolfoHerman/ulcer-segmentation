package com.rodolfo.ulcer.segmentation.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.core.classification.MachineLearning;
import com.rodolfo.ulcer.segmentation.core.classification.SVM;
import com.rodolfo.ulcer.segmentation.core.data.preparation.Normalization;
import com.rodolfo.ulcer.segmentation.core.data.preparation.Preparation;
import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.core.descriptors.DescriptorFactory;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightMaskDetection;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightRemoval;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl.Inpainting;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl.SpecularReflectionDetection;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.Superpixels;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsLSC;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsSEEDS;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsSLIC;
import com.rodolfo.ulcer.segmentation.core.segmentation.Grabcut;
import com.rodolfo.ulcer.segmentation.core.segmentation.Skeletonization;
import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.enums.OperationEnum;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.models.ImageStatistic;
import com.rodolfo.ulcer.segmentation.services.FileService;
import com.rodolfo.ulcer.segmentation.services.ImageService;
import com.rodolfo.ulcer.segmentation.services.impl.FileServiceImpl;
import com.rodolfo.ulcer.segmentation.services.impl.ImageServiceImpl;
import com.rodolfo.ulcer.segmentation.utils.ImageStatisticUtil;
import com.rodolfo.ulcer.segmentation.utils.Util;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_photo;

import javafx.concurrent.Task;
import weka.core.converters.ConverterUtils.DataSource;

public class Worker extends Task<Void> {

    private static final FileService FILE_SERVICE = new FileServiceImpl();
    private static final ImageService IMAGE_SERVICE = new ImageServiceImpl();

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

    private void createARFFFile() throws InterruptedException {

        int [] process = new int[this.arffFiles.size() + 3];
        int maxProcess = this.arffFiles.size() + 3;
        int index = 0;

        List<Descriptor> descriptors = new ArrayList<>();

        for(int x = 0; x < maxProcess; x++) {

            process[x] = x;
        }

        for(File arffFile: this.arffFiles) {

            descriptors.addAll(Util.getContentFromARFFFile(arffFile));
            updateProgress(process[index++], maxProcess);
        }

        Preparation normalization = new Normalization(this.conf, descriptors, Util.getDescriptorsNames());
        normalization.preparation();
        updateProgress(process[index++], maxProcess);

        FILE_SERVICE.saveDescriptors(normalization.getDescriptors(), this.conf.getDatasource());
        updateProgress(process[index++], maxProcess);

        FILE_SERVICE.saveMinMaxDescriptors(normalization.getMinMaxDescriptors(), this.conf.getMinMax());
        updateProgress(maxProcess, maxProcess);

        Thread.sleep(500l);
    }

    private void segmentation() throws Exception {

        int [] process = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
        int maxProcess = process.length;
        int index = 0;
        ImageStatistic svmImageStatistic;
        ImageStatistic grabImageStatistic;

        DataSource dataSource = FILE_SERVICE.openDataSoruce(this.conf.getDatasource());
        Object model = FILE_SERVICE.openMlModel(this.conf.getMlModel());
        Map<String,List<Double>> minMax = FILE_SERVICE.openMinMaxDescriptors(this.conf.getMinMax());

        List<DescriptorFactory> dFactories = new ArrayList<>();
        List<Descriptor> descriptors = new ArrayList<>();

        if(!this.validateFiles(dataSource, model, minMax)) {

            System.err.println("Erro ao encontrar arquivos (datasource ou model ou minMax)");
            System.exit(1);
        }

        IMAGE_SERVICE.open(this.image);
        updateProgress(process[index++], maxProcess);

        if(isWithSRRemoval) {

            this.executeLightRemoval();

        } else {

            this.image.setImageWithoutReflection(this.image.getImage());
        }
        updateProgress(process[index++], maxProcess);

        Long startTime = System.currentTimeMillis();
        
        Superpixels superpixels = this.createSuperpixelsMethod();
        updateProgress(process[index++], maxProcess);

        superpixels.createSuperpixels();
        updateProgress(process[index++], maxProcess);

        superpixels.getSuperpixelsSegmentation().forEach((key, points) -> {

            dFactories.add(new DescriptorFactory(this.image, this.conf, Util.getListOfDescriptorsNamesFromDataSource(dataSource), points, key, null));
        });
        updateProgress(process[index++], maxProcess);

        dFactories.stream().forEach(factory -> factory.processColor());
        updateProgress(process[index++], maxProcess);

        dFactories.stream().forEach(factory -> factory.processHaralick());
        updateProgress(process[index++], maxProcess);
        
        dFactories.stream().forEach(factory -> factory.processVariationHaralick());
        updateProgress(process[index++], maxProcess);

        dFactories.stream().forEach(factory -> factory.processLBPH());
        updateProgress(process[index++], maxProcess);

        dFactories.stream().forEach(factory -> factory.processWavelet());
        updateProgress(process[index++], maxProcess);

        dFactories.stream().forEach(factory -> descriptors.add(new Descriptor(null, factory.getDescriptors(), factory.getPoints())));
        updateProgress(process[index++], maxProcess);

        Preparation normalization = new Normalization(this.conf, minMax, descriptors, Util.getListOfDescriptorsNamesFromDataSource(dataSource));
        normalization.preparation();
        updateProgress(process[index++], maxProcess);

        MachineLearning ml = new SVM(model, dataSource, this.image.getSize(), normalization.getDescriptors());
        ml.createInstances();
        updateProgress(process[index++], maxProcess);

        ml.classify();
        updateProgress(process[index++], maxProcess);

        Long svmTime = System.currentTimeMillis();

        Skeletonization skeletonization = new Skeletonization(this.conf, ml.getOutlineFilled());
        skeletonization.process();
        updateProgress(process[index++], maxProcess);

        Grabcut grabcut = new Grabcut(this.image, this.conf, ml.getOutlineFilled(), skeletonization.getSkeletonWithoutBranchs());
        grabcut.process();
        updateProgress(process[index++], maxProcess);

        grabcut.createFinalBinarySegmentation();

        Long grabTime = System.currentTimeMillis();

        grabcut.createGrabCutHumanMask();
        updateProgress(process[index++], maxProcess);

        grabcut.createHumanMaskWithLabeledContour();
        updateProgress(process[index++], maxProcess);

        Double execSvmTime = this.calculateExecutionTime(startTime, svmTime);
        Double execGrabTime = this.calculateExecutionTime(startTime, grabTime);

        if(this.image.getDirectory().hasLabeledImagePath()) {

            svmImageStatistic = new ImageStatistic.Builder(
                this.image,
                MethodEnum.SVM, 
                superpixels.getSuperpixelsAmount(), 
                execSvmTime
            ).overlappingImage(ml.getClassified(), this.image.getLabeledFilledContourImage())
            .hasLabeledImage()
            .sensitivity()
            .specificity()
            .precision()
            .accuracy()
            .iou()
            .build();

            grabImageStatistic = new ImageStatistic.Builder(
                this.image,
                MethodEnum.GRAB, 
                superpixels.getSuperpixelsAmount(), 
                execGrabTime
            ).overlappingImage(grabcut.getFinalBinarySegmentation(), this.image.getLabeledFilledContourImage())
            .hasLabeledImage()
            .sensitivity()
            .specificity()
            .precision()
            .accuracy()
            .iou()
            .build();
        
        } else {

            svmImageStatistic = new ImageStatistic.Builder(
                this.image,
                MethodEnum.SVM, 
                superpixels.getSuperpixelsAmount(), 
                execSvmTime
            ).build();

            grabImageStatistic = new ImageStatistic.Builder(
                this.image,
                MethodEnum.GRAB, 
                superpixels.getSuperpixelsAmount(), 
                execGrabTime
            ).build();
        }
        updateProgress(process[index++], maxProcess);

        this.image.setGrabCutHumanMask(grabcut.getGrabCutHumanMask());
        this.image.setFinalUlcerSegmentation(grabcut.getFinalUlcerSegmentation());
        this.image.setFinalBinarySegmentation(grabcut.getFinalBinarySegmentation());
        this.image.setMlCLassifiedImage(ml.getClassified());
        this.image.setSkeletonWithBranchs(skeletonization.getSkeletonView());
        this.image.setSkeletonWithoutBranchs(skeletonization.getSkeletonWithoutBranchsNot());
        this.image.setMlOverlappingImage(svmImageStatistic.getOverlappingImage());
        this.image.setGrabCutOverlappingImage(grabImageStatistic.getOverlappingImage());
        updateProgress(process[index++], maxProcess);

        IMAGE_SERVICE.save(this.image);
        FILE_SERVICE.saveImageStatistics(
            (new ImageStatisticUtil(Arrays.asList(svmImageStatistic))).statisticsCsvHeader().csvFormatter().toString(), 
            svmImageStatistic.getImage().getDirectory().getStatisticsSvmCsvPath()
        );
        FILE_SERVICE.saveImageStatistics(
            (new ImageStatisticUtil(Arrays.asList(grabImageStatistic))).statisticsCsvHeader().csvFormatter().toString(), 
            grabImageStatistic.getImage().getDirectory().getStatisticsGrabCsvPath()
        );
        FILE_SERVICE.saveImageStatistics(
            ImageStatisticUtil.getStatisticsMerged(Arrays.asList(svmImageStatistic), Arrays.asList(grabImageStatistic)),
            grabImageStatistic.getImage().getDirectory().getStatisticsVisualizationPath()
        );
        updateProgress(maxProcess, maxProcess);

        Thread.sleep(500l);
    }

    private Double calculateExecutionTime(Long startTime, long finalTime) {

        return ((finalTime - startTime)/1000.0);
    }

    private boolean validateFiles(DataSource datasource, Object model, Map<String,List<Double>> minMax) {

        return datasource != null && model != null && !minMax.isEmpty();
    }

    private void featureExtraction() throws InterruptedException {

        int [] process = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
        int maxProcess = process.length;
        int index = 0;

        List<Descriptor> descriptors2Save = new ArrayList<>();

        IMAGE_SERVICE.open(this.image);
        updateProgress(process[index++], maxProcess);

        if(!this.image.getDirectory().hasLabeledImagePath()) {

            throw new IllegalArgumentException(
                "Não existe a imagem rotulada para realizar a extração de características no camiho: " 
                + this.image.getDirectory().getDirPath().getAbsolutePath()
            );
        }

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

        // *************************************************
        List<DescriptorFactory> dFactoriesNonUlcers = new ArrayList<>();

        superpixels.getNonUlcerRegion().forEach((key, points) -> {

            dFactoriesNonUlcers.add(new DescriptorFactory(this.image, this.conf, Util.getDescriptorsNames(), points, key, "NON_ULCER"));
        });


        dFactoriesNonUlcers.stream().forEach(factory -> factory.processColor());
        updateProgress(process[index++], maxProcess);

        dFactoriesNonUlcers.stream().forEach(factory -> factory.processHaralick());
        updateProgress(process[index++], maxProcess);
        
        dFactoriesNonUlcers.stream().forEach(factory -> factory.processVariationHaralick());
        updateProgress(process[index++], maxProcess);

        dFactoriesNonUlcers.stream().forEach(factory -> factory.processLBPH());
        updateProgress(process[index++], maxProcess);

        dFactoriesNonUlcers.stream().forEach(factory -> factory.processWavelet());
        updateProgress(process[index++], maxProcess);

        // *************************************************
        List<DescriptorFactory> dFactoriesUlcers = new ArrayList<>();

        superpixels.getUlcerRegion().forEach((key, points) -> {

            dFactoriesUlcers.add(new DescriptorFactory(this.image, this.conf, Util.getDescriptorsNames(), points, key, "ULCER"));
        });
        
        dFactoriesUlcers.stream().forEach(factory -> factory.processColor());
        updateProgress(process[index++], maxProcess);

        dFactoriesUlcers.stream().forEach(factory -> factory.processHaralick());
        updateProgress(process[index++], maxProcess);
        
        dFactoriesUlcers.stream().forEach(factory -> factory.processVariationHaralick());
        updateProgress(process[index++], maxProcess);

        dFactoriesUlcers.stream().forEach(factory -> factory.processLBPH());
        updateProgress(process[index++], maxProcess);

        dFactoriesUlcers.stream().forEach(factory -> factory.processWavelet());
        updateProgress(process[index++], maxProcess);

        // *************************************************
        dFactoriesUlcers.stream().forEach(
            factory -> descriptors2Save.add(new Descriptor(factory.getUlcerClass(), factory.getDescriptors()))
        );
        updateProgress(process[index++], maxProcess);
        
        dFactoriesNonUlcers.stream().forEach(
            factory -> descriptors2Save.add(new Descriptor(factory.getUlcerClass(), factory.getDescriptors()))
        );
        updateProgress(process[index++], maxProcess);

        FILE_SERVICE.saveDescriptors(descriptors2Save, this.image.getDirectory().getFeaturesExtractedPath());
        updateProgress(process[index++], maxProcess);
        
        this.image.setSuperpixelsContourImage(superpixels.getContourImage());
        this.image.setSuperpixelsColorInformativeImage(superpixels.getColorInformativePixels());

        IMAGE_SERVICE.save(this.image);
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