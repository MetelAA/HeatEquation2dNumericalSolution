package org.example.testfx.Compare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.Utils.FileUtils.GNUPlotGraphicsWriter;
import org.example.testfx.Utils.FileUtils.TempMapReader;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class MaxTemperatureDifferenceGraphicBuilder {
    private final static Logger log = LogManager.getLogger(MaxTemperatureDifferenceGraphicBuilder.class);

    public void buildGraphic(ExperimentNMapParameters params) {
        log.info("MaxTemperatureDifferenceGraphicBuilder start data processing");
        TempMapReaderWrapper numReaderWrapper;
        TempMapReaderWrapper analyticalReaderWrapper;
        {
            TempMapReader numReader = new TempMapReader(params.getNy(), params.getNx(), Constants.TEMP_MAP_FOR_NUM_METHOD_FILE_LOCATION);
            TempMapReader analyticalReader = new TempMapReader(params.getNy(), params.getNx(), Constants.TEMP_MAP_FOR_ANALYTICAL_METHOD_FILE_LOCATION);
            numReaderWrapper = new TempMapReaderWrapper(numReader, params.getWroteFramesCount(), 1);
            analyticalReaderWrapper = new TempMapReaderWrapper(analyticalReader, params.getWroteFramesCount(), 1);
        }

        double[] maxDif = new double[params.getWroteFramesCount()];
        double[] timestamps = new double[params.getWroteFramesCount()];
        for(int i = 0; i < params.getWroteFramesCount(); i++){
            try {
                Optional<double[][]> numMap = numReaderWrapper.getNextFrame();
                Optional<double[][]> analyticalMap = analyticalReaderWrapper.getNextFrame();

                if(numMap.isEmpty() || analyticalMap.isEmpty()){
                    throw new RuntimeException("Error when reading next frame for max diff graphic build, some map is empty, numMap isEmpty?: " + numMap.isEmpty() + ", analyticalMap isEmpty?: " + analyticalMap.isEmpty());
                }
                timestamps[i] = i / params.getExParams().getSimulationParameters().getFrameWritesPerSecond();
                maxDif[i] = getMaxDif(numMap.get(), analyticalMap.get());
            } catch (IOException e) {
                throw new RuntimeException("Error when reading next frame for max diff graphic build, with message: " + e);
            }
        }

        try {
            GNUPlotGraphicsWriter writer = new GNUPlotGraphicsWriter();
            writer.initWriter(Constants.MAX_TEMPERATURE_DIFFERENCE_GRAPHIC_DATA_FILE_LOCATION);
            writer.writeData(maxDif, timestamps);
            writer.closeWriter();
        } catch (IOException e) {
            throw new RuntimeException("Error when writing max diff data to file, with message: " + e);
        }
        log.info("MaxTemperatureDifferenceGraphicBuilder successfully wrote all data");
    }

    private double getMaxDif(double[][] fTMap, double[][] sTMap){ //разность будем всегда брать по модулю
        double maxDif = 0;
        for (int i = 0; i < fTMap.length; i++) {
            for (int j = 0; j < fTMap[0].length; j++) {
                maxDif = Math.max(maxDif, Math.abs(fTMap[i][j] - sTMap[i][j]));
            }
        }
        return maxDif;
    }
}
