package org.example.testfx.Utils;

import org.example.testfx.Exceptions.CompareArraysLengthNotEqualException;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.IOException;
import java.util.Optional;

public class TempMapsComparator implements TempMapDataProducer {
    private final TempMapReaderWrapper dataReaderNum;
    private final TempMapReaderWrapper dataReaderAnalytical;

    public TempMapsComparator(TempMapReaderWrapper dataReaderNum, TempMapReaderWrapper dataReaderAnalytical) {
        this.dataReaderNum = dataReaderNum;
        this.dataReaderAnalytical = dataReaderAnalytical;
    }

    @Override
    public Optional<double[][]> getNextFrame() throws IOException {
        return compare(dataReaderNum.getNextFrame(), dataReaderAnalytical.getNextFrame());
    }

    @Override
    public void setPointerToLastFrame() throws IOException {
        dataReaderNum.setPointerToLastFrame();
        dataReaderAnalytical.setPointerToLastFrame();
    }

    @Override
    public Optional<double[][]> goToNGetFrame(int frameNumber) throws IOException {
        return compare(dataReaderNum.goToNGetFrame(frameNumber), dataReaderAnalytical.goToNGetFrame(frameNumber));
    }

    @Override
    public void changeFrameStepMultiplier(int stepMultiplier) {
        dataReaderAnalytical.changeFrameStepMultiplier(stepMultiplier);
        dataReaderNum.changeFrameStepMultiplier(stepMultiplier);
    }

    @Override
    public int getCurrentFrameNumber() {
        return dataReaderNum.getCurrentFrameNumber();
    }

    private Optional<double[][]> compare(Optional<double[][]> dataNum, Optional<double[][]> dataAnalytical){
        if(dataNum.isEmpty() || dataAnalytical.isEmpty()){
            return Optional.empty();
        }
        if(dataNum.get().length != dataAnalytical.get().length) throw new CompareArraysLengthNotEqualException("dataNum length not equals dataAnalytical length, illegal behavior");
        if(dataNum.get()[0].length != dataAnalytical.get()[0].length) throw new CompareArraysLengthNotEqualException("dataNum nested array length not equals dataAnalytical nested array length, illegal behavior");
//кидаем нечекабельные исключения, т.к. ошибка на более раннем этапе, и ошибка в коде в логике записи или что-то около, ну короче, уже не исправить, значит падаем

        double[][] cmpRes = new double[dataNum.get().length][dataNum.get()[0].length];
        for(int i = 0; i < dataNum.get().length; i++){
            for (int j = 0; j < dataNum.get()[0].length; j++){
                cmpRes[i][j] = dataAnalytical.get()[i][j] - dataNum.get()[i][j];
            }
        }
        return Optional.of(cmpRes);
    }
}
