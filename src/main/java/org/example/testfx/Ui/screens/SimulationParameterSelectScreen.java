package org.example.testfx.Ui.screens;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.Ui.Screen;

import java.util.function.Consumer;

public class SimulationParameterSelectScreen implements Screen {

    private final BorderPane root;
    private final Consumer<SimulationParameters> callback;

    public SimulationParameterSelectScreen(Consumer<SimulationParameters> callback) {
        this.callback = callback;
        root = new BorderPane();

        VBox vl = new VBox();

        Text dtText = new Text("Введите шаг по времени, dt:");
        Text dxText = new Text("Введите шаг по горизонтали, dx:");
        Text dyText = new Text("Введите шаг по вертикали, dy:");
        Text timeText = new Text("Введите длину симуляции, time:");

        TextField dtField = new TextField();
        TextField dxField = new TextField();
        TextField dyField = new TextField();
        TextField timeField = new TextField();

        Text errorText = new Text();
        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button validateBtn = new Button("Далее");

        validateBtn.setOnAction(e ->
            {
                try{
                    callback.accept(validateParameters(
                            dtField.getText(),
                            dxField.getText(),
                            dyField.getText(),
                            timeField.getText()
                    ));
                }catch(Exception ex) {
                    errorText.setText(ex.getMessage());
                }
            }
        );

        vl.getChildren().addAll(dtText, dtField, dxText, dxField, dyText, dyField, timeText, timeField);

        HBox buttonAndErrorBox = new HBox();
        buttonAndErrorBox.getChildren().addAll(validateBtn, errorText);

        vl.getChildren().add(buttonAndErrorBox);
        buttonAndErrorBox.setStyle("-fx-spacing: 35px;");

        vl.setStyle("-fx-padding: 20; -fx-spacing: 15px;");

        root.setCenter(vl);
    }

    private SimulationParameters validateParameters(String dtStr, String dxStr, String dyStr, String timeStr) throws IllegalArgumentException, NumberFormatException {
        double dt = fromStrToDouble(dtStr, "dt");
        double dx = fromStrToDouble(dxStr, "dx");
        double dy = fromStrToDouble(dyStr, "dy");
        long time = fromStrToLong(timeStr, "time");

        if (dt <= 0)
            throw new IllegalArgumentException("dt должен быть положительным числом, но получено: " + dt);

        if (dx <= 0)
            throw new IllegalArgumentException("dx должен быть положительным числом, но получено: " + dx);

        if (dy <= 0)
            throw new IllegalArgumentException("dy должен быть положительным числом, но получено: " + dy);

        if (time < 0)
            throw new IllegalArgumentException("time не может быть отрицательным, но получено: " + time);

        return new SimulationParameters(dt, dx, dy, time);
    }

    private double fromStrToDouble(String str, String fieldName) throws NumberFormatException{
        str = str.trim();
        if (str.isEmpty())
            throw new NumberFormatException("Пустое значение в графе " + fieldName);
        str = str.replace(',', '.');
        try{
            return Double.parseDouble(str);
        }catch(Exception e){
            throw new NumberFormatException("Ошибка конвертации значения графы " + fieldName);
        }
    }

    private long fromStrToLong(String str, String fieldName){
        str = str.trim();
        if (str.isEmpty())
            throw new NumberFormatException("Пустое значение в графе " + fieldName);
        str = str.replace(',', '.');
        try{
            return Long.parseLong(str);
        }catch(Exception e){
            throw new NumberFormatException("Ошибка конвертации значения графы " + fieldName);
        }
    }

    @Override
    public Parent getView() {
        return root;
    }
}
