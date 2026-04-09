package org.example.testfx.Ui.Controllers;

import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.screens.ModeSelectionScreen;

public class ModeSelectionController implements Controller{
    private final ScreenSwitcher screenSwitcher;
    private final Runnable defaultMode;
    private final Runnable analyticalVsNumericCompare;

    public ModeSelectionController(ScreenSwitcher screenSwitcher, Runnable defaultMode, Runnable analyticalVsNumericCompare) {
        this.screenSwitcher = screenSwitcher;
        this.defaultMode = defaultMode;
        this.analyticalVsNumericCompare = analyticalVsNumericCompare;
    }

    @Override
    public void takeControl() {
        ModeSelectionScreen screen = new ModeSelectionScreen(defaultMode, analyticalVsNumericCompare);
        screenSwitcher.show(screen);
    }
}
