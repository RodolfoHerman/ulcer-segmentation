package com.rodolfo.ulcer.segmentation.services.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.repositories.FileRepository;
import com.rodolfo.ulcer.segmentation.services.FileService;

import lombok.extern.slf4j.Slf4j;
import weka.core.converters.ConverterUtils.DataSource;

@Slf4j
public class FileServiceImpl implements FileService {

    private static final FileRepository FILE_REPOSITORY = new FileRepository();

    @Override
    public void saveDescriptors(List<Descriptor> descriptors, File path) {

        log.info("Salvando descritores no caminho : {}", path.getAbsolutePath());

        FILE_REPOSITORY.saveDescriptors(descriptors, path);
    }

    @Override
    public void saveMinMaxDescriptors(Map<String, List<Double>> minMaxDescriptors, File path) {

        log.info("Salvando min/max descritores no caminho : {}", path.getAbsolutePath());

        FILE_REPOSITORY.saveMinMaxDescriptors(minMaxDescriptors, path);
    }

    @Override
    public Map<String, List<Double>> openMinMaxDescriptors(File path) {

        log.info("Abrindo min/max descritores no caminho : {}", path.getAbsolutePath());

        return FILE_REPOSITORY.openMinMaxDescriptors(path);
    }

    @Override
    public DataSource openDataSoruce(File path) {
        
        log.info("Abrindo datasource no caminho : {}", path.getAbsolutePath());

        return FILE_REPOSITORY.openDataSoruce(path);
    }

    @Override
    public Object openMlModel(File path) {
        
        log.info("Abrindo o modelo do classificador no caminho : {}", path.getAbsolutePath());

        return FILE_REPOSITORY.openMlModel(path);
    }

    @Override
    public void saveImageStatistics(String statistic, File path) {

        log.info("Salvando estatísticas no caminho : {}", path.getAbsolutePath());

        FILE_REPOSITORY.saveImageStatistics(statistic, path);
    }

    @Override
    public List<String> getFileContent(File path) {

        log.info("Lendo estatísticas no caminho : {}", path.getAbsolutePath());

        return FILE_REPOSITORY.getFileContent(path);
    }
    
}
