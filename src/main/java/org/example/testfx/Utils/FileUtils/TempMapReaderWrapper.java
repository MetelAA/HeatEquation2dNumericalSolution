package org.example.testfx.Utils.FileUtils;

import java.io.IOException;
import java.util.Optional;

public class TempMapReaderWrapper {
    private final TempMapReader reader;
    private final int frameCount;
    private int frameStepMultiplier;


    public TempMapReaderWrapper(TempMapReader reader, int frameCount, int frameStepMultiplier) {
        this.reader = reader;
        this.frameCount = frameCount;
        this.frameStepMultiplier = frameStepMultiplier;
    }

    public Optional<double[][]> getNextFrame() throws IOException {
        reader.skipFrames(frameStepMultiplier-1);
        return Optional.ofNullable(reader.readNextFrame());
    }

    public void setPointerToLastFrame() throws IOException { // устанавливаем указатель на кадр перед последним кадром!
        reader.skipFrames(frameCount-reader.getCurrentFrameNumber()-1);
        frameStepMultiplier = 1; // чтобы не перескочить!
    }

    public Optional<double[][]> goToNGetFrame(int frameNumber) throws IOException {
        if (frameNumber < 0 || frameNumber >= frameCount) return Optional.empty();
        int delta = frameNumber - reader.getCurrentFrameNumber() - 1;
        if (delta > 0) {
            reader.skipFrames(delta);
        } else if (delta < 0) {
            reader.goBackFrames(-delta);
        }
        return Optional.ofNullable(reader.readNextFrame());
    }


    public void changeFrameStepMultiplier(int stepMultiplier){
        this.frameStepMultiplier = stepMultiplier;
    }

    public int getCurrentFrameNumber(){
        return reader.getCurrentFrameNumber();
    }

}
