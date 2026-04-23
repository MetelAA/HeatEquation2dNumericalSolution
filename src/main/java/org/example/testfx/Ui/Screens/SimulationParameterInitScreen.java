package org.example.testfx.Ui.Screens;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.Ui.Screen;

import java.util.function.Consumer;

public class SimulationParameterInitScreen implements Screen {

    private final BorderPane root;
    private final Consumer<SimulationParameters> callback;

    public SimulationParameterInitScreen(Consumer<SimulationParameters> callback) {
        this.callback = callback;
        root = new BorderPane();

        VBox vl = new VBox();

        Text dtText = new Text("Введите шаг по времени, dt, сек:");
        Text dxText = new Text("Введите шаг по горизонтали, dx, м:");
        Text dyText = new Text("Введите шаг по вертикали, dy, м:");
        Text timeText = new Text("Введите длину симуляции, time, сек:");
        Text framesWriteText = new Text("Введите количество записываемы кадров в секунду, от числа стремящегося к нулю до 1 (осторожно с этим параметром, может нагенирить файл на 200гб за 10минут)");

        TextField dtField = new TextField();
        dtField.setPromptText("сек");
        TextField dxField = new TextField();
        dxField.setPromptText("м");
        TextField dyField = new TextField();
        dyField.setPromptText("м");
        TextField timeField = new TextField();
        timeField.setPromptText("сек");
        TextField framesWritesField = new TextField();
        framesWritesField.setText(String.valueOf(Constants.WRITE_FRAME_PER_SECOND));


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
                            timeField.getText(),
                            framesWritesField.getText()
                    ));
                } catch (IllegalArgumentException ex){
                    errorText.setText(ex.getMessage());
                }
            }
        );

        vl.getChildren().addAll(dtText, dtField, dxText, dxField, dyText, dyField, timeText, timeField, framesWriteText, framesWritesField);

        HBox buttonAndErrorBox = new HBox();
        buttonAndErrorBox.getChildren().addAll(validateBtn, errorText);

        vl.getChildren().add(buttonAndErrorBox);
        buttonAndErrorBox.setStyle("-fx-spacing: 35px;");

        vl.setStyle("-fx-padding: 20; -fx-spacing: 15px;");

        root.setCenter(vl);
    }

    private SimulationParameters validateParameters(String dtStr, String dxStr, String dyStr, String timeStr, String framesPerSecStr) throws IllegalArgumentException, NumberFormatException {
        double dt = fromStrToDouble(dtStr, "dt");
        double dx = fromStrToDouble(dxStr, "dx");
        double dy = fromStrToDouble(dyStr, "dy");
        long time = fromStrToLong(timeStr, "time");
        double framesPerSec = fromStrToDouble(framesPerSecStr, "frames per second");

        if (dt <= 0)
            throw new IllegalArgumentException("dt должен быть положительным числом, но получено: " + dt);

        if (dx <= 0)
            throw new IllegalArgumentException("dx должен быть положительным числом, но получено: " + dx);

        if (dy <= 0)
            throw new IllegalArgumentException("dy должен быть положительным числом, но получено: " + dy);

        if (time < 0)
            throw new IllegalArgumentException("time не может быть отрицательным, но получено: " + time);
        if(framesPerSec > 1 || framesPerSec <= 0)
            throw new IllegalArgumentException("frames pre second должно быть в полуинтервале (0, 1], но получено: " + framesPerSec);

        return new SimulationParameters(dt, dx, dy, time, framesPerSec);
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
