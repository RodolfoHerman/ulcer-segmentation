package com.rodolfo.ulcer.segmentation.core.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.core.descriptors.color.ColorDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.color.models.Color;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.HaralickDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.LBPHDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.WaveletDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.enums.GlcmDegreeEnum;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.HaralickGlcm;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.LBPH;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.Wavelet;
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

        List<Double> colorBGR = this.getColorProcessed(this.bgr, "color_bgr_", "b", "g");
        List<Double> colorLAB = this.getColorProcessed(this.lab, "color_lab_", "l", "a");
        List<Double> colorLUV = this.getColorProcessed(this.luv, "color_luv_", "l", "u");
        List<Double> colorNORM = this.getColorProcessed(this.norm, "color_norm_", "b", "g");

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

    private List<Double> getColorProcessed(Mat mat, String colorComponent, String channel1, String channel2) {

        if(this.isConsidered(colorComponent)) {

            List<String> channelsAndDescriptors = this.extractColorChannelAndDescriptorsToBeconsidered(colorComponent);

            return this.extractColorDescriptors(mat, channelsAndDescriptors);
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

    private List<Double> extractColorDescriptors(Mat img, List<String> descriptorsAndChannelsToBeConsidered) {

        List<Double> descriptorsValue = new ArrayList<>();

        Color color = new Color(img, this.points, 2);
        color.process();

        ColorDescriptors cDescriptorsChannel1 = null;
        ColorDescriptors cDescriptorsChannel2 = null;
        ColorDescriptors cDescriptorsChannel3 = null;

        boolean hasChannel1 = descriptorsAndChannelsToBeConsidered.stream().filter(desc -> desc.contains("1_")).count() > 0l ? true : false;
        boolean hasChannel2 = descriptorsAndChannelsToBeConsidered.stream().filter(desc -> desc.contains("2_")).count() > 0l ? true : false;
        boolean hasChannel3 = descriptorsAndChannelsToBeConsidered.stream().filter(desc -> desc.contains("3_")).count() > 0l ? true : false;

        if(hasChannel1) {

            cDescriptorsChannel1 = new ColorDescriptors(color.getChannel1());
        }

        if(hasChannel2) {

            cDescriptorsChannel2 = new ColorDescriptors(color.getChannel2());
        }

        if(hasChannel3) {

            cDescriptorsChannel3 = new ColorDescriptors(color.getChannel3());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_mean")) {

            descriptorsValue.add(cDescriptorsChannel1.mean());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_mean")) {

            descriptorsValue.add(cDescriptorsChannel2.mean());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_mean")) {

            descriptorsValue.add(cDescriptorsChannel3.mean());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_variance")) {

            descriptorsValue.add(cDescriptorsChannel1.variance());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_variance")) {

            descriptorsValue.add(cDescriptorsChannel2.variance());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_variance")) {

            descriptorsValue.add(cDescriptorsChannel3.variance());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_centroid")) {

            descriptorsValue.add((double)color.getCentroid()[0]);
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_centroid")) {

            descriptorsValue.add((double)color.getCentroid()[1]);
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_centroid")) {

            descriptorsValue.add((double)color.getCentroid()[2]);
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_asymetry")) {

            descriptorsValue.add(cDescriptorsChannel1.asymmetry());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_asymetry")) {

            descriptorsValue.add(cDescriptorsChannel2.asymmetry());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_asymetry")) {

            descriptorsValue.add(cDescriptorsChannel3.asymmetry());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_intensity1")) {

            descriptorsValue.add(cDescriptorsChannel1.intensity1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_intensity1")) {

            descriptorsValue.add(cDescriptorsChannel2.intensity1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_intensity1")) {

            descriptorsValue.add(cDescriptorsChannel3.intensity1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_intensity2")) {

            descriptorsValue.add(cDescriptorsChannel1.intensity2());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_intensity2")) {

            descriptorsValue.add(cDescriptorsChannel2.intensity2());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_intensity2")) {

            descriptorsValue.add(cDescriptorsChannel3.intensity2());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_frequency1")) {

            descriptorsValue.add(cDescriptorsChannel1.frequency1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_frequency1")) {

            descriptorsValue.add(cDescriptorsChannel2.frequency1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_frequency1")) {

            descriptorsValue.add(cDescriptorsChannel3.frequency1());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("1_frequency2")) {

            descriptorsValue.add(cDescriptorsChannel1.frequency2());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("2_frequency2")) {

            descriptorsValue.add(cDescriptorsChannel2.frequency2());
        }

        if(descriptorsAndChannelsToBeConsidered.contains("3_frequency2")) {

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

    private List<String> extractColorChannelAndDescriptorsToBeconsidered(String colorComponent) {

        return this.descriptorsNames.stream()
            .filter(descName -> descName.contains(colorComponent))
            .map(descName -> {

                if(descName.contains("_bgr_b_")) {

                    return this.getStandardizedName(descName, "_bgr_b_", "_bgr_1_");
                }

                if(descName.contains("_bgr_g_")) {

                    return this.getStandardizedName(descName, "_bgr_g_", "_bgr_2_");
                }

                if(descName.contains("_bgr_r_")) {

                    return this.getStandardizedName(descName, "_bgr_r_", "_bgr_3_");
                }

                if(descName.contains("_norm_b_")) {

                    return this.getStandardizedName(descName, "_norm_b_", "_norm_1_");
                }

                if(descName.contains("_norm_g_")) {

                    return this.getStandardizedName(descName, "_norm_g_", "_norm_2_");
                }

                if(descName.contains("_norm_r_")) {

                    return this.getStandardizedName(descName, "_norm_r_", "_norm_3_");
                }

                if(descName.contains("_lab_l_")) {

                    return this.getStandardizedName(descName, "_lab_l_", "_lab_1_");
                }

                if(descName.contains("_lab_a_")) {

                    return this.getStandardizedName(descName, "_lab_a_", "_lab_2_");
                }

                if(descName.contains("_lab_b_")) {

                    return this.getStandardizedName(descName, "_lab_b_", "_lab_3_");
                }

                if(descName.contains("_luv_l_")) {

                    return this.getStandardizedName(descName, "_luv_l_", "_luv_1_");
                }

                if(descName.contains("_luv_u_")) {

                    return this.getStandardizedName(descName, "_luv_u_", "_luv_2_");
                }

                return this.getStandardizedName(descName, "_luv_v_", "_luv_3_");
            }).collect(Collectors.toList());
    }

    private String getStandardizedName(String descName, String seacrh, String newValue) {

        String[] temp = descName.replace(seacrh, newValue).split("_");

        return temp[temp.length - 2] + "_" + temp[temp.length - 1];
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

        return descriptors;
    }
}
