package org.example.testfx.Ui.Controllers;

import org.example.testfx.DTO.CompareNExperimentParameters;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.Screens.InitialParamsForComparisonScreen;
import org.example.testfx.Utils.InitParametersForCompareFinishedCallback;

import java.util.function.Consumer;

public class InputInitCompareModeController implements Controller{
    private final ScreenSwitcher switcher;
    private final InitParametersForCompareFinishedCallback dataCollectedCallback;

    public InputInitCompareModeController(ScreenSwitcher switcher, InitParametersForCompareFinishedCallback dataCollectedCallback) {
        this.switcher = switcher;
        this.dataCollectedCallback = dataCollectedCallback;
    }

    @Override
    public void takeControl() {
        Consumer<CompareNExperimentParameters> shortCutProducer = (CompareNExperimentParameters params) -> {
            dataCollectedCallback.callback(params.getExParams().getPlateParameters(), params.getExParams().getSimulationParameters(), params.getHormonicCount());
        };

        InitialParamsForComparisonScreen screen = new InitialParamsForComparisonScreen(dataCollectedCallback, shortCutProducer);
        switcher.show(screen);


    }
}
