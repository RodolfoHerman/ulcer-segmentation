package com.rodolfo.ulcer.segmentation.repositories;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.utils.Util;

public class FileRepository {
    
    public void saveDescriptors(List<Descriptor> descriptors, File path) {

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

            System.err.println(e);
        }
    }

    public void saveMinMaxDescriptors(Map<String, List<Double>> minMaxDescriptors, File path) {

        try(ObjectOutputStream oStream = new ObjectOutputStream(new FileOutputStream(path))) {
            
            oStream.writeObject(minMaxDescriptors);

        } catch (Exception e) {
            
            System.err.println(e);
        }
    }

    public Map<String, List<Double>> openMinMaxDescriptors(File path) {

        Map<String, List<Double>> minMaxDescriptors = new HashMap<>();
        
        try(ObjectInputStream oStream = new ObjectInputStream(new FileInputStream(path))) {
            
            Object aux = oStream.readObject();

            if(aux instanceof HashMap) {

                minMaxDescriptors = (HashMap<String,List<Double>>) aux;
            }

        } catch (Exception e) {
            
            System.err.println(e);
        }

        return minMaxDescriptors;
    }

}
