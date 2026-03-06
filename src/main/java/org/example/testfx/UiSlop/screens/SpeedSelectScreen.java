package org.example.testfx.UiSlop.screens;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.example.testfx.UiSlop.Screen;

import java.util.function.Consumer;

public class SpeedSelectScreen implements Screen {

    private final BorderPane root;
    private final Consumer<Double> callback;

    public SpeedSelectScreen(Consumer<Double> callback) {
        this.callback = callback;
        root = new BorderPane();

        HBox horizontalButtonLayout = new HBox();
        Button lessSpeedBtn = new Button("0.5x");
        Button normalSpeedBtn = new Button("1x");
        Button upSpeedBtn = new Button("5x");
        Button highSpeedBtn = new Button("10x");

        lessSpeedBtn.setOnAction(this::handler);
        normalSpeedBtn.setOnAction(this::handler);
        upSpeedBtn.setOnAction(this::handler);
        highSpeedBtn.setOnAction(this::handler);
        horizontalButtonLayout.setAlignment(Pos.CENTER);
        horizontalButtonLayout.setSpacing(20);

        horizontalButtonLayout.getChildren().addAll(lessSpeedBtn, normalSpeedBtn, upSpeedBtn, highSpeedBtn);
        root.setCenter(horizontalButtonLayout);
    }

    private void handler(ActionEvent actionEvent){
        if(actionEvent.getSource() != null && actionEvent.getSource() instanceof Button clickedBtn){
            callback.accept(Double.parseDouble(clickedBtn.getText().split("x")[0]));
        }
    }

    @Override
    public Parent getView() {
        return root;
    }
}
