package org.example.testfx.HeatEquation.matrix;

public class ThreeDiagonalMatrixFirstStep implements ThreeDiagonalMatrix{
    private final int size;
    private final double centralValue, sideValue;

    private final MatrixRow staticRow;

    public ThreeDiagonalMatrixFirstStep(int size, double r) {
        // условная r-ка в зависимости от входных параметров это или rx или ry
        this.size = size;

        centralValue = 1+2*r;
        sideValue = -r;

        staticRow = new MatrixRow(sideValue, centralValue, sideValue); //как раз одинаковые строки в диапазоне от [1, size-1];
    }

    public MatrixRow getMatrixRow(int i){
        if(i == 0)
            return new MatrixRow(0, centralValue, 2*sideValue);
        if(i == size-1)
            return new MatrixRow(2*sideValue, centralValue, 0);
        return staticRow;
    }
}
