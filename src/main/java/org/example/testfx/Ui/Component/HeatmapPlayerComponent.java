package org.example.testfx.Ui.Component;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.testfx.Constants.Constants;
import org.example.testfx.DTO.ExperimentalNMapParameters;
import org.example.testfx.Ui.Screens.OutputDefaultModeScreen;
import org.example.testfx.Utils.FileUtils.TempMapReaderWrapper;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

public class HeatmapPlayerComponent extends VBox {

    private final static Logger log = LogManager.getLogger(HeatmapPlayerComponent.class);

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
    private static final double FIXED_VALUE_WIDTH = 130;

    public HeatmapPlayerComponent(TempMapReaderWrapper framesSupplier, ExperimentalNMapParameters params) {
        this.framesSupplier = framesSupplier;
        this.params = params;
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
            Text timeWriteStepText = new Text();
            // Форматирование double до двух знаков
            timeStepInfoText.setText(String.format("dt: %.2f сек", params.getExParams().getSimulationParameters().getDt()));
            timeWriteStepText.setText(String.format("Кадр зап. раз в %.2f сек", 1.0 / params.getExParams().getSimulationParameters().getFrameWritesPerSecond()));
            actualTimeInfoText.setText(String.format("Тек. время: %.2f сек", 0.0));

            // Оборачиваем Text в контейнер с фиксированной шириной
            timeStepAndTimeInfo.getChildren().addAll(
                    wrapWithFixedWidth(timeStepInfoText),
                    wrapWithFixedWidth(timeWriteStepText),
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
            changeFPSChoiceBox.getItems().addAll("x1", "x0.5", "x2");
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

                        double currentTime = framesSupplier.getCurrentFrameNumber() / params.getExParams().getSimulationParameters().getFrameWritesPerSecond();
                        Platform.runLater(() -> {
                            actualTimeInfoText.setText(String.format("тек. время: %.2f сек", currentTime));
                            if (frame.isEmpty()) {
                                playPauseBtn.setText("pause");
                                playPauseBtn.setDisable(true);
                                log.info("Frames ended! No more video!");
                            }
                        });
                        return frame;
                    }
                },
                (1.0 / fps)
        );
        playbackThread.setDaemon(true);
        playbackThread.start();

        getChildren().addAll(heatmap, controlPanel);
        setSpacing(10);
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
        playPauseBtn.fire();

        PauseTransition delay1 = new PauseTransition(Duration.millis(100));
        delay1.setOnFinished(e -> {
            try {
                framesSupplier.setPointerToLastFrame();
            } catch (IOException ex) {
                throw new RuntimeException("OutputDefaultModeScreen: error when setting fileReader pointer before last frame, with error: " + ex);
            }

            PauseTransition delay2 = new PauseTransition(Duration.millis(50));
            delay2.setOnFinished(e2 -> {
                playPauseBtn.fire();
                double currentTime = (framesSupplier.getCurrentFrameNumber() + 1) / params.getExParams().getSimulationParameters().getFrameWritesPerSecond();
                actualTimeInfoText.setText(String.format("тек. время: %.2f сек", currentTime));
            });
            delay2.play();
        });
        delay1.play();
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
            case "x0.5":
                fps = (int) (Constants.DEFAULT_FPS * 0.5);
                playbackThread.changeFrameChangeInterval(1.0 / fps);
                break;
            case "x2":
                fps = Constants.DEFAULT_FPS * 2;
                playbackThread.changeFrameChangeInterval(1.0 / fps);
                break;
        }
    }

    public void dispose() {
        if (playbackThread != null && playbackThread.isAlive()) {
            playbackThread.interrupt();
        }
    }

}
