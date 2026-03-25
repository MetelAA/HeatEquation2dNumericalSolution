package org.example.testfx.DTO;

public class PlateParameters {
    private NumeralInitialPlateParameters numeralParameters;
    private String boundaryTemperatureEquationTop;
    private String boundaryTemperatureEquationBottom;

    public PlateParameters() {
    }

    public void setNumeralParameters(NumeralInitialPlateParameters numeralParameters) {
        this.numeralParameters = numeralParameters;
    }

    public void setBoundaryTemperatureEquationTop(String boundaryTemperatureEquationTop) {
        this.boundaryTemperatureEquationTop = boundaryTemperatureEquationTop;
    }

    public void setBoundaryTemperatureEquationBottom(String boundaryTemperatureEquationBottom) {
        this.boundaryTemperatureEquationBottom = boundaryTemperatureEquationBottom;
    }


    public NumeralInitialPlateParameters getNumeralParameters() {
        return numeralParameters;
    }

    public String getBoundaryTemperatureEquationTop() {
        if (numeralParameters == null || boundaryTemperatureEquationTop == null || boundaryTemperatureEquationBottom == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationTop;
    }

    public String getBoundaryTemperatureEquationBottom() {
        if (numeralParameters == null || boundaryTemperatureEquationTop == null || boundaryTemperatureEquationBottom == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationBottom;
    }


    @Override
    public String toString() {
        return "InitialPlateParameters{" +
                "numeralParameters=" + numeralParameters +
                ", boundaryTemperatureEquationTop='" + boundaryTemperatureEquationTop + '\'' +
                ", boundaryTemperatureEquationBottom='" + boundaryTemperatureEquationBottom + '\'' +
                '}';
    }
}