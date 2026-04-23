package org.example.testfx.Ui.Controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Ui.ScreenSwitcher;

public class OutputCompareModController implements Controller{
    private final static Logger log = LogManager.getLogger(OutputDefaultModeController.class);
    private final ScreenSwitcher switcher;

    public OutputCompareModController(ScreenSwitcher switcher) {
        this.switcher = switcher;
    }


    @Override
    public void takeControl() {

    }
}
