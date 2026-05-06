package org.example.testfx.DTO;

public class CompareNExperimentParameters {
    private ExperimentParameters exParams;
    private int hormonicCount;

    public CompareNExperimentParameters() {
    }

    public CompareNExperimentParameters(ExperimentParameters exParams, int hormonicCount) {
        this.exParams = exParams;
        this.hormonicCount = hormonicCount;
    }

    public ExperimentParameters getExParams() {
        return exParams;
    }

    public int getHormonicCount() {
        return hormonicCount;
    }

    public void setExParams(ExperimentParameters exParams) {
        this.exParams = exParams;
    }

    public void setHormonicCount(int hormonicCount) {
        this.hormonicCount = hormonicCount;
    }

    @Override
    public String toString() {
        return "CompareNExperimentParameters{" +
                "exParams=" + exParams +
                ", hormonicCount=" + hormonicCount +
                '}';
    }
}