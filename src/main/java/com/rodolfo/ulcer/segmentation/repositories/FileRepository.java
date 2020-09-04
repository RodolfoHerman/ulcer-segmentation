package com.rodolfo.ulcer.segmentation.repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.utils.Util;

public class FileRepository {
    
    public void saveDescritores(List<Descriptor> descriptors, File path) {

        String header = Util.createARFFHeader();

        try(BufferedWriter bWriter = new BufferedWriter(new FileWriter(path))) {

            bWriter.write(header);

            for(Descriptor descriptor: descriptors) {

                bWriter.write(descriptor.getDescriptors().stream().map(value -> value.toString()).collect(Collectors.joining(",")));

                bWriter.write(",".concat(descriptor.getUlcerClass()));
                bWriter.newLine();
                bWriter.flush();
            }

            bWriter.flush();

        } catch (Exception e) {

        }
    }

}
