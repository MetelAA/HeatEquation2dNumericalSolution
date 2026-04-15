package org.example.testfx.utils;

import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;

public interface InitParametersForCompareFinishedCallback {
    void callback(PlateParameters plateParams, SimulationParameters simParams, int hormonicCount);
}
