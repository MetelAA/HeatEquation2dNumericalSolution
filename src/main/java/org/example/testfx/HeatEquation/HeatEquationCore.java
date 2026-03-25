package org.example.testfx.HeatEquation;

import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.utils.ExpressionParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class HeatEquationCore {
    private final PlateParameters plateParams;

    private final double dx, dy, dt;
    private final int nx, ny;
    private final double rx, ry; //те самые r-ки
    private final double[][] tMap;
    private final ThreeDiagonalMatrix firstStepMatrix;
    private final ThreeDiagonalMatrix secondStepMatrix;
    private final double[] rightPartFirstStep;
    private final double[] rightPartSecondStep;
    private final double[][] tStepMap;

    public HeatEquationCore(PlateParameters plateParams, double dxC, double dyC, double dtC) {
        this.plateParams = plateParams;

        this.dt = dtC;

        int acc;
        if ((acc = (int) (Math.floor(plateParams.getNumeralParameters().width() / dxC) + 1)) < 3) { //проверяем чтобы кол-во столбцов было больше > 3
            dxC = (1.0 / 3.0) * plateParams.getNumeralParameters().width();
            nx = 3;
        } else
            nx = acc;
        dx = dxC;

        if ((acc = ((int) Math.floor(plateParams.getNumeralParameters().height() / dyC) + 1)) < 3) { //проверяем чтобы кол-во строк было больше 3х
            dyC = (1.0 / 3.0) * plateParams.getNumeralParameters().height();
            ny = 3;
        } else
            ny = acc;
        dy = dyC;

        tMap = new double[ny][nx]; // инициализируем сетку температур

        ExpressionParser.Expression expressionForTopBorder = new ExpressionParser().compile(plateParams.getBoundaryTemperatureEquationTop());
        ExpressionParser.Expression expressionForBottomBorder = new ExpressionParser().compile(plateParams.getBoundaryTemperatureEquationBottom());


        for (int j = 1; j < ny - 1; j++) { //задаём t0 для всех кроме границ
            for (int i = 0; i < nx; i++) {
                tMap[j][i] = plateParams.getNumeralParameters().plateTemperature();
            }
        }

        double c;
        for (int i = 0; i < nx; i++) { //задаём границы
            c = dx * i;
            tMap[0][i] = expressionForTopBorder.evaluate(c);
            tMap[ny - 1][i] = expressionForBottomBorder.evaluate(c);
        }

        //коэффициент температуропроводности, которые не теплопроводности, а который в r-ках
        double thermalDiffusivity = plateParams.getNumeralParameters().heatConductivity() / (plateParams.getNumeralParameters().density() * plateParams.getNumeralParameters().heatCapacity());
        rx = (thermalDiffusivity * dt) / (2*dx);
        ry = (thermalDiffusivity * dt) / (2*dy);
        //считаем неизменяемые матрицы для шагов метода прогонки
        firstStepMatrix = new ThreeDiagonalMatrix(nx, rx);
        secondStepMatrix = new ThreeDiagonalMatrix(ny - 2, ry);

        //выделяем память под промежуточную tMap для первого полушага, которая далее используется во втором
        tStepMap = new double[ny][nx];
        //заполняем её края, которые некогда не должны быть изменены, они не будут нести никакой полезной информации, но пусть будут, дабы в индексации не путаться и проверки можно было провести, можно потом убрать 3 строки ниже
        tStepMap[0] = Arrays.copyOf(tMap[0], nx);
        tStepMap[ny-1] = Arrays.copyOf(tMap[ny-1], nx);
    }


    public void step(ExecutorService executor) {

    }


    public void firstStep(){

    }


}