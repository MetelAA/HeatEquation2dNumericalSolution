package org.example.testfx.Ui.Controllers;

import org.example.testfx.DTO.CompareNExperimentParameters;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.Screens.InitialParamsForComparisonScreen;
import org.example.testfx.Utils.InitParametersForCompareFinishedCallback;

import java.util.function.Consumer;

public class InputInitCompareModeController implements Controller{
    private final ScreenSwitcher switcher;
    private final InitParametersForCompareFinishedCallback callback;

    public InputInitCompareModeController(ScreenSwitcher switcher, InitParametersForCompareFinishedCallback callback) {
        this.switcher = switcher;
        this.callback = callback;
    }

    @Override
    public void takeControl() {
        Consumer<CompareNExperimentParameters> shortCutProducer = (CompareNExperimentParameters params) -> {
            callback.callback(params.getExParams().getPlateParameters(), params.getExParams().getSimulationParameters(), params.getHormonicCount());
        };

        InitialParamsForComparisonScreen screen = new InitialParamsForComparisonScreen(callback, shortCutProducer);
        switcher.show(screen);


    }
}
