package com.rodolfo.ulcer.segmentation.preprocessing.superpixels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.models.Point;
import com.rodolfo.ulcer.segmentation.opencv.OpenCV;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bytedeco.javacpp.indexer.IntRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;

import lombok.Data;

@Data
public abstract class Superpixels {

    private static final Logger log = LoggerFactory.getLogger(Superpixels.class);
    
    private Integer imageEdge;
    protected Integer iterations;
    protected Integer amount;
    protected Integer compactenssI;
    protected Float compactenssF;
    protected Image image;
    protected Mat labels = new Mat();
    protected Mat contour = new Mat();
    private Mat contourImage;
    private Mat colorInformativePixels;
    protected Integer superpixelsAmount;
    private Set<Integer> intLabels;
    private Map<Integer, List<Point>> superpixelsSegmentation;
    private Map<Integer, List<Point>> ulcerRegion;
    private Map<Integer, List<Point>> nonUlcerRegion;
    private Map<Integer, List<Point>> excludedRegion;

    public Superpixels(Image image, Integer imageEdge, Integer iterations, Integer amount, Integer compactness) {

        this.image = image;
        this.imageEdge = imageEdge;
        this.iterations = iterations;
        this.amount = amount;
        this.compactenssI = compactness;
    }

    public Superpixels(Image image, Integer imageEdge, Integer iterations, Integer amount, Float compactness) {

        this.image = image;
        this.imageEdge = imageEdge;
        this.iterations = iterations;
        this.amount = amount;
        this.compactenssF = compactness;
    }

    abstract public void createSuperpixels();

    protected void makeContourImage() {

        Mat mask = new Mat();
        contourImage = this.image.getImageWithoutReflection().clone();
        MatVector channels = new MatVector();

        opencv_core.split(contourImage, channels);
        
        Mat channel_1 = channels.get(0);
        Mat channel_2 = channels.get(1);
        Mat channel_3 = channels.get(2);
        
        opencv_core.bitwise_not(OpenCV.dilateByCross(this.contour, 3), mask);

        opencv_core.bitwise_and(channel_1, mask, channel_1);
        opencv_core.bitwise_and(channel_2, mask, channel_2);
        opencv_core.bitwise_and(channel_3, mask, channel_3);

        opencv_core.merge(channels, contourImage);
    }

    protected void makeSuperpixelsSegmentation() {

        this.intLabels = new HashSet<>();
        this.superpixelsSegmentation = new HashMap<>();

        IntRawIndexer index = this.labels.createIndexer();

        for(int row = 0; row < this.labels.rows(); row++) {
            for(int col = 0; col < this.labels.cols(); col++) {

                this.intLabels.add(index.get(row, col));
            }
        }

        this.intLabels.stream().forEach(label -> {

            Mat comp = new Mat(1, 1, opencv_core.CV_32SC1, new Scalar(label.doubleValue()));
            Mat aux  = new Mat(this.labels.size(), opencv_core.CV_8UC1, new Scalar(Double.MAX_VALUE));

            opencv_core.compare(this.labels, comp, aux, opencv_core.CMP_EQ);

            this.superpixelsSegmentation.put(label, this.extractPixelsIndex(aux));
        });

        index.release();
    }

    public void extractRegionLabels() {

        log.info("Criação dos superpixels para os conjuntos de treinamento");

        Mat gray = OpenCV.matImage2GRAY(this.image.getLabeledImage());
        Mat outlineFilled = OpenCV.findLargerOutlineAndFill(gray);
        // Mat largerOutline = OpenCV.dilateByCross(OpenCV.findLargerOutline(outlineFilled), 3);
        Mat largerOutline = OpenCV.findLargerOutline(outlineFilled);

        Set<Integer> labelsUlcer = this.labelsToBeConsidered(outlineFilled);

        opencv_core.bitwise_not(outlineFilled, outlineFilled);

        Set<Integer> labelsNonUlcer = this.labelsToBeConsidered(outlineFilled);
        Set<Integer> labelExcluded = this.labelsToBeConsidered(largerOutline);
        labelsNonUlcer.add(0);

        labelsUlcer.removeAll(labelExcluded);
        labelsNonUlcer.removeAll(labelExcluded);

        this.createColorInformativePixels(labelsUlcer, labelsNonUlcer, labelExcluded);

        this.excludedRegion = this.createRegionsExtracted(labelExcluded);

        int balanceAmount = labelsUlcer.size() - labelsNonUlcer.size();

        if(balanceAmount < 0) {

            List<Integer> remove = this.balanceAmountOfSuperpixels(new ArrayList<>(labelsNonUlcer), Math.abs(balanceAmount));
            labelsNonUlcer.removeAll(remove);
            
        } else {
            
            List<Integer> remove = this.balanceAmountOfSuperpixels(new ArrayList<>(labelsUlcer), Math.abs(balanceAmount));
            labelsUlcer.removeAll(remove);
        }

        this.nonUlcerRegion = this.createRegionsExtracted(labelsNonUlcer);
        this.ulcerRegion = this.createRegionsExtracted(labelsUlcer);
    }

