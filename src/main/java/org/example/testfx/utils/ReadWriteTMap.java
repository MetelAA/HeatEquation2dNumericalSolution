package org.example.testfx.utils;


import java.io.*;

public class ReadWriteTMap {
    private final File file;
    private FileWriter writer;
    private FileReader reader;

    public ReadWriteTMap(String fileName) {
        file = new File(fileName);
        System.out.println("ReadWriteTMap успешно создан, запись будет проводиться в " + file.getAbsolutePath());
    }

    public void initWriter() throws IOException {
        writer = new FileWriter(file, true);
    }

    public void writeMap(double[][] tMap, int step) throws IOException {
        writer.write("---------" + step + "---------");
        for (double[] doubles : tMap) {
            for (int j = 0; j < tMap[0].length; j++) {
                writer.write(Double.toString(doubles[j]) + " ");
            }
            writer.append('\n');
        }
        writer.flush();
    }
}
