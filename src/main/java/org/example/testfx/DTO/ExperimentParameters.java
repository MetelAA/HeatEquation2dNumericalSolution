package org.example.testfx.DTO;

public class ExperimentParameters {
    private PlateParameters plateParameters;
    private SimulationParameters simulationParameters;

    public ExperimentParameters() {
    }

    public ExperimentParameters(PlateParameters plateParameters, SimulationParameters simulationParameters) {
        this.plateParameters = plateParameters;
        this.simulationParameters = simulationParameters;
    }

    public PlateParameters getPlateParameters() {
        return plateParameters;
    }

    public SimulationParameters getSimulationParameters() {
        return simulationParameters;
    }
}
