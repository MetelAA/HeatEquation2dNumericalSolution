package org.example.testfx.UiSlop;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.testfx.Controller;
import org.example.testfx.DTO.InitialPlateParameters;

import java.util.function.Consumer;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Controller screenController = new Controller(primaryStage, new Consumer<InitialPlateParameters>() {
            @Override
            public void accept(InitialPlateParameters initialPlateParameters) {
                System.out.println(initialPlateParameters.toString());
            }
        });
        screenController.collectInitialData();
    }



    public static void main(String[] args) {
        launch(args);
    }
}