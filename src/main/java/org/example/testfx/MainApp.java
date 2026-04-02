package org.example.testfx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.utils.InitParametersFinishedCallback;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainApp extends Application {
    private static final Logger log = LogManager.getLogger(MainApp.class);
    @Override
    public void start(Stage primaryStage) {
        if(Constants.workMod.equals("debug")){
            log.info("debug mode selected!");
            ExperimentParameters params;
            try {
                FileReader reader = new FileReader("params.json");
                Gson gson = new Gson();
                params = gson.fromJson(reader, ExperimentParameters.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            setUpCoreController(params);
            return;
        }

        InputInitController screenInputInitController = new InputInitController(primaryStage, new InitParametersFinishedCallback() {
            @Override
            public void callback(PlateParameters plateParameters, SimulationParameters simulationParameters) {
                log.info("Form data collected");
                log.debug("Collected data from forms screen, plateParams: {} simulationParameters: {}", plateParameters.toString(), simulationParameters.toString());

                log.debug("Writing collected form data");
                ExperimentParameters params = new ExperimentParameters(plateParameters, simulationParameters);

                try {
                    FileWriter writer = new FileWriter("params.json");
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    writer.write(gson.toJson(params));
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException("Error when writing collected form data: " + e);
                }
                log.debug("Data wrote!");
                setUpCoreController(params);
            }
        });
        log.info("Show collect data form screens");
        screenInputInitController.collectInitialData();
    }

    private static void setUpCoreController(ExperimentParameters params){
        log.info("Setting up CoreController");
        CoreController coreController = new CoreController(params.getPlateParameters(), params.getSimulationParameters());
        log.info("Transfer control to CoreController");
        coreController.run();
    }


    public static void main(String[] args) {
        launch(args);
    }
}