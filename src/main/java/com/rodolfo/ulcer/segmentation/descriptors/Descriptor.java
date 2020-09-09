package com.rodolfo.ulcer.segmentation.descriptors;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Descriptor {
    
    private String ulcerClass;
    private List<Double> descriptors;

    public Descriptor(String ulcerClass, List<Double> descriptors) {
        this.ulcerClass = ulcerClass;
        this.descriptors = descriptors;
    }

    public Integer getNumberOfDescriptors() {

        return this.descriptors.size();
    }
}
