package org.example.testfx.HeatEquation.NumSolution.Equation.executors.ThreadLocalDTO;

//храним тут три вектора (выделенную память под каждый поток, чтобы каждый раз заново не перевыделять)
public class ThreadVectors {
    public double[] rightPartVector;
    public double[] ksiVector;
    public double[] etaVector;

    public ThreadVectors(double[] rightPartVector, double[] ksiVector, double[] etaVector) {
        this.rightPartVector = rightPartVector;
        this.ksiVector = ksiVector;
        this.etaVector = etaVector;
    }
}
