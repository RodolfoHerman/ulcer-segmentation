package com.rodolfo.ulcer.segmentation.controllers;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.rodolfo.ulcer.segmentation.MainApp;
import com.rodolfo.ulcer.segmentation.config.Configuration;
import com.rodolfo.ulcer.segmentation.enums.MethodEnum;
import com.rodolfo.ulcer.segmentation.enums.OperationEnum;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.process.Worker;
import com.rodolfo.ulcer.segmentation.process.WorkerMonitor;
import com.rodolfo.ulcer.segmentation.tests.Test;
import com.rodolfo.ulcer.segmentation.utils.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

public class SceneController implements Initializable {

    @FXML
    private RadioButton methodSEEDS;

    @FXML
    private RadioButton methodSLIC;

    @FXML
    private RadioButton methodLSC;

    @FXML
    private CheckBox srRemoval;
    
    @FXML
    private RadioButton operationSegmentation;
    
    @FXML
    private RadioButton operationFeature;

    @FXML
    private TextField iterations;

    @FXML
    private TextField amount;

    @FXML
    private TextField compactness;

    @FXML
    private TextField directory;

    @FXML
    private TextField imageNumber;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button btnProcess;

    @FXML
    private MenuItem btnOpen;

    private Configuration configuration;
    private DirectoryChooser directoryChooser;
    private List<Image> images;
    private String principalDirectory;

    private static final Logger log = LoggerFactory.getLogger(SceneController.class);
    private final String PROPERTIES = "application.properties";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        log.info("Iniciando a aplicação");
        
        this.configuration = new Configuration();

        Properties properties = new Properties();

        try(InputStream input = SceneController.class.getClassLoader().getResourceAsStream(this.PROPERTIES)) {

            properties.load(input);

            this.configuration.setCannyThreshold(Integer.valueOf(properties.getProperty("opencv.canny.threshold")));
            this.configuration.setSobelSize(Integer.valueOf(properties.getProperty("opencv.sobel.size")));
            this.configuration.setKernelFilterSize(Integer.valueOf(properties.getProperty("opencv.kernle.filter.size")));
            this.configuration.setInpaintingNeighbor(Integer.valueOf(properties.getProperty("opencv.inpainting.neighbor")));
            this.configuration.setResampleHeight(Integer.valueOf(properties.getProperty("image.resample.height")));
            this.configuration.setResampleWidth(Integer.valueOf(properties.getProperty("image.resample.width")));
            this.configuration.setSpecularReflectionElemntSize(Integer.valueOf(properties.getProperty("specular.reflection.element.size")));
            this.configuration.setSpecularReflectionThreshold(Float.valueOf(properties.getProperty("specular.reflection.threshold")));
            this.configuration.setWaveletLevel(Integer.valueOf(properties.getProperty("wavelet.level")));
            this.configuration.setImageEdgePixelDistance(Integer.valueOf(properties.getProperty("image.edge.pixel.distance")));
            this.configuration.setHaralickPixelDistance(Integer.valueOf(properties.getProperty("haralick.pixel.distance")));
            this.configuration.setExtension(properties.getProperty("image.extension"));
            this.configuration.setImageName(properties.getProperty("image.name"));
            this.configuration.setLabeledImageName(properties.getProperty("image.labeled.name"));
            this.configuration.setImageWithoutReflectionsName(properties.getProperty("image.without.reflections.name"));
            this.configuration.setSuperpixelsLabelImageName(properties.getProperty("image.superpixels.label.name"));
            this.configuration.setSvmClassificationImageName(properties.getProperty("image.svm.classification.name"));
            this.configuration.setGrabcutSegmentationBinaryImageName(properties.getProperty("image.grabcut.segmentation.binary.name"));
            this.configuration.setGrabcutSegmentationImageName(properties.getProperty("image.grabcut.segmentation.name"));
            this.configuration.setGrabcutMaskImageName(properties.getProperty("image.grabcut.mask.name"));
            this.configuration.setExecutionTimeFile(properties.getProperty("file.execution.time"));
            this.configuration.setFeaturesExtractedFile(properties.getProperty("file.features.extracted"));
            this.configuration.setUserDirectory(properties.getProperty("user.local.directory"));

        } catch (Exception e) {

            log.error("Error : ", e.getCause());
            System.exit(1);
        }

