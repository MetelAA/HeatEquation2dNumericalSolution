package org.example.testfx.DTO;

public class PlateParameters {
    private NumeralInitialPlateParameters numeralParameters;
    private String boundaryTemperatureEquationUp;
    private String boundaryTemperatureEquationBottom;

    public PlateParameters() {
    }

    public PlateParameters(NumeralInitialPlateParameters numeralParameters, String boundaryTemperatureEquationUp, String boundaryTemperatureEquationBottom) {
        this.numeralParameters = numeralParameters;
        this.boundaryTemperatureEquationUp = boundaryTemperatureEquationUp;
        this.boundaryTemperatureEquationBottom = boundaryTemperatureEquationBottom;
    }

    public void setNumeralParameters(NumeralInitialPlateParameters numeralParameters) {
        this.numeralParameters = numeralParameters;
    }

    public void setBoundaryTemperatureEquationUp(String boundaryTemperatureEquationUp) {
        this.boundaryTemperatureEquationUp = boundaryTemperatureEquationUp;
    }

    public void setBoundaryTemperatureEquationBottom(String boundaryTemperatureEquationBottom) {
        this.boundaryTemperatureEquationBottom = boundaryTemperatureEquationBottom;
    }


    public NumeralInitialPlateParameters getNumeralParameters() {
        return numeralParameters;
    }

    public String getBoundaryTemperatureEquationUp() {
        if (numeralParameters == null || boundaryTemperatureEquationUp == null || boundaryTemperatureEquationBottom == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationUp;
    }

    public String getBoundaryTemperatureEquationBottom() {
        if (numeralParameters == null || boundaryTemperatureEquationUp == null || boundaryTemperatureEquationBottom == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationBottom;
    }


    @Override
    public String toString() {
        return "InitialPlateParameters{" +
                "numeralParameters=" + numeralParameters +
                ", boundaryTemperatureEquationTop='" + boundaryTemperatureEquationUp + '\'' +
                ", boundaryTemperatureEquationBottom='" + boundaryTemperatureEquationBottom + '\'' +
                '}';
    }
}