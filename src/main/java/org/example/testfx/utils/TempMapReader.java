package org.example.testfx.utils;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;

import java.io.*;

public class TempMapReader {
    private final static Logger log = LogManager.getLogger(TempMapReader.class);
    private final File file;
    private BufferedReader reader;
    private final int rows;
    private final int cols;

    public TempMapReader(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        file = new File(Constants.TEMP_MAP_FILE_LOCATION);
        log.info("TempMapReader успешно создан, чтение будет проводиться из {}", file.getAbsolutePath());
    }

    public static ExperimentalNMapParameters getParams() throws FileNotFoundException {
        FileReader paramReader = new FileReader(Constants.TEMP_MAP_PARAMS_FILE_LOCATION);
        Gson gson = new Gson();
        return gson.fromJson(paramReader, ExperimentalNMapParameters.class);
    }

    public void initReader() throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }

    public double[][] readNextStep() throws IOException {
        if (reader == null) initReader();

        String line;
        while ((line = reader.readLine()) != null && !line.startsWith("---------")) {
            // пропускаем мусор до заголовка
        }
        if (line == null) return null;

        int frameNumber = extractFrameNumber(line);
        log.debug("Reading frame number: |{}|", frameNumber);

        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            line = reader.readLine();
            if (line == null || line.startsWith("---------")) break;
            String[] tokens = line.trim().split(" ");
            for (int j = 0; j < cols; j++) {
                result[i][j] = Double.parseDouble(tokens[j]);
            }
        }
        return result;
    }

    private int extractFrameNumber(String headerLine) {
        String numberStr = headerLine.replaceAll("-", "");
        return Integer.parseInt(numberStr);
    }

    public void skipFrames(int n) throws IOException {
        if (reader == null) initReader();

        for (int i = 0; i < n; i++) {
            String line;
            while ((line = reader.readLine()) != null && !line.startsWith("---------")) {
            }
            if (line == null) break;

            for (int r = 0; r < rows; r++) {
                reader.readLine();
            }
        }
    }

    public void closeReader() throws IOException {
        if (reader != null) reader.close();
    }
}