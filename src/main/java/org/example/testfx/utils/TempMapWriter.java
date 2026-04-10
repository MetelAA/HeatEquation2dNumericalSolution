package org.example.testfx.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;

import java.io.*;

public class TempMapWriter {
    private final static Logger log = LogManager.getLogger(TempMapWriter.class);
    private final File file;
    private BufferedWriter writer;

    public TempMapWriter() {
        file = new File(Constants.TEMP_MAP_FILE_LOCATION);
        log.debug("TempMapWriter успешно создан, запись будет проводиться в {}", file.getAbsolutePath());
    }

    public static void writeExperimentalNMapParameters(ExperimentalNMapParameters exParams) throws IOException {
        FileWriter paramWriter = new FileWriter(Constants.TEMP_MAP_PARAMS_FILE_LOCATION, false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        paramWriter.write(gson.toJson(exParams));
        paramWriter.flush();
        paramWriter.close();
    }

    public void initWriter() throws IOException {
        writer = new BufferedWriter(new FileWriter(file, false));
    }

    public void writeMap(double[][] tMap, int step) throws IOException {
        if (writer == null) initWriter();
        writer.write("---------" + step + "---------\n");
        for (double[] doubles : tMap) {
            for (int j = 0; j < tMap[0].length; j++) {
                writer.write(Double.toString(doubles[j]) + " ");
            }
            writer.newLine();
        }
        writer.flush();
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}