    private void createColorInformativePixels(Set<Integer> labelsUlcer, Set<Integer> labelsNonUlcer, Set<Integer> labelExcluded) {

        this.colorInformativePixels = new Mat(this.labels.size(), opencv_core.CV_8UC1, Scalar.BLACK);
        Mat mask = new Mat();

        final int WHITE = 255;
        final int LIGHT_GRAY = 180;
        final int DARK_GRAY = 90;

        UByteRawIndexer index = this.colorInformativePixels.createIndexer();

        labelsUlcer.stream().forEach(label -> {

            List<Point> points = this.superpixelsSegmentation.get(label);

            points.forEach(point -> {

                int row = point.getRow();
                int col = point.getCol();

                index.put(row, col, WHITE);
            });
        });

        labelsNonUlcer.stream().forEach(label -> {

            List<Point> points = this.superpixelsSegmentation.get(label);

            points.forEach(point -> {

                int row = point.getRow();
                int col = point.getCol();

                index.put(row, col, LIGHT_GRAY);
            });
        });

        labelExcluded.stream().forEach(label -> {

            List<Point> points = this.superpixelsSegmentation.get(label);

            points.forEach(point -> {

                int row = point.getRow();
                int col = point.getCol();

                index.put(row, col, DARK_GRAY);
            });
        });

        index.release();

        opencv_core.bitwise_not(OpenCV.dilateByCross(this.contour, 3), mask);
        opencv_core.bitwise_and(this.colorInformativePixels, mask, this.colorInformativePixels);
    }

    private Set<Integer> labelsToBeConsidered(Mat mask) {

        Mat dst = new Mat();
        Set<Integer> labelsToBeConsidered = new HashSet<>();

        mask.convertTo(mask, this.labels.type());
        opencv_core.bitwise_and(mask, this.labels, dst);

        IntRawIndexer index = dst.createIndexer();

        int total = (int) dst.total();

        for(int x = 0; x < total; x++) {

            int labelValue = index.get(x);
            
            if(labelValue != 0) {

                labelsToBeConsidered.add(labelValue);
            }
        }

        index.release();

        return labelsToBeConsidered;
    }

    private List<Integer> balanceAmountOfSuperpixels(List<Integer> regions, int balanceAmount) {

        List<Integer> labelsToRemove = new ArrayList<>();

        int start = balanceAmount/2;
		int end = balanceAmount/2;
        int size = regions.size();
        
        if((start + end) < balanceAmount) {

			end++;
        }

		for(int indice = 0; indice < start; indice++) {

			labelsToRemove.add(regions.get(indice));
			labelsToRemove.add(regions.get(size - end));

			end--;
		}

		if(end > 0) {

			labelsToRemove.add(regions.get(size - end));
		}

		return labelsToRemove;
    }

    private Map<Integer, List<Point>> createRegionsExtracted(Set<Integer> regions) {

        Map<Integer, List<Point>> temp = new HashMap<>();
        
        regions.stream().forEach(region -> {

            temp.put(region, this.superpixelsSegmentation.get(region));
        });
        
        return temp;
    }

    private List<Point> extractPixelsIndex(Mat aux) {

        UByteRawIndexer index = aux.createIndexer();
        List<Point> points = new ArrayList<>();

        for(int row = this.imageEdge; row < aux.rows() -  this.imageEdge; row++) {
            for(int col = this.imageEdge; col < aux.cols() -  this.imageEdge; col++) {

                if(index.get(row, col) != 0) {

                    points.add(new Point(row, col));
                }
            }
        }

        index.release();

        return points;
    }

}