package com.rodolfo.ulcer.segmentation.opencv;

import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.rodolfo.ulcer.segmentation.models.Point;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

public class OpenCV {
    
    /**
     * Realiza o dump da imagem
     * CV_8U   0 - Byte
     * CV_8S   1 - Byte
     * CV_16U  2 - Integer
     * CV_16S  3 - Integer
     * CV_32S  4 - Integer
     * CV_32F  5 - Float
     * CV_64F  6 - Float
     * 
     * @param img
     */
    public static void dump(Mat img) {
        
        StringBuilder data = new StringBuilder(); 

        int depth = img.depth();

        data.append("Dimensão (linha/coluna) : ").append("\t").append(img.rows()).append("/").append(img.cols());
        data.append(System.lineSeparator());
        data.append("Tipo da imagem : ").append("\t").append(img.type());
        data.append(System.lineSeparator());
        data.append("Qtd canais da imagem : ").append("\t").append(img.channels());
        data.append(System.lineSeparator());
        data.append(System.lineSeparator());

        if(depth <= 1) {

            data.append(dump_byte(img));
            
        } else if(depth <= 4) {
            
            data.append(dump_integer(img));
            
        } else {
            
            data.append(dump_float(img));
        }

        System.out.println(data.toString());
    }
    
    /**
     * Realizar o dump da imagem Mat do tipo Byte
     * 
     * @param img
     * @return dados
     */
    private static String dump_byte(Mat img) {
        
        UByteRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }

    /**
     * Realizar o dump da imagem Mat do tipo Integer
     * 
     * @param img
     * @return dados
     */
    private static String dump_integer(Mat img) {
        
        IntRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }
    
    /**
     * Realizar o dump da imagem Mat do tipo Float
     * 
     * @param img
     * @return dados
     */
    private static String dump_float(Mat img) {
        
        FloatRawIndexer indice = img.createIndexer();
        StringBuilder dados = new StringBuilder();

        for(int row = 0; row < img.rows(); row++) {
            for(int col = 0; col < img.cols(); col++) {

                dados.append(indice.get(row, col) + " ");
            }

            dados.append(System.lineSeparator());
        }

        indice.release();

        return dados.toString();
    }
    
    
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

