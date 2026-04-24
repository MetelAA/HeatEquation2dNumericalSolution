package org.example.testfx.Ui.Controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.Screens.OutputDefaultModeScreen;
import org.example.testfx.Utils.FileUtils.TempMapReader;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;

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

        tMapReader = new TempMapReader(params.getNy(), params.getNx(), Constants.TEMP_MAP_FOR_NUM_METHOD_FILE_LOCATION);
        try {
            tMapReader.initReader();
        } catch (IOException e) {
            throw new RuntimeException("Error when initializing heat map reader, with message: " + e);
        }

        TempMapReaderWrapper readerWrapper = new TempMapReaderWrapper(tMapReader, params.getWroteFramesCount(), 1);


        OutputDefaultModeScreen screen = new OutputDefaultModeScreen(readerWrapper, params);
        switcher.show(screen);
    }

}
