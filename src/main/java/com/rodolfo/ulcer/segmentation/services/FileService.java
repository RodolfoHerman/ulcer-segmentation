package com.rodolfo.ulcer.segmentation.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;

import weka.core.converters.ConverterUtils.DataSource;

public interface FileService {
    
    void saveDescriptors(List<Descriptor> descriptors, File path);

    void saveMinMaxDescriptors(Map<String,List<Double>> minMaxDescriptors, File path);

    Map<String, List<Double>> openMinMaxDescriptors(File path);

    DataSource openDataSoruce(File path);

    Object openMlModel(File path);

}
