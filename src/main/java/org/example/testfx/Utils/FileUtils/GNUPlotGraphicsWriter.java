package org.example.testfx.Utils.FileUtils;

import java.io.FileWriter;
import java.io.IOException;

public class GNUPlotGraphicsWriter {
    private FileWriter writer;

    public GNUPlotGraphicsWriter() {
    }

    public void initWriter(String fileName) throws IOException {
        writer = new FileWriter(fileName);
    }

    public void writeData(double[] data, double[] time) throws IOException {
        for (int i = 0; i < data.length; i++){
            writer.write(time[i] + " " + data[i]);
        }
        writer.flush();
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}
