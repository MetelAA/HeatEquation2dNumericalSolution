package org.example.testfx.Utils.FileUtils;

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
    private int currentFrameNumber = -1;

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
        currentFrameNumber = 0;
    }

    public double[][] readNextFrame() throws IOException {
        if (reader == null) initReader();

        String line;
        while ((line = reader.readLine()) != null && !line.startsWith("---------")) {
        }
        if (line == null) return null;

        currentFrameNumber = extractFrameNumber(line);

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

        int skipped = 0;
        for (int i = 0; i < n; i++) {
            String line;
            while ((line = reader.readLine()) != null && !line.startsWith("---------")) {
            }
            if (line == null) break;

            for (int r = 0; r < rows; r++) {
                if (reader.readLine() == null) break;
            }
            skipped++;
        }
        currentFrameNumber += skipped;
    }

    public void goBackFrames(int n) throws IOException {
        if (n <= 0) {
            return;
        }

        int targetFrame = Math.max(0, currentFrameNumber - n);
        if (reader != null) {
            reader.close();
            reader = null;
        }

        initReader();
        if (targetFrame > 0) {
            skipFrames(targetFrame);
        }
    }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

    public void closeReader() throws IOException {
        if (reader != null) reader.close();
    }
}