package com.rodolfo.ulcer.segmentation.core.classification;

import java.util.List;

import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;

import lombok.extern.slf4j.Slf4j;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import weka.classifiers.functions.LibSVM;
import weka.core.converters.ConverterUtils.DataSource;

@Slf4j
public class SVM extends MachineLearning {

    public SVM(Object model, DataSource dataSource, Size imageSize, List<Descriptor> descriptors) {

        super(model, dataSource, imageSize, descriptors);
    }

    @Override
    public void classify() throws Exception {

        log.info("Realizando a classificação dos superpixels em úlcera ou não úlcera com o SVM");

        LibSVM svm = (LibSVM) this.model;
        this.classified = new Mat(this.imageSize, opencv_core.CV_8UC1, Scalar.WHITE);

        final Integer ULCER = 0;
        final Integer NON_ULCER = 255;

        UByteRawIndexer index = this.classified.createIndexer();

        for(Descriptor descriptor: this.descriptors) {

            this.setValues(descriptor);

            double[] classificationResult = svm.distributionForInstance(this.instance);

            int assignClass = classificationResult[0] > classificationResult[1] ? ULCER : NON_ULCER;

            descriptor.getPoints().stream().forEach(point -> {

                index.put(point.getRow(), point.getCol(), assignClass);
            });
        }
        
        index.release();
    }
}
