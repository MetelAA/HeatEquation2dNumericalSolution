package org.example.testfx.HeatEquation.NumSolution.executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.HeatEquation.NumSolution.matrix.ThreeDiagonalMatrix;
import org.example.testfx.HeatEquation.NumSolution.matrix.ThreeDiagonalMatrixFirstStep;
import org.example.testfx.HeatEquation.NumSolution.executors.ThreadLocalDTO.ThreadVectors;

public class FirstHalfStepRunnable implements Runnable {
    private final static Logger log = LogManager.getLogger(FirstHalfStepRunnable.class);

    private final double[][] tMapPrevious, tStepMap; //общие для всех потоков, работаем только в своей строке
    private final ThreeDiagonalMatrix baseMatrix; //общие для всех потоков, работаем только в своей строке
    private final double ry;
    private final int nx, j;
    //на первом полушаге размеры этих троих - nx
    private final ThreadLocal<ThreadVectors> threadLocalVectors;

    public FirstHalfStepRunnable(double[][] tMapPrevious, double[][] tStepMap, ThreeDiagonalMatrixFirstStep baseMatrix, double ry, int nx, int ny, int j, ThreadLocal<ThreadVectors> vectors) {

        if (j <= 0 || j >= ny)
            throw new IllegalArgumentException("j (by y) must be in range from 1 to n_y-2");

        this.tMapPrevious = tMapPrevious;
        this.tStepMap = tStepMap;
        this.ry = ry;
        this.baseMatrix = baseMatrix;
        this.nx = nx;
        this.j = j;
        threadLocalVectors = vectors;
    }

    @Override
    public void run() {
        ThreadVectors threadVectors = threadLocalVectors.get();
        double[] rightPartVector = threadVectors.rightPartVector;// их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим,
        double[] ksiVector = threadVectors.ksiVector;
        double[] etaVector = threadVectors.etaVector;


        log.trace("FirstHalfStepRunnable from thread: {}, step(by y): {}", Thread.currentThread().getName(), j);
        for (int i = 0; i < nx; i++) {
            rightPartVector[i] = tMapPrevious[j][i] + ry * (tMapPrevious[j - 1][i] - 2 * tMapPrevious[j][i] + tMapPrevious[j + 1][i]);
        }
        ThreeDiagonalMatrix.MatrixRow row = baseMatrix.getMatrixRow(0);
        //прямой проход, заполняем кси и ета векторы
        ksiVector[0] = -row.c / row.b;
        etaVector[0] = rightPartVector[0] / row.b;
        double d;
        for (int i = 1; i < nx; i++) {
            row = baseMatrix.getMatrixRow(i);
            d = row.a * ksiVector[i - 1] + row.b;
            ksiVector[i] = -(row.c / d);
            etaVector[i] = (rightPartVector[i] - etaVector[i - 1] * row.a) / d;
        }

        //обратный проход
        tStepMap[j][nx - 1] = etaVector[nx - 1];
        for (int i = nx - 2; i >= 0; i--) {
            tStepMap[j][i] = ksiVector[i] * tStepMap[j][i + 1] + etaVector[i];
        }
    }
}
