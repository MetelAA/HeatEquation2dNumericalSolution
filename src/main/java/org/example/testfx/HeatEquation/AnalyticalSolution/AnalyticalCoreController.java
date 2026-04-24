package org.example.testfx.HeatEquation.AnalyticalSolution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.HeatEquation.AnalyticalSolution.Equation.AnalyticalHeatEquationCore;
import org.example.testfx.Utils.ExpressionParser;
import org.example.testfx.Utils.FileUtils.TempMapWriter;

import java.io.IOException;

public class AnalyticalCoreController { // тут в отличие от численного решения будет реализовано API для доступа к посчитанным данным, тк ну по правде, городить ещё один читатель/писатель мне лень, да и незачем, объём данных не такой, чтобы его нельзя было просто полностью поместить в память, тут объём данных в nx*nt раз меньше
    private final static Logger log = LogManager.getLogger(AnalyticalCoreController.class);
    private final int harmonicCount;
    private final ExperimentParameters exParams;
    private final AnalyticalHeatEquationCore equation;
    private final int nx, ny;

    public AnalyticalCoreController(ExperimentParameters params, int harmonicCount) {  //dt здесь и в NumCoreController это разные dt, там это параметр эксперимента, здесь - шаг по времени, который зависит от частоты записи кадров в численном методе
        this.harmonicCount = harmonicCount;
        this.exParams = params;
        log.debug("setting up AnalyticalHeatEquationCore");
        log.debug("all parameters to string: |{}|, harmonic count: |{}|, dy: |{}|", params.toString(), harmonicCount, exParams.getSimulationParameters().getDy());

        nx = Math.max((int) Math.round(exParams.getPlateParameters().getNumeralParameters().width() / exParams.getSimulationParameters().getDx()) + 1, 3);
        ny = Math.max((int) Math.round(exParams.getPlateParameters().getNumeralParameters().height() / exParams.getSimulationParameters().getDy()) + 1, 3);
        equation = new AnalyticalHeatEquationCore(params.getPlateParameters(), harmonicCount, ny, exParams.getSimulationParameters().getDy());
        log.info("AnalyticalController initialized successfully");
    }

    public void run(){
        log.info("AnalyticalController run start! there will be |{}| steps", exParams.getSimulationParameters().getTime()/ exParams.getSimulationParameters().getFrameWritesPerSecond());


        TempMapWriter mapI = new TempMapWriter(Constants.TEMP_MAP_FOR_ANALYTICAL_METHOD_FILE_LOCATION);
        try {
            mapI.initWriter();
            mapI.writeMap(getZeroTMap(nx, ny), 0);
        } catch (IOException e) {
            throw new RuntimeException("Exception while setting up data writer, with message: " + e);
        }


        double timeStep = 1.0 / exParams.getSimulationParameters().getFrameWritesPerSecond();
        for (int i = 1; i <= exParams.getSimulationParameters().getTime() * exParams.getSimulationParameters().getFrameWritesPerSecond(); i++) {
            equation.stepTo(i * timeStep);
            writeToFile(mapI, equation.gettMapColumn(), i, nx, ny);
        }
        log.info("AnalyticalController run ended!");
    }

    private void writeToFile(TempMapWriter mapI, double[] column, int i, int nx, int ny){
        double[][] tMap = new double[ny][nx];
        for (int y = 0; y < ny; y++) {
            for (int x = 0; x < nx; x++) {
                tMap[y][x] = column[y];
            }
        }

        try {
            mapI.writeMap(tMap, i);
        } catch (IOException e) {
            throw new RuntimeException("Exception while writing ExperimentalNMapParameters, with message: " + e);
        }
    }

    private double[][] getZeroTMap(int nx, int ny){
        double[][] tMap = new double[ny][nx];

        ExpressionParser.Expression expressionForTopBorder;
        ExpressionParser.Expression expressionForBottomBorder;
        try {
            expressionForTopBorder = new ExpressionParser().compile(exParams.getPlateParameters().getBoundaryTemperatureEquationUp());
            expressionForBottomBorder = new ExpressionParser().compile(exParams.getPlateParameters().getBoundaryTemperatureEquationBottom());
        } catch (ExpressionParser.ExpressionException e) {
            throw new RuntimeException("AnalyticalCoreController: expression parser setting up exception, with message: " + e);
        }


        for (int j = 1; j < ny - 1; j++) { //задаём t0 для всех кроме границ
            for (int i = 0; i < nx; i++) {
                tMap[j][i] = exParams.getPlateParameters().getNumeralParameters().plateTemperature();
            }
        }

        double c;
        for (int i = 0; i < nx; i++) { //задаём границы
            c = exParams.getSimulationParameters().getDx() * i;
            tMap[0][i] = expressionForTopBorder.evaluate(c);
            tMap[ny - 1][i] = expressionForBottomBorder.evaluate(c);
        }

        return tMap;
    }


}
