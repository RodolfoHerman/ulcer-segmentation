package com.rodolfo.ulcer.segmentation.core.descriptors.texture.models;

import com.rodolfo.ulcer.segmentation.core.descriptors.Process;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

import lombok.Data;

/**
 * Implementation: adapted from https://www.jeejava.com/haar-wavelet-transform-using-java/
 */
@Data
public class Wavelet implements Process {

    private Mat ds;
    
    private Mat lh;
    private Mat hh;
    private Mat hl;

    private Mat lhNormalized;
    private Mat hhNormalized;
    private Mat hlNormalized;

    private Mat img;
    private Size size;
    private int cycles;

    public Wavelet(Mat img, int cycles) {

        this.img = img;
        this.size = img.size();
        this.cycles = cycles;

        this.lhNormalized = new Mat();
        this.hhNormalized = new Mat();
        this.hlNormalized = new Mat();
    }

    @Override
    public void process() {

        this.applyDiscretWaveletTransform();

        int div = this.divImage(this.ds.rows());

        Mat tempLh = OpenCV.resize(OpenCV.croppMat(this.ds, div, div, div, div), this.ds.rows(), this.ds.cols());
        Mat tempHh = OpenCV.resize(OpenCV.croppMat(this.ds, div, div, div, div), this.ds.rows(), this.ds.cols());
        Mat tempHl = OpenCV.resize(OpenCV.croppMat(this.ds, 0, div, div, div), this.ds.rows(), this.ds.cols());

        Float minIntensityLh = Math.abs(OpenCV.getMinMaxIntensityFloatMat(tempLh)[0]);
        Float minIntensityHh = Math.abs(OpenCV.getMinMaxIntensityFloatMat(tempHh)[0]);
        Float minIntensityHl = Math.abs(OpenCV.getMinMaxIntensityFloatMat(tempHl)[0]);

        this.lh = OpenCV.croppMat(tempLh, 0, 0, this.size.height(), this.size.width());
        this.hh = OpenCV.croppMat(tempHh, 0, 0, this.size.height(), this.size.width());
        this.hl = OpenCV.croppMat(tempHl, 0, 0, this.size.height(), this.size.width());

        Mat sumLh = new Mat(this.size, opencv_core.CV_32F, new Scalar(minIntensityLh.doubleValue()));
        Mat sumHh = new Mat(this.size, opencv_core.CV_32F, new Scalar(minIntensityHh.doubleValue()));
        Mat sumHl = new Mat(this.size, opencv_core.CV_32F, new Scalar(minIntensityHl.doubleValue()));

        opencv_core.add(this.lh, sumLh, this.lhNormalized);
        opencv_core.add(this.hh, sumHh, this.hhNormalized);
        opencv_core.add(this.hl, sumHl, this.hlNormalized);
    }

