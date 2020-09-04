package com.rodolfo.ulcer.segmentation.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.DescriptorsEnum;
import com.rodolfo.ulcer.segmentation.models.Directory;
import com.rodolfo.ulcer.segmentation.models.Image;

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
}