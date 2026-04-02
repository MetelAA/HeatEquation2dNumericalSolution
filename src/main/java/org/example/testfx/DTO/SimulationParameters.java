package org.example.testfx.DTO;

public class SimulationParameters {

    private double dt;
    private double dx;
    private double dy;
    private long time;

    public SimulationParameters(double dt, double dx, double dy, long time) {
        this.dt = dt;
        this.dx = dx;
        this.dy = dy;
        this.time = time;
    }

    public SimulationParameters() {
    }

    public double getDt() {
        return dt;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SimulationParameter{" +
                "dt=" + dt +
                ", dx=" + dx +
                ", dy=" + dy +
                ", time=" + time +
                '}';
    }
}
