package com.rodolfo.ulcer.segmentation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.descriptors.DescriptorsEnum;
import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.models.Directory;
import com.rodolfo.ulcer.segmentation.models.Image;

import weka.core.Attribute;
import weka.core.converters.ConverterUtils.DataSource;

public class Util {
    
    public static String extractFileNameFromPath(String path) {
        
        String fileName = Paths.get(path).getFileName().toString();

        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String extractOnlyPath(String path) {

        File file = new File(path);

        return file.getParent();
    }

    public static List<Image> createImageFiles(String [] folders, Configuration configuration) {

        return Arrays.stream(folders).map(folder -> {

            File temp = new File(configuration.getChooseDirectory().concat("\\").concat(folder));
            
            if(temp.isDirectory()) {

                String imageName = configuration.hasImageName() ? configuration.getImageName() : folder;

                Directory directory = new Directory();
                directory.updatePaths(temp, imageName, configuration);

                return (new Image(directory, configuration.getResampleWidth(), configuration.getResampleHeight()));
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Map<Float,Integer> createHistogram(List<Float> values) {
        
        Map<Float,Integer> histogram = new HashMap<>();

        for (float val : values) {
            
            Integer temp = histogram.get(val);

            if(temp != null) {

                histogram.put(val, ++temp);

            } else {

                histogram.put(val, 1);
            }
        }

        return histogram;
    }

    public static List<String> getDescriptorsNames(String descriptorName) {

        DescriptorsEnum[] descriptorsEnums = DescriptorsEnum.values();

        return Arrays.stream(descriptorsEnums)
            .filter(descriptor -> descriptor.name().contains(descriptorName))
            .map(DescriptorsEnum::name)
            .collect(Collectors.toList());
    }

    public static List<String> getDescriptorsNames() {

        DescriptorsEnum[] descriptorsEnums = DescriptorsEnum.values();

        return Arrays.stream(descriptorsEnums)
            .map(DescriptorsEnum::name)
            .collect(Collectors.toList());
    }

    public static String createARFFHeader() {
        
        StringBuffer header = new StringBuffer("@relation ulcer_classification");
        header.append("\n");
        header.append("\n");

        for (String feature : getDescriptorsNames()) {
            
            header.append("@attribute ");
            header.append(feature);
            header.append(" real");
            header.append("\n");
        }

        header.append("@attribute classification {ULCER,NON_ULCER}");
        header.append("\n");
        header.append("\n");
        header.append("@data");
        header.append("\n");

        return header.toString();
    }

    public static List<Descriptor> getContentFromARFFFile(File arffFile) {

        List<Descriptor> aux = new ArrayList<>();

        try (BufferedReader bReader = new BufferedReader(new FileReader(arffFile))) {
            
            String content = null;

            while (!(content = bReader.readLine()).equals("@data")) {}

            while ((content = bReader.readLine()) != null) {

                String[] contentAux = content.split(",");
                List<Double> descriptors = new ArrayList<>();

                for(int index = 0; index < contentAux.length - 1; index++) {

                    descriptors.add(Double.valueOf(contentAux[index]));
                }

                if(descriptors.size() < 186) {

                    System.out.println("MENOR QUE 186");
                }

                aux.add(new Descriptor(contentAux[contentAux.length-1], descriptors));
            }

        } catch (Exception e) {
            
            System.err.println(e);
        }

        return aux;
    }

    public static File createDatasourceFile(String path, MethodEnum method, Configuration configuration) {

        File dFile = null;

        if(method.name().equals("SEEDS")) {

            dFile = new File("files/datasources/".concat(path).concat("/").concat(configuration.getDatasourceSEEDSName()));
        }

        if(method.name().equals("LSC")) {

            dFile = new File("files/datasources/".concat(path).concat("/").concat(configuration.getDatasourceLSCName()));
        }

        if(method.name().equals("SLIC")) {

            dFile = new File("files/datasources/".concat(path).concat("/").concat(configuration.getDatasourceSLICName()));
        }

        return dFile;
    }

    public static File createMinMaxFile(MethodEnum method, Configuration configuration) {

        File dFile = null;

        if(method.name().equals("SEEDS")) {

            dFile = new File("files/datasources/".concat(configuration.getMinMaxSEEDSName()));
        }

        if(method.name().equals("LSC")) {

            dFile = new File("files/datasources/".concat(configuration.getMinMaxLSCName()));
        }

        if(method.name().equals("SLIC")) {

            dFile = new File("files/datasources/".concat(configuration.getMinMaxSLICName()));
        }

        return dFile;
    }

    public static File createMlModelFile(MethodEnum method, Configuration configuration) {

        File dFile = null;

        if(method.name().equals("SEEDS")) {

            dFile = new File("files/models/".concat(configuration.getMlModelSEEDSName()));
        }

        if(method.name().equals("LSC")) {

            dFile = new File("files/models/".concat(configuration.getMlModelLSCName()));
        }

        if(method.name().equals("SLIC")) {

            dFile = new File("files/models/".concat(configuration.getMlModelSLICName()));
        }

        return dFile;
    }

    public static List<String> getListOfDescriptorsNamesFromDataSource(DataSource dataSource) {

        List<String> descriptorsNames = new ArrayList<>();
        
        try {
        
            Enumeration<Attribute> enumeration = dataSource.getDataSet().enumerateAttributes();

            while(enumeration.hasMoreElements()) {

                descriptorsNames.add(enumeration.nextElement().name());
            }
    
            if(!descriptorsNames.isEmpty()) {
    
                descriptorsNames.remove(descriptorsNames.size() - 1);
            }
            
        } catch (Exception e) {
        
            e.printStackTrace();
            System.exit(1);
        }
        
        return descriptorsNames;
    }
}