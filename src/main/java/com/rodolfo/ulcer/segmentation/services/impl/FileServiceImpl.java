package com.rodolfo.ulcer.segmentation.services.impl;

import java.io.File;
import java.util.List;

import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.repositories.FileRepository;
import com.rodolfo.ulcer.segmentation.services.FileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileServiceImpl implements FileService {

    private static final FileRepository fileRepository = new FileRepository();
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public void saveDescritores(List<Descriptor> descriptors, File path) {
        
        log.info("Salvando descritores no caminho : {}", path.getAbsolutePath());

        fileRepository.saveDescritores(descriptors, path);
    }
    
}
