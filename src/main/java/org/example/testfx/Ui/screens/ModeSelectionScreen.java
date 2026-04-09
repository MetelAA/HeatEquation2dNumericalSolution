package org.example.testfx.Ui.screens;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.Ui.Screen;

public class ModeSelectionScreen implements Screen {
    private final BorderPane root;

    public ModeSelectionScreen(Runnable defaultMode, Runnable analyticalVsNumericCompare){
        root = new BorderPane();

        VBox verticalBox = new VBox();
        verticalBox.setAlignment(Pos.CENTER);
        verticalBox.setSpacing(20);

        Text text = new Text("Выберете режим работы программы:");

        Button numMeth = new Button("Обычный");
        Button compare = new Button("Сравнение аналитического и численного решения");

        numMeth.setOnAction((e)->{
            defaultMode.run();
        });
        compare.setOnAction((e)->{
            analyticalVsNumericCompare.run();
        });

        verticalBox.getChildren().addAll(text, numMeth, compare);
        root.setCenter(verticalBox);
    }

    @Override
    public Parent getView() {
        return root;
    }
}
