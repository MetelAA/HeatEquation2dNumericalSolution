package org.example.testfx.Ui.Screens;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.testfx.DTO.ExperimentNMapParameters;
import org.example.testfx.Ui.Component.HeatmapPlayerComponent;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Utils.DefaultCallback;
import org.example.testfx.Utils.TempMapDataProducer;

public class OutputCompareModFirstScreen implements Screen {
    private final BorderPane root;
    private final DefaultCallback callback;

    public OutputCompareModFirstScreen(TempMapDataProducer frameDataProducer, ExperimentNMapParameters params, DefaultCallback callback) {
        this.callback = callback;
        root = new BorderPane();

        VBox v1 = new VBox();
        HeatmapPlayerComponent playerComponent = new HeatmapPlayerComponent(frameDataProducer, params, -10, 10, HeatmapPlayerComponent.HeatmapPlayerComponentWorkMods.DYNAMIC_GRADIENT);

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
