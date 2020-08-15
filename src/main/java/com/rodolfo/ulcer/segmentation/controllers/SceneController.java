package com.rodolfo.ulcer.segmentation.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class SceneController implements Initializable {

    @FXML
    private RadioButton methodSEEDS;

    @FXML
    private RadioButton methodSLIC;

    @FXML
    private RadioButton methodLSC;
    
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
    }

    @FXML
    public void actionProcess() {
        // TODO
        System.out.println("ENTROU PROCESS");
    }

    @FXML
    public void actionOpen() {
        // TODO
        System.out.println("ENTROU OPEN");
    }

    @FXML
    public void actionClose() {
        // TODO
        System.out.println("ENTROU CLOSE");
    }
}