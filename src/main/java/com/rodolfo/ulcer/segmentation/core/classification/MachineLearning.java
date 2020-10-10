package com.rodolfo.ulcer.segmentation.core.classification;

import java.util.List;

import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import lombok.Getter;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

@Getter
public abstract class MachineLearning {
    
    protected Object model;
    protected DataSource dataSource;
    protected Size imageSize;
    protected List<Descriptor> descriptors;
    protected Instance instance;
    protected Mat classified;

    public MachineLearning(Object model, DataSource dataSource, Size imageSize, List<Descriptor> descriptors) {

        this.model = model;
        this.dataSource = dataSource;
        this.imageSize = imageSize;
        this.descriptors = descriptors;
    }

    public void createInstances() throws Exception {

        Instances instances = this.dataSource.getDataSet();
        instances.setClassIndex(instances.numAttributes() - 1);

        this.instance = new DenseInstance(instances.numAttributes());
        this.instance.setDataset(instances);
    }

    public abstract void classify() throws Exception;

    public Mat getOutlineFilled() {

        return OpenCV.findLargerOutlineAndFill(this.classified);
    }

    protected void setValues(Descriptor descriptor) {
        
        List<Double> values = descriptor.getDescriptors();

        for(int index = 0; index < values.size(); index++) {

            this.instance.setValue(index, values.get(index));
        }
    }
}
