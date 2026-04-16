package org.example.testfx.HeatEquation.AnalyticalSolution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.HeatEquation.AnalyticalSolution.Equation.AnalyticalHeatEquationCore;

public class AnalyticalCoreController { // тут в отличие от численного решения будет реализовано API для доступа к посчитанным данным, тк ну по правде, городить ещё один читатель/писатель мне лень, да и незачем, объём данных не такой, чтобы его нельзя было просто полностью поместить в память, тут объём данных в nx*nt раз меньше
    private final static Logger log = LogManager.getLogger(AnalyticalCoreController.class);
    private final PlateParameters plateParams;
    private final int harmonicCount;
    private final double dy;
    private final AnalyticalHeatEquationCore equation;

    public AnalyticalCoreController(PlateParameters plateParams, int harmonicCount, double dy) {
        this.plateParams = plateParams;
        this.harmonicCount = harmonicCount;
        this.dy = dy;
        log.debug("setting up AnalyticalHeatEquationCore");
        log.debug("all parameters to string: |{}|, harmonic count: |{}|, dy: |{}|", plateParams.toString(), harmonicCount, dy);
        equation = new AnalyticalHeatEquationCore(plateParams, harmonicCount, dy);
        log.info("AnalyticalController initialized successfully");
    }

    public void run(double time){
        log.info("AnalyticalController run start! equation step to |{}|s", time);
        equation.stepTo(time);
        log.info("");
    }

    public double[] getTMapColumn(){
        return equation.gettMapColumn();
    }
}
