package com.rodolfo.ulcer.segmentation.core.data.preparation;

import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Preparation {
    
    protected Configuration conf;
    protected List<Descriptor> descriptors;
    protected Map<String,List<Double>> minMaxDescriptors;
    protected List<String> descriptorsNames;

    public Preparation(Configuration conf, List<Descriptor> descriptors, List<String> descriptorsNames) {

        this.conf = conf;
        this.descriptors = descriptors;
        this.descriptorsNames = descriptorsNames;
    }

    public Preparation(Configuration conf, Map<String,List<Double>> minMaxDescriptors, List<Descriptor> descriptors, List<String> descriptorsNames) {

        this.conf = conf;
        this.minMaxDescriptors = minMaxDescriptors;
        this.descriptors = descriptors;
        this.descriptorsNames = descriptorsNames;
    }

    public abstract void preparation();
}
