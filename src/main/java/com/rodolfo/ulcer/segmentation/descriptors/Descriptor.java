package com.rodolfo.ulcer.segmentation.descriptors;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Descriptor {
    
    final private String ulcerClass;
    final private List<Double> descriptors;

    public Descriptor(String ulcerClass, List<Double> descriptors) {
        this.ulcerClass = ulcerClass;
        this.descriptors = descriptors;
    }
}
