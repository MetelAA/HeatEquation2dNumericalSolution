package org.example.testfx.Ui.Controllers;

import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.NumeralInitialPlateParameters;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.screens.*;
import org.example.testfx.utils.InitParametersFinishedCallback;

import java.util.function.Consumer;


public class InputInitDefaultModeController implements Controller{
    private final ScreenSwitcher screenSwitcher;
    private final InitParametersFinishedCallback callback;

    private  PlateParameters plateParams;
    private SimulationParameters simParams;

    private int step = 0;
    private String pendingEdgeType;

    public InputInitDefaultModeController(ScreenSwitcher screenSwitcher, InitParametersFinishedCallback callback) {
        this.screenSwitcher = screenSwitcher;
        this.callback = callback;

        plateParams = new PlateParameters();
    }

    @Override
    public void takeControl() {
        showCurrentStep();
    }

    private void showCurrentStep(){
        switch (step) {
            case 0 -> showNumeralParamCollectorScreen();
            case 1 -> showBoundaryTypeScreen("верхней");
            case 2 -> showBoundaryTypeScreen("нижней");
            case 3 -> showSimulationScreen();
            default -> callback.callback(plateParams, simParams); // данные собраны, вызываем внешний callback
        }
    }

    private void showNumeralParamCollectorScreen() {
        Consumer<NumeralInitialPlateParameters> firstScreenConsumer = (res ->{
            plateParams.setNumeralParameters(res);
            step = 1;
            showCurrentStep();
        });
        Consumer<ExperimentParameters> shortCutLoad = (exParams -> {
            callback.callback(exParams.getPlateParameters(), exParams.getSimulationParameters());
        });
        Screen screen = new InitialPlateParametersFormScreen(firstScreenConsumer, shortCutLoad);
        screenSwitcher.show(screen);
    }

    private void showBoundaryTypeScreen(String edgeType) {
        pendingEdgeType = edgeType;
        SelectBoundaryTemperatureSelectionTypeScreen screen = new SelectBoundaryTemperatureSelectionTypeScreen((type) -> {
            if ("Bezier".equals(type))
                showBezierEditor();
            else
                showEquationEditor();
        }, edgeType);
        screenSwitcher.show(screen);
    }

    private void showBezierEditor() {
        InitialBorderTemperatureBezierCurveScreen screen = new InitialBorderTemperatureBezierCurveScreen(
                plateParams.getNumeralParameters().width(),
                Constants.MAX_TEMPERATURE, Constants.MIN_TEMPERATURE,
                this::onBoundaryEquation
        );
        screenSwitcher.show(screen);
    }

    private void showEquationEditor() {
        InitialBorderTemperatureEquationEditor screen = new InitialBorderTemperatureEquationEditor(
                plateParams.getNumeralParameters().width(),
                Constants.MAX_TEMPERATURE, Constants.MIN_TEMPERATURE,
                this::onBoundaryEquation
        );
        screenSwitcher.show(screen);
    }

    private void onBoundaryEquation(String equation) {
        if ("верхней".equals(pendingEdgeType))
            plateParams.setBoundaryTemperatureEquationUp(equation);
        else
            plateParams.setBoundaryTemperatureEquationBottom(equation);
        step++;
        showCurrentStep();
    }

    private void showSimulationScreen() {
        SimulationParameterSelectScreen screen = new SimulationParameterSelectScreen(params -> {
            simParams = params;
            step = 4;
            showCurrentStep();
        });
        screenSwitcher.show(screen);
    }


}
