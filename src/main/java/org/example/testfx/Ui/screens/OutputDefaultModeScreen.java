package org.example.testfx.Ui.screens;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.example.testfx.Ui.Component.HeatmapComponent;
import org.example.testfx.Ui.Screen;

public class OutputDefaultModeScreen implements Screen {
    private final BorderPane root;
    private final HeatmapComponent heatmap;

    public OutputDefaultModeScreen(double width, double height, double minT, double maxT, double[][] tMap) {
        root = new BorderPane();
        heatmap = new HeatmapComponent(width, height, minT, maxT, tMap);
        root.setCenter(heatmap);
    }

    @Override
    public Parent getView() {
        return root;
    }
}
