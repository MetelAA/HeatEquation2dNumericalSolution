package org.example.testfx.Ui.Screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.Component.HeatmapPlayerComponent;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Utils.DefaultCallback;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

public class OutputCompareModFirstScreen implements Screen {
    private final BorderPane root;
    private final DefaultCallback callback;

    public OutputCompareModFirstScreen(TempMapReaderWrapper framesSupplier, ExperimentalNMapParameters params, DefaultCallback callback) {
        this.callback = callback;
        root = new BorderPane();

        VBox v1 = new VBox();
        HeatmapPlayerComponent playerComponent = new HeatmapPlayerComponent(framesSupplier, params);

        Button nextScreenBtn = new Button("Дальше");
        nextScreenBtn.setOnAction(actionEvent -> callback.callback());

        HBox buttonWrapper = new HBox(nextScreenBtn);
        buttonWrapper.setAlignment(Pos.CENTER_RIGHT);

        v1.getChildren().addAll(playerComponent, buttonWrapper);
        root.setCenter(v1);

    }

    @Override
    public Parent getView() {
        return root;
    }
}
