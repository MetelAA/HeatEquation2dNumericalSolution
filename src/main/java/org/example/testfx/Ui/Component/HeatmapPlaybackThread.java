package org.example.testfx.Ui.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;

public class HeatmapPlaybackThread extends Thread{
    private final static Logger log = LogManager.getLogger(HeatmapPlaybackThread.class);
    private PlaybackStatus status;
    private final HeatmapComponent heatmap;
    private final Supplier<Optional<double[][]>> nextFrameSupplier;
    private long frameChangeInterval;

    public HeatmapPlaybackThread(HeatmapComponent heatmap, Supplier<Optional<double[][]>> nextFrameSupplier, double interval) {
        this.heatmap = heatmap;
        this.nextFrameSupplier = nextFrameSupplier;
        this.frameChangeInterval = Math.round(interval * 1000);
        status = PlaybackStatus.PAUSE;
    }

    @Override
    public void run() {
        log.info("playback thread started with status: |{}|, frameChangeInterval: |{}|mills", status, frameChangeInterval);
        while(!this.isInterrupted()){
            if(status.equals(PlaybackStatus.PLAY)){
                Optional<double[][]> frame = nextFrameSupplier.get();
                if (frame.isEmpty()){
                    status = PlaybackStatus.END;
                    continue;
                }
                heatmap.updateFrame(frame.get());
                sleepT(frameChangeInterval);
            }else if(status.equals(PlaybackStatus.PAUSE) || status.equals(PlaybackStatus.END)){
                sleepT(frameChangeInterval*10);
            }
        }
    }

    private void sleepT(long mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error when trying to sleep in playback thread, with message: " + e);
        }
    }

    public void changeFrameChangeInterval(double interval){
        this.frameChangeInterval = Math.round(interval * 1000);
    }

    public void setStatus(PlaybackStatus status) {
        this.status = status;
    }


    public enum PlaybackStatus{
        PLAY, PAUSE, END
    }
}
