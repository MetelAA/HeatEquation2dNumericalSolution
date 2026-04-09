package org.example.testfx.Ui.Component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Тепловая карта с фиксированными пропорциями поля (realWidth x realHeight).
 * Данные приходят в виде матрицы double[][] (размер матрицы = количество узлов по Y и X).
 * При отрисовке карта вписывается в Canvas с сохранением пропорций (letterboxing).
 * Градиент температур рисуется слева.
 */
public class HeatmapComponent extends Pane {
    private final double minTemp;
    private final double maxTemp;
    private final double realWidth;
    private final double realHeight;
    private double[][] currentFrame;

    // Цветовая палитра (синий -> голубой -> зелёный -> жёлтый -> красный)
    private static final Color[] GRADIENT_COLORS = {
            Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED
    };

    public HeatmapComponent(double realWidth, double realHeight,
                            double minTemp, double maxTemp,
                            double[][] initialFrame) {
        this.realWidth = realWidth;
        this.realHeight = realHeight;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.currentFrame = initialFrame;



    }
}