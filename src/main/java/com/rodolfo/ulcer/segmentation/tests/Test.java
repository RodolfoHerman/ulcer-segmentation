package com.rodolfo.ulcer.segmentation.tests;

import java.io.File;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.descriptors.texture.models.Wavelet;
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

        Superpixels lsc = new SuperpixelsLSC(image.getImageWithoutReflection(), 400, 20, 0.060f);
        Superpixels slic = new SuperpixelsSLIC(image.getImageWithoutReflection(), 400, 20, 20);
        Superpixels seeds = new SuperpixelsSEEDS(image.getImageWithoutReflection(), 400, 300, 3);

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

}