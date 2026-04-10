package org.example.testfx.Ui.Component;


import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HeatmapComponent extends Pane {
    private final static Logger log = LogManager.getLogger(HeatmapComponent.class);
    private final double minTemp;
    private final double maxTemp;
    private double[][] currentFrame;
    private final Canvas tMap;
    private final Canvas gradient;

    private int height;
    private int width;

    private final static int maxHeight = 600;
    private final static int maxWidth = 800;
    private final static int minHeight = 200;
    private final static int minWidth = 266;

    private static final Color[] GRADIENT_COLORS = {
            Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED
    };

    public HeatmapComponent(double realWidth, double realHeight,
                            double minTemp, double maxTemp,
                            double[][] initialFrame) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.currentFrame = initialFrame;

        setHeightNWidthForTMap(realWidth, realHeight);
        log.info("Установлены следующие размеры HeatMap: {} по x, {} по y", width, height);



        tMap = new Canvas(width, height);
        gradient = new Canvas(50, 600);

        StackPane rightContainer = new StackPane(tMap);
        HBox root = new HBox(10, gradient, rightContainer);
        getChildren().addAll(root);

        setStyle("-fx-background-color: #f0f0f0;");
        drawGradient();
        drawTMap();
    }


    public void updateFrame(double[][] newFrame) {
        if (newFrame == null || newFrame.length == 0 || newFrame[0].length == 0) return;
        Platform.runLater(() -> {
            this.currentFrame = newFrame;
            drawTMap();
        });
    }

    private void drawTMap() {
        if (currentFrame == null) return;

        double canvasW = tMap.getWidth();
        double canvasH = tMap.getHeight();
        if (canvasW <= 0 || canvasH <= 0) return;

        int dataRows = currentFrame.length;
        int dataCols = currentFrame[0].length;

        PixelWriter pw = tMap.getGraphicsContext2D().getPixelWriter();

        for (int y = 0; y < canvasH; y++) {
            // Прямое отображение координаты пикселя на индекс строки матрицы
            double dataY = (y / canvasH) * (dataRows - 1);
            int y0 = (int) Math.floor(dataY);
            int y1 = Math.min(y0 + 1, dataRows - 1);
            double dy = dataY - y0;

            for (int x = 0; x < canvasW; x++) {
                double dataX = (x / canvasW) * (dataCols - 1);
                int x0 = (int) Math.floor(dataX);
                int x1 = Math.min(x0 + 1, dataCols - 1);
                double dx = dataX - x0;

                double v00 = currentFrame[y0][x0];
                double v10 = currentFrame[y0][x1];
                double v01 = currentFrame[y1][x0];
                double v11 = currentFrame[y1][x1];

                double v0 = v00 * (1 - dx) + v10 * dx;
                double v1 = v01 * (1 - dx) + v11 * dx;
                double value = v0 * (1 - dy) + v1 * dy;

                double t = (value - minTemp) / (maxTemp - minTemp);
                t = Math.min(1.0, Math.max(0.0, t));
                Color color = getColorForValue(t);
                pw.setColor(x, y, color);
            }
        }
    }

    private void drawGradient(){
        double gradWidth = gradient.getWidth();
        double gradHeight = gradient.getHeight();

        PixelWriter pw = gradient.getGraphicsContext2D().getPixelWriter();

        for (int y = 0; y < gradHeight; y++) {
            double t = 1.0 - (y / (gradHeight - 1));
            Color color = getColorForValue(t);
            //красим
            for (int x = 0; x < gradWidth; x++) {
                pw.setColor(x, y, color);
            }
        }

        // Рисуем подписи minTemp и maxTemp поверх градиента
        GraphicsContext gc = gradient.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(12));
        gc.setTextAlign(TextAlignment.CENTER);

        gc.fillText(String.format("%.1f", maxTemp), gradWidth / 2, 15);
        gc.fillText(String.format("%.1f", minTemp), gradWidth / 2, gradHeight - 10);

        // обводка
        gc.setStroke(Color.GRAY);
        gc.strokeRect(0, 0, gradWidth, gradHeight);
    }

    private void setHeightNWidthForTMap(double realWidth, double realHeight) {
        double widthToHeight = realWidth / realHeight;

        double minWidthByHeight = widthToHeight * minHeight;
        double maxWidthByHeight = widthToHeight * maxHeight;

        double allowedMinWidth = Math.max(minWidth, minWidthByHeight);
        double allowedMaxWidth = Math.min(maxWidth, maxWidthByHeight);

        int newWidth, newHeight;

        if (allowedMinWidth <= allowedMaxWidth) {
            newWidth = (int) Math.floor(allowedMaxWidth);
            newHeight = (int) Math.floor(newWidth / widthToHeight);

            if (newHeight > maxHeight) {
                newHeight = maxHeight;
                newWidth = (int) Math.floor(newHeight * widthToHeight);
            } else if (newHeight < minHeight) {
                newHeight = minHeight;
                newWidth = (int) Math.floor(newHeight * widthToHeight);
            }
        } else {
            if (minWidth > maxWidthByHeight) {
                // Случай А: изображение слишком широкое -> упираемся в minWidth и maxHeight
                newWidth = (int) minWidth;
                newHeight = (int) maxHeight;
            } else if (minWidthByHeight > maxWidth) {
                // Случай Б: изображение слишком узкое -> упираемся в maxWidth и minHeight
                newWidth = (int) maxWidth;
                newHeight = (int) minHeight;
            } else {
                newWidth = (int) maxWidth;
                newHeight = (int) maxHeight;
            }
        }

        width = newWidth;
        height = newHeight;
    }

    private Color getColorForValue(double t) {
        if (t <= 0) return GRADIENT_COLORS[0];
        if (t >= 1) return GRADIENT_COLORS[GRADIENT_COLORS.length - 1];
        double segment = 1.0 / (GRADIENT_COLORS.length - 1);
        int idx = (int) (t / segment);
        double localT = (t - idx * segment) / segment;
        return GRADIENT_COLORS[idx].interpolate(GRADIENT_COLORS[idx + 1], localT);
    }
}