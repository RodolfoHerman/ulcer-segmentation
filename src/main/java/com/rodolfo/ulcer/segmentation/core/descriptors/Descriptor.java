package com.rodolfo.ulcer.segmentation.core.descriptors;

import java.util.List;

import com.rodolfo.ulcer.segmentation.models.Point;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Descriptor {
    
    private String ulcerClass;
    private List<Double> descriptors;
    private List<Point> points;

    public Descriptor(String ulcerClass, List<Double> descriptors) {
        
        this.ulcerClass = ulcerClass;
        this.descriptors = descriptors;
    }

    public Descriptor(String ulcerClass, List<Double> descriptors, List<Point> points) {
        
        this.ulcerClass = ulcerClass;
        this.descriptors = descriptors;
        this.points = points;
    }

    public Integer getNumberOfDescriptors() {

        return this.descriptors.size();
    }
}
