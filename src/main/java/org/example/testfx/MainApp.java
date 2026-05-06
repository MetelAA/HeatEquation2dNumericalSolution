package org.example.testfx;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Compare.AverageQuadTemperatureDifferenceGraphicBuilder;
import org.example.testfx.Compare.MaxTemperatureDifferenceGraphicBuilder;
import org.example.testfx.DTO.CompareNExperimentParameters;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.Exceptions.ParameterFileParseException;
import org.example.testfx.HeatEquation.AnalyticalSolution.AnalyticalCoreController;
import org.example.testfx.HeatEquation.NumSolution.NumCoreController;
import org.example.testfx.Ui.Controllers.*;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Utils.FileUtils.ReadWriteNumericParamsFromFile;
import org.example.testfx.Utils.FileUtils.TempMapReader;

import java.io.FileNotFoundException;


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
        log.info("");
        InputInitDefaultModeController controller = new InputInitDefaultModeController(switcher, ((plateParams, simParams) -> {
            exParams = new ExperimentParameters(plateParams, simParams);
            try {
                ReadWriteNumericParamsFromFile.writeSimulationParameters(exParams);
            } catch (ParameterFileParseException e) {
                throw new RuntimeException("Error when trying to write experimental parameters, with message: " + e);
            }
            Pair<Double, Double> dxdyChange = setUpNumCoreController(exParams);
            //заменяем dxdy в параметрах эксперимента, тк потом те же параметры поедут в AnalyticalCore (здесь не поедут, но в любом случае, для консистентности)
            exParams.getSimulationParameters().setDx(dxdyChange.getKey());
            exParams.getSimulationParameters().setDy(dxdyChange.getValue());
            showResultsDefaultMode();
        })
        );
        controller.takeControl();
    }

    private void startCompareNumAndAnalyticalMethods(){
        try{
            InputInitCompareModeController controller = new InputInitCompareModeController(switcher, ((plateParams, simParams, hormonicCount) -> {
                exParams = new ExperimentParameters(plateParams, simParams);

                //запишем в файл для шорткатов
                {
                    try {
                        ReadWriteNumericParamsFromFile.writeCompareParameters(new CompareNExperimentParameters(exParams, hormonicCount));
                    } catch (ParameterFileParseException e) {
                        throw new RuntimeException("Error when trying to write data for shortcut in compare mod, with message: " + e);
                    }
                }

                //запускаем контроллер численного решения
                Pair<Double, Double> dxdyChange = setUpNumCoreController(exParams);
                //заменяем dxdy в параметрах эксперимента, тк потом те же параметры поедут в AnalyticalCore
                exParams.getSimulationParameters().setDx(dxdyChange.getKey());
                exParams.getSimulationParameters().setDy(dxdyChange.getValue());
                //запускаем контроллер аналитического решения
                setUpAndRunAnalyticalCoreController(exParams, hormonicCount);
                //запускаем построителей датасета для графиков ошибок
                setUpAndRunDifferenceGraphicsBuilders();

                showRezOfCompareAnalyticalAndNumMethod();
            })
            );
            controller.takeControl();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void setUpAndRunDifferenceGraphicsBuilders() {
        //сначала прочитаем файл с записанными параметрами экспериментов с предыдущих шагов
        ExperimentNMapParameters params;
        try {
            params = TempMapReader.getParams();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error when reading ex params from file, with message: " + e);
        }

        MaxTemperatureDifferenceGraphicBuilder maxDiffGraphicBuilder = new MaxTemperatureDifferenceGraphicBuilder();
        AverageQuadTemperatureDifferenceGraphicBuilder avgQuadDiffGraphicsBuilder = new AverageQuadTemperatureDifferenceGraphicBuilder();
        maxDiffGraphicBuilder.buildGraphic(params);
        avgQuadDiffGraphicsBuilder.buildGraphic(params);
    }


    private void showRezOfCompareAnalyticalAndNumMethod(){
        OutputCompareModController controller = new OutputCompareModController(switcher);
        controller.takeControl();
    }

    private void showResultsDefaultMode(){
        OutputDefaultModeController controller = new OutputDefaultModeController(switcher);
        controller.takeControl();
    }


    private Pair<Double, Double> setUpNumCoreController(ExperimentParameters params){ //возвращает изменённые (не факт что) dx и dy
        log.info("Setting up CoreController");
        NumCoreController numCoreController = new NumCoreController(params.getPlateParameters(), params.getSimulationParameters());
        log.info("Transfer control to CoreController");
        numCoreController.run();
        return new Pair<>(params.getSimulationParameters().getDx(), params.getSimulationParameters().getDy());
    }

    private void setUpAndRunAnalyticalCoreController(ExperimentParameters exParams, int hormonicCount){
        AnalyticalCoreController controller = new AnalyticalCoreController(exParams, hormonicCount);
        controller.run();
    }


    public static void main(String[] args) {
        launch(args);
    }
}