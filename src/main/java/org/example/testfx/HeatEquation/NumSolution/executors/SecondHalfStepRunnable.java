package org.example.testfx.HeatEquation.executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.HeatEquation.matrix.ThreeDiagonalMatrix;
import org.example.testfx.HeatEquation.matrix.ThreeDiagonalMatrixSecondStep;
import org.example.testfx.HeatEquation.executors.ThreadLocalDTO.ThreadVectors;

public class SecondHalfStepRunnable implements Runnable {
    private final static Logger log = LogManager.getLogger(SecondHalfStepRunnable.class);

    private final double[][] tMapPrevious, tStepMap; //общие для всех потоков, работает только в своём столбце
    private final ThreeDiagonalMatrix baseMatrix; //общие для всех потоков, работает только в своём столбце
    private final double rx, ry;
    private final int ny, nx, i;
    // на втором полушаге размеры этих троих - ny-2
    private final ThreadLocal<ThreadVectors> threadLocalVectors;


    // во второй части нет уравнений для верхней и нижней строк, тк там температура константа, а значит у нас всего ny-2 уравнений на каждом шаге по i,
    // соответственно в rightPartVector всего ny-2 значений, а итоговый вектор заполняем с офсетом, границы переносим тоже здесь!
    // тоже самое с ksi и eta vectors!
    public SecondHalfStepRunnable(double[][] tMapPrevious, double[][] tStepMap, ThreeDiagonalMatrixSecondStep baseMatrix, double rx, double ry, int nx, int ny, int i, ThreadLocal<ThreadVectors> vectors) {
        this.tMapPrevious = tMapPrevious;
        this.tStepMap = tStepMap;
        this.baseMatrix = baseMatrix;
        this.rx = rx;
        this.ry = ry;
        this.nx = nx;
        this.ny = ny;
        this.i = i;
        threadLocalVectors = vectors;
    }

    @Override
    public void run() {
        ThreadVectors threadVectors = threadLocalVectors.get();
        double[] rightPartVector = threadVectors.rightPartVector;// их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим
        double[] ksiVector = threadVectors.ksiVector;
        double[] etaVector = threadVectors.etaVector;

        log.trace("SecondHalfStepRunnable from thread {}, from step (by x): {}", Thread.currentThread().getName(), i);
        //инициализация правой части из-за условий Дирихле
        if (i == 0) {
            for (int j = 0; j < ny - 2; j++) {
                rightPartVector[j] = (1 - 2 * rx) * tMapPrevious[j+1][i] + 2 * rx * tMapPrevious[j+1][i + 1];
            }
        } else if (i == nx - 1) {
            for (int j = 0; j < ny - 2; j++) {
                rightPartVector[j] = (1 - 2 * rx) * tMapPrevious[j+1][i] + 2 * rx * tMapPrevious[j+1][i - 1];
            }
        } else {
            for (int j = 0; j < ny - 2; j++) {
                rightPartVector[j] = tMapPrevious[j+1][i] + rx * (tMapPrevious[j+1][i - 1] - 2 * tMapPrevious[j+1][i] + tMapPrevious[j+1][i + 1]);
            }
        }

        // достаём значения g1 и g2 из матрицы предыдущего полушага, они там записаны!!!
        rightPartVector[0] += ry * tMapPrevious[0][i];
        rightPartVector[ny - 3] += ry * tMapPrevious[ny - 1][i];


        //прямой ход
        ThreeDiagonalMatrix.MatrixRow row = baseMatrix.getMatrixRow(0);
        ksiVector[0] = -row.c / row.b;
        etaVector[0] = rightPartVector[0] / row.b;
        double d;
        for (int j = 1; j < ny - 2; j++) {
            row = baseMatrix.getMatrixRow(j);
            d = row.a * ksiVector[j - 1] + row.b;
            ksiVector[j] = -(row.c / d);
            etaVector[j] = (rightPartVector[j] - row.a * etaVector[j - 1]) / d;
        }

        //обратный ход! Работаем с офсетом!
        tStepMap[ny - 2][i] = etaVector[ny - 3];
        for (int j = ny - 4; j >= 0; j--) {
            tStepMap[j + 1][i] = ksiVector[j] * tStepMap[j + 2][i] + etaVector[j];
        }

        //задаём значения для j=0 и j=ny-1 из матрицы с предыдущего шага, там они тоже не трогались а были заполнены на этапе создания задания
        tStepMap[0][i] = tMapPrevious[0][i];
        tStepMap[ny - 1][i] = tMapPrevious[ny - 1][i];
    }
}
