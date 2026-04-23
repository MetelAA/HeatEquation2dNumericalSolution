package org.example.tests;

import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Utils.FileUtils.TempMapReader;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainTest {
    public static void main(String[] args) throws IOException {
        ExperimentalNMapParameters params;
        try {
            params = TempMapReader.getParams();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        TempMapReader reader = new TempMapReader(params.getNy(), params.getNx());
        reader.initReader();

        TempMapReaderWrapper wrap = new TempMapReaderWrapper(reader, params.getWroteFramesCount(), 1);


        wrap.getNextFrame();
        wrap.getNextFrame();

        wrap.goToNGetFrame(5);

        wrap.setPointerToLastFrame();
    }
}