        // new Test(11, configuration);
    }

    @FXML
    public void actionProcess() {

        List<String> errors = this.validateParameters();

        if(!errors.isEmpty()) {

            log.error("Errors : {}", errors.stream().collect(Collectors.joining(" -- ")));

        } else {

            this.reset();

            WorkerMonitor wMonitor = new WorkerMonitor();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            
            this.configuration.setAmount(Integer.valueOf(this.amount.getText()));
            this.configuration.setIterations(Integer.valueOf(this.iterations.getText()));

            MethodEnum method = this.getSelectedMethod();

            if(method.equals(MethodEnum.LSC)) {

                this.configuration.setCompactnessF(Float.valueOf(this.compactness.getText()));

            } else {

                this.configuration.setCompactnessI(Integer.valueOf(this.compactness.getText()));
            }

            List<Worker> workers = this.images.stream()
                .map(image -> new Worker(this.configuration, this.isWithSRRemoval(), image, method, this.getSelectedOperation()))
                .collect(Collectors.toList());

            wMonitor.monitor(workers);
            this.setWorkerProperties(wMonitor);

            workers.stream().forEach(worker -> {

                executor.execute(worker);
            });
        }
    }

    private void setWorkerProperties(WorkerMonitor wMonitor) {

        this.imageNumber.textProperty().bind(wMonitor.getDirectory());
        this.progressBar.progressProperty().bind(wMonitor.getProgress());
        this.btnProcess.disableProperty().bind(wMonitor.getIdle().not());
        this.btnOpen.disableProperty().bind(wMonitor.getIdle().not());
    }

    private void reset() {
        
        // this.bntSalvarArff.disableProperty().unbind();
        this.btnOpen.disableProperty().unbind();
        this.progressBar.progressProperty().unbind();
        this.btnProcess.disableProperty().unbind();
        this.imageNumber.textProperty().unbind();
        this.imageNumber.setText("");
        this.directory.textProperty().unbind();
        this.directory.setText(this.principalDirectory);
        
        // this.bntSalvarArff.setDisable(false);
        this.progressBar.setProgress(0);
        this.btnProcess.setDisable(false);
        this.btnOpen.setDisable(false);
    }

    @FXML
    public void actionOpen() {
        
        this.getDirectoryChooser();

        File file = this.directoryChooser.showDialog(MainApp.mainStage);

        if(file != null && file.exists()) {

            log.info("Abrindo o diretório -> {}", file.getAbsolutePath());

            this.directoryChooser.setInitialDirectory(file);
            this.configuration.setChooseDirectory(file.getAbsolutePath());
            this.images = Util.createImageFiles(file.list(), this.configuration);
            this.principalDirectory = file.getAbsolutePath();

            this.reset();
        }
    }

    @FXML
    public void actionClose() {
        
        Platform.exit();
        System.exit(0);
    }

    private MethodEnum getSelectedMethod() {

        return 
            this.methodSEEDS.isSelected() ? MethodEnum.SEEDS :
            this.methodSLIC.isSelected()  ? MethodEnum.SLIC  :
            MethodEnum.LSC;
    }

    private OperationEnum getSelectedOperation() {

        return
            this.operationFeature.isSelected() ? OperationEnum.FEATURE_EXTRACTION :
            OperationEnum.SEGMENTATION;
    }

    private boolean isWithSRRemoval() {

        return this.srRemoval.isSelected();
    }

    private List<String> validateParameters() {

        List<String> errors = new ArrayList<>();

        if(!StringUtils.isNotBlank(this.iterations.getText()) || !NumberUtils.isCreatable(this.iterations.getText())) {

            errors.add("Necessário informar o número de 'ITERAÇÕES' e o número de 'SUPERPIXELS'. Têm que obdecer o formato numérico");
        }

        if(!StringUtils.isNotBlank(this.amount.getText()) || !NumberUtils.isCreatable(this.amount.getText())) {

            errors.add("Necessário informar a quantidade/tamanho de 'SUPERPIXELS'. Têm que obdecer o formato numérico");
        }

        if(!StringUtils.isNotBlank(this.compactness.getText()) || !NumberUtils.isCreatable(this.compactness.getText())) {

            errors.add("Necessário informar a compacidade. Têm que obdecer o formato numérico");
        }

        return errors;
    }

    private void getDirectoryChooser() {

        if(this.directoryChooser == null) {

            this.directoryChooser = new DirectoryChooser();
            this.directoryChooser.setInitialDirectory(
                new File(System.getProperty(this.configuration.getUserDirectory()))
            );
            this.directoryChooser.setTitle("Open Directory");
        }
    }
}