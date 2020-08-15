package com.rodolfo.ulcer.segmentation;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {

    public static Stage mainStage;
    private static BorderPane mainPane;

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        
        mainStage.setTitle("Ulcer Segmentation");
        mainStage.setResizable(false);
        mainStage.centerOnScreen();
        
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });

        showScene();
    }

    public void showScene() throws IOException {
        
        mainPane = FXMLLoader.load(getClass().getResource("/views/Scene.fxml"));
        
        Scene scene = new Scene (mainPane);
        scene.getStylesheets().add("/styles/Styles.css");
        mainStage.setScene(scene);
        mainStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
