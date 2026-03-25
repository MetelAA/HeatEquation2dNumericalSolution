package org.example.testfx.DTO;

public record NumeralInitialPlateParameters(double width, double height, double density, double heatCapacity, double heatConductivity, double plateTemperature) {
    @Override
    public String toString() {
        return "NumeralInitialPlateParameters{" +
                "width=" + width +
                ", height=" + height +
                ", density=" + density +
                ", heatCapacity=" + heatCapacity +
                ", heatConductivity=" + heatConductivity +
                ", plateTemperature=" + plateTemperature +
                '}';
    }
}
