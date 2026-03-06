package org.example.testfx.DTO;

public record NumeralInitialPlateParameters(double width, double height, double density, double heatCapacity, double conductivity, double plateTemperature) {
    @Override
    public String toString() {
        return "NumeralInitialPlateParameters{" +
                "conductivity=" + conductivity +
                ", plateTemperature=" + plateTemperature +
                ", heatCapacity=" + heatCapacity +
                ", density=" + density +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
