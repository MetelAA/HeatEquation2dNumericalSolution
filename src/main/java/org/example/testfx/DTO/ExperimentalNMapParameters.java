package org.example.testfx.DTO;

public class ExperimentalNMapParameters {
    private ExperimentParameters exParams;
    private double minTemp, maxTemp;
    private int nt, ny, nx, wroteFramesCount;

    public ExperimentalNMapParameters() {
    }

    public ExperimentalNMapParameters(ExperimentParameters exParams, double minTemp, double maxTemp, int nt, int ny, int nx, int wroteFramesCount) {
        this.exParams = exParams;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.nt = nt;
        this.ny = ny;
        this.nx = nx;
        this.wroteFramesCount = wroteFramesCount;
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

    public int getNt() {
        return nt;
    }

    public int getNy() {
        return ny;
    }

    public int getNx() {
        return nx;
    }

    public int getWroteFramesCount() {
        return wroteFramesCount;
    }

    @Override
    public String toString() {
        return "ExperimentalNMapParameters{" +
                "exParams=" + exParams +
                ", minTemp=" + minTemp +
                ", maxTemp=" + maxTemp +
                ", nt=" + nt +
                ", ny=" + ny +
                ", nx=" + nx +
                ", wroteFramesCount=" + wroteFramesCount +
                '}';
    }
}
