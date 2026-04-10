package org.example.testfx.HeatEquation.NumSolution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.HeatEquation.NumSolution.Equation.HeatEquationCore;
import org.example.testfx.utils.TempMapWriter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class NumCoreController {
    private final static Logger log = LogManager.getLogger(NumCoreController.class);
    private final HeatEquationCore heatEquation;
    private final PlateParameters plateParameters;
    private final SimulationParameters simulationParameters;

    public NumCoreController(PlateParameters plateParameters, SimulationParameters simulationParameters) {
        this.plateParameters = plateParameters;
        this.simulationParameters = simulationParameters;
        log.debug("Setting up HeatEquationCore");
        log.debug("all parameters to string, |{}|, |{}|", plateParameters.toString(), simulationParameters.toString());
        heatEquation = new HeatEquationCore(plateParameters, simulationParameters.getDx(), simulationParameters.getDy(), simulationParameters.getDt());
        log.info("CoreController initialized successfully");
    }

    public void run(){
        long startTime = System.currentTimeMillis();
        log.info("CoreController run");
        int nt = (int) (simulationParameters.getTime() / simulationParameters.getDt());

        log.info("There are |{}| time steps,  |{}| x steps, |{}| y steps", nt, heatEquation.getNx(), heatEquation.getNy());
        log.info("Finally, total {} steps", nt*heatEquation.getNx()* heatEquation.getNy());

        int timeStepsPerSecond = (int) (1 / simulationParameters.getDt());
        int timeStepsPerWrite = (int) (timeStepsPerSecond / Constants.WRITES_PER_SECOND);
        log.info("There are |{}| writes per second and there are |{}| time steps per second", Constants.WRITES_PER_SECOND, timeStepsPerSecond);
        log.info("There are |{}| time steps per write => write will be done once every |{}| secs", timeStepsPerWrite, (double)(1.0/Constants.WRITES_PER_SECOND));
        log.info("There will be |{}| total writes", nt / timeStepsPerWrite);
        TempMapWriter mapIO = new TempMapWriter();
        try {
            mapIO.initWriter();
        } catch (IOException e) {
            throw new RuntimeException("Exception while setting up data writer, with message: " + e);
        }

        {
            double[][] heatMapFStep = heatEquation.gettMap();
            double minT = Integer.MAX_VALUE, maxT = Integer.MIN_VALUE;
            for (int i = 0; i < heatMapFStep.length; i++) {
                for (int j = 0; j < heatMapFStep[0].length; j++) {
                    minT = min(minT, heatMapFStep[i][j]);
                    maxT = max(maxT, heatMapFStep[i][j]);
                }
            }
            ExperimentalNMapParameters params = new ExperimentalNMapParameters(
                    new ExperimentParameters(plateParameters, simulationParameters),
                    minT,
                    maxT,
                    nt / timeStepsPerWrite,
                    heatEquation.getNy(),
                    heatEquation.getNx()
            );
            log.info("Writing experimental and temperature map parameters, toString: {}", params.toString());
            try {
                TempMapWriter.writeExperimentalNMapParameters(params);
            } catch (IOException e) {
                throw new RuntimeException("Exception while writing ExperimentalNMapParameters, with message: " + e);
            }
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

        try {
            mapIO.closeWriter();
        } catch (IOException e) {
            throw new RuntimeException("Exception close Writer, with message: " + e);
        }
        long secs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime);
        log.info("All steps completed, time spent: {}m {}s", secs/60, secs% 60);
    }

    public HeatEquationCore getHeatEquation() {
        return heatEquation;
    }
}
