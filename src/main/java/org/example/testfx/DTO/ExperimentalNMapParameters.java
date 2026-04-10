package org.example.testfx.DTO;

public class ExperimentalNMapParameters {
    private ExperimentParameters exParams;
    private double minTemp, maxTemp;
    private int dt, ny, nx;

    public ExperimentalNMapParameters() {
    }

    public ExperimentalNMapParameters(ExperimentParameters exParams, double minTemp, double maxTemp, int dt, int ny, int nx) {
        this.exParams = exParams;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.dt = dt;
        this.ny = ny;
        this.nx = nx;
    }

    public ExperimentParameters getExParams() {
        return exParams;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public int getDt() {
        return dt;
    }

    public int getNy() {
        return ny;
    }

    public int getNx() {
        return nx;
    }

    @Override
    public String toString() {
        return "ExperimentalNMapParameters{" +
                "exParams=" + exParams +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", dt=" + dt +
                ", ny=" + ny +
                ", nx=" + nx +
                '}';
    }
}
