package org.example.testfx.Ui.Controllers;

import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.screens.InitialParamsForComparisonScreen;
import org.example.testfx.utils.InitParametersFinishedCallback;
import org.example.testfx.utils.InitParametersForCompareFinishedCallback;

public class InputInitCompareModeController implements Controller{
    private final ScreenSwitcher switcher;
    private final InitParametersForCompareFinishedCallback callback;

    public InputInitCompareModeController(ScreenSwitcher switcher, InitParametersForCompareFinishedCallback callback) {
        this.switcher = switcher;
        this.callback = callback;
    }

    @Override
    public void takeControl() {
        InitialParamsForComparisonScreen screen = new InitialParamsForComparisonScreen(callback);
        switcher.show(screen);
    }
}