    public static Mat matImage2BGRNorm(Mat src) {

        Mat dst = new Mat();

        src.convertTo(dst, opencv_core.CV_32FC3);

        FloatRawIndexer indice = dst.createIndexer();

        float[] pixel = new float[dst.channels()];

        for (int row = 0; row < dst.rows(); row++) {
            for(int col = 0; col < dst.cols(); col++) {

                indice.get(row, col, pixel);

                float r = (pixel[2]/(pixel[0] + pixel[1] + pixel[2])) * 255f;
                float g = (pixel[1]/(pixel[0] + pixel[1] + pixel[2])) * 255f;
                float b = (pixel[0]/(pixel[0] + pixel[1] + pixel[2])) * 255f;

                indice.put(row, col, new float[]{b,g,r});
            }
        }

        indice.release();

        dst.convertTo(dst, opencv_core.CV_8UC3);

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

    public static int[] getMinMaxIntensityFromSuperpixel(Mat src, List<Point> points, int distance) {

        UByteRawIndexer index = src.createIndexer();

        int[] minMax = {Integer.MAX_VALUE, Integer.MIN_VALUE};

        for(Point point: points) {

            int aux1 = index.get(point.getRow(), point.getCol());
            // 0º
            int aux2 = index.get(point.getRow(), point.getCol() + distance);
            // 45º
            int aux3 = index.get(point.getRow() - distance, point.getCol() + distance);
            // 90º
            int aux4 = index.get(point.getRow() - distance, point.getCol());
            // 135º
            int aux5 = index.get(point.getRow() - distance, point.getCol() - distance);

            OpenCV.getMinMaxVals(minMax, aux1);
            OpenCV.getMinMaxVals(minMax, aux2);
            OpenCV.getMinMaxVals(minMax, aux3);
            OpenCV.getMinMaxVals(minMax, aux4);
            OpenCV.getMinMaxVals(minMax, aux5);
        }

        index.release();

        return minMax;
    }

    private static void getMinMaxVals(int[] minMax, int aux) {

        if(minMax[1] < aux) {

            minMax[1] = aux;
        }

        if(minMax[0] > aux) {

            minMax[0] = aux;
        }
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

    public static Mat erodeByCross(Mat src, int elementSize) {
        
        Mat dst = new Mat();

        Mat kernel = opencv_imgproc.getStructuringElement(opencv_imgproc.MORPH_CROSS, new Size(elementSize, elementSize));
        opencv_imgproc.erode(src, dst, kernel);

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

    public static void showHistogram1DFromMat(Mat src) {

        int histW = 600;
        int histH = 600;
        int binW = (int) Math.round((double) histW / 256.0);

        Mat hist = new Mat();
        Mat histImage = new Mat(histH, histW, opencv_core.CV_8UC1, Scalar.BLACK);

        opencv_imgproc.calcHist(src, 1, new int[]{0}, new Mat(), hist, 1, new int[]{256}, new float[]{0f, 256f}, true, false);

        float[] histData = new float[(int)(hist.total() * hist.channels())];

        FloatRawIndexer index = hist.createIndexer();

        index.get(0, 0, histData);

        index.release();

        for(int x = 1; x < 256; x++) {

            opencv_imgproc.line(histImage, new org.bytedeco.javacpp.opencv_core.Point(binW * (x - 1), histH - Math.round(histData[x - 1])), 
                new org.bytedeco.javacpp.opencv_core.Point(binW * x, histH - Math.round(histData[x])), Scalar.WHITE);
        }

        OpenCV.showImageGUI(histImage);
    }

    public static Mat findLargerOutlineAndFill(Mat src) {

        Mat aux = new Mat(src.size(), opencv_core.CV_8UC1, Scalar.WHITE);
        Mat dst = new Mat(src.size(), opencv_core.CV_8UC1, Scalar.BLACK);

        UByteRawIndexer srcIndex = src.createIndexer();
        UByteRawIndexer auxIndex = aux.createIndexer();

        int total = (int)src.total();

        for(int index = 0; index < total; index++) {

            if(srcIndex.get(index) <= 10) {

                auxIndex.put(index, 0);
            }
        }
        
        srcIndex.release();
        auxIndex.release();

        Mat largerOutline = OpenCV.findLargerContour(aux);

        opencv_imgproc.fillPoly(dst, new MatVector(largerOutline), Scalar.WHITE);

        return dst;
    }

    public static Mat findLargerOutlineAndFill(Mat src, double objectColor, double backgroundColor) { 

        Mat dst = new Mat(src.size(), opencv_core.CV_8UC1, new Scalar(backgroundColor));

        Mat largerOutline = OpenCV.findLargerContour(src);

        opencv_imgproc.fillPoly(dst, new MatVector(largerOutline), new Scalar(objectColor));

        return dst;
    }

    public static Mat findLargerOutlineAndFill(Mat mat1, Mat mat2, double objectColor) { 

        Mat largerOutline = OpenCV.findLargerContour(mat1);

        opencv_imgproc.fillPoly(mat2, new MatVector(largerOutline), new Scalar(objectColor));

        return mat2;
    }

    public static Mat findLargerOutline(Mat src) {

        Mat dst = new Mat(src.size(), opencv_core.CV_8UC1, Scalar.BLACK);
        Mat largerOutline = OpenCV.findLargerContour(src);

        opencv_imgproc.polylines(dst, new MatVector(largerOutline), true, Scalar.WHITE);

        return dst;
    }

    private static Mat findLargerContour(Mat src) {

        MatVector contours = new MatVector();
        Mat largerOutline = null;
        long max = 0L;

        opencv_imgproc.findContours(src, contours, new Mat(), opencv_imgproc.RETR_TREE, opencv_imgproc.CHAIN_APPROX_SIMPLE);

        List<Mat> listOfContours = Arrays.asList(contours.get());

        for(Mat contour: listOfContours) {

            if(max < contour.total()) {

                largerOutline = contour;
                max = contour.total();
            }
        }

        return largerOutline;
    }

    public static void showImageGUI(Mat src) {

        byte[] bytes = new byte[(int)src.elemSize() * src.cols() * src.rows()];
        opencv_imgcodecs.imencode(".jpg", src, bytes);

        JOptionPane.showMessageDialog(null, new JLabel(new ImageIcon(bytes)));
    }

}