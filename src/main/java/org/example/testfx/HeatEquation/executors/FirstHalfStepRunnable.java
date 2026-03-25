package org.example.testfx.HeatEquation.executors;

import org.example.testfx.HeatEquation.ThreeDiagonalMatrix;

public class FirstHalfStepRunnable implements Runnable{

    private final double[][] tMapPrevious, tStepMap; //общие для всех потоков, работаем только в своей строке
    private final ThreeDiagonalMatrix baseMatrix; //общие для всех потоков, работаем только в своей строке
    private final double ry;
    private final int nx, j;
    private final double[] rightPartVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим
    private final double[] ksiVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим
    private final double[] etaVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим

    public FirstHalfStepRunnable(double[][] tMapPrevious, double[][] tStepMap, double ry, ThreeDiagonalMatrix baseMatrix, int nx, int ny, int j, double[] rightPartVector, double[] ksiVector, double[] etaVector) {

        if(j <= 0 || j >= ny)
            throw new IllegalArgumentException("j (по y) должна находиться в диапазоне от 1 до N_y-2");

        this.tMapPrevious = tMapPrevious;
        this.tStepMap = tStepMap;
        this.ry = ry;
        this.baseMatrix = baseMatrix;
        this.nx = nx;
        this.j = j;
        this.rightPartVector = rightPartVector;
        this.ksiVector = ksiVector;
        this.etaVector = etaVector;
    }

    @Override
    public void run() {
        for (int i = 0; i < nx; i++) {
            rightPartVector[i] = tMapPrevious[j][i] + ry * (tMapPrevious[j-1][i] - 2 * tMapPrevious[j][i] + tMapPrevious[j][i]);
        }
        ThreeDiagonalMatrix.MatrixRow row = baseMatrix.getMatrixRow(0);
        //прямой проход, заполняем кси и ета векторы
        ksiVector[0] = -row.c/row.b;
        etaVector[0] = rightPartVector[0]/row.b;
        for (int i = 1; i < nx - 1; i++){
            row = baseMatrix.getMatrixRow(i);
            ksiVector[i] = row.c / (row.a * ksiVector[i-1] + row.b);
            etaVector[i] = (rightPartVector[i] - etaVector[i-1]*row.a)/(row.a * ksiVector[i-1] + row.b);
        }

        //обратный проход
        tStepMap[j][nx-1] = etaVector[nx-1];
        for (int i = nx-2; i >= 0; i--){
            tStepMap[j][i] = ksiVector[i] * tStepMap[j][i+1] + etaVector[i];
        }
    }
}
