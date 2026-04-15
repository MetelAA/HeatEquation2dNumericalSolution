package org.example.testfx.HeatEquation.AnalyticalSolution.Equation;

import org.example.testfx.DTO.PlateParameters;

import java.util.ArrayList;
import java.util.List;

public class AnalyticalHeatEquationCore {
    private final PlateParameters plateParams;
    private final int harmonicCount;
    private final List<Double> C_coefficients;

    public AnalyticalHeatEquationCore(PlateParameters plateParams, int harmonicCount) {
        this.plateParams = plateParams;
        this.harmonicCount = harmonicCount;

        
        C_coefficients = new ArrayList<>(harmonicCount);
    }


    public void stepTo(double time){ // время в секундах с начала эксперимента

    }
}
