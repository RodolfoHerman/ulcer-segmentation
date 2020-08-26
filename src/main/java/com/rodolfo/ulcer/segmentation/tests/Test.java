package com.rodolfo.ulcer.segmentation.tests;

import java.io.File;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.color.ColorDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.color.models.Color;
import com.rodolfo.ulcer.segmentation.descriptors.texture.HaralickDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.LBPHDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.WaveletDescriptors;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.HaralickGlcm;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.LBPH;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.Wavelet;
import com.rodolfo.ulcer.segmentation.enums.GlcmDegreeEnum;
import com.rodolfo.ulcer.segmentation.models.Directory;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightMaskDetection;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.LightRemoval;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl.Inpainting;
import com.rodolfo.ulcer.segmentation.preprocessing.light_removal.impl.SpecularReflectionDetection;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.Superpixels;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsLSC;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsSEEDS;
import com.rodolfo.ulcer.segmentation.preprocessing.superpixels.SuperpixelsSLIC;
import com.rodolfo.ulcer.segmentation.services.ImageService;
import com.rodolfo.ulcer.segmentation.services.impl.ImageServiceImpl;

import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacpp.opencv_core.Mat;

public class Test {

    private static Image image;
    private static Configuration conf;

    private static final int CROPP_IMAGE = 0;
    private static final int ZERO_PADDING_IMAGE = 1;
    private static final int LIGHT_REMOVAL = 2;
    private static final int SUPERPIXELS = 3;
    private static final int APPLY_DWT = 4;
    private static final int HARALICK = 5;
    private static final int WAVELET = 6;
    private static final int LBPH = 7;
    private static final int COLOR = 8;

    public Test(int testnumber, Configuration configuration) {

        conf = configuration;
        Directory directory = new Directory();
        ImageService imageService = new ImageServiceImpl();

        File imagePath = new File(Test.class.getClassLoader().getResource("tests/img_test.jpg").getFile());
        File labeledImagePath = new File(Test.class.getClassLoader().getResource("tests/img_test_00.jpg").getFile());

        directory.setImagePath(imagePath);
        directory.setLabeledImagePath(labeledImagePath);

        image = new Image(directory, conf.getResampleWidth(), conf.getResampleHeight());
        imageService.openWithLabeled(image);

        switch (testnumber) {
            
            case CROPP_IMAGE:
                
                Test.croppImage();

            break;

            case ZERO_PADDING_IMAGE:
                
                Test.createZeroPadding();

            break;

            case LIGHT_REMOVAL:
                
                Test.lightRemoval();

            break;

            case SUPERPIXELS:
                
                Test.superpixels();

            break;

            case APPLY_DWT:
                
                Test.applyDWT();

            break;

            case HARALICK:
                
                Test.haralickDescriptors();

            break;

            case WAVELET:
                
                Test.waveletDescriptors();

            break;

            case LBPH:
                
                Test.LBPHDescriptors();

            break;

            case COLOR:
                
                Test.colorDescriptors();

            break;
        
            default:
            break;
        }
    }

    private static void croppImage() {

        Mat cropped = OpenCV.croppMat(image.getImage(), 0, 0, 10, 60);

        OpenCV.showImageGUI(cropped);
    }

    private static void createZeroPadding() {

        Mat zeroPadding = OpenCV.createImageWithZeroPadding(image.getImage());
        
        System.out.println("ROWS : " + zeroPadding.rows());
        System.out.println("COLS : " + zeroPadding.cols());

        OpenCV.showImageGUI(zeroPadding);
    }

