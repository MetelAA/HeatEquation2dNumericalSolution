package org.example.testfx.Ui;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ScreenSwitcher {
    private final Stage stage;

    public ScreenSwitcher(Stage stage) {
        this.stage = stage;
        if (stage.getScene() == null) {
            stage.setScene(new Scene(new StackPane(), 950, 700));
        }
    }

    public void show(Screen screen) {
        Scene scene = stage.getScene();

        Parent root = screen.getView();
        if (root == null) {
            throw new IllegalArgumentException("Screen.getView() returned null");
        }
        scene.setRoot(root);
        stage.sizeToScene();
    }
}
