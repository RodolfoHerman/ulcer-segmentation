package com.rodolfo.ulcer.segmentation.core.data.preparation;

import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;

public class Standardization extends Preparation {

    public Standardization(Configuration conf, List<Descriptor> descriptors, List<String> descriptorsNames) {
        
        super(conf, descriptors, descriptorsNames);
    }

    public Standardization(Configuration conf, Map<String,List<Double>> minMaxDescriptors, List<Descriptor> descriptors, List<String> descriptorsNames) {

        super(conf, minMaxDescriptors, descriptors, descriptorsNames);
    }

    @Override
    public void preparation() {
        // TODO implementation
    }
    
}
