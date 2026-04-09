package org.example.testfx.utils;


import java.io.*;

public class ReadWriteTemperatureMap {
    private final File file;
    private FileWriter writer;
    private FileReader reader;

    public ReadWriteTemperatureMap(String fileName) {
        file = new File(fileName);
        System.out.println("ReadWriteTMap успешно создан, запись будет проводиться в " + file.getAbsolutePath());
    }

    public void initWriter() throws IOException {
        writer = new FileWriter(file, false);
    }

    public void writeMap(double[][] tMap, int step) throws IOException {
        writer.write("---------" + step + "---------\n");
        for (double[] doubles : tMap) {
            for (int j = 0; j < tMap[0].length; j++) {
                writer.write(Double.toString(doubles[j]) + " ");
            }
            writer.append('\n');
        }
        writer.flush();
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}
