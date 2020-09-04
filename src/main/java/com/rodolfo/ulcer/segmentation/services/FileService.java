package com.rodolfo.ulcer.segmentation.services;

import java.io.File;
import java.util.List;

import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;

public interface FileService {
    
    void saveDescritores(List<Descriptor> descriptors, File path);

}
