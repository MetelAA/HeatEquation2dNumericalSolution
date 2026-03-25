package org.example.testfx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.utils.InitParametersFinishedCallback;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        Controller screenController = new Controller(primaryStage, new InitParametersFinishedCallback() {
            @Override
            public void callback(PlateParameters plateParameters, SimulationParameters simulationParameters) {
                System.out.println("plateParams: " + plateParameters + "  simParams: " + simulationParameters);
            }
        });
        screenController.collectInitialData();
    }



    public static void main(String[] args) {
        launch(args);
    }
}