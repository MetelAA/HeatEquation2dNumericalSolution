package org.example.testfx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.HeatEquation.NumSolution.NumCoreController;
import org.example.testfx.Ui.Controllers.InputInitCompareModeController;
import org.example.testfx.Ui.Controllers.InputInitDefaultModeController;
import org.example.testfx.Ui.Controllers.ModeSelectionController;
import org.example.testfx.Ui.Controllers.OutputDefaultModeController;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.utils.ReadWriteNumericParamsFromFile;

import java.io.IOException;


public class MainApp extends Application {
    private static final Logger log = LogManager.getLogger(MainApp.class);
    private ScreenSwitcher switcher;
    private Stage primaryStage;
    private ExperimentParameters exParams;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        switcher = new ScreenSwitcher(primaryStage);
        selectProgramMode();
    }

    private void selectProgramMode(){
        ModeSelectionController controller = new ModeSelectionController(switcher, this::startDefaultMode, this::startCompareNumAndAnalyticalMethods);
        controller.takeControl();
        primaryStage.show();
    }

    private void startDefaultMode(){
        InputInitDefaultModeController controller = new InputInitDefaultModeController(switcher, ((plateParams, simParams) -> {
            exParams = new ExperimentParameters(plateParams, simParams);
            try {
                ReadWriteNumericParamsFromFile.writeSimulationParameters(exParams);
            } catch (IOException e) {
                throw new RuntimeException("Error when trying to write experimental parameters, with message: " + e);
            }
            setUpCoreController(exParams);
            showResultsDefaultMode();
        })
        );
        controller.takeControl();
    }

    private void startCompareNumAndAnalyticalMethods(){
        InputInitCompareModeController controller = new InputInitCompareModeController(switcher, ((plateParams, simParams) -> {

        })
        );
        controller.takeControl();
    }

    private void showResultsDefaultMode(){
        OutputDefaultModeController controller = new OutputDefaultModeController(switcher);
        controller.takeControl();
    }

    private void setUpCoreController(ExperimentParameters params){
        log.info("Setting up CoreController");
        NumCoreController numCoreController = new NumCoreController(params.getPlateParameters(), params.getSimulationParameters());
        log.info("Transfer control to CoreController");
        numCoreController.run();
    }


    public static void main(String[] args) {
        launch(args);
    }
}