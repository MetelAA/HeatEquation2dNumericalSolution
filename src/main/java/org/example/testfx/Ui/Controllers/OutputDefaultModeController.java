package org.example.testfx.Ui.Controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.screens.OutputDefaultModeScreen;
import org.example.testfx.utils.TempMapReader;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Math.ceil;

public class OutputDefaultModeController implements Controller{
    private final static Logger log = LogManager.getLogger(OutputDefaultModeController.class);
    private final ScreenSwitcher switcher;
    private final ExperimentalNMapParameters params;
    private TempMapReader tMapReader;

    public OutputDefaultModeController(ScreenSwitcher switcher) {
        this.switcher = switcher;
        try {
            this.params = TempMapReader.getParams();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error when reading experimental and temperature map parameters, with message: " + e);
        }
    }

    @Override
    public void takeControl() {

        tMapReader = new TempMapReader(params.getNy(), params.getNx());
        double[][] firstStep;
        try {
            tMapReader.initReader();
            firstStep = tMapReader.readNextStep();
        } catch (IOException e) {
            throw new RuntimeException("Error when reading heat map form file, with message: " + e);
        }

        log.debug(params);

        OutputDefaultModeScreen screen = new OutputDefaultModeScreen(
                params.getExParams().getPlateParameters().getNumeralParameters().width(),
                params.getExParams().getPlateParameters().getNumeralParameters().height(),
                (ceil(params.getMinTemp() / 10.0) * 10),
                (ceil(params.getMaxTemp() / 10.0) * 10),
                firstStep);
        switcher.show(screen);
    }
}
