package com.rodolfo.ulcer.segmentation.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.color.ColorDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.color.models.Color;
import com.rodolfo.ulcer.segmentation.descriptors.texture.HaralickDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.LBPHDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.WaveletDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.enums.GlcmDegreeEnum;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.HaralickGlcm;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.LBPH;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.Wavelet;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.models.Point;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DescriptorFactory {
    
    private Mat bgr;
    private Mat lab;
    private Mat luv;
    private Mat norm;

    private Mat bgr_b;
    private Mat bgr_g;
    private Mat bgr_r;

    private Mat lab_a;
    private Mat lab_b;

    private Mat luv_u;
    private Mat luv_v;

    private Configuration conf;
    private List<String> descriptorsNames;
    private List<Point> points;
    private int label;
    private String ulcerClass;

    private List<Double> colorDescriptors;
    private List<Double> haralickDescriptors;
    private List<Double> variationHaralickDescriptors;
    private List<Double> lbphDescriptors;
    private List<Double> waveletDescriptors;


    public DescriptorFactory(Image image, Configuration configuration, List<String> descriptorsNames, List<Point> points, int label, String ulcerClass) {
        
        this.bgr = image.matImage2BGR();
        this.lab = image.matImage2LAB();
        this.luv = image.matImage2LUV();
        this.norm = image.matImage2BGRNorm();

        this.bgr_b = OpenCV.getMatChannel(bgr, 0);
        this.bgr_g = OpenCV.getMatChannel(bgr, 1);
        this.bgr_r = OpenCV.getMatChannel(bgr, 2);

        this.lab_a = OpenCV.getMatChannel(lab, 1);
        this.lab_b = OpenCV.getMatChannel(lab, 2);

        this.luv_u = OpenCV.getMatChannel(luv, 1);
        this.luv_v = OpenCV.getMatChannel(luv, 2);
        
        this.conf = configuration;
        this.descriptorsNames = descriptorsNames;
        this.points = points;
        this.label = label;
        this.ulcerClass = ulcerClass;

        colorDescriptors = new ArrayList<>();
        haralickDescriptors = new ArrayList<>();
        variationHaralickDescriptors = new ArrayList<>();
        lbphDescriptors = new ArrayList<>();
        waveletDescriptors = new ArrayList<>();
    }

    public void processColor() {

        List<Double> colorBGR = this.getColorProcessed("color_bgr_", "b", "g");
        List<Double> colorLAB = this.getColorProcessed("color_lab_", "l", "a");
        List<Double> colorLUV = this.getColorProcessed("color_luv_", "l", "u");
        List<Double> colorNORM = this.getColorProcessed("color_norm_", "b", "g");

        this.addElementsToList(colorBGR, this.colorDescriptors);
        this.addElementsToList(colorLAB, this.colorDescriptors);
        this.addElementsToList(colorLUV, this.colorDescriptors);
        this.addElementsToList(colorNORM, this.colorDescriptors);
    }

    public void processHaralick() {

        List<Double> haralickBGR_B = this.getHaralickProcessed(this.bgr_b, "haralick_bgr_b_");
        List<Double> haralickBGR_G = this.getHaralickProcessed(this.bgr_g, "haralick_bgr_g_");
        List<Double> haralickBGR_R = this.getHaralickProcessed(this.bgr_r, "haralick_bgr_r_");
        List<Double> haralickLAB_A = this.getHaralickProcessed(this.lab_a, "haralick_lab_a_");
        List<Double> haralickLAB_B = this.getHaralickProcessed(this.lab_b, "haralick_lab_b_");
        List<Double> haralickLUV_U = this.getHaralickProcessed(this.luv_u, "haralick_luv_u_");
        List<Double> haralickLUV_V = this.getHaralickProcessed(this.luv_v, "haralick_luv_v_");

        this.addElementsToList(haralickBGR_B, this.haralickDescriptors);
        this.addElementsToList(haralickBGR_G, this.haralickDescriptors);
        this.addElementsToList(haralickBGR_R, this.haralickDescriptors);
        this.addElementsToList(haralickLAB_A, this.haralickDescriptors);
        this.addElementsToList(haralickLAB_B, this.haralickDescriptors);
        this.addElementsToList(haralickLUV_U, this.haralickDescriptors);
        this.addElementsToList(haralickLUV_V, this.haralickDescriptors);
    }

    public void processVariationHaralick() {

        List<Double> haralickBGR_BG = this.getVariationHaralickProcessed(this.bgr_b, this.bgr_g, "haralick_bgr_bg_");
        List<Double> haralickBGR_BR = this.getVariationHaralickProcessed(this.bgr_b, this.bgr_r, "haralick_bgr_br_");
        List<Double> haralickBGR_GR = this.getVariationHaralickProcessed(this.bgr_g, this.bgr_r, "haralick_bgr_gr_");
        List<Double> haralickLAB_AB = this.getVariationHaralickProcessed(this.lab_a, this.lab_b, "haralick_lab_ab_");
        List<Double> haralickLUV_UV = this.getVariationHaralickProcessed(this.luv_u, this.luv_v, "haralick_luv_uv_");

        this.addElementsToList(haralickBGR_BG, this.variationHaralickDescriptors);
        this.addElementsToList(haralickBGR_BR, this.variationHaralickDescriptors);
        this.addElementsToList(haralickBGR_GR, this.variationHaralickDescriptors);
        this.addElementsToList(haralickLAB_AB, this.variationHaralickDescriptors);
        this.addElementsToList(haralickLUV_UV, this.variationHaralickDescriptors);
    }

    public void processLBPH() {

        List<Double> LBPHBGR_B = this.getLBPHProcessed(this.bgr_b, "lbph_bgr_b_");
        List<Double> LBPHBGR_G = this.getLBPHProcessed(this.bgr_g, "lbph_bgr_g_");
        List<Double> LBPHBGR_R = this.getLBPHProcessed(this.bgr_r, "lbph_bgr_r_");

        this.addElementsToList(LBPHBGR_B, this.lbphDescriptors);
        this.addElementsToList(LBPHBGR_G, this.lbphDescriptors);
        this.addElementsToList(LBPHBGR_R, this.lbphDescriptors);
    }

    public void processWavelet() {

        List<Double> WaveletBGR_B = this.getWaveletProcessed(this.bgr_b, "wavelet_bgr_b_");
        List<Double> WaveletBGR_G = this.getWaveletProcessed(this.bgr_g, "wavelet_bgr_g_");
        List<Double> WaveletBGR_R = this.getWaveletProcessed(this.bgr_r, "wavelet_bgr_r_");

        this.addElementsToList(WaveletBGR_B, this.waveletDescriptors);
        this.addElementsToList(WaveletBGR_G, this.waveletDescriptors);
        this.addElementsToList(WaveletBGR_R, this.waveletDescriptors);
    }

    private List<Double> getColorProcessed(String colorComponent, String channel1, String channel2) {

        if(this.isConsidered(colorComponent)) {

            List<String> channels = this.extractColorChannelToBeconsidered(colorComponent).stream().map(channel -> {

                return channel.equals(channel1) ? "1" : 
                       channel.equals(channel2) ? "2" : "3";

            }).collect(Collectors.toList());

            return this.extractColorDescriptors(this.norm, this.extractDescriptorsToBeConsidered(colorComponent), channels);
        }

        return null;
    }

    private List<Double> getHaralickProcessed(Mat mat, String colorComponent) {

        if(this.isConsidered(colorComponent)) {

            return this.extractHaralickDescriptors(mat, this.extractDescriptorsToBeConsidered(colorComponent));
        }

        return null;
    }

    private List<Double> getVariationHaralickProcessed(Mat mat1, Mat mat2, String colorComponent) {

        if(this.isConsidered(colorComponent)) {

            return this.extractVariationHaralickDescriptors(mat1, mat2, this.extractDescriptorsToBeConsidered(colorComponent));
        }

        return null;
    }

    private List<Double> getLBPHProcessed(Mat mat, String colorComponent) {

        if(this.isConsidered(colorComponent)) {

            return this.extractLBPHDescriptors(mat, this.extractDescriptorsToBeConsidered(colorComponent));
        }

        return null;
    }

    private List<Double> getWaveletProcessed(Mat mat, String colorComponent) {

        if(this.isConsidered(colorComponent)) {

            return this.extractWaveletDescriptors(mat);
        }

        return null;
    }

    private List<Double> extractColorDescriptors(Mat img, List<String> descriptorsToBeConsidered, List<String> channelsToBeConsidered) {

        List<Double> descriptorsValue = new ArrayList<>();

        Color color = new Color(img, this.points, 2);
        color.process();

        ColorDescriptors cDescriptorsChannel1 = null;
        ColorDescriptors cDescriptorsChannel2 = null;
        ColorDescriptors cDescriptorsChannel3 = null;

        boolean hasChannel1 = channelsToBeConsidered.contains("1");
        boolean hasChannel2 = channelsToBeConsidered.contains("2");
        boolean hasChannel3 = channelsToBeConsidered.contains("3");

        if(hasChannel1) {

            cDescriptorsChannel1 = new ColorDescriptors(color.getChannel1());
        }

        if(hasChannel2) {

            cDescriptorsChannel2 = new ColorDescriptors(color.getChannel2());
        }

        if(hasChannel3) {

            cDescriptorsChannel3 = new ColorDescriptors(color.getChannel3());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("mean")) {

            descriptorsValue.add(cDescriptorsChannel1.mean());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("mean")) {

            descriptorsValue.add(cDescriptorsChannel2.mean());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("mean")) {

            descriptorsValue.add(cDescriptorsChannel3.mean());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("variance")) {

            descriptorsValue.add(cDescriptorsChannel1.variance());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("variance")) {

            descriptorsValue.add(cDescriptorsChannel2.variance());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("variance")) {

            descriptorsValue.add(cDescriptorsChannel3.variance());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("centroid")) {

            descriptorsValue.add((double)color.getCentroid()[0]);
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("centroid")) {

            descriptorsValue.add((double)color.getCentroid()[1]);
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("centroid")) {

            descriptorsValue.add((double)color.getCentroid()[2]);
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("asymetry")) {

            descriptorsValue.add(cDescriptorsChannel1.asymmetry());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("asymetry")) {

            descriptorsValue.add(cDescriptorsChannel2.asymmetry());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("asymetry")) {

            descriptorsValue.add(cDescriptorsChannel3.asymmetry());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("intensity1")) {

            descriptorsValue.add(cDescriptorsChannel1.intensity1());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("intensity1")) {

            descriptorsValue.add(cDescriptorsChannel2.intensity1());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("intensity1")) {

            descriptorsValue.add(cDescriptorsChannel3.intensity1());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("intensity2")) {

            descriptorsValue.add(cDescriptorsChannel1.intensity2());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("intensity2")) {

            descriptorsValue.add(cDescriptorsChannel2.intensity2());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("intensity2")) {

            descriptorsValue.add(cDescriptorsChannel3.intensity2());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("frequency1")) {

            descriptorsValue.add(cDescriptorsChannel1.frequency1());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("frequency1")) {

            descriptorsValue.add(cDescriptorsChannel2.frequency1());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("frequency1")) {

            descriptorsValue.add(cDescriptorsChannel3.frequency1());
        }

        if(hasChannel1 && descriptorsToBeConsidered.contains("frequency2")) {

            descriptorsValue.add(cDescriptorsChannel1.frequency2());
        }

        if(hasChannel2 && descriptorsToBeConsidered.contains("frequency2")) {

            descriptorsValue.add(cDescriptorsChannel2.frequency2());
        }

        if(hasChannel3 && descriptorsToBeConsidered.contains("frequency2")) {

            descriptorsValue.add(cDescriptorsChannel3.frequency2());
        }
        
        return descriptorsValue;
    }

    private List<Double> extractHaralickDescriptors(Mat img, List<String> descriptorsToBeConsidered) {

        List<Double> descriptorsValue = new ArrayList<>();
        Mat hGlcm = this.createHaralickGlcm(img);

        HaralickDescriptors hDescriptors = new HaralickDescriptors(hGlcm);

        if(descriptorsToBeConsidered.contains("contrast")) {

            descriptorsValue.add(hDescriptors.contrast().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("energy")) {

            descriptorsValue.add(hDescriptors.energy().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("entropy")) {

            descriptorsValue.add(hDescriptors.entropy().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("homogeneity")) {

            descriptorsValue.add(hDescriptors.homogeneity().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("correlation")) {

            descriptorsValue.add(hDescriptors.correlation().doubleValue());
        }

        return descriptorsValue;
    }

    private List<Double> extractVariationHaralickDescriptors(Mat mat1, Mat mat2, List<String> descriptorsToBeConsidered) { 

        List<Double> descriptorsValue = new ArrayList<>();
        Mat hGlcm = new Mat();

        Mat glcmChannel1 = this.createHaralickGlcm(mat1);
        Mat glcmChannel2 = this.createHaralickGlcm(mat2);

        opencv_core.add(glcmChannel1, glcmChannel2, hGlcm);

        HaralickDescriptors hDescriptors = new HaralickDescriptors(hGlcm);

        if(descriptorsToBeConsidered.contains("contrast")) {

            descriptorsValue.add(hDescriptors.contrast().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("energy")) {

            descriptorsValue.add(hDescriptors.energy().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("entropy")) {

            descriptorsValue.add(hDescriptors.entropy().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("homogeneity")) {

            descriptorsValue.add(hDescriptors.homogeneity().doubleValue());
        }

        if(descriptorsToBeConsidered.contains("correlation")) {

            descriptorsValue.add(hDescriptors.correlation().doubleValue());
        }

        return descriptorsValue;
    }

    private List<Double> extractLBPHDescriptors(Mat img, List<String> descriptorsToBeConsidered) {
        
        List<Double> descriptorsValue = new ArrayList<>();

        LBPH lbph = new LBPH(img, this.points);
        lbph.process();

        LBPHDescriptors lbphDescriptors = new LBPHDescriptors(lbph.getValues());

        if(descriptorsToBeConsidered.contains("mean")) {

            descriptorsValue.add(lbphDescriptors.mean());
        }

        if(descriptorsToBeConsidered.contains("variance")) {

            descriptorsValue.add(lbphDescriptors.variance());
        }

        if(descriptorsToBeConsidered.contains("entropy")) {

            descriptorsValue.add(lbphDescriptors.entropy());
        }

        if(descriptorsToBeConsidered.contains("energy")) {

            descriptorsValue.add(lbphDescriptors.energy());
        }

        return descriptorsValue;
    }

    private List<Double> extractWaveletDescriptors(Mat img) {
        
        List<Double> descriptorsValue = new ArrayList<>();

        Wavelet wavelet = new Wavelet(img, this.conf.getWaveletLevel());
        wavelet.process();

        WaveletDescriptors wDescriptorsLH = null;
        WaveletDescriptors wDescriptorsHH = null;
        WaveletDescriptors wDescriptorsHL = null;

        if(this.isConsidered("_3_lh_")) {

            wDescriptorsLH = new WaveletDescriptors(wavelet.getLh(), wavelet.getLhNormalized(), this.points);
        }

        if(this.isConsidered("_3_hh_")) {

            wDescriptorsHH = new WaveletDescriptors(wavelet.getHh(), wavelet.getHhNormalized(), this.points);
        }

        if(this.isConsidered("_3_hl_")) {

            wDescriptorsHL = new WaveletDescriptors(wavelet.getHl(), wavelet.getHlNormalized(), this.points);
        }

        if(this.isConsidered("_3_lh_entropy")) {

            descriptorsValue.add(wDescriptorsLH.entropy());
        }

        if(this.isConsidered("_3_hh_entropy")) {

            descriptorsValue.add(wDescriptorsHH.entropy());
        }

        if(this.isConsidered("_3_hl_entropy")) {

            descriptorsValue.add(wDescriptorsHL.entropy());
        }

        // ***

        if(this.isConsidered("_3_lh_energy")) {

            descriptorsValue.add(wDescriptorsLH.energy());
        }

        if(this.isConsidered("_3_hh_energy")) {

            descriptorsValue.add(wDescriptorsHH.energy());
        }

        if(this.isConsidered("_3_hl_energy")) {

            descriptorsValue.add(wDescriptorsHL.energy());
        }

        return descriptorsValue;
    }

    private Mat createHaralickGlcm(Mat img) {

        HaralickGlcm hGlcm0 = new HaralickGlcm(img, this.points, GlcmDegreeEnum.DEGREE_0, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm45 = new HaralickGlcm(img, this.points, GlcmDegreeEnum.DEGREE_45, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm90 = new HaralickGlcm(img, this.points, GlcmDegreeEnum.DEGREE_90, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm135 = new HaralickGlcm(img, this.points, GlcmDegreeEnum.DEGREE_135, this.conf.getHaralickPixelDistance());

        hGlcm0.process();
        hGlcm45.process();
        hGlcm90.process();
        hGlcm135.process();

        return this.averageHaralickGlcm(hGlcm0.getGlcm(), hGlcm45.getGlcm(), hGlcm90.getGlcm(), hGlcm135.getGlcm());
    }

    private Mat averageHaralickGlcm(Mat hGlcm0, Mat hGlcm45, Mat hGlcm90, Mat hGlcm135) {

        Mat resp = new Mat();
        Mat divider = new Mat(hGlcm0.size(), opencv_core.CV_32FC1, new Scalar(4.0));

        opencv_core.add(hGlcm0, hGlcm45, resp);
        opencv_core.add(resp, hGlcm90, resp);
        opencv_core.add(resp, hGlcm135, resp);

        opencv_core.divide(resp, divider, resp);

        return resp;
    }

    private boolean isConsidered(String consider) {

        boolean resp = false;

        for(String descriptorName: this.descriptorsNames) {

            if(descriptorName.contains(consider)) {

                resp = true;
                break;
            }
        }

        return resp;
    }

    private List<String> extractDescriptorsToBeConsidered(String descriptor) {

        return this.descriptorsNames.stream()
            .filter(descName -> descName.contains(descriptor))
            .map(descName -> {

                String[] descs = descName.split("_");

                return descs[descs.length - 1];

            }).collect(Collectors.toList());
    }

    private List<String> extractColorChannelToBeconsidered(String colorComponent) {

        return this.descriptorsNames.stream()
            .filter(descName -> descName.contains(colorComponent))
            .map(descName -> {

                String[] descs = descName.split("_");

                return descs[descs.length - 2];

            }).collect(Collectors.toList());
    }

    private void addElementsToList(List<Double> elements, List<Double> list) {

        if(elements != null) {

            elements.stream().forEach(element -> list.add(element));
        }
    }

    public List<Double> getDescriptors() {

        List<Double> descriptors = new ArrayList<>();

        descriptors.addAll(this.colorDescriptors);
        descriptors.addAll(this.haralickDescriptors);
        descriptors.addAll(this.variationHaralickDescriptors);
        descriptors.addAll(this.lbphDescriptors);
        descriptors.addAll(this.waveletDescriptors);

        if(descriptors.isEmpty()) {

            System.out.println("VAZIO");
        }

        return descriptors;
    }
}
