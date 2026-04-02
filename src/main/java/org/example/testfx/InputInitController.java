package org.example.testfx;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.NumeralInitialPlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Ui.screens.*;
import org.example.testfx.utils.InitParametersFinishedCallback;

import java.util.function.Consumer;

public class InputInitController {
    private final Stage stage;
    private PlateParameters initParams;
    private int borderTemperatureConfigureStage = 0;
    private final InitParametersFinishedCallback callback;


    public InputInitController(Stage stage, InitParametersFinishedCallback callback) {
        this.stage = stage;
        this.callback = callback;
    }

    public void collectInitialData(){
        initParams = new PlateParameters();
        showNumeralParamCollectorScreen();
    }

    private void showNumeralParamCollectorScreen() {
        Consumer<NumeralInitialPlateParameters> firstScreenConsumer = res ->{
            initParams.setNumeralParameters(res);
            showBorderTemperatureSelectionTypeCollectorScreen();
        };
        Screen screen = new InitialPlateParametersFormScreen(firstScreenConsumer);
        stage.setScene(new Scene(screen.getView(), 800, 600));
        stage.show();
    }

    private void showBorderTemperatureSelectionTypeCollectorScreen(){
        if (borderTemperatureConfigureStage < 2){
            Consumer<String> secondConsumer = res -> {
                if (res.equals("Bezier")){
                    showBoundaryTemperatureEquationWithBezierCurvesCollectorScreen();
                }else{
                    showBoundaryTemperatureEquationWithSymbolEquationCollectorScreen();
                }
            };
            Screen screen = new SelectBoundaryTemperatureSelectionTypeScreen(secondConsumer, borderTemperatureConfigureStage == 0 ? "верхней" : "нижней");
            stage.setScene(new Scene(screen.getView(), 800, 600));
            stage.show();
        }else{
            showSpeedParameterCollectorScreen();
        }
    }

    private void showBoundaryTemperatureEquationWithBezierCurvesCollectorScreen(){
        Consumer<String> secondScreenConsumer = res -> {
            System.out.println(res);
            if (borderTemperatureConfigureStage == 0){
                initParams.setBoundaryTemperatureEquationTop(res);
                borderTemperatureConfigureStage++;
                showBorderTemperatureSelectionTypeCollectorScreen();
            }else if(borderTemperatureConfigureStage == 1){
                initParams.setBoundaryTemperatureEquationBottom(res);
                borderTemperatureConfigureStage++;
                showBorderTemperatureSelectionTypeCollectorScreen();
            }
        };
        Screen screen = new InitialBorderTemperatureBezierCurveScreen(initParams.getNumeralParameters().width(), Constants.MAX_TEMPERATURE, Constants.minTemperature, secondScreenConsumer);
        stage.setScene(new Scene(screen.getView(), 800, 600));
        stage.show();
    }

    private void showBoundaryTemperatureEquationWithSymbolEquationCollectorScreen(){
        Consumer<String> secondScreenConsumer = res -> {
            if (borderTemperatureConfigureStage == 0){
                initParams.setBoundaryTemperatureEquationTop(res);
                borderTemperatureConfigureStage++;
                showBorderTemperatureSelectionTypeCollectorScreen();
            }else if(borderTemperatureConfigureStage == 1){
                initParams.setBoundaryTemperatureEquationBottom(res);
                borderTemperatureConfigureStage++;
                showBorderTemperatureSelectionTypeCollectorScreen();
            }
        };
        Screen screen = new InitialBorderTemperatureEquationEditor(initParams.getNumeralParameters().width(), Constants.MAX_TEMPERATURE, Constants.minTemperature, secondScreenConsumer);
        stage.setScene(new Scene(screen.getView(), 800, 600));
        stage.show();
    }

    private void showSpeedParameterCollectorScreen(){
        Consumer<SimulationParameters> thirdConsumer = res -> {
            callback.callback(initParams, res);
        };
        Screen screen = new SimulationParameterSelectScreen(thirdConsumer);
        stage.setScene(new Scene(screen.getView(), 800, 600));
        stage.show();
    }
}
