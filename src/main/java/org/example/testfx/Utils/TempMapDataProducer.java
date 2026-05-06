package org.example.testfx.Utils;

import java.io.IOException;
import java.util.Optional;

public interface TempMapDataProducer {
    Optional<double[][]> getNextFrame() throws IOException;
    void setPointerToLastFrame() throws IOException;
    Optional<double[][]> goToNGetFrame(int frameNumber) throws IOException;
    void changeFrameStepMultiplier(int stepMultiplier);
    int getCurrentFrameNumber();
}
