package org.example.testfx.Ui.Screens;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.Component.HeatmapComponent;
import org.example.testfx.Ui.Component.HeatmapPlaybackThread;
import org.example.testfx.Ui.Screen;
import org.example.testfx.Utils.DefaultCallback;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class OutputDefaultModeScreen implements Screen {
    private final static Logger log = LogManager.getLogger(OutputDefaultModeScreen.class);
    private final BorderPane root;

    private final HeatmapComponent heatmap;
    private final TempMapReaderWrapper framesSupplier;
    private final HeatmapPlaybackThread playbackThread;

    private final ExperimentalNMapParameters params;

    private final Text actualTimeInfoText = new Text();
    private final Button playPauseBtn = new Button();
    private final ChoiceBox<String> changeStepMultiplierChoiceBox = new ChoiceBox<>();
    private final ChoiceBox<String> changeFPSChoiceBox = new ChoiceBox<>();
    private int fps = Constants.DEFAULT_FPS;


    // Фиксированная ширина для числовых полей, чтобы текст не сдвигал соседей
    private static final double FIXED_VALUE_WIDTH = 80;

    public OutputDefaultModeScreen(TempMapReaderWrapper framesSupplier, ExperimentalNMapParameters params) {
        this.framesSupplier = framesSupplier;
        this.params = params;
        root = new BorderPane();
        {
            double[][] firstFrame = getFrameWithoutError().get(); // нулевой кадр всегда есть!!!!
            heatmap = new HeatmapComponent(params.getExParams().getPlateParameters().getNumeralParameters().width(),
                    params.getExParams().getPlateParameters().getNumeralParameters().height(),
                    Math.ceil(params.getMinTemp() / 10.0) * 10,
                    Math.ceil(params.getMaxTemp() / 10.0) * 10,
                    firstFrame);
        }

        HBox controlPanel = new HBox(15); // расстояние между элементами
        controlPanel.setStyle("-fx-padding: 10; -fx-alignment: center-left;");

        // Блок информации о времени (в VBox, но внутри HBox будет фикс. ширина)
        {
            VBox timeStepAndTimeInfo = new VBox(5);
            Text timeStepInfoText = new Text();
            // Форматирование double до двух знаков
            timeStepInfoText.setText(String.format("%.2f сек", params.getExParams().getSimulationParameters().getDt()));
            actualTimeInfoText.setText(String.format("%.2f сек", 0.0));

            // Оборачиваем Text в контейнер с фиксированной шириной
            timeStepAndTimeInfo.getChildren().addAll(
                    wrapWithFixedWidth(timeStepInfoText),
                    wrapWithFixedWidth(actualTimeInfoText)
            );
            controlPanel.getChildren().add(timeStepAndTimeInfo);
        }

        playPauseBtn.setText("play");
        playPauseBtn.setOnAction(this::playPauseBtnClick);
        controlPanel.getChildren().add(playPauseBtn);

        {
            VBox speedAndStepChanging = new VBox(5);
            HBox h1 = new HBox(5);
            Text stepMultiplierText = new Text("Множитель шага: ");
            changeStepMultiplierChoiceBox.getItems().addAll("x1", "x3", "x5");
            changeStepMultiplierChoiceBox.setValue("x1");
            changeStepMultiplierChoiceBox.setOnAction(this::changeStepMultiplierChoiceBox);
            h1.getChildren().addAll(stepMultiplierText, changeStepMultiplierChoiceBox);

            HBox h2 = new HBox(5);
            Text FPSMultiplierText = new Text("Скорость: ");
            changeFPSChoiceBox.getItems().addAll("x1", "x1.5", "x2");
            changeFPSChoiceBox.setValue("x1");
            changeFPSChoiceBox.setOnAction(this::changeFPSChoiceBox);
            h2.getChildren().addAll(FPSMultiplierText, changeFPSChoiceBox);

            speedAndStepChanging.getChildren().addAll(h1, h2);
            controlPanel.getChildren().add(speedAndStepChanging);
        }

        Button toEndButton = new Button("В конец >>");
        toEndButton.setOnAction(this::toEndBtnClick);
        controlPanel.getChildren().add(toEndButton);

        playbackThread = new HeatmapPlaybackThread(heatmap,
                new Supplier<Optional<double[][]>>() {
                    @Override
                    public Optional<double[][]> get() {
                        Optional<double[][]> frame = getFrameWithoutError();

                        double currentTime = framesSupplier.getCurrentFrameNumber() / params.getWriteFramesPerSeconds();
                        actualTimeInfoText.setText(String.format("%.2f сек", currentTime));

                        if (frame.isEmpty()){
                            playPauseBtn.setText("pause");
                            playPauseBtn.setDisable(true);
                            log.info("Frames ended! No more video!");
                        }
                        return frame;
                    }
                },
                (1.0 / fps)
        );
        playbackThread.setDaemon(true);
        playbackThread.start();

        VBox v1 = new VBox(10);
        v1.getChildren().addAll(heatmap, controlPanel);
        root.setCenter(v1);
    }



    private Optional<double[][]> getFrameWithoutError(){
        try {
            return framesSupplier.getNextFrame();
        } catch (IOException e) {
            throw new RuntimeException("OutputDefaultModeScreen: error when reading frame NUM: |" + framesSupplier.getCurrentFrameNumber() + "| frame by tmapReaderWrapper, with error: " + e);
        }
    }

    // Вспомогательный метод для оборачивания Text в контейнер фиксированной ширины
    private HBox wrapWithFixedWidth(Text text) {
        HBox wrapper = new HBox();
        wrapper.setPrefWidth(FIXED_VALUE_WIDTH);
        wrapper.setMinWidth(FIXED_VALUE_WIDTH);
        wrapper.setMaxWidth(FIXED_VALUE_WIDTH);
        wrapper.getChildren().add(text);
        wrapper.setStyle("-fx-alignment: center-right;");
        return wrapper;
    }

    public void playPauseBtnClick(ActionEvent actionEvent) {
        if (playPauseBtn.getText().equals("pause")) {
            playbackThread.setStatus(HeatmapPlaybackThread.PlaybackStatus.PAUSE);
            playPauseBtn.setText("play");
        } else {
            playbackThread.setStatus(HeatmapPlaybackThread.PlaybackStatus.PLAY);
            playPauseBtn.setText("pause");
        }
    }

    public void toEndBtnClick(ActionEvent actionEvent) {
        try {
            framesSupplier.setPointerToLastFrame();
        } catch (IOException e) {
            throw new RuntimeException("OutputDefaultModeScreen: error when setting fileReader pointer before last frame, with error: " + e);
        }
        double currentTime = params.getWriteFramesPerSeconds() * (framesSupplier.getCurrentFrameNumber()+1);
        actualTimeInfoText.setText(String.format("%.2f сек", currentTime));
    }

    public void changeStepMultiplierChoiceBox(ActionEvent actionEvent) {
        switch (changeStepMultiplierChoiceBox.getValue()) {
            case "x1":
                framesSupplier.changeFrameStepMultiplier(1);
                break;
            case "x3":
                framesSupplier.changeFrameStepMultiplier(3);
                break;
            case "x5":
                framesSupplier.changeFrameStepMultiplier(5);
                break;
        }
    }

    public void changeFPSChoiceBox(ActionEvent actionEvent) {
        switch (changeFPSChoiceBox.getValue()) {
            case "x1":
                fps = Constants.DEFAULT_FPS;
                playbackThread.changeFrameChangeInterval(1.0 / fps);
                break;
            case "x1.5":
                fps = Constants.DEFAULT_FPS * 3;
                playbackThread.changeFrameChangeInterval(1.0 / fps);
                break;
            case "x2":
                fps = Constants.DEFAULT_FPS * 5;
                playbackThread.changeFrameChangeInterval(1.0 / fps);
                break;
        }
    }

    @Override
    public Parent getView() {
        return root;
    }
}