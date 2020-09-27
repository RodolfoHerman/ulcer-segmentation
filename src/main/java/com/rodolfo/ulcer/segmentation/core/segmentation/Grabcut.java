package com.rodolfo.ulcer.segmentation.core.segmentation;

import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Grabcut implements Process {

    private Image image;
    private Configuration conf;

    private Mat outlineFilled;
    private Skeletonization skeletonization;
    
    private Mat finalBinarySegmentation;
    private Mat finalUlcerSegmentation;
    private Mat grabCutMask;
    private Mat grabCutHumanMask;

    public Grabcut(Image image, Configuration conf, Mat outlineFilled, Skeletonization skeletonization) {

        this.image = image;
        this.conf = conf;
        this.outlineFilled = outlineFilled.clone();
        this.skeletonization = skeletonization;
        this.finalUlcerSegmentation = new Mat(this.image.getSize(), this.image.getType(), Scalar.WHITE);
    }

    @Override
    public void process() {

        Mat bgdModel = new Mat();
        Mat fgdModel = new Mat();

        this.createMaskRegionsForGrabcut();
        Mat auxMask = this.grabCutMask.clone();

        Mat aux1 = new Mat(auxMask.size(), opencv_core.CV_8UC1);
        Mat aux2 = new Mat(auxMask.size(), opencv_core.CV_8UC1);

        Mat comparator1 = new Mat(1, 1, opencv_core.CV_8U, new Scalar(Double.valueOf(opencv_imgproc.GC_PR_FGD)));
        Mat comparator2 = new Mat(1, 1, opencv_core.CV_8U, new Scalar(Double.valueOf(opencv_imgproc.GC_FGD)));

        opencv_imgproc.grabCut(
            this.image.getImage(), 
            auxMask, 
            new Rect(), 
            bgdModel, 
            fgdModel, 
            this.conf.getGrabcutNumberOfIterations(), 
            opencv_imgproc.GC_INIT_WITH_MASK
        );

        opencv_core.compare(auxMask, comparator1, aux1, opencv_core.CMP_EQ);
        opencv_core.compare(auxMask, comparator2, aux2, opencv_core.CMP_EQ); 

        this.image.getImage().copyTo(this.finalUlcerSegmentation, aux1);
        this.image.getImage().copyTo(this.finalUlcerSegmentation, aux2);
    }

    private void createMaskRegionsForGrabcut() {

        Mat firstMask = OpenCV.findLargerOutlineAndFill(this.outlineFilled, opencv_imgproc.GC_PR_FGD, opencv_imgproc.GC_BGD);
        this.grabCutMask = OpenCV.findLargerOutlineAndFill(this.skeletonization.getSkeletonWithoutBranchs(), firstMask, opencv_imgproc.GC_FGD);
    }

    public void createGrabCutHumanMask() {

        this.grabCutHumanMask = new Mat(this.grabCutMask.size(), opencv_core.CV_8UC1, Scalar.WHITE);

        UByteRawIndexer indexHuman = this.grabCutHumanMask.createIndexer();
        UByteRawIndexer indexMask = this.grabCutMask.createIndexer();

        final int ulcer = 0;
        final int nonUlcer = 110;
        final int probable = 190;

        for(int row = 0; row < this.grabCutMask.rows(); row++) {
            for(int col = 0; col < this.grabCutMask.cols(); col++) {

                if(indexMask.get(row, col) == opencv_imgproc.GC_PR_FGD) {

                    indexHuman.put(row, col, probable);
                }

                if(indexMask.get(row, col) == opencv_imgproc.GC_FGD) {

                    indexHuman.put(row, col, ulcer);
                }

                if(indexMask.get(row, col) == opencv_imgproc.GC_BGD) {

                    indexHuman.put(row, col, nonUlcer);
                }
            }
        }

        indexHuman.release();
        indexMask.release();
    }

    public void createFinalBinarySegmentation() {

        this.finalBinarySegmentation = new Mat();

        Mat aux1 = Mat.zeros(this.finalUlcerSegmentation.size(), opencv_core.CV_8U).asMat(); //new Mat(this.finalUlcerSegmentation.size(), opencv_core.CV_8UC1);
        Mat comparator1 = new Mat(1, 1, opencv_core.CV_8U, Scalar.WHITE);

        opencv_core.compare(this.finalUlcerSegmentation, comparator1, aux1, opencv_core.CMP_EQ);

        aux1 = OpenCV.erodeByCross(aux1, 3);
        aux1 = OpenCV.dilateByCross(aux1, 3);

        aux1.convertTo(this.finalBinarySegmentation, opencv_core.CV_8UC1);
    }
}
