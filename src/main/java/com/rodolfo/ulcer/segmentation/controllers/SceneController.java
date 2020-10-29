package com.rodolfo.ulcer.segmentation.controllers;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
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
import com.rodolfo.ulcer.segmentation.factories.ImageStatisticFactory;
import com.rodolfo.ulcer.segmentation.models.Image;
import com.rodolfo.ulcer.segmentation.models.ImageStatistic;
import com.rodolfo.ulcer.segmentation.process.Worker;
import com.rodolfo.ulcer.segmentation.process.WorkerMonitor;
import com.rodolfo.ulcer.segmentation.services.FileService;
import com.rodolfo.ulcer.segmentation.services.impl.FileServiceImpl;
import com.rodolfo.ulcer.segmentation.utils.ImageStatisticUtil;
import com.rodolfo.ulcer.segmentation.utils.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    private RadioButton operationFeature;

    @FXML
    private RadioButton operationARFF;

    @FXML
    private RadioButton operationSegmentation;

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
    private Button btnStatistics;

    @FXML
    private MenuItem btnOpen;

    private Configuration configuration;
    private DirectoryChooser directoryChooser;
    private List<Image> images;
    private String principalDirectory;

    private final String PROPERTIES = "application.properties";
    private final FileService FILE_SERVICE = new FileServiceImpl();

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
            this.configuration.setLabeledResampleImageName(properties.getProperty("image.labeled.resample.name"));
            this.configuration.setImageWithoutReflectionsName(properties.getProperty("image.without.reflections.name"));
            this.configuration.setSuperpixelsLabelImageName(properties.getProperty("image.superpixels.label.name"));
            this.configuration.setSuperpixelsInformationalImageName(properties.getProperty("image.superpixels.informational.name"));
            this.configuration.setSvmClassificationImageName(properties.getProperty("image.svm.classification.name"));
            this.configuration.setSvmOverlappingName(properties.getProperty("image.svm.overlapping"));
            this.configuration.setGrabcutSegmentationBinaryImageName(properties.getProperty("image.grabcut.segmentation.binary.name"));
            this.configuration.setGrabcutSegmentationImageName(properties.getProperty("image.grabcut.segmentation.name"));
            this.configuration.setGrabcutMaskImageName(properties.getProperty("image.grabcut.mask.name"));
            this.configuration.setGrabcutOverlappingName(properties.getProperty("image.grabcut.overlapping"));
            this.configuration.setSkeletonWithBranchsName(properties.getProperty("image.skeleton.with.brnachs"));
            this.configuration.setSkeletonWithoutBranchsName(properties.getProperty("image.skeleton.without.brnachs"));
            this.configuration.setPreparationNormalizationDecimalPlaces(Integer.valueOf(properties.getProperty("data.preparation.normalization.decimal.places")));
            this.configuration.setFeaturesExtractedFile(properties.getProperty("file.features.extracted"));
            this.configuration.setImageStatisticsSvmCsv(properties.getProperty("image.statistics.svm.csv"));
            this.configuration.setImageStatisticsGrabCsv(properties.getProperty("image.statistics.grab.csv"));
            this.configuration.setImageStatisticsVisualization(properties.getProperty("image.statistics.visualization"));
            this.configuration.setDatasourceSEEDSName(properties.getProperty("ml.file.datasource.seeds.name"));
            this.configuration.setDatasourceLSCName(properties.getProperty("ml.file.datasource.lsc.name"));
            this.configuration.setDatasourceSLICName(properties.getProperty("ml.file.datasource.slic.name"));
            this.configuration.setMinMaxSEEDSName(properties.getProperty("ml.file.min.max.seeds.name"));
            this.configuration.setMinMaxLSCName(properties.getProperty("ml.file.min.max.lsc.name"));
            this.configuration.setMinMaxSLICName(properties.getProperty("ml.file.min.max.slic.name"));
            this.configuration.setMlModelSEEDSName(properties.getProperty("ml.file.seeds.model"));
            this.configuration.setMlModelLSCName(properties.getProperty("ml.file.lsc.model"));
            this.configuration.setMlModelSLICName(properties.getProperty("ml.file.slic.model"));
            this.configuration.setSkeletonKernelErodeSize(Integer.valueOf(properties.getProperty("skeleton.kernel.erode.size")));
            this.configuration.setSkeletonAreaErode(Double.valueOf(properties.getProperty("skeleton.area.proportion.erode")));
            this.configuration.setGrabcutNumberOfIterations(Integer.valueOf(properties.getProperty("grabcut.number.iterations")));
            this.configuration.setUserDirectory(properties.getProperty("user.local.directory"));

        } catch (Exception e) {

            log.error("Error : ", e.getCause());
            System.exit(1);
        }

        // new Test(14, configuration);
    }

    @FXML
    public void changeSelectedMethod() {

        this.btnStatistics.disableProperty().unbind();
        this.btnStatistics.setDisable(true);
    }

    @FXML
    public void actionStatistics() {

        final String dirPath = "files/statistics";
        final String statisticsPath = "static files saved in: "
            .concat(this.getStatisticsPath())
            .replaceAll("\\\\", "/")
            .concat(System.lineSeparator())
            .concat(System.lineSeparator());

        log.info("Calculando estátisticas e salvando no caminho : '{}'", statisticsPath);

        this.progressBar.progressProperty().unbind();
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        File csvSvmFile = Util.createFile(dirPath, this.getSelectedMethod(), this.configuration.getImageStatisticsSvmCsv());
        File csvGrabFile = Util.createFile(dirPath, this.getSelectedMethod(), this.configuration.getImageStatisticsGrabCsv());
        File statisticsFile = Util.createFile(dirPath, this.getSelectedMethod(), this.configuration.getImageStatisticsVisualization());

        List<ImageStatistic> svmImageStatistic = (new ImageStatisticFactory(this.images)).createImageStatisticsSvm().getImagesStatistics();
        List<ImageStatistic> grabImageStatistic = (new ImageStatisticFactory(this.images)).createImageStatisticsGrab().getImagesStatistics();

        String statisticsMerged = ImageStatisticUtil.getStatisticsMerged(svmImageStatistic, grabImageStatistic);

        FILE_SERVICE.saveImageStatistics(
            (new ImageStatisticUtil(svmImageStatistic)).statisticsCsvHeader().csvFormatter().toString(), 
            csvSvmFile
        );
        FILE_SERVICE.saveImageStatistics(
            (new ImageStatisticUtil(grabImageStatistic)).statisticsCsvHeader().csvFormatter().toString(), 
            csvGrabFile
        );
        FILE_SERVICE.saveImageStatistics(
            statisticsMerged,
            statisticsFile
        );

        Message.showInformation("Statistics", statisticsPath.concat(statisticsMerged));

        this.progressBar.progressProperty().unbind();
        this.progressBar.setProgress(0.0);
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
            
            if(this.getSelectedOperation() == OperationEnum.CREATE_ARFF_FILE) {

                this.arffExecutor(wMonitor, executor);

            } else {

                this.imageExecutor(wMonitor, executor);
            }
        }
    }

    private String getStatisticsPath() {

        return Paths.get(".").toAbsolutePath().normalize().toString().concat("/files/statistics/").concat(this.getSelectedMethod().name().toLowerCase());
    }

    private void arffExecutor(WorkerMonitor wMonitor, ExecutorService executor) {

        log.info("Realizando criação do arquivo .arff para o método : '{}'", this.getSelectedMethod().name().toLowerCase());

        File dFile = Util.createDatasourceFile("full", this.getSelectedMethod(), this.configuration);
        File minMaxFile = Util.createMinMaxFile(this.getSelectedMethod(), this.configuration);
        
        this.configuration.setDatasource(dFile);
        this.configuration.setMinMax(minMaxFile);

        List<File> arffFiles = this.images.stream()
            .map(image -> image.getDirectory().getFeaturesExtractedPath())
            .collect(Collectors.toList());

        Worker worker = new Worker(this.configuration, arffFiles, this.getSelectedOperation());

        wMonitor.monitor(worker);
        this.setWorkerProperties(wMonitor);

        executor.execute(worker);
    }

    private void imageExecutor(WorkerMonitor wMonitor, ExecutorService executor) {

        log.info("Realizando operações com as imagens utilizando o método : '{}'", this.getSelectedMethod().name().toLowerCase());

        this.configuration.setAmount(Integer.valueOf(this.amount.getText()));
        this.configuration.setIterations(Integer.valueOf(this.iterations.getText()));

        File dFile = Util.createDatasourceFile("reduced", this.getSelectedMethod(), this.configuration);
        File minMaxFile = Util.createMinMaxFile(this.getSelectedMethod(), this.configuration);
        File mlModel = Util.createMlModelFile(this.getSelectedMethod(), this.configuration);
        
        this.configuration.setDatasource(dFile);
        this.configuration.setMinMax(minMaxFile);
        this.configuration.setMlModel(mlModel);

        MethodEnum method = this.getSelectedMethod();

        if(method.name().equals("LSC")) {

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

        this.btnStatistics.disableProperty().unbind();
        this.btnStatistics.setDisable(false);
        this.btnStatistics.disableProperty().bind(wMonitor.getIdle().not());
    }

    private void setWorkerProperties(WorkerMonitor wMonitor) {

        this.imageNumber.textProperty().bind(wMonitor.getDirectory());
        this.progressBar.progressProperty().bind(wMonitor.getProgress());
        this.btnProcess.disableProperty().bind(wMonitor.getIdle().not());
        this.btnOpen.disableProperty().bind(wMonitor.getIdle().not());
        this.methodSEEDS.disableProperty().bind(wMonitor.getIdle().not());
        this.methodLSC.disableProperty().bind(wMonitor.getIdle().not());
        this.methodSLIC.disableProperty().bind(wMonitor.getIdle().not());
        this.operationARFF.disableProperty().bind(wMonitor.getIdle().not());
        this.operationFeature.disableProperty().bind(wMonitor.getIdle().not());
        this.operationSegmentation.disableProperty().bind(wMonitor.getIdle().not());
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
        this.btnStatistics.disableProperty().unbind();
        this.btnStatistics.setDisable(true);

        this.methodSEEDS.disableProperty().unbind();
        this.methodLSC.disableProperty().unbind();
        this.methodSLIC.disableProperty().unbind();
        this.operationARFF.disableProperty().unbind();
        this.operationFeature.disableProperty().unbind();
        this.operationSegmentation.disableProperty().unbind();

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
            
            this.btnProcess.disableProperty().unbind();
            this.btnProcess.setDisable(false);
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
            this.operationARFF.isSelected() ? OperationEnum.CREATE_ARFF_FILE :
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

    private static class Message {

        private Message() {}

        public static void showInformation(String title, String text) {

            Alert alert = new Alert(AlertType.INFORMATION);

            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle(title);
            alert.setHeaderText("Information");
            alert.setResizable(false);

            TextArea textArea = new TextArea(text);

            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            textArea.setStyle("-fx-font-family: monospace");
            textArea.setFont(new Font(12));

            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane content = new GridPane();

            content.setMaxWidth(Double.MAX_VALUE);
            content.add(textArea, 0, 0);

            alert.getDialogPane().setContent(content);
            alert.getDialogPane().setMinHeight(900);
            alert.getDialogPane().setMinWidth(1600);
            alert.showAndWait();
        }
    }
}