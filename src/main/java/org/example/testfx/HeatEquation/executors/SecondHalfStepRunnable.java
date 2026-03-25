package org.example.testfx.HeatEquation.executors;

import org.example.testfx.HeatEquation.ThreeDiagonalMatrix;

public class SecondHalfStepRunnable implements Runnable{

    private final double[][] tMapPrevious, tStepMap; //общие для всех потоков, работает только в своём столбце
    private final ThreeDiagonalMatrix baseMatrix; //общие для всех потоков, работает только в своём столбце
    private final double rx;
    private final int ny, i;
    private final double[] rightPartVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим
    private final double[] ksiVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим
    private final double[] etaVector; // их должно быть по числу потоков, тк работает несколько потоков и каждый работает со своим

    public SecondHalfStepRunnable(double[][] tMapPrevious, double[][] tStepMap, ThreeDiagonalMatrix baseMatrix, double rx, int nx, int ny, int i, double[] rightPartVector, double[] ksiVector, double[] etaVector) {
        this.tMapPrevious = tMapPrevious;
        this.tStepMap = tStepMap;
        this.baseMatrix = baseMatrix;
        this.rx = rx;
        this.ny = ny;
        this.i = i;
        this.rightPartVector = rightPartVector;
        this.ksiVector = ksiVector;
        this.etaVector = etaVector;


        //инициализация правой части из за условий неймана
         if(i == 0){

         }else if(i == nx -1){

         }else{

         }



    }

    @Override
    public void run() {

    }
}
