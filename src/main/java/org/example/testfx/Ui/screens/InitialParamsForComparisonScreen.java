package org.example.testfx.Ui.screens;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.NumeralInitialPlateParameters;
import org.example.testfx.DTO.PlateParameters;
import org.example.testfx.DTO.SimulationParameters;
import org.example.testfx.Ui.Screen;
import org.example.testfx.utils.InitParametersFinishedCallback;

public class InitialParamsForComparisonScreen implements Screen {
    private final BorderPane root;
    private final InitParametersFinishedCallback callback;

    private final TextField widthTextField = new TextField();
    private final TextField heightTextField = new TextField();
    private final TextField densityTextField = new TextField();
    private final TextField specificHeatCapacityTextField = new TextField();
    private final TextField coefficientOfThermalConductivity = new TextField();
    private final TextField materialTemperatureTextField = new TextField();
    private final TextField materialTemperatureBootomTextField = new TextField();
    private final TextField materialTemperatureUpTextField = new TextField();
    private final TextField dtField = new TextField();
    private final TextField dxField = new TextField();
    private final TextField dyField = new TextField();
    private final TextField timeField = new TextField();


    public InitialParamsForComparisonScreen(InitParametersFinishedCallback callback) {
        root = new BorderPane();
        this.callback = callback;


        VBox mainLayout = new VBox();
        mainLayout.setStyle("-fx-padding: 20; -fx-spacing: 15px;");

        Text areaSizeCaption = new Text("Введите размеры плоскости:");
        HBox areaSizeFields = new HBox();
        areaSizeFields.setStyle("-fx-spacing: 25px;");
        widthTextField.setPromptText("Введите ширину, м");
        heightTextField.setPromptText("Введите высоту, м");
        areaSizeFields.getChildren().addAll(widthTextField, heightTextField);

        Text materialPropertiesCaption = new Text("Введите характеристики материала:");
        densityTextField.setPromptText("Введите плотность вещества, кг/м^3");
        specificHeatCapacityTextField.setPromptText("Введите удельную теплоёмкость вещества, Дж/(кг*°К)");
        coefficientOfThermalConductivity.setPromptText("Введите коэффициент теплопроводности вещества, Вт/(м*°К)");

        Text materialTemperatureCaption = new Text("Введите температуру плоскости (не включая граничные условия):");
        materialTemperatureTextField.setPromptText("Температура плоскости, °C");
        Text materialTemperatureBottomCaption = new Text("Введите температуру плоскости снизу:");
        materialTemperatureBootomTextField.setPromptText("Температура плоскости снизу, °C");
        Text materialTemperatureUpCaption = new Text("Введите температуру плоскости сверху:");
        materialTemperatureUpTextField.setPromptText("Температура плоскости сверху, °C");

        Text titleSim = new Text("Введите параметры симуляции:");

        Text dtText = new Text("Введите шаг по времени, dt, сек:");
        dtField.setPromptText("сек");

        Text dxText = new Text("Введите шаг по горизонтали, dx, м:");
        dxField.setPromptText("м");

        Text dyText = new Text("Введите шаг по вертикали, dy, м:");
        dyField.setPromptText("м");

        Text timeText = new Text("Введите длину симуляции, time, сек:");
        timeField.setPromptText("сек");


        Button nextButton = new Button("Далее");
        Text errorText = new Text();
        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");

        HBox buttonAndErrorBox = new HBox();
        buttonAndErrorBox.setStyle("-fx-spacing: 35px;");
        buttonAndErrorBox.getChildren().addAll(nextButton, errorText);

        mainLayout.getChildren().addAll(
                areaSizeCaption, areaSizeFields,
                materialPropertiesCaption, densityTextField, specificHeatCapacityTextField, coefficientOfThermalConductivity,
                materialTemperatureCaption, materialTemperatureTextField,
                titleSim,
                dtText, dtField,
                dxText, dxField,
                dyText, dyField,
                timeText, timeField,
                buttonAndErrorBox
        );

        root.setCenter(mainLayout);

        nextButton.setOnAction(e -> {
            try {
                NumeralInitialPlateParameters numPlateParams = validateAndCreatePlateParams();
                SimulationParameters simParams = validateAndCreateSimulationParams();
                Pair<String, String> bottomUpTemp = validateUpNBottomTemperature();

                callback.callback(new PlateParameters(numPlateParams, bottomUpTemp.getValue(), bottomUpTemp.getKey()), simParams);
                errorText.setText("");
            } catch (Exception ex) {
                errorText.setText(ex.getMessage());
            }
        });
    }

