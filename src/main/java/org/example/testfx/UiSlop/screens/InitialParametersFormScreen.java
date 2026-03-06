package org.example.testfx.UiSlop.screens;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.DTO.NumeralInitialPlateParameters;
import org.example.testfx.UiSlop.Screen;

import java.util.function.Consumer;

public class InitialParametersFormScreen implements Screen {

    final private BorderPane root;
    private NumeralInitialPlateParameters result;
    private final Consumer<NumeralInitialPlateParameters> callback;


    public InitialParametersFormScreen(Consumer<NumeralInitialPlateParameters> callback) {
        this.callback = callback;
        root = new BorderPane();
        VBox vertInputLayout = new VBox();
        vertInputLayout.setStyle("-fx-padding: 20; -fx-spacing: 15px;");
        Text areaSizeCaption = new Text("Введите размеры плоскости:");
        HBox areaSizeFields = new HBox();
        areaSizeFields.setStyle("-fx-spacing: 25px;");

        TextField widthTextField = new TextField();
        TextField heightTextField = new TextField();
        TextField densityTextField = new TextField();
        TextField specificHeatCapacityTextField = new TextField();
        TextField coefficientOfThermalConductivity = new TextField();
        TextField materialTemperatureTextField = new TextField();

        widthTextField.setPromptText("Введите ширину, м");
        heightTextField.setPromptText("Введите высоту, м");
        areaSizeFields.getChildren().addAll(widthTextField, heightTextField);
        vertInputLayout.getChildren().addAll(areaSizeCaption, areaSizeFields);
        Text materialPropertiesCaption = new Text("Введите характеристики материала:");
        densityTextField.setPromptText("Введите плотность вещества, кг/м^3");
        specificHeatCapacityTextField.setPromptText("Введите удельную теплоёмкость вещества, Дж/(кг*°К)");
        coefficientOfThermalConductivity.setPromptText("Введите коэффициент теплопроводности вещества, Вт/(м*°К)");
        vertInputLayout.getChildren().addAll(materialPropertiesCaption, densityTextField, specificHeatCapacityTextField, coefficientOfThermalConductivity);
        Text materialTemperatureCaption = new Text("Введите температуру плоскости (не включая граничные условия)");
        materialTemperatureTextField.setPromptText("Температура плоскости, °C");
        vertInputLayout.getChildren().addAll(materialTemperatureCaption, materialTemperatureTextField);
        Button validateAllAndGoToTheNextScreen = new Button("Далее");




        Text errorText = new Text();


        errorText.setStyle("-fx-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
        validateAllAndGoToTheNextScreen.setOnAction(e ->
            {
                try{
                    result = handleInput(
                            widthTextField.getText(),
                            heightTextField.getText(),
                            densityTextField.getText(),
                            specificHeatCapacityTextField.getText(),
                            coefficientOfThermalConductivity.getText(),
                            materialTemperatureTextField.getText()
                    );
                    callback.accept(result);
                }catch(Exception ex) {
                    errorText.setText(ex.getMessage());
                }
            }
        );
        HBox buttonAndErrorBox = new HBox();
        buttonAndErrorBox.setStyle("-fx-spacing: 35px;");
        buttonAndErrorBox.getChildren().addAll(validateAllAndGoToTheNextScreen, errorText);

        vertInputLayout.getChildren().add(buttonAndErrorBox);
        root.setCenter(vertInputLayout);

    }

    private NumeralInitialPlateParameters handleInput(String widthStr, String heightStr, String densityStr,
                                                      String heatCapacityStr, String conductivityStr, String temperatureStr) throws NumberFormatException{
        double width = fromStrToDouble(widthStr, "ширина");
        double height = fromStrToDouble(heightStr, "высота");
        double density = fromStrToDouble(densityStr, "плотность");
        double heatCapacity = fromStrToDouble(heatCapacityStr, "теплоёмкость");
        double conductivity = fromStrToDouble(conductivityStr, "теплопроводность");
        double temperature = fromStrToDouble(temperatureStr, "начальная температура");
        if (width <= 0) throw new IllegalArgumentException("Ширина должна быть положительным числом");
        if (height <= 0) throw new IllegalArgumentException("Высота должна быть положительным числом");
        if (density <= 0) throw new IllegalArgumentException("Плотность должна быть положительной");
        if (heatCapacity <= 0) throw new IllegalArgumentException("Теплоёмкость должна быть положительной");
        if (conductivity <= 0) throw new IllegalArgumentException("Теплопроводность должна быть положительной");
        if (temperature < -273.15) throw new IllegalArgumentException("Температура не может быть ниже абсолютного нуля (-273.15°C)");
        if (temperature > 2500) throw new IllegalArgumentException("Температура не может превышать 2500°C");
        return new NumeralInitialPlateParameters(width, height, density, heatCapacity, conductivity, temperature);
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

    @Override
    public Parent getView() {
        return root;
    }
}
