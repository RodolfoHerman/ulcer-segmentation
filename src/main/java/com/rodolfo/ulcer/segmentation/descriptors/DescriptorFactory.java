package com.rodolfo.ulcer.segmentation.descriptors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private final Map<Integer,List<Point>> superpixels;
    private final Configuration conf;
    private List<List<Double>> colorDescriptors;
    private List<List<Double>> haralickDescriptors;
    private List<List<Double>> variationHaralickDescriptors;
    private List<List<Double>> lbphDescriptors;
    private List<List<Double>> waveletDescriptors;

    public DescriptorFactory(Image image, Configuration conf, Map<Integer,List<Point>> superpixels) {

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

        this.conf = conf;
        this.superpixels = superpixels;
        this.colorDescriptors = new ArrayList<>();
        this.haralickDescriptors = new ArrayList<>();
        this.variationHaralickDescriptors = new ArrayList<>();
        this.lbphDescriptors = new ArrayList<>();
        this.waveletDescriptors = new ArrayList<>();
    }

    public void processColor() {

        List<List<Double>> colorBGR = this.extractColorDescriptors(this.bgr);
        List<List<Double>> colorLAB = this.extractColorDescriptors(this.lab);
        List<List<Double>> colorLUV = this.extractColorDescriptors(this.luv);
        List<List<Double>> colorNORM = this.extractColorDescriptors(this.norm);

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> aux = new ArrayList<>();

            aux.addAll(colorBGR.get(index));
            aux.addAll(colorLAB.get(index));
            aux.addAll(colorLUV.get(index));
            aux.addAll(colorNORM.get(index));

            this.colorDescriptors.add(aux);
        }
    }

    public void processHaralick() {

        List<List<Double>> haralickBGR_B = this.extractHaralickDescriptors(this.bgr_b);
        List<List<Double>> haralickBGR_G = this.extractHaralickDescriptors(this.bgr_g);
        List<List<Double>> haralickBGR_R = this.extractHaralickDescriptors(this.bgr_r);
        List<List<Double>> haralickLAB_A = this.extractHaralickDescriptors(this.lab_a);
        List<List<Double>> haralickLAB_B = this.extractHaralickDescriptors(this.lab_b);
        List<List<Double>> haralickLUV_U = this.extractHaralickDescriptors(this.luv_u);
        List<List<Double>> haralickLUV_V = this.extractHaralickDescriptors(this.luv_v);

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> aux = new ArrayList<>();

            aux.addAll(haralickBGR_B.get(index));
            aux.addAll(haralickBGR_G.get(index));
            aux.addAll(haralickBGR_R.get(index));
            aux.addAll(haralickLAB_A.get(index));
            aux.addAll(haralickLAB_B.get(index));
            aux.addAll(haralickLUV_U.get(index));
            aux.addAll(haralickLUV_V.get(index));

            this.haralickDescriptors.add(aux);
        }
    }

    public void processVariationHaralick() {

        List<List<Double>> haralickBGR_BG = this.extractVariationHaralickDescriptors(this.bgr_b, this.bgr_g);
        List<List<Double>> haralickBGR_BR = this.extractVariationHaralickDescriptors(this.bgr_b, this.bgr_r);
        List<List<Double>> haralickBGR_GR = this.extractVariationHaralickDescriptors(this.bgr_g, this.bgr_r);
        List<List<Double>> haralickLAB_AB = this.extractVariationHaralickDescriptors(this.lab_a, this.lab_b);
        List<List<Double>> haralickLUV_UV = this.extractVariationHaralickDescriptors(this.luv_u, this.luv_v);

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> aux = new ArrayList<>();

            aux.addAll(haralickBGR_BG.get(index));
            aux.addAll(haralickBGR_BR.get(index));
            aux.addAll(haralickBGR_GR.get(index));
            aux.addAll(haralickLAB_AB.get(index));
            aux.addAll(haralickLUV_UV.get(index));

            this.variationHaralickDescriptors.add(aux);
        }
    }

    public void processLBPH() {

        List<List<Double>> LBPHBGR_B = this.extractLBPHDescriptors(this.bgr_b);
        List<List<Double>> LBPHBGR_G = this.extractLBPHDescriptors(this.bgr_g);
        List<List<Double>> LBPHBGR_R = this.extractLBPHDescriptors(this.bgr_r);

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> aux = new ArrayList<>();

            aux.addAll(LBPHBGR_B.get(index));
            aux.addAll(LBPHBGR_G.get(index));
            aux.addAll(LBPHBGR_R.get(index));

            this.lbphDescriptors.add(aux);
        }
    }


    public void processWavelet() {

        List<List<Double>> WaveletBGR_B = this.extractWaveletDescriptors(this.bgr_b);
        List<List<Double>> WaveletBGR_G = this.extractWaveletDescriptors(this.bgr_g);
        List<List<Double>> WaveletBGR_R = this.extractWaveletDescriptors(this.bgr_r);

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> aux = new ArrayList<>();
            
            aux.addAll(WaveletBGR_B.get(index));
            aux.addAll(WaveletBGR_G.get(index));
            aux.addAll(WaveletBGR_R.get(index));

            this.waveletDescriptors.add(aux);
        }
    }

    private List<List<Double>> extractColorDescriptors(Mat img) {

        List<List<Double>> descriptorsValue = new ArrayList<>();

        this.superpixels.forEach((key, points) -> { 

            List<Double> descTemp = new ArrayList<>();

            Color color = new Color(img, points, 2);
            color.process();

            ColorDescriptors cDescriptorsChannel1 = new ColorDescriptors(color.getChannel1());
            ColorDescriptors cDescriptorsChannel2 = new ColorDescriptors(color.getChannel2());
            ColorDescriptors cDescriptorsChannel3 = new ColorDescriptors(color.getChannel3());

            descTemp.add(cDescriptorsChannel1.mean());
            descTemp.add(cDescriptorsChannel2.mean());
            descTemp.add(cDescriptorsChannel3.mean());

            descTemp.add(cDescriptorsChannel1.variance());
            descTemp.add(cDescriptorsChannel2.variance());
            descTemp.add(cDescriptorsChannel3.variance());

            descTemp.add((double)color.getCentroid()[0]);
            descTemp.add((double)color.getCentroid()[1]);
            descTemp.add((double)color.getCentroid()[2]);

            descTemp.add(cDescriptorsChannel1.asymmetry());
            descTemp.add(cDescriptorsChannel2.asymmetry());
            descTemp.add(cDescriptorsChannel3.asymmetry());

            descTemp.add(cDescriptorsChannel1.intensity1());
            descTemp.add(cDescriptorsChannel2.intensity1());
            descTemp.add(cDescriptorsChannel3.intensity1());

            descTemp.add(cDescriptorsChannel1.intensity2());
            descTemp.add(cDescriptorsChannel2.intensity2());
            descTemp.add(cDescriptorsChannel3.intensity2());

            descTemp.add(cDescriptorsChannel1.frequency1());
            descTemp.add(cDescriptorsChannel2.frequency1());
            descTemp.add(cDescriptorsChannel3.frequency1());

            descTemp.add(cDescriptorsChannel1.frequency2());
            descTemp.add(cDescriptorsChannel2.frequency2());
            descTemp.add(cDescriptorsChannel3.frequency2());

            descriptorsValue.add(descTemp);
        });

        return descriptorsValue;
    }

    private List<List<Double>> extractHaralickDescriptors(Mat img) {

        List<List<Double>> descriptorsValue = new ArrayList<>();

        this.superpixels.forEach((key, points) -> {

            List<Double> descTemp = new ArrayList<>();

            Mat hGlcm = this.createHaralickGlcm(img, points);

            HaralickDescriptors hDescriptors = new HaralickDescriptors(hGlcm);

            descTemp.add(hDescriptors.contrast().doubleValue());
            descTemp.add(hDescriptors.energy().doubleValue());
            descTemp.add(hDescriptors.entropy().doubleValue());
            descTemp.add(hDescriptors.homogeneity().doubleValue());
            descTemp.add(hDescriptors.correlation().doubleValue());

            descriptorsValue.add(descTemp);
        });

        return descriptorsValue;
    }

    private List<List<Double>> extractVariationHaralickDescriptors(Mat img1, Mat img2) { 

        List<List<Double>> descriptorsValue = new ArrayList<>();

        this.superpixels.forEach((key, points) -> { 

            List<Double> descTemp = new ArrayList<>();
            Mat hGlcm = new Mat();

            Mat glcmChannel1 = this.createHaralickGlcm(img1, points);
            Mat glcmChannel2 = this.createHaralickGlcm(img2, points);

            opencv_core.add(glcmChannel1, glcmChannel2, hGlcm);

            HaralickDescriptors hDescriptors = new HaralickDescriptors(hGlcm);

            descTemp.add(hDescriptors.contrast().doubleValue());
            descTemp.add(hDescriptors.energy().doubleValue());
            descTemp.add(hDescriptors.entropy().doubleValue());
            descTemp.add(hDescriptors.homogeneity().doubleValue());
            descTemp.add(hDescriptors.correlation().doubleValue());

            descriptorsValue.add(descTemp);
        });

        return descriptorsValue;
    }

    private Mat createHaralickGlcm(Mat img, List<Point> points) {

        HaralickGlcm hGlcm0 = new HaralickGlcm(img, points, GlcmDegreeEnum.DEGREE_0, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm45 = new HaralickGlcm(img, points, GlcmDegreeEnum.DEGREE_45, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm90 = new HaralickGlcm(img, points, GlcmDegreeEnum.DEGREE_90, this.conf.getHaralickPixelDistance());
        HaralickGlcm hGlcm135 = new HaralickGlcm(img, points, GlcmDegreeEnum.DEGREE_135, this.conf.getHaralickPixelDistance());

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


    private List<List<Double>> extractLBPHDescriptors(Mat img) {
        
        List<List<Double>> descriptorsValue = new ArrayList<>();

        this.superpixels.forEach((key, points) -> { 

            List<Double> descTemp = new ArrayList<>();

            LBPH lbph = new LBPH(img, points);
            lbph.process();

            LBPHDescriptors lbphDescriptors = new LBPHDescriptors(lbph.getValues());

            descTemp.add(lbphDescriptors.mean());
            descTemp.add(lbphDescriptors.variance());
            descTemp.add(lbphDescriptors.entropy());
            descTemp.add(lbphDescriptors.energy());

            descriptorsValue.add(descTemp);
        });

        return descriptorsValue;
    }

    private List<List<Double>> extractWaveletDescriptors(Mat img) {
        
        List<List<Double>> descriptorsValue = new ArrayList<>();

        Wavelet wavelet = new Wavelet(img, this.conf.getWaveletLevel());
        wavelet.process();

        this.superpixels.forEach((key, points) -> { 

            List<Double> descTemp = new ArrayList<>();

            WaveletDescriptors wDescriptorsLH = new WaveletDescriptors(wavelet.getLh(), wavelet.getLhNormalized(), points);
            WaveletDescriptors wDescriptorsHH = new WaveletDescriptors(wavelet.getHh(), wavelet.getHhNormalized(), points);
            WaveletDescriptors wDescriptorsHL = new WaveletDescriptors(wavelet.getHl(), wavelet.getHlNormalized(), points);

            descTemp.add(wDescriptorsLH.entropy());
            descTemp.add(wDescriptorsHH.entropy());
            descTemp.add(wDescriptorsHL.entropy());
            descTemp.add(wDescriptorsLH.energy());
            descTemp.add(wDescriptorsHH.energy());
            descTemp.add(wDescriptorsHL.energy());

            descriptorsValue.add(descTemp);
        });

        return descriptorsValue;
    }

    public List<List<Double>> getDescriptors() {

        List<List<Double>> descriptors = new ArrayList<>();

        for(int index = 0; index < this.superpixels.size(); index++) {

            List<Double> descTemp = new ArrayList<>();

            descTemp.addAll(this.colorDescriptors.get(index));
            descTemp.addAll(this.haralickDescriptors.get(index));
            descTemp.addAll(this.variationHaralickDescriptors.get(index));
            descTemp.addAll(this.lbphDescriptors.get(index));
            descTemp.addAll(this.waveletDescriptors.get(index));

            descriptors.add(descTemp);
        }

        return descriptors;
    }

}
