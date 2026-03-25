package org.example.testfx.Ui.screens;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.example.testfx.Ui.Screen;
import org.example.testfx.utils.ExpressionParser;

import java.util.function.Consumer;

public class InitialBorderTemperatureEquationEditor implements Screen {
    private final BorderPane root;
    private final Canvas graphCanvas;
    private final TextField equationField;
    private final Label topMessageLabel;
    private final double maxX;
    private final double yMin;
    private final double yMax;
    private final ExpressionParser parser;
    private final Button nextButton;

    private ExpressionParser.Expression compiledExpr;

    private static final double LEFT_MARGIN = 60;
    private static final double RIGHT_MARGIN = 20;
    private static final double TOP_MARGIN = 20;
    private static final double BOTTOM_MARGIN = 40;
    private final Consumer<String> callback;

    public InitialBorderTemperatureEquationEditor(double maxX, double yMax, double yMin, Consumer<String> callback) {
        this.maxX = maxX;
        this.yMin = yMin;
        this.yMax = yMax;
        this.callback = callback;
        this.parser = new ExpressionParser();
        root = new BorderPane();

        topMessageLabel = new Label("Введите уравнение y = ...");
        topMessageLabel.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
        root.setTop(topMessageLabel);

        graphCanvas = new Canvas(680, 425);
        graphCanvas.widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        graphCanvas.heightProperty().addListener((obs, oldVal, newVal) -> redraw());
        root.setCenter(graphCanvas);

        VBox controlPanel = new VBox(5);
        controlPanel.setStyle("-fx-padding: 10;");

        HBox equationBox = new HBox(5);
        Label eqLabel = new Label("y =");
        equationField = new TextField();
        equationField.setPromptText("например: x^2 + sin(x)");
        nextButton = new Button("Далее");
        nextButton.setOnAction(e -> onNextButtonClicked(equationField.getText().trim()));
        nextButton.setDisable(true);
        equationBox.getChildren().addAll(eqLabel, equationField, nextButton);

        TextArea rulesArea = new TextArea();
        rulesArea.setEditable(false);
        rulesArea.setPrefRowCount(3);
        rulesArea.setText("Допустимые операции:\n" +
                "x, числа, +, -, *, /, ^ (степень), sin(), cos(), ( )\n" +
                "Примеры: 2*x, sin(x)^2, (x+1)/3");
        rulesArea.setWrapText(true);

        controlPanel.getChildren().addAll(equationBox, rulesArea);
        root.setBottom(controlPanel);

        equationField.textProperty().addListener((obs, oldVal, newVal) -> {
            compileExpression(newVal.trim());
            redraw();
        });

        compileExpression("");
        redraw();
    }

    @Override
    public BorderPane getView() {
        return root;
    }

    public void onNextButtonClicked(String equation) {
        // Пустая реализация (может быть переопределена)
        System.out.println("Уравнение: " + equation);
        callback.accept(equation);
    }

    private void compileExpression(String expr) {
        if (expr.isEmpty()) {
            compiledExpr = null;
            topMessageLabel.setText("Введите уравнение y = ...");
            return;
        }
        try {
            compiledExpr = parser.compile(expr);
            nextButton.setDisable(false);
        } catch (Exception e) {
            compiledExpr = null;
            nextButton.setDisable(true);
            topMessageLabel.setText("Ошибка в выражении: " + e.getMessage());
        }
    }

    private void redraw() {
        GraphicsContext gc = graphCanvas.getGraphicsContext2D();
        double w = graphCanvas.getWidth();
        double h = graphCanvas.getHeight();
        gc.clearRect(0, 0, w, h);

        double graphWidth = w - LEFT_MARGIN - RIGHT_MARGIN;
        double graphHeight = h - TOP_MARGIN - BOTTOM_MARGIN;
        if (graphWidth <= 0 || graphHeight <= 0) return;

        drawGrid(gc, graphWidth, graphHeight);
        drawAxes(gc, graphWidth, graphHeight);

        if (compiledExpr != null) {
            plotFunction(gc, graphWidth, graphHeight);
        }
    }

