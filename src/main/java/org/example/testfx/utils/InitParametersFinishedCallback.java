package org.example.testfx.utils;

import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;

public interface InitParametersFinishedCallback {
    void callback(PlateParameters parameters, SimulationParameters simulationParameters);
}
