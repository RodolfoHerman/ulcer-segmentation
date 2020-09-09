package com.rodolfo.ulcer.segmentation.data.preparation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.Descriptor;

public class Normalization extends Preparation {

    public Normalization(Configuration conf, List<Descriptor> descriptors, List<String> descriptorsNames) {
        
        super(conf, descriptors, descriptorsNames);
        this.createMinMaxDescriptors();
    }

    public Normalization(Configuration conf, Map<String,List<Double>> minMaxDescriptors, List<Descriptor> descriptors, List<String> descriptorsNames) {

        super(conf, minMaxDescriptors, descriptors, descriptorsNames);
    }

    @Override
    public void preparation() {

        this.descriptors = this.descriptors.stream().map(desc -> {

            List<Double> aux = new ArrayList<>();
            
            for(int index = 0; index < desc.getDescriptors().size(); index++) {

                Double min = this.minMaxDescriptors.get(this.descriptorsNames.get(index)).get(0);
                Double max = this.minMaxDescriptors.get(this.descriptorsNames.get(index)).get(1);
                Double divider = max - min == 0.0 ? 1.0 : max - min;
                Double value = desc.getDescriptors().get(index);
                
                Double newValue = 2 * ((value - min)/divider) - 1;

                BigDecimal bd = new BigDecimal(newValue).setScale(this.getConf().getPreparationNormalizationDecimalPlaces(), RoundingMode.HALF_UP);
                
                aux.add(bd.doubleValue());
            }

            return new Descriptor(desc.getUlcerClass(), aux);

        }).collect(Collectors.toList());
    }

    private void createMinMaxDescriptors() {

        this.minMaxDescriptors = new HashMap<>();

        this.descriptorsNames.stream().forEach(descriptorName -> this.minMaxDescriptors.put(descriptorName, new ArrayList<>()));

        this.descriptors.stream().map(descriptor -> descriptor.getDescriptors()).forEach(desc -> {

            for(int index = 0; index < desc.size(); index++) {

                List<Double> aux = this.minMaxDescriptors.get(this.descriptorsNames.get(index));
                aux.add(desc.get(index));

                this.minMaxDescriptors.put(this.descriptorsNames.get(index), aux);
            }
        });

        this.descriptorsNames.stream().forEach(descriptorName -> {

            Double min = this.minMaxDescriptors.get(descriptorName).stream().mapToDouble(val -> val).min().orElse(0.0);
            Double max = this.minMaxDescriptors.get(descriptorName).stream().mapToDouble(val -> val).max().orElse(0.0);

            this.minMaxDescriptors.put(descriptorName, Arrays.asList(min,max));
        });
    }
}
