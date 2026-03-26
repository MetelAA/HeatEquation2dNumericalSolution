package org.example.testfx.HeatEquation;

import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.HeatEquation.executors.FirstHalfStepRunnable;
import org.example.testfx.HeatEquation.executors.SecondHalfStepRunnable;
import org.example.testfx.HeatEquation.executors.ThreadLocalDTO.ThreadLocalVectors;
import org.example.testfx.HeatEquation.matrix.ThreeDiagonalMatrixFirstStep;
import org.example.testfx.HeatEquation.matrix.ThreeDiagonalMatrixSecondStep;
import org.example.testfx.utils.ExpressionParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Math.pow;

public class HeatEquationCore {
    private final PlateParameters plateParams;

    private final double dx, dy, dt;
    private final int nx, ny;
    private final double rx, ry; //те самые r-ки
    private final double[][] tMap;
    private final ThreeDiagonalMatrixFirstStep firstStepMatrix;
    private final ThreeDiagonalMatrixSecondStep secondStepMatrix; //инициализировать размером ny-2, тк см докс
    private final double[][] tStepMap;

    private final ThreadLocal<ThreadLocalVectors> threadLocalVectorsFirstStep, threadLocalVectorsSecondStep; // умные контейнеры для уменьшения расхода памяти (на один поток, коих конечное кол-во, выделяется по 3 массива единожды, при первом вызове метода get на ThreadLocal инстансе)
    private final ExecutorService executorService;

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
        rx = (thermalDiffusivity * dt) / (2*pow(dx, 2));
        ry = (thermalDiffusivity * dt) / (2*pow(dy, 2));
        //считаем неизменяемые матрицы для шагов метода прогонки
        firstStepMatrix = new ThreeDiagonalMatrixFirstStep(nx, rx);
        secondStepMatrix = new ThreeDiagonalMatrixSecondStep(ny - 2, ry);

        //выделяем память под промежуточную tMap для первого полушага, которая далее используется во втором
        tStepMap = new double[ny][nx];
        //заполняем её края, которые некогда не должны быть изменены, они не будут нести никакой полезной информации, но пусть будут, дабы в индексации не путаться и проверки можно было провести, можно потом убрать 3 строки ниже
        tStepMap[0] = Arrays.copyOf(tMap[0], nx);
        tStepMap[ny-1] = Arrays.copyOf(tMap[ny-1], nx);


        // начинаем инициализировать нужное для работы потоков
        threadLocalVectorsFirstStep = ThreadLocal.withInitial(
                () -> new ThreadLocalVectors(
                        new double[nx],
                        new double[nx],
                        new double[nx]
                ));
        threadLocalVectorsSecondStep = ThreadLocal.withInitial(
                () -> new ThreadLocalVectors(
                        new double[ny-2],
                        new double[ny-2],
                        new double[ny-2]
                ));

        executorService = Executors.newFixedThreadPool(Constants.threadCount);
    }


    public void step() {
        //после выполнения первого полушага, актуальные значения лежат в tStepMap, а устаревшие в tMap, меняем их местами

        List<Future<?>> awaitList = new ArrayList<>(ny-1);
        for (int j = 1; j < ny-1; j++) {
            FirstHalfStepRunnable firstHalfStepIter = new FirstHalfStepRunnable(tMap, tStepMap, firstStepMatrix, ry, nx, ny, j, threadLocalVectorsFirstStep);
            awaitList.add(executorService.submit(firstHalfStepIter));
        }

        for (int j = 0; j < awaitList.size(); j++) {
            try {
                awaitList.get(j).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Ошибка в потоке на j-ой|" + j + "| итерации первого полушага с сообщением: " + e);
            }
        }

        //обязательно дождаться конца первого для консистентности данных
        awaitList = new ArrayList<>(nx);
        for (int i = 0; i < nx; i++) {
            SecondHalfStepRunnable secondHalfStepIter = new SecondHalfStepRunnable(tStepMap, tMap, secondStepMatrix, rx, ry, nx, ny, i, threadLocalVectorsSecondStep);
            awaitList.add(executorService.submit(secondHalfStepIter));
        }

        for (int i = 0; i < awaitList.size(); i++) {
            try {
                awaitList.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Ошибка в потоке на i-ой |" + i + "| итерации второго полушага с сообщением: " + e);
            }
        }

    }



}