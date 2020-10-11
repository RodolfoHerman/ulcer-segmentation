package com.rodolfo.ulcer.segmentation.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.core.descriptors.Descriptor;
import com.rodolfo.ulcer.segmentation.core.descriptors.DescriptorFactory;
import com.rodolfo.ulcer.segmentation.core.descriptors.color.ColorDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.color.models.Color;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.HaralickDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.LBPHDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.WaveletDescriptors;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.enums.GlcmDegreeEnum;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.HaralickGlcm;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.LBPH;
import com.rodolfo.ulcer.segmentation.core.descriptors.texture.models.Wavelet;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightMaskDetection;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.LightRemoval;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl.Inpainting;
import com.rodolfo.ulcer.segmentation.core.preprocessing.light_removal.impl.SpecularReflectionDetection;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.Superpixels;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsLSC;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsSEEDS;
import com.rodolfo.ulcer.segmentation.core.preprocessing.superpixels.SuperpixelsSLIC;
import com.rodolfo.ulcer.segmentation.core.segmentation.Skeletonization;
import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.models.Directory;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;
import com.rodolfo.ulcer.segmentation.services.FileService;
import com.rodolfo.ulcer.segmentation.services.ImageService;
import com.rodolfo.ulcer.segmentation.services.impl.FileServiceImpl;
import com.rodolfo.ulcer.segmentation.services.impl.ImageServiceImpl;
import com.rodolfo.ulcer.segmentation.utils.Util;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_photo;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import weka.core.converters.ConverterUtils.DataSource;

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
    private static final int LARGER_OUTLINE = 9;
    private static final int CREATE_LABELS_FROM_LABELED_IMAGE = 10;
    private static final int DESCRIPTORS_EXTRACTION = 11;
    private static final int MIN_MAX_CONTENT = 12;
    private static final int DATA_SOURCE_CONTENT = 13;
    private static final int SKELETON = 14;

    public Test(int testnumber, Configuration configuration) {

        conf = configuration;
        Directory directory = new Directory();
        ImageService imageService = new ImageServiceImpl();

        File imagePath = new File(Test.class.getClassLoader().getResource("tests/img_test.jpg").getFile());
        File labeledImagePath = new File(Test.class.getClassLoader().getResource("tests/img_test_00.jpg").getFile());

        directory.setImagePath(imagePath);
        directory.setLabeledImagePath(labeledImagePath);

        image = new Image(directory, conf.getResampleWidth(), conf.getResampleHeight());
        imageService.open(image);

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

            case LARGER_OUTLINE:

                Test.findLargerOutlineAndFill();

            break;

            case CREATE_LABELS_FROM_LABELED_IMAGE:

                Test.createSuperpixelsLabelsFromLabeledImage();

            break;

            case DESCRIPTORS_EXTRACTION:

                Test.descriptorsExtractionAndLabel();

            break;

            case MIN_MAX_CONTENT:

                Test.readMinMaxContent();

            break;

            case DATA_SOURCE_CONTENT:

                Test.readDataSourceContent();

            break;

            case SKELETON:

                Test.testSkeletonMethods();

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

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(),
                conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());
        OpenCV.showImageGUI(mask);

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        OpenCV.showImageGUI(image.getImageWithoutReflection());
    }

    private static void superpixels() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(),
                conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels lsc = new SuperpixelsLSC(image, conf.getImageEdgePixelDistance(), 400, 20, 0.060f);
        Superpixels slic = new SuperpixelsSLIC(image, conf.getImageEdgePixelDistance(), 400, 20, 20);
        Superpixels seeds = new SuperpixelsSEEDS(image, conf.getImageEdgePixelDistance(), 400, 300, 3);

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

        Superpixels superpixels = Test.createSuperpixel();

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        HaralickGlcm haralickGlcm = new HaralickGlcm(gray, superpixels.getSuperpixelsSegmentation().get(0),
                GlcmDegreeEnum.DEGREE_45, conf.getHaralickPixelDistance());
        haralickGlcm.process();

        HaralickDescriptors hDescriptors = new HaralickDescriptors(haralickGlcm.getGlcm());

        System.out.println("Energia: " + hDescriptors.energy());
        System.out.println("Entropia: " + hDescriptors.entropy());
        System.out.println("Contraste: " + hDescriptors.contrast());
        System.out.println("Hemogeneidade: " + hDescriptors.homogeneity());
        System.out.println("Correlation: " + hDescriptors.correlation());
    }

    private static void waveletDescriptors() {

        Superpixels superpixels = Test.createSuperpixel();

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        Wavelet wavelet = new Wavelet(gray, conf.getWaveletLevel());

        wavelet.process();

        WaveletDescriptors wDescriptors = new WaveletDescriptors(wavelet.getHh(), wavelet.getHhNormalized(),
                superpixels.getSuperpixelsSegmentation().get(0));

        System.out.println("Energia: " + wDescriptors.energy());
        System.out.println("Entropia: " + wDescriptors.entropy());
    }

    private static void LBPHDescriptors() {

        Superpixels superpixels = Test.createSuperpixel();

        Mat gray = OpenCV.matImage2GRAY(image.getImageWithoutReflection());

        LBPH lbph = new LBPH(gray, superpixels.getSuperpixelsSegmentation().get(0));
        lbph.process();

        LBPHDescriptors lDescriptors = new LBPHDescriptors(lbph.getValues());

        System.out.println("Média: " + lDescriptors.mean());
        System.out.println("Variância: " + lDescriptors.variance());
        System.out.println("Entropia: " + lDescriptors.entropy());
        System.out.println("Energia: " + lDescriptors.energy());
    }

    private static void colorDescriptors() {

        Superpixels superpixels = Test.createSuperpixel();

        Color color = new Color(image.getImage(), superpixels.getSuperpixelsSegmentation().get(0), 2);
        color.process();

        ColorDescriptors cDescriptors = new ColorDescriptors(color.getChannel1());

        System.out.println("Média: " + cDescriptors.mean());
        System.out.println("Variância: " + cDescriptors.variance());
        System.out.println("Assimetria: " + cDescriptors.asymmetry());
        System.out.println("Frequência 1: " + cDescriptors.frequency1());
        System.out.println("Intensidade 1: " + cDescriptors.intensity1());
        System.out.println("Frequência 2: " + cDescriptors.frequency2());
        System.out.println("Intensidade 2: " + cDescriptors.intensity2());
        System.out.println("Dominante 1: " + color.getCentroid()[0]);
        System.out.println("Dominante 2: " + color.getCentroid()[1]);
        System.out.println("Dominante 3: " + color.getCentroid()[2]);
    }

    private static void findLargerOutlineAndFill() {

        Mat gray = OpenCV.matImage2GRAY(image.getLabeledImage());

        OpenCV.showImageGUI(gray);

        Mat outlineFilled = OpenCV.findLargerOutlineAndFill(gray);

        OpenCV.showImageGUI(outlineFilled);
        OpenCV.showImageGUI(OpenCV.findLargerOutline(outlineFilled));
    }

    private static void createSuperpixelsLabelsFromLabeledImage() {

        Superpixels superpixels = Test.createSuperpixel();

        superpixels.extractRegionLabels();
        OpenCV.showImageGUI(superpixels.getColorInformativePixels());
    }

    private static void descriptorsExtractionAndLabel() {

        List<Descriptor> nonUlcerDescriptors = new ArrayList<>();
        List<Descriptor> ulcerDescriptors = new ArrayList<>();

        Superpixels superpixels = Test.createSuperpixel();

        superpixels.createSuperpixels();
        superpixels.extractRegionLabels();

        List<DescriptorFactory> dFactoriesNonUlcers = new ArrayList<>();

        superpixels.getNonUlcerRegion().forEach((key, points) -> {

            dFactoriesNonUlcers.add(new DescriptorFactory(image, conf, Util.getDescriptorsNames(), points, key, "NON_ULCER"));
        });

        dFactoriesNonUlcers.stream().forEach(factory -> factory.processColor());
        dFactoriesNonUlcers.stream().forEach(factory -> factory.processHaralick());
        dFactoriesNonUlcers.stream().forEach(factory -> factory.processVariationHaralick());
        dFactoriesNonUlcers.stream().forEach(factory -> factory.processLBPH());
        dFactoriesNonUlcers.stream().forEach(factory -> factory.processWavelet());


        List<DescriptorFactory> dFactoriesUlcers = new ArrayList<>();

        superpixels.getUlcerRegion().forEach((key, points) -> {

            dFactoriesUlcers.add(new DescriptorFactory(image, conf, Util.getDescriptorsNames(), points, key, "ULCER"));
        });

        dFactoriesUlcers.stream().forEach(factory -> factory.processColor());
        dFactoriesUlcers.stream().forEach(factory -> factory.processHaralick());
        dFactoriesUlcers.stream().forEach(factory -> factory.processVariationHaralick());
        dFactoriesUlcers.stream().forEach(factory -> factory.processLBPH());
        dFactoriesUlcers.stream().forEach(factory -> factory.processWavelet());


        dFactoriesUlcers.stream().forEach(
            factory -> ulcerDescriptors.add(new Descriptor(factory.getUlcerClass(), factory.getDescriptors()))
        );

        dFactoriesNonUlcers.stream().forEach(
            factory -> nonUlcerDescriptors.add(new Descriptor(factory.getUlcerClass(), factory.getDescriptors()))
        );

        System.out.println();
    }

    private static void readMinMaxContent() {

        FileService fService = new FileServiceImpl();

        File minMaxFile = Util.createMinMaxFile(MethodEnum.SEEDS, conf);
        conf.setMinMax(minMaxFile);

        System.out.println(fService.openMinMaxDescriptors(conf.getMinMax()));
    }

    private static void readDataSourceContent() {

        FileService fService = new FileServiceImpl();

        File dataSourceFile = Util.createDatasourceFile("reduced", MethodEnum.SEEDS, conf);
        conf.setDatasource(dataSourceFile);

        DataSource dataSource = fService.openDataSoruce(conf.getDatasource());

        try {

            System.out.println(Util.getListOfDescriptorsNamesFromDataSource(dataSource));

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void testSkeletonMethods() {

        Directory directory = new Directory();
        ImageService imageService = new ImageServiceImpl();

        File imagePath = new File(Test.class.getClassLoader().getResource("tests/skeleton.jpg").getFile());

        directory.setImagePath(imagePath);

        image = new Image(directory, conf.getResampleWidth(), conf.getResampleHeight());
        imageService.open(image);

        image.setImage(OpenCV.getMatChannel(image.getImage(), 1));

        skeletonNormalizer(image.getImage());

        Skeletonization skeletonization = new Skeletonization(conf, image.getImage());
        skeletonization.process();

        OpenCV.showImageGUI(skeletonization.getSkeletonView());
    }

    private static Superpixels createSuperpixel() {

        LightMaskDetection lDetection = new SpecularReflectionDetection(conf.getSpecularReflectionElemntSize(), conf.getSpecularReflectionThreshold());

        Mat mask = lDetection.lightMask(image.getImage());

        LightRemoval lRemoval = new Inpainting(mask, opencv_photo.INPAINT_TELEA, conf.getInpaintingNeighbor());
        lRemoval.lightRemoval(image, conf.getKernelFilterSize());

        Superpixels slic = new SuperpixelsSLIC(image, conf.getImageEdgePixelDistance(), 400, 20, 20);

        slic.createSuperpixels();

        return slic;
    }

    private static void skeletonNormalizer(Mat img) {

        UByteRawIndexer index = img.createIndexer();

        int total = img.rows() * img.cols();

        for(int pointer = 0; pointer < total; pointer++) {

            if(index.get(pointer) < 200) {

                index.put(pointer, 0);

            } else {

                index.put(pointer, 255);
            }
        }

        index.release();
    }
}