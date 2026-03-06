package org.example.testfx;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.InitialPlateParameters;
import org.example.testfx.DTO.NumeralInitialPlateParameters;
import org.example.testfx.UiSlop.Screen;
import org.example.testfx.UiSlop.screens.*;

import java.util.function.Consumer;

public class Controller {
    private final Stage stage;
    private InitialPlateParameters initParams;
    private int borderTemperatureConfigureStage = 0;
    private final Consumer<InitialPlateParameters> callback;


    public Controller(Stage stage, Consumer<InitialPlateParameters> callback) {
        this.stage = stage;
        this.callback = callback;
    }

    public void collectInitialData(){
        initParams = new InitialPlateParameters();
        showNumeralParamCollectorScreen();
    }

    private void showNumeralParamCollectorScreen() {
        Consumer<NumeralInitialPlateParameters> firstScreenConsumer = res ->{
            initParams.setNumeralParameters(res);
            showBorderTemperatureSelectionTypeCollectorScreen();
        };
        Screen screen = new InitialParametersFormScreen(firstScreenConsumer);
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
            System.out.println("equation res - " + res);
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
        Consumer<Double> thirdConsumer = res -> {
            initParams.setModelSpeed(res);
            callback.accept(initParams);
        };
        Screen screen = new SpeedSelectScreen(thirdConsumer);
        stage.setScene(new Scene(screen.getView(), 800, 600));
        stage.show();
    }
}