    private Pair<String, String> validateUpNBottomTemperature(){
        double bottomTemp = fromStrToDouble(materialTemperatureBootomTextField.getText(), "температура снизу плоскости");
        double upTemp = fromStrToDouble(materialTemperatureUpTextField.getText(), "температура сверху плоскости");

        if(bottomTemp > Constants.MAX_TEMPERATURE) throw new IllegalArgumentException("Температура не может превышать " + Constants.MAX_TEMPERATURE + "°C");
        if(bottomTemp < Constants.MIN_TEMPERATURE) throw new IllegalArgumentException("Температура не может быть ниже абсолютного нуля (-273.15°C)");
        if(upTemp > Constants.MAX_TEMPERATURE) throw new IllegalArgumentException("Температура не может превышать " + Constants.MAX_TEMPERATURE + "°C");
        if(upTemp < Constants.MIN_TEMPERATURE) throw new IllegalArgumentException("Температура не может быть ниже абсолютного нуля (-273.15°C)");

        return new Pair<>(Double.toString(bottomTemp), Double.toString(upTemp));
    }

    private NumeralInitialPlateParameters validateAndCreatePlateParams() {
        double width = fromStrToDouble(widthTextField.getText(), "ширина");
        double height = fromStrToDouble(heightTextField.getText(), "высота");
        double density = fromStrToDouble(densityTextField.getText(), "плотность");
        double heatCapacity = fromStrToDouble(specificHeatCapacityTextField.getText(), "теплоёмкость");
        double conductivity = fromStrToDouble(coefficientOfThermalConductivity.getText(), "теплопроводность");
        double temperature = fromStrToDouble(materialTemperatureTextField.getText(), "начальная температура");

        if (width <= 0) throw new IllegalArgumentException("Ширина должна быть положительным числом");
        if (height <= 0) throw new IllegalArgumentException("Высота должна быть положительным числом");
        if (density <= 0) throw new IllegalArgumentException("Плотность должна быть положительной");
        if (heatCapacity <= 0) throw new IllegalArgumentException("Теплоёмкость должна быть положительной");
        if (conductivity <= 0) throw new IllegalArgumentException("Теплопроводность должна быть положительной");
        if (temperature < -273.15) throw new IllegalArgumentException("Температура не может быть ниже абсолютного нуля (-273.15°C)");
        if (temperature > 2500) throw new IllegalArgumentException("Температура не может превышать " + Constants.MAX_TEMPERATURE + "°C");

        return new NumeralInitialPlateParameters(width, height, density, heatCapacity, conductivity, temperature);
    }

    private SimulationParameters validateAndCreateSimulationParams() {
        double dt = fromStrToDouble(dtField.getText(), "dt");
        double dx = fromStrToDouble(dxField.getText(), "dx");
        double dy = fromStrToDouble(dyField.getText(), "dy");
        long time = fromStrToLong(timeField.getText(), "time");

        if (dt <= 0) throw new IllegalArgumentException("dt должен быть положительным числом, но получено: " + dt);
        if (dx <= 0) throw new IllegalArgumentException("dx должен быть положительным числом, но получено: " + dx);
        if (dy <= 0) throw new IllegalArgumentException("dy должен быть положительным числом, но получено: " + dy);
        if (time < 0) throw new IllegalArgumentException("time не может быть отрицательным, но получено: " + time);

        return new SimulationParameters(dt, dx, dy, time);
    }

    private double fromStrToDouble(String str, String fieldName) {
        str = str.trim();
        if (str.isEmpty())
            throw new NumberFormatException("Пустое значение в графе " + fieldName);
        str = str.replace(',', '.');
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            throw new NumberFormatException("Ошибка конвертации значения графы " + fieldName);
        }
    }

    private long fromStrToLong(String str, String fieldName) {
        str = str.trim();
        if (str.isEmpty())
            throw new NumberFormatException("Пустое значение в графе " + fieldName);
        str = str.replace(',', '.');
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            throw new NumberFormatException("Ошибка конвертации значения графы " + fieldName);
        }
    }


    @Override
    public Parent getView() {
        return root;
    }

}