package org.example.testfx.Ui.Screens;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.Component.HeatmapComponent;
import org.example.testfx.Ui.Component.HeatmapPlaybackThread;
import org.example.testfx.Ui.Component.HeatmapPlayerComponent;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Utils.DefaultCallback;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class OutputDefaultModeScreen implements Screen {
    private final static Logger log = LogManager.getLogger(OutputDefaultModeScreen.class);
    private final BorderPane root;

    public OutputDefaultModeScreen(TempMapReaderWrapper framesSupplier, ExperimentalNMapParameters params) {
        root = new BorderPane();

        HeatmapPlayerComponent playerComponent = new HeatmapPlayerComponent(framesSupplier, params);

        root.setCenter(playerComponent);
    }





    @Override
    public Parent getView() {
        return root;
    }
}