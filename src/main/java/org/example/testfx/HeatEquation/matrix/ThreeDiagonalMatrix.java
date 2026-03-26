package org.example.testfx.HeatEquation.matrix;

public interface ThreeDiagonalMatrix {
    public MatrixRow getMatrixRow(int i);
    public class MatrixRow{
        public final double a, b, c;
        public MatrixRow(double a, double b, double c) {
            this.a = a;
            this.b = b;
            this.c = c;
        }
    }
}
