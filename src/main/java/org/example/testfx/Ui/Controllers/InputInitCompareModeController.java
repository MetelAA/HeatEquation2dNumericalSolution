package org.example.testfx.Ui.Controllers;

import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.Screens.InitialParamsForComparisonScreen;
import org.example.testfx.Utils.InitParametersForCompareFinishedCallback;

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
