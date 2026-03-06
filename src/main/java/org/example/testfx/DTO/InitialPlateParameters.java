package org.example.testfx.DTO;

public class InitialPlateParameters {
    private NumeralInitialPlateParameters numeralParameters;
    private String boundaryTemperatureEquationTop;
    private String boundaryTemperatureEquationBottom;
    private Double modelSpeed;

    public InitialPlateParameters() {
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

    public void setModelSpeed(Double modelSpeed) {
        this.modelSpeed = modelSpeed;
    }

    public NumeralInitialPlateParameters getNumeralParameters() {
        return numeralParameters;
    }

    public String getBoundaryTemperatureEquationTop() {
        if (numeralParameters == null || boundaryTemperatureEquationTop == null || boundaryTemperatureEquationBottom == null || modelSpeed == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationTop;
    }

    public String getBoundaryTemperatureEquationBottom() {
        if (numeralParameters == null || boundaryTemperatureEquationTop == null || boundaryTemperatureEquationBottom == null || modelSpeed == null)
            throw new NullPointerException("one of fields is empty");
        return boundaryTemperatureEquationBottom;
    }

    public Double getModelSpeed() {
        if (numeralParameters == null || boundaryTemperatureEquationTop == null || boundaryTemperatureEquationBottom == null || modelSpeed == null)
            throw new NullPointerException("one of fields is empty");
        return modelSpeed;
    }

    @Override
    public String toString() {
        return "InitialPlateParameters{" +
                "numeralParameters=" + numeralParameters +
                ", boundaryTemperatureEquationTop='" + boundaryTemperatureEquationTop + '\'' +
                ", boundaryTemperatureEquationBottom='" + boundaryTemperatureEquationBottom + '\'' +
                ", modelSpeed=" + modelSpeed +
                '}';
    }
}