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

    public AnalyticalHeatEquationCore(PlateParameters plateParams, int harmonicCount, int ny, double dy) {
        this.plateParams = plateParams;
        this.harmonicCount = harmonicCount;
        this.ny = ny;
        this.dy = dy;

        thermalDiffusivity = plateParams.getNumeralParameters().heatConductivity() /
                (plateParams.getNumeralParameters().density() * plateParams.getNumeralParameters().heatCapacity());

        try {
            g2 = Double.parseDouble(plateParams.getBoundaryTemperatureEquationUp());
            g1 = Double.parseDouble(plateParams.getBoundaryTemperatureEquationBottom());
        } catch (NumberFormatException e) {
            log.error("Error when converting boundary temp equation to constant");
            throw new RuntimeException("AnalyticalHeatEquationCore: boundary temp must be constant", e);
        }

        // Вычисляем коэффициенты ряда Фурье
        C_coefficients = new double[harmonicCount + 1];  //сделана на один элемент больше, 0ой индекс некогда не заполняется и не используется
        double c1 = 2 / Math.PI;
        double c2 = plateParams.getNumeralParameters().plateTemperature() - g1;
        double c3 = g2 - g1;
        for (int i = 1; i <= harmonicCount; i++) {
            C_coefficients[i] = (c1 / i) * ((c2 * (1 - (i % 2 == 0 ? 1 : -1))) + (c3 * (i % 2 == 0 ? 1 : -1)));
        }

        tMapColumn = new double[ny];
    }


    public void stepTo(double time) { // время в секундах с начала эксперимента
        double c1 = (g2 - g1) / plateParams.getNumeralParameters().height();

        // Граничные условия
        tMapColumn[0] = g2;
        tMapColumn[ny - 1] = g1;

        for (int sy = 1; sy < ny - 1; sy++) { // sy = step y (внутренние точки)
            // для y характерно - верх (sy=0), низ (sy=ny-1)
            double y = plateParams.getNumeralParameters().height() - sy * dy;

            double row = 0; //считаем ряд Фурье
            for (int i = 1; i <= harmonicCount; i++) {
                row += C_coefficients[i]
                        * Math.sin((i * Math.PI * y) / plateParams.getNumeralParameters().height())
                        * Math.exp(-thermalDiffusivity
                        * Math.pow((i * Math.PI) / plateParams.getNumeralParameters().height(), 2)
                        * time);
            }
            tMapColumn[sy] = g1 + c1 * y + row;
        }
    }

    public double[] gettMapColumn() {
        return tMapColumn;
    }
}
