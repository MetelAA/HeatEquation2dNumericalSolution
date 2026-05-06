package org.example.testfx.Utils.FileUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class GNUPlotGraphicsWriter {
    private final static Logger log = LogManager.getLogger(GNUPlotGraphicsWriter.class);
    private FileWriter writer;

    public GNUPlotGraphicsWriter() {
    }

    public void initWriter(String fileName) throws IOException {
        log.info("Запись будет производиться в {}", fileName);
        writer = new FileWriter(fileName);
    }

    public void writeData(double[] data, double[] time) throws IOException {
        for (int i = 0; i < data.length; i++){
            writer.write(time[i] + " " + data[i] + '\n');
        }
        writer.flush();
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}