    private void drawGrid(GraphicsContext gc, double graphWidth, double graphHeight) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);

        double stepX = niceStep(maxX, 8);
        double stepY = niceStep(yMax - yMin, 8);

        // Вертикальные линии
        for (double x = 0; x <= maxX; x += stepX) {
            double canvasX = LEFT_MARGIN + (x / maxX) * graphWidth;
            gc.strokeLine(canvasX, TOP_MARGIN, canvasX, TOP_MARGIN + graphHeight);
        }
        // Горизонтальные линии
        for (double y = yMin; y <= yMax; y += stepY) {
            double canvasY = TOP_MARGIN + (yMax - y) / (yMax - yMin) * graphHeight;
            gc.strokeLine(LEFT_MARGIN, canvasY, LEFT_MARGIN + graphWidth, canvasY);
        }
    }

    private void drawAxes(GraphicsContext gc, double graphWidth, double graphHeight) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.5);

        // Ось X (y=0) – рисуем, только если 0 входит в диапазон [yMin, yMax]
        if (yMin <= 0 && 0 <= yMax) {
            double yZero = TOP_MARGIN + (yMax - 0) / (yMax - yMin) * graphHeight;
            gc.strokeLine(LEFT_MARGIN, yZero, LEFT_MARGIN + graphWidth, yZero);
        }

        // Ось Y (x=0)
        double xZero = LEFT_MARGIN + (0 / maxX) * graphWidth;
        gc.strokeLine(xZero, TOP_MARGIN, xZero, TOP_MARGIN + graphHeight);

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(10));

        double stepX = niceStep(maxX, 8);
        double stepY = niceStep(yMax - yMin, 8);

        // Подписи на оси X (вдоль оси X, на уровне y=0, если 0 в диапазоне; иначе под осью)
        double labelYBase;
        if (yMin <= 0 && 0 <= yMax) {
            labelYBase = TOP_MARGIN + (yMax - 0) / (yMax - yMin) * graphHeight + 15;
        } else {
            labelYBase = TOP_MARGIN + graphHeight + 5; // под нижней границей
        }
        for (double x = 0; x <= maxX; x += stepX) {
            double canvasX = LEFT_MARGIN + (x / maxX) * graphWidth;
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(formatNumber(x), canvasX, labelYBase);
        }

        // Подписи на оси Y (слева)
        for (double y = yMin; y <= yMax; y += stepY) {
            double canvasY = TOP_MARGIN + (yMax - y) / (yMax - yMin) * graphHeight;
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(formatNumber(y), LEFT_MARGIN - 5, canvasY + 4);
        }

        // Подпись нуля, если 0 в диапазоне и ещё не подписан
        if (yMin <= 0 && 0 <= yMax && Math.abs(stepY) > 1e-12) {
            // Проверяем, не совпадает ли 0 с одним из делений
            boolean zeroLabeled = false;
            for (double y = yMin; y <= yMax; y += stepY) {
                if (Math.abs(y) < 1e-10) {
                    zeroLabeled = true;
                    break;
                }
            }
            if (!zeroLabeled) {
                double canvasY = TOP_MARGIN + (yMax - 0) / (yMax - yMin) * graphHeight;
                gc.setTextAlign(TextAlignment.RIGHT);
                gc.fillText("0", LEFT_MARGIN - 5, canvasY + 4);
            }
        }
    }

    private void plotFunction(GraphicsContext gc, double graphWidth, double graphHeight) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);
        double prevX = Double.NaN, prevY = Double.NaN;

        for (int px = 0; px <= graphWidth; px++) {
            double x = (px / graphWidth) * maxX;
            try {
                double y = compiledExpr.evaluate(x);
                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    prevX = Double.NaN;
                    continue;
                }
                // Ограничиваем y в пределах [yMin, yMax]
                double clampedY = Math.max(yMin, Math.min(yMax, y));
                double canvasX = LEFT_MARGIN + px;
                double canvasY = TOP_MARGIN + (yMax - clampedY) / (yMax - yMin) * graphHeight;
                if (!Double.isNaN(prevX)) {
                    gc.strokeLine(prevX, prevY, canvasX, canvasY);
                }
                prevX = canvasX;
                prevY = canvasY;
            } catch (Exception e) {
                prevX = Double.NaN;
            }
        }
    }

    private String formatNumber(double num) {
        if (Math.abs(num) < 1e-10) return "0";
        String s = String.format("%.3f", num).replaceAll("0*$", "").replaceAll("\\.$", "");
        return s;
    }

    private double niceStep(double range, int targetCount) {
        if (range <= 0) return 1.0;
        double roughStep = range / targetCount;
        double magnitude = Math.pow(10, Math.floor(Math.log10(roughStep)));
        double normalized = roughStep / magnitude;
        if (normalized < 1.5) return magnitude;
        else if (normalized < 3) return 2 * magnitude;
        else if (normalized < 7) return 5 * magnitude;
        else return 10 * magnitude;
    }
}