package org.example.testfx.Ui.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.Ui.Screen;

import java.util.function.Consumer;

public class SelectBoundaryTemperatureSelectionTypeScreen implements Screen {
    private final BorderPane root;
    private final Consumer<String> callback;
    private final String edgeType;

    public SelectBoundaryTemperatureSelectionTypeScreen(Consumer<String> callback, String edgeType) {
        this.callback = callback;
        this.edgeType = edgeType;
        root = new BorderPane();

        VBox verticalBox = new VBox();
        Text text = new Text("Выберите способ задачи температуры границы для " + edgeType + " плоскости: ");
        Button bezierCurves = new Button("С помощью кривых Безье");
        bezierCurves.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                callback.accept("Bezier");
            }
        });

        Button equation = new Button("С помощью уравнения");
        equation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                callback.accept("Equation");
            }
        });
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.setSpacing(20);
        verticalBox.getChildren().addAll(text, bezierCurves, equation);

        root.setCenter(verticalBox);
    }

    @Override
    public Parent getView() {
        return root;
    }
}
