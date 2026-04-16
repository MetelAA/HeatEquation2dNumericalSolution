package org.example.testfx.HeatEquation.AnalyticalSolution.Equation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.DTO.PlateParameters;

public class AnalyticalHeatEquationCore {
    private final static Logger log = LogManager.getLogger(AnalyticalHeatEquationCore.class);
    private final PlateParameters plateParams;
    private final int harmonicCount;
    private final double[] C_coefficients;
    private final double thermalDiffusivity;
    private final double dy;
    private final double[] tMapColumn;
    private final int ny;
    private final double g2, g1; //значения температуры на границах сверху и снизу соответственно

    public AnalyticalHeatEquationCore(PlateParameters plateParams, int harmonicCount, double dy) {
        this.plateParams = plateParams;
        this.harmonicCount = harmonicCount;
        this.dy = dy;

        thermalDiffusivity = plateParams.getNumeralParameters().heatConductivity() / (plateParams.getNumeralParameters().density() * plateParams.getNumeralParameters().heatCapacity());


        try {
            g2 = Double.parseDouble(plateParams.getBoundaryTemperatureEquationUp());
            g1 = Double.parseDouble(plateParams.getBoundaryTemperatureEquationBottom());
        } catch (NumberFormatException e) {
            log.error("Error when converting boundary temp equation to constant (must be catch on previous stage)");
            throw new RuntimeException("AnalyticalHeatEquationCore: Error when converting boundary temp equation to constant (must be catch on previous stage),  with message: " + e);
        }


        C_coefficients = new double[harmonicCount+1]; //сделана на один элемент больше, 0ой индекс некогда не заполняется и не используется
        double c1 = 2 / Math.PI;
        double c2 = plateParams.getNumeralParameters().plateTemperature() - g1;
        double c3  = g2 - g1;
        for(int i = 1; i <= harmonicCount; i++){
            C_coefficients[i] = (c1 / i) * ( (c2 * (1 - (i % 2 == 0 ? 1 : -1) ) ) + (c3 * (i % 2 == 0 ? 1 : -1) ));
        }

        ny = (int) Math.round(plateParams.getNumeralParameters().height() / dy);
        tMapColumn = new double[ny];
    }


    public void stepTo(double time) { // время в секундах с начала эксперимента
        double c1 = (g2 - g1) / plateParams.getNumeralParameters().height();

        for(int sy = 0; sy < ny; sy++){ // sy = step y
            double y = sy * dy;

            double row = 0; // ряд Фурье
            for(int i = 1; i <= harmonicCount; i++){
                row += C_coefficients[i] * Math.sin( (i*Math.PI*y) / plateParams.getNumeralParameters().height() ) * Math.exp( -thermalDiffusivity * Math.pow( (i*Math.PI) / plateParams.getNumeralParameters().height(), 2) * time);
            }
            tMapColumn[sy] = g1 + c1 * y + row;
        }
    }

    public double[] gettMapColumn() {
        return tMapColumn;
    }
}
