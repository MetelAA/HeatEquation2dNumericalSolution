package org.example.testfx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.HeatEquation.HeatEquationCore;
import org.example.testfx.utils.ReadWriteTMap;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class CoreController {
    private final static Logger log = LogManager.getLogger(CoreController.class);
    private final HeatEquationCore heatEquation;
    private final PlateParameters plateParameters;
    private final SimulationParameters simulationParameters;

    public CoreController(PlateParameters plateParameters, SimulationParameters simulationParameters) {
        this.plateParameters = plateParameters;
        this.simulationParameters = simulationParameters;
        log.debug("Setting up HeatEquationCore");
        log.debug("all parameters to string, |{}|, |{}|", plateParameters.toString(), simulationParameters.toString());
        heatEquation = new HeatEquationCore(plateParameters, simulationParameters.getDx(), simulationParameters.getDy(), simulationParameters.getDt());
        log.info("CoreController initialized successfully");
    }

    void run(){
        long startTime = System.currentTimeMillis();
        log.info("CoreController run");
        int nt = (int) (simulationParameters.getTime() / simulationParameters.getDt());

        log.info("There are |{}| time steps,  |{}| x steps, |{}| y steps", nt, heatEquation.getNx(), heatEquation.getNy());
        log.info("Finally, total {} steps", nt*heatEquation.getNx()* heatEquation.getNy());

        int timeStepsPerSecond = (int) (1 / simulationParameters.getDt());
        int timeStepsPerWrite = (int) (timeStepsPerSecond / Constants.writesPerSecond);
        log.info("There are |{}| writes per second and there are |{}| time steps per second", Constants.writesPerSecond, timeStepsPerSecond);
        log.info("There are |{}| time steps per write => write will be done once every |{}| secs", timeStepsPerWrite, (double)(1.0/Constants.writesPerSecond));
        log.info("There will be |{}| total writes", nt / timeStepsPerWrite);
        ReadWriteTMap mapIO = new ReadWriteTMap("test.txt");
        try {
            mapIO.initWriter();
        } catch (IOException e) {
            throw new RuntimeException("Exception while setting up data writer, with message: " + e);
        }

        // запись перед началом шагов, далее следующая запись через timeStepsPerWrite шагов!
        try {
            mapIO.writeMap(heatEquation.gettMap(), 0);
        } catch (IOException e) {
            throw new RuntimeException("Exception while writing step, with message: " + e);
        }

        for (int i = 1; i <= nt; i++) {
            heatEquation.step();
            if(i % timeStepsPerWrite == 0){
                try {
                    mapIO.writeMap(heatEquation.gettMap(), i / timeStepsPerWrite);
                } catch (IOException e) {
                    throw new RuntimeException("Exception while writing step, with message: " + e);
                }
            }
        }
//        for (int i = 1; i <= nt; i++) {
//            heatEquation.step();
//
//                try {
//                    mapIO.writeMap(heatEquation.gettMap(), i);
//                } catch (IOException e) {
//                    throw new RuntimeException("Exception while writing step, with message: " + e);
//                }
//        }

        try {
            mapIO.closeWriter();
        } catch (IOException e) {
            throw new RuntimeException("Exception close Writer, with message: " + e);
        }
        long secs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
        log.info("All steps completed, time spent: {}m {}s", secs/60, secs% 60);
    }
}
