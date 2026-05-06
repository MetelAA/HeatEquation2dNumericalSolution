package org.example.testfx.Ui.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HeatmapGradientTemperatureDynamicLimitsChecker {
    private static final Logger log = LogManager.getLogger(HeatmapGradientTemperatureDynamicLimitsChecker.class);

    private final ExecutorService executor;
    private final HeatmapComponent heatmap;
    private double minTemperature;
    private double maxTemperature;

    public HeatmapGradientTemperatureDynamicLimitsChecker(HeatmapComponent heatmap, double minTemperature, double maxTemperature) {
        this.heatmap = heatmap;
        executor = Executors.newSingleThreadExecutor();
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public void checkAndIfTrueRedrawGradient(double[][] tMap){
        log.debug("start checking temperature limits");
        executor.submit(() -> {
            double iterMax = Integer.MIN_VALUE, iterMin = Integer.MAX_VALUE;
            for (int i = 0; i < tMap.length; i++){
               for (int j = 0; j < tMap[0].length; j++) {
                   iterMax = Math.max(iterMax, tMap[i][j]);
                   iterMin = Math.min(iterMin, tMap[i][j]);
               }
            }

            boolean hasChanges = false;
            if (minTemperature * (1.0-Constants.DYNAMIC_GRADIENT_TEMPERATURE_PERCENT_DELTA_TO_REDRAW_GRADIENT) > iterMin){
                minTemperature = iterMin;
                hasChanges = true;
            }


            if(maxTemperature * (1.0-Constants.DYNAMIC_GRADIENT_TEMPERATURE_PERCENT_DELTA_TO_REDRAW_GRADIENT) < iterMax){
                maxTemperature = iterMax;
                hasChanges = true;
            }

            if(hasChanges){
                double delta = Math.abs(maxTemperature - minTemperature);
                if (delta < Constants.DYNAMIC_GRADIENT_MINIMUM_ABS_TEMPERATURE_DELTA){
                    double maxMinIncrements = (Constants.DYNAMIC_GRADIENT_MINIMUM_ABS_TEMPERATURE_DELTA - delta) / 2; //делаем приращения на мин макс значения для соответствия константе
                    maxTemperature += maxMinIncrements;
                    minTemperature -= maxMinIncrements;
                }


                log.info("gradient temperature limits has changes!, new minT: |{}|, new maxT: |{}|", minTemperature, maxTemperature);


                heatmap.changeGradientAndTempLimits(
                        Math.floor(minTemperature * 100) / 100,
                        Math.ceil(maxTemperature * 100) / 100
                );

            }



        });
    }

    public void stopExecutor(){
        executor.shutdown();
    }
}
