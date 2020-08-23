package com.rodolfo.ulcer.segmentation.opencv;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.rodolfo.ulcer.segmentation.models.Point;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

public class OpenCV {
    
    public static Mat matImage2HSV(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2HSV);

        return dst;
    }

    public static Mat matImage2LAB(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2Lab);

        return dst;
    }

    public static Mat matImage2LUV(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2Luv);

        return dst;
    }

    public static Mat matImage2GRAY(Mat src) {

        Mat dst = new Mat();
        opencv_imgproc.cvtColor(src, dst, opencv_imgproc.COLOR_BGR2GRAY);

        return dst;
    }

    public static Mat getMatChannel(Mat src, int channel) {

        MatVector matVector = new MatVector();

        opencv_core.split(src, matVector);

        return matVector.get(channel).clone();
    }

    public static List<Double> neighborhoodFloatMat_8(int row, int col, FloatRawIndexer index) {

        //  Vizinhaça 8
        // | 1-X | 2-O | 3-X |
        // | 4-O |     | 5-O |
        // | 6-X | 7-O | 8-X |
        Float pixel1 = index.get(row - 1 , col - 1);
        Float pixel2 = index.get(row - 1 , col);
        Float pixel3 = index.get(row - 1 , col + 1);
        Float pixel4 = index.get(row, col - 1);
        Float pixel5 = index.get(row, col + 1);
        Float pixel6 = index.get(row + 1 , col - 1);
        Float pixel7 = index.get(row + 1 , col);
        Float pixel8 = index.get(row + 1 , col + 1);

        return Arrays.asList(
            pixel1.doubleValue(),
            pixel2.doubleValue(),
            pixel3.doubleValue(),
            pixel4.doubleValue(),
            pixel5.doubleValue(),
            pixel6.doubleValue(),
            pixel7.doubleValue(),
            pixel8.doubleValue()
        );
    }

    public static int[] getMinMaxIntensityFromSuperpixel(Mat src, List<Point> points) {

        UByteRawIndexer index = src.createIndexer();

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        for(Point point: points) {

            int aux = index.get(point.getRow(), point.getCol());

            if(max < aux) {

                max = aux;
            }

            if(min > aux) {

                min = aux;
            }

        }

        index.release();

        return new int[]{min, max};
    }

    public static float[] getMinMaxIntensityFloatMat(Mat src) {
        
        FloatRawIndexer indice = src.createIndexer();

        float max = Integer.MIN_VALUE;
        float min = Integer.MAX_VALUE;

        for(int row = 3; row < src.rows() - 3; row++) {
            for(int col = 3; col < src.cols() - 3; col++){

                float aux = indice.get(row,col);

                if(max < aux) {

                    max = aux;
                }

                if(min > aux) {

                    min = aux;
                }
            }
        }

        indice.release();

        return new float[]{min, max};
    }

    public static Mat dilateByEllipse(Mat src, int elementSize) {
        
        Mat dst = new Mat();

        Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_ELLIPSE, new Size(elementSize, elementSize));
        opencv_imgproc.dilate(src, dst, kernel);

        return dst;
    }

    public static Mat dilateByCross(Mat src, int elementSize) {
        
        Mat dst = new Mat();

        Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_CROSS, new Size(elementSize, elementSize));
        opencv_imgproc.dilate(src, dst, kernel);

        return dst;
    }

    public static Mat findAndFillContoursCV_8UC1(Mat src) {

        Mat dst = new Mat(src.size(), opencv_core.CV_8UC1, Scalar.BLACK);

        MatVector contornos = new MatVector();

        opencv_imgproc.findContours(src, contornos, new Mat(), opencv_imgproc.RETR_TREE, opencv_imgproc.CHAIN_APPROX_SIMPLE);
        opencv_imgproc.fillPoly(dst, contornos, Scalar.WHITE);

        return dst;
    }


    public static Mat createImageWithZeroPadding(Mat src) {

        double log = Math.max(Math.log10(src.rows())/Math.log10(2), Math.log10(src.cols())/Math.log10(2));
        
        if(log <= 2) {

            int dimension = (int)Math.pow(2, 2);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 3) {

            int dimension = (int)Math.pow(2, 3);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 4) {

            int dimension = (int)Math.pow(2, 4);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 5) {

            int dimension = (int)Math.pow(2, 5);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 6) {

            int dimension = (int)Math.pow(2, 6);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 7) {

            int dimension = (int)Math.pow(2, 7);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 8) {

            int dimension = (int)Math.pow(2, 8);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 9) {

            int dimension = (int)Math.pow(2, 9);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 10) {

            int dimension = (int)Math.pow(2, 10);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 11) {

            int dimension = (int)Math.pow(2, 11);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 12) {

            int dimension = (int)Math.pow(2, 12);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 13) {

            int dimension = (int)Math.pow(2, 13);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        if(log <= 14) {

            int dimension = (int)Math.pow(2, 14);
            return OpenCV.createImageWithAndWithoutZeroPadding(src, dimension, dimension);
        }

        return src;
    }

    public static Mat createImageWithAndWithoutZeroPadding(Mat src, int rows, int cols) {

        Mat newMat = Mat.zeros(rows, cols, src.type()).asMat();
        OpenCV.pasteImageOnBackground(newMat, src, 0, src.rows(), 0, src.cols());

        return newMat;
    }

    public static void pasteImageOnBackground(Mat src, Mat croppedImage, int rowIni, int rowEnd, int colIni, int colEnd) {

        croppedImage.copyTo(src.rowRange(rowIni, rowEnd).colRange(colIni, colEnd));
    }

    public static Mat croppMat(Mat src, int rowIni, int colIni, int height, int width) {

        Rect rect = new Rect(colIni, rowIni, width, height);

        return src.apply(rect);
    }

    public static Mat resize(Mat src, int width, int height) {

        Size size = new Size(width, height);
        Mat dst = new Mat();

        opencv_imgproc.resize(src, dst, size, 0, 0, opencv_imgproc.INTER_CUBIC);

        return dst;
    }

    public static void showImageGUI(Mat src) {

        byte[] bytes = new byte[(int)src.elemSize() * src.cols() * src.rows()];
        opencv_imgcodecs.imencode(".jpg", src, bytes);

        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bytes)));
    }

}