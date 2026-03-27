package org.example.testfx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.utils.InitParametersFinishedCallback;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        InputInitController screenInputInitController = new InputInitController(primaryStage, new InitParametersFinishedCallback() {
            @Override
            public void callback(PlateParameters plateParameters, SimulationParameters simulationParameters) {
                System.out.println("plateParams: " + plateParameters + "  simParams: " + simulationParameters);
                System.out.println("Запускаю CoreController");
                CoreController coreController = new CoreController(plateParameters, simulationParameters);
                coreController.run();
            }
        });
        screenInputInitController.collectInitialData();
    }



    public static void main(String[] args) {
        launch(args);
    }
}