package org.example.testfx.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.SimulationParameters;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadWriteNumericParamsFromFile {
    public static ExperimentParameters readSimulationParameters() throws FileNotFoundException {
        FileReader reader = new FileReader(Constants.PARAMS_FILE_LOCATION);
        Gson gson = new Gson();
        return gson.fromJson(reader, ExperimentParameters.class);
    }

    public static void writeSimulationParameters(ExperimentParameters exParams) throws IOException {
        FileWriter writer = new FileWriter(Constants.PARAMS_FILE_LOCATION);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.write(gson.toJson(exParams));
        writer.flush();
    }
}
