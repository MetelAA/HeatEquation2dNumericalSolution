package org.example.testfx.Ui.Screens;

import javafx.scene.Parent;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.Ui.Component.HeatmapPlayerComponent;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

public class OutputDefaultModeScreen implements Screen {
    private final static Logger log = LogManager.getLogger(OutputDefaultModeScreen.class);
    private final BorderPane root;

    public OutputDefaultModeScreen(TempMapReaderWrapper framesSupplier, ExperimentNMapParameters params) {
        root = new BorderPane();
        log.info("starting ");

        HeatmapPlayerComponent playerComponent = new HeatmapPlayerComponent(framesSupplier, params, HeatmapPlayerComponent.HeatmapPlayerComponentWorkMods.STATIC_GRADIENT);

        root.setCenter(playerComponent);
    }





    @Override
    public Parent getView() {
        return root;
    }
}