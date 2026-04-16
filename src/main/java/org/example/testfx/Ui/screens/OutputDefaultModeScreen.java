package org.example.testfx.Ui.screens;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.Component.HeatmapComponent;
import org.example.testfx.Ui.Screen;

import java.util.function.Supplier;

public class OutputDefaultModeScreen implements Screen {
    private final BorderPane root;
    private final HeatmapComponent heatmap;
    private final Supplier<double[][]> nextFrameTMapSupplier;

    private final Text timeStepInfoText = new Text();
    private final Text actTimeInfoText = new Text();
    private final Button playPauseBtn = new Button();
    private final ChoiceBox<String> changeStepMultiplierChoiceBox = new ChoiceBox<>();
    private final ChoiceBox<String> changeFPSChoiceBox = new ChoiceBox<>();
    private int fps = Constants.DEFAULT_FPS;
    private int stepMultiplier = 1;

    public OutputDefaultModeScreen(ExperimentalNMapParameters params, Supplier<double[][]> nextFrameTMapSupplier) {
        this.nextFrameTMapSupplier = nextFrameTMapSupplier;
        root = new BorderPane();
        heatmap = new HeatmapComponent(params.getExParams().getPlateParameters().getNumeralParameters().width(),
                params.getExParams().getPlateParameters().getNumeralParameters().height(),
                Math.ceil(params.getMinTemp() / 10.0) * 10,
                Math.ceil(params.getMaxTemp() / 10.0) * 10,
                nextFrameTMapSupplier.get());


        HBox controlPanel = new HBox();

        {
            VBox timeStepAndTimeInfo = new VBox();
            timeStepInfoText.setText(params.getExParams().getSimulationParameters().getDt() + " сек");
            actTimeInfoText.setText("0 сек");
            timeStepAndTimeInfo.getChildren().addAll(timeStepInfoText, actTimeInfoText);
            controlPanel.getChildren().add(timeStepAndTimeInfo);
        }

        playPauseBtn.setText("pause");
        playPauseBtn.setOnAction(this::playPauseBtnClick);

        {
            VBox speedAndStepChanging = new VBox();
            HBox h1 = new HBox();
            Text stepMultiplierText = new Text("Множитель шага: ");
            changeStepMultiplierChoiceBox.getItems().addAll("x1", "x2", "x5");
            changeStepMultiplierChoiceBox.setValue("x1");
            changeStepMultiplierChoiceBox.setOnAction(this::changeStepMultiplierChoiceBox);
            h1.getChildren().addAll(stepMultiplierText, changeStepMultiplierChoiceBox);
            HBox h2 = new HBox();
            Text FPSMultiplierText = new Text("Скорость: ");
            changeFPSChoiceBox.getItems().addAll("x1", "x3", "x5");
            changeFPSChoiceBox.setValue("x1");
            changeFPSChoiceBox.setOnAction(this::changeFPSChoiceBox);
            h2.getChildren().addAll(FPSMultiplierText, changeFPSChoiceBox);
            speedAndStepChanging.getChildren().addAll(h1, h2);
            root.getChildren().add(speedAndStepChanging);
        }


        Button toEndButton = new Button("В конец >>");
        toEndButton.setOnAction(this::toEndBtnClick);

        root.setCenter(heatmap);
    }

    public void playPauseBtnClick(ActionEvent actionEvent){
        if(playPauseBtn.getText().equals("pause")){


            playPauseBtn.setText("play");
        }else{


            playPauseBtn.setText("pause");
        }
    }

    public void toEndBtnClick(ActionEvent actionEvent){

    }

    public void changeStepMultiplierChoiceBox(ActionEvent actionEvent){
        switch(changeStepMultiplierChoiceBox.getValue()){
            case "x1":

            break;
            case "x2":

            break;
            case "x5":

            break;
            default:

            break;
        }
    }

    public void changeFPSChoiceBox(ActionEvent actionEvent){
        switch(changeFPSChoiceBox.getValue()){
            case "x1":

                break;
            case "x3":

                break;
            case "x5":

                break;
            default:

                break;
        }
    }




    @Override
    public Parent getView() {
        return root;
    }
}
