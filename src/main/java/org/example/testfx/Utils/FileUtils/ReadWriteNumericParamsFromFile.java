package org.example.testfx.Utils.FileUtils;

import com.google.gson.*;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.CompareNExperimentParameters;
import org.example.testfx.DTO.ExperimentParameters;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.Exceptions.ParameterFileParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadWriteNumericParamsFromFile {
    public static ExperimentParameters readSimulationParameters() throws ParameterFileParseException {
        FileReader reader = null;
        try {
            reader = new FileReader(Constants.PARAMS_FILE_LOCATION);
            Gson gson = new Gson();
            return gson.fromJson(reader, ExperimentParameters.class);
        } catch (FileNotFoundException | JsonSyntaxException | JsonIOException e) {
            throw new ParameterFileParseException("Error when reading simulationParameters, with message: " + e);
        }
    }

    public static void writeSimulationParameters(ExperimentParameters exParams) throws ParameterFileParseException {
        try {
            FileWriter writer = new FileWriter(Constants.PARAMS_FILE_LOCATION);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(exParams));
            writer.flush();
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            throw new ParameterFileParseException("Error when writing compareParameters, with message: " + e);
        }
    }

    public static void writeCompareParameters(CompareNExperimentParameters params) throws ParameterFileParseException {
        try {
            FileWriter writer = new FileWriter(Constants.COMPARE_PARAMS_FILE_LOCATION);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(params));
            writer.flush();
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            throw new ParameterFileParseException("Error when writing compareParameters, with message: " + e);
        }
    }

    public static CompareNExperimentParameters readCompareParameters() throws ParameterFileParseException {
        try {
            FileReader reader = new FileReader(Constants.COMPARE_PARAMS_FILE_LOCATION);
            Gson gson = new Gson();
            return gson.fromJson(reader, CompareNExperimentParameters.class);
        } catch (FileNotFoundException | JsonSyntaxException | JsonIOException e) {
            throw new ParameterFileParseException("Error when reading compareParameters, with message: " + e);
        }
    }
}