    private void applyDiscretWaveletTransform() {

        this.ds = OpenCV.createImageWithZeroPadding(this.img);

        this.ds.convertTo(ds, opencv_core.CV_32F);

        int w = this.ds.cols();
        int h = this.ds.rows();

        if(this.isCyclesAllowed(w) && this.img.channels() == 1) {

            Mat temp = Mat.zeros(this.ds.size(), opencv_core.CV_32F).asMat();

            FloatRawIndexer dsIndex   = this.ds.createIndexer();
            FloatRawIndexer tempIndex = temp.createIndexer();

            for(int ciclo = 0; ciclo < this.cycles; ciclo++) {

                w /= 2;

                for(int x = 0; x < h; x++) {
                    for(int y = 0; y < w; y++) {

                        float a = dsIndex.get(x, 2 * y);
                        float b = dsIndex.get(x, 2 * y + 1);

                        float soma = a + b;
                        float subt = a - b;

                        float mediaSoma = soma/2;
                        float mediaSubt = subt/2;

                        tempIndex.put(x, y, mediaSoma);
                        tempIndex.put(x, y + w, mediaSubt);
                    }
                }

                for(int x = 0; x < h; x++) {
                    for(int y = 0; y < w; y++) {

                        dsIndex.put(x, y, tempIndex.get(x, y));
                        dsIndex.put(x, y + w, tempIndex.get(x, y + w));
                    }
                }

                h /= 2;

                for(int x = 0; x < w*2; x++) {
                    for(int y = 0; y < h; y++) {

                        float a = dsIndex.get(2 * y, x);
                        float b = dsIndex.get(2 * y + 1, x);

                        float soma = a + b;
                        float subt = a - b;

                        float mediaSoma = soma/2;
                        float mediaSubt = subt/2;

                        tempIndex.put(y, x, mediaSoma);
                        tempIndex.put(y + h, x, mediaSubt);
                    }
                }

                for(int x = 0; x < w*2; x++) {
                    for(int y = 0; y < h; y++) {

                        dsIndex.put(y, x, tempIndex.get(y, x));
                        dsIndex.put(y + h, x, tempIndex.get(y + h, x));
                    }
                }
            }

            dsIndex.release();
            tempIndex.release();
        }
    }

    public void applyInverseDiscretWaveletTransform() {

        if(this.ds != null) {

            this.ds  = OpenCV.createImageWithZeroPadding(this.ds);
            ds.convertTo(ds, opencv_core.CV_32F);

            int w = ds.cols();
            int h = ds.rows();

            Mat temp = Mat.zeros(ds.size(), opencv_core.CV_32F).asMat();

            FloatRawIndexer indiceDs   = ds.createIndexer();
            FloatRawIndexer indiceTemp = temp.createIndexer();

            int hh = h / (int) Math.pow(2, this.cycles);
            int ww = w / (int) Math.pow(2, this.cycles);

            for(int ciclo = this.cycles; ciclo > 0; ciclo--) {

                for(int x = 0; x < ww*2; x++){
                    for(int y = 0; y < hh; y++) {

                        float a = indiceDs.get(y, x);
                        float b = indiceDs.get(y + hh, x);

                        float soma = a + b;
                        float subt = a - b;

                        indiceTemp.put(2 * y, x, soma);
                        indiceTemp.put(2 * y + 1, x, subt);
                    }
                }

                for (int x = 0; x < ww*2; x++) {
                    for (int y = 0; y < hh; y++) {
                        
                        indiceDs.put(2 * y, x, indiceTemp.get(2 * y, x));
                        indiceDs.put(2 * y + 1, x, indiceTemp.get(2 * y + 1, x));
                    }
                }

                hh *= 2;

                for(int x = 0; x < hh; x++){
                    for(int y = 0; y < ww; y++) {

                        float a = indiceDs.get(x, y);
                        float b = indiceDs.get(x, y + ww);

                        float soma = a + b;
                        float subt = a - b;

                        indiceTemp.put(x, 2 * y, soma);
                        indiceTemp.put(x, 2 * y + 1, subt);
                    }
                }

                for (int x = 0; x < hh; x++) {
                    for (int y = 0; y < ww; y++) {
                        
                        indiceDs.put(x, 2 * y, indiceTemp.get(x, 2 * y));
                        indiceDs.put(x, 2 * y + 1, indiceTemp.get(x, 2 * y + 1));
                    }
                }

                ww *= 2;
            }

            indiceDs.release();
            indiceTemp.release();

            this.ds = OpenCV.croppMat(this.ds, 0, 0, this.size.height(), this.size.width());
        }
    }
    
    private boolean isCyclesAllowed(int size) {

        int temp = 0;

        while(size > 1) {

            temp++;
            size/= 2;
        }

        return this.cycles <= temp;
    }

    private int divImage(int height) {

        if(this.cycles == 0) {

            return (int)(height/Math.pow(2,1));
        }

        return (int)(height/Math.pow(2, this.cycles));
    }

}