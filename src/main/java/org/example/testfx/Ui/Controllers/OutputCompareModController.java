package org.example.testfx.Ui.Controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.Ui.ScreenSwitcher;
import org.example.testfx.Ui.Screens.OutputCompareModFirstScreen;
import org.example.testfx.Utils.DefaultCallback;
import org.example.testfx.Utils.FileUtils.TempMapReader;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;
import org.example.testfx.Utils.TempMapsComparator;

import java.io.*;

public class OutputCompareModController implements Controller{
    private final static Logger log = LogManager.getLogger(OutputDefaultModeController.class);
    private final ScreenSwitcher switcher;

    public OutputCompareModController(ScreenSwitcher switcher) {
        this.switcher = switcher;

        ExperimentNMapParameters params;
        try {
            params = TempMapReader.getParams();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error when reading ex params from file, with message: " + e);
        }

        TempMapsComparator mapsComparator;
        {
            TempMapReader numReader = new TempMapReader(params.getNy(), params.getNx(), Constants.TEMP_MAP_FOR_NUM_METHOD_FILE_LOCATION);
            TempMapReader analyticalReader = new TempMapReader(params.getNy(), params.getNx(), Constants.TEMP_MAP_FOR_ANALYTICAL_METHOD_FILE_LOCATION);
            TempMapReaderWrapper numReaderWrapper = new TempMapReaderWrapper(numReader, params.getWroteFramesCount(), 1);
            TempMapReaderWrapper analyticalReaderWrapper = new TempMapReaderWrapper(analyticalReader, params.getWroteFramesCount(), 1);
            mapsComparator = new TempMapsComparator(numReaderWrapper, analyticalReaderWrapper);
        }

        showFirstScreen(mapsComparator);



    }

    private void showFirstScreen(TempMapsComparator mapsComparator) {
        log.info("setting up first compare screen");

        ExperimentNMapParameters params;
        try {
            params = TempMapReader.getParams();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("error when trying to read ExperimentalNMapParams with message: " + e);
        }

        log.info("got ExperimentalNMapParameters, to str|{}|", params.toString());

        OutputCompareModFirstScreen firstScreen = new OutputCompareModFirstScreen(mapsComparator, params, new DefaultCallback() {
            @Override
            public void callback() {

                showGraphic("max_diff.plot");
                showGraphic("avg_quad_diff.plot");
            }
        });

        switcher.show(firstScreen);
    }
    private void showGraphic(String plotScriptFileName) {
        try {
            new ProcessBuilder(
                    "cmd", "/c", "start", "gnuplot", plotScriptFileName
            )
                    .directory(new File("C:\\Users\\Artem\\ideaProj\\HeatEquation2dNumericalSolution"))
                    .start();
            // Не ждём завершения, окно живёт само по себе
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void takeControl() {

    }
}
