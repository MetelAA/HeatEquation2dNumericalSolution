package org.example.testfx.Ui.Controllers;

import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.screens.OutputDefaultModeScreen;

public class OutputDefaultModeController implements Controller{
    private final ScreenSwitcher switcher;
    private final double width;
    private final double height;
    private final double minT;
    private final double maxT;
    private final double[][] tMap;

    public OutputDefaultModeController(ScreenSwitcher switcher, double width, double height, double minT, double maxT, double[][] tMap) {
        this.switcher = switcher;
        this.width = width;
        this.height = height;
        this.minT = minT;
        this.maxT = maxT;
        this.tMap = tMap;
    }

    @Override
    public void takeControl() {
        OutputDefaultModeScreen screen = new OutputDefaultModeScreen(width, height, minT, maxT, tMap);
        switcher.show(screen);
    }
}