    private static void lightRemoval() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());
        OpenCV.showImageGUI(mask);

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        OpenCV.showImageGUI(image.getImageWithoutReflection());
    }

    private static void superpixels() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels lsc = new SuperpixelsLSC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 0.060f);
        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 20);
        Superpixels seeds = new SuperpixelsSEEDS(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 300, 3);

        lsc.createSuperpixels();
        slic.createSuperpixels();
        seeds.createSuperpixels();

        System.out.println("LSC SUPERPIXELS : " + lsc.getSuperpixelsAmount());
        System.out.println("SLIC SUPERPIXELS : " + slic.getSuperpixelsAmount());
        System.out.println("SEDDS SUPERPIXELS : " + seeds.getSuperpixelsAmount());

        OpenCV.showImageGUI(lsc.getContourImage());
        OpenCV.showImageGUI(slic.getContourImage());
        OpenCV.showImageGUI(seeds.getContourImage());
    }

    private static void applyDWT() { 

        Mat gray = OpenCV.matImage2GRAY(image.getImage());

        Wavelet wavelet = new Wavelet(gray, conf.getWaveletLevel());

        wavelet.process();

        OpenCV.showImageGUI(wavelet.getDs());
        OpenCV.showImageGUI(wavelet.getHhNormalized());
        OpenCV.showImageGUI(wavelet.getHlNormalized());
        OpenCV.showImageGUI(wavelet.getLhNormalized());
        wavelet.applyInverseDiscretWaveletTransform();
        OpenCV.showImageGUI(wavelet.getDs());
    }

    private static void haralickDescriptors() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 20);

        slic.createSuperpixels();

        HaralickGlcm haralickGlcm = new HaralickGlcm(gray, slic.getSuperpixelsSegmentation().get(0), GlcmDegreeEnum.DEGREE_45, conf.getHaralickPixelDistance());
        haralickGlcm.process();

        HaralickDescriptors hDescriptors = new HaralickDescriptors(haralickGlcm);

        System.out.println("Energia: " + hDescriptors.energy());
        System.out.println("Entropia: " + hDescriptors.entropy());
        System.out.println("Contraste: " + hDescriptors.contrast());
        System.out.println("Hemogeneidade: " + hDescriptors.homogeneity());
        System.out.println("Correlation: " + hDescriptors.correlation());
    }

    private static void waveletDescriptors() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 20);

        slic.createSuperpixels();

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        Wavelet wavelet = new Wavelet(gray, conf.getWaveletLevel());

        wavelet.process();

        WaveletDescriptors wDescriptors = new WaveletDescriptors(wavelet.getHh(), wavelet.getHhNormalized(), slic.getSuperpixelsSegmentation().get(0));

        System.out.println("Energia: " + wDescriptors.energy());
        System.out.println("Entropia: " + wDescriptors.entropy());
    }

    private static void LBPHDescriptors() {
        
        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 20);

        slic.createSuperpixels();

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        LBPH lbph = new LBPH(gray, slic.getSuperpixelsSegmentation().get(0));
        lbph.process();

        LBPHDescriptors lDescriptors = new LBPHDescriptors(lbph.getValues());

        System.out.println("Média: " + lDescriptors.mean());
        System.out.println("Variância: " + lDescriptors.variance());
        System.out.println("Entropia: " + lDescriptors.entropy());
        System.out.println("Energia: " + lDescriptors.energy());
    }

    private static void colorDescriptors() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), conf.getImageEdgePixelDistance(), 400, 20, 20);

        slic.createSuperpixels();

        Color color = new Color(image.getImage(), slic.getSuperpixelsSegmentation().get(0), 2);
        color.process();

        ColorDescriptors cDescriptors = new ColorDescriptors(color.getChannel1());
        
        System.out.println("Média: " + cDescriptors.mean());
        System.out.println("Variância: " + cDescriptors.variance());
        System.out.println("Assimetria: " + cDescriptors.asymmetry());
        System.out.println("Frequência 1: " + cDescriptors.frequency1());
        System.out.println("Intensidade 1: " + cDescriptors.intensity1());
        System.out.println("Frequência 2: " + cDescriptors.frequency2());
        System.out.println("Intensidade 2: " + cDescriptors.intensity2());
        System.out.println("Dominante 1: " + color.getDominantColor()[0]);
        System.out.println("Dominante 2: " + color.getDominantColor()[1]);
        System.out.println("Dominante 3: " + color.getDominantColor()[2]);
    }
}