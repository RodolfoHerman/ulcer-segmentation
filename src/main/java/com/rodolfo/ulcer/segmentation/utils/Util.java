package com.rodolfo.ulcer.segmentation.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
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

                configuration.updateImageName(folder);

                Directory directory = new Directory();
                directory.updatePaths(temp, configuration);

                return (new Image(directory, configuration.getResampleWidth(), configuration.getResampleHeight()));
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}