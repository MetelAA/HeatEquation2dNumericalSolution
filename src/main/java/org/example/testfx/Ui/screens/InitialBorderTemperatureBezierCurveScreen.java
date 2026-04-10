package org.example.testfx.Ui.screens;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.example.testfx.Ui.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Экран редактора кривой Безье.
 * Реализует кривую Безье с фиксированными равномерными X-координатами контрольных точек.
 * Полином y(x) строится из параметрической кривой Безье и гарантированно лежит
 * в пределах [yMin, yMax] для всех x ∈ [xMin, xMax] (свойство выпуклой оболочки).
 */
public class InitialBorderTemperatureBezierCurveScreen implements Screen {

    private static final int N_POINTS = 6;               // количество контрольных точек
    private final double xMin;
    private final double xMax;
    private final double yMin;
    private final double yMax;
    private final double margin = 50;

    private Pane drawingPane;
    private Group gridGroup;
    private Group curveGroup;
    private List<Circle> points;
    private List<Double> logicalX;   // фиксированные равномерные X
    private List<Double> logicalY;   // изменяемые Y контрольных точек
    private Path bezierPath;
    private TextArea equationArea;
    private Label errorLabel;
    private BorderPane root;
    private final Consumer<String> callback;

    public InitialBorderTemperatureBezierCurveScreen(double xMax, double tMax, double tMin, Consumer<String> callback) {
        this.callback = callback;
        this.xMin = 0.0;
        this.xMax = xMax;
        this.yMax = tMax;
        this.yMin = tMin;

        root = new BorderPane();
        drawingPane = new Pane();
        drawingPane.setPrefSize(800, 500);
        drawingPane.setStyle("-fx-background-color: #ffffff;");

        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> refresh());
        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> refresh());

        gridGroup = new Group();
        curveGroup = new Group();
        drawingPane.getChildren().addAll(gridGroup, curveGroup);

        initializePoints();

        bezierPath = new Path();
        bezierPath.setStroke(Color.BLUE);
        bezierPath.setStrokeWidth(2);
        curveGroup.getChildren().add(bezierPath);

        for (Circle c : points) {
            curveGroup.getChildren().add(c);
        }

        root.setCenter(drawingPane);

        HBox bottomPanel = new HBox(10);
        Button calcButton = new Button("Получить уравнение");
        equationArea = new TextArea();
        equationArea.setPrefRowCount(3);
        equationArea.setPrefColumnCount(40);
        equationArea.setEditable(false);
        errorLabel = new Label();
        bottomPanel.getChildren().addAll(calcButton, equationArea, errorLabel);
        root.setBottom(bottomPanel);

        calcButton.setOnAction(e -> showEquation());
    }

    @Override
    public Parent getView() {
        return root;
    }

    private void refresh() {
        drawGridAndAxes();
        updatePointsPosition();
        updateBezier();
    }

    /** Инициализация: X фиксированы равномерно, Y = 0 */
    private void initializePoints() {
        logicalX = new ArrayList<>();
        logicalY = new ArrayList<>();
        points = new ArrayList<>();

        double step = (xMax - xMin) / (N_POINTS - 1);
        for (int i = 0; i < N_POINTS; i++) {
            double lx = xMin + i * step;
            logicalX.add(lx);
            logicalY.add(0.0);
        }

        for (int i = 0; i < N_POINTS; i++) {
            double[] scr = logicalToScreen(logicalX.get(i), logicalY.get(i));
            Circle circle = new Circle(scr[0], scr[1], 8);
            circle.setFill(Color.RED);
            circle.setStroke(Color.BLACK);
            circle.setStrokeWidth(1.5);
            int index = i;
            setupMouseHandling(circle, index);
            points.add(circle);
        }
    }

    private double[] logicalToScreen(double lx, double ly) {
        double w = drawingPane.getWidth() - 2 * margin;
        double h = drawingPane.getHeight() - 2 * margin;
        if (w <= 0 || h <= 0) return new double[]{margin, margin};
        double sx = margin + (lx - xMin) / (xMax - xMin) * w;
        double sy = drawingPane.getHeight() - margin - (ly - yMin) / (yMax - yMin) * h;
        return new double[]{sx, sy};
    }

    private double[] screenToLogical(double sx, double sy) {
        double w = drawingPane.getWidth() - 2 * margin;
        double h = drawingPane.getHeight() - 2 * margin;
        if (w <= 0 || h <= 0) return new double[]{xMin, yMin};
        double lx = xMin + (sx - margin) / w * (xMax - xMin);
        double ly = yMax - (sy - margin) / h * (yMax - yMin);
        return new double[]{lx, ly};
    }

    private void updatePointsPosition() {
        for (int i = 0; i < N_POINTS; i++) {
            double[] scr = logicalToScreen(logicalX.get(i), logicalY.get(i));
            points.get(i).setCenterX(scr[0]);
            points.get(i).setCenterY(scr[1]);
        }
    }

    /** Перетаскивание только по вертикали, X не меняется */
    private void setupMouseHandling(Circle circle, int index) {
        circle.setOnMousePressed(event -> {
            double deltaY = circle.getCenterY() - event.getSceneY();
            circle.setUserData(deltaY);
        });

        circle.setOnMouseDragged(event -> {
            Double deltaY = (Double) circle.getUserData();
            if (deltaY != null) {
                double newSy = event.getSceneY() + deltaY;
                double[] newLog = screenToLogical(circle.getCenterX(), newSy);
                double newLy = newLog[1];
                // ограничение по Y в пределах [yMin, yMax]
                if (newLy < yMin) newLy = yMin;
                if (newLy > yMax) newLy = yMax;

                logicalY.set(index, newLy);
                double[] scr = logicalToScreen(logicalX.get(index), newLy);
                circle.setCenterX(scr[0]);
                circle.setCenterY(scr[1]);

                updateBezier();
            }
        });

        circle.setOnMouseReleased(event -> circle.setUserData(null));
    }

    private void drawGridAndAxes() {
        gridGroup.getChildren().clear();

        double w = drawingPane.getWidth();
        double h = drawingPane.getHeight();
        if (w == 0 || h == 0) return;

        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        double xStep = roundStep(xRange / 10);
        double yStep = roundStep(yRange / 10);

        // вертикальные линии
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            double[] scr1 = logicalToScreen(x, yMin);
            double[] scr2 = logicalToScreen(x, yMax);
            if (isValidPoint(scr1) && isValidPoint(scr2)) {
                Line line = new Line(scr1[0], scr1[1], scr2[0], scr2[1]);
                line.setStroke(Color.LIGHTGRAY);
                line.setStrokeWidth(0.5);
                gridGroup.getChildren().add(line);
            }
        }

        // горизонтальные линии
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            double[] scr1 = logicalToScreen(xMin, y);
            double[] scr2 = logicalToScreen(xMax, y);
            if (isValidPoint(scr1) && isValidPoint(scr2)) {
                Line line = new Line(scr1[0], scr1[1], scr2[0], scr2[1]);
                line.setStroke(Color.LIGHTGRAY);
                line.setStrokeWidth(0.5);
                gridGroup.getChildren().add(line);
            }
        }

        // ось X (y=0)
        double[] xAxisStart = logicalToScreen(xMin, 0);
        double[] xAxisEnd = logicalToScreen(xMax, 0);
        if (isValidPoint(xAxisStart) && isValidPoint(xAxisEnd)) {
            Line xAxis = new Line(xAxisStart[0], xAxisStart[1], xAxisEnd[0], xAxisEnd[1]);
            xAxis.setStroke(Color.BLACK);
            xAxis.setStrokeWidth(1.5);
            gridGroup.getChildren().add(xAxis);
        }

        // ось Y (x=0)
        double[] yAxisStart = logicalToScreen(0, yMin);
        double[] yAxisEnd = logicalToScreen(0, yMax);
        if (isValidPoint(yAxisStart) && isValidPoint(yAxisEnd)) {
            Line yAxis = new Line(yAxisStart[0], yAxisStart[1], yAxisEnd[0], yAxisEnd[1]);
            yAxis.setStroke(Color.BLACK);
            yAxis.setStrokeWidth(1.5);
            gridGroup.getChildren().add(yAxis);
        }

        // подписи
        for (double x = Math.ceil(xMin / xStep) * xStep; x <= xMax; x += xStep) {
            double[] scr = logicalToScreen(x, 0);
            if (isValidPoint(scr)) {
                Text text = new Text(scr[0] - 10, scr[1] - 5, String.format("%.2f", x));
                gridGroup.getChildren().add(text);
            }
        }
        for (double y = Math.ceil(yMin / yStep) * yStep; y <= yMax; y += yStep) {
            double[] scr = logicalToScreen(0, y);
            if (isValidPoint(scr)) {
                Text text = new Text(scr[0] + 5, scr[1] + 3, String.format("%.2f", y));
                gridGroup.getChildren().add(text);
            }
        }
    }

    private double roundStep(double rawStep) {
        double exponent = Math.floor(Math.log10(rawStep));
        double fraction = rawStep / Math.pow(10, exponent);
        if (fraction < 1.5) return Math.pow(10, exponent) * 1;
        else if (fraction < 3.5) return Math.pow(10, exponent) * 2;
        else if (fraction < 7.5) return Math.pow(10, exponent) * 5;
        else return Math.pow(10, exponent) * 10;
    }

    private boolean isValidPoint(double[] p) {
        return p[0] >= 0 && p[0] <= drawingPane.getWidth() && p[1] >= 0 && p[1] <= drawingPane.getHeight();
    }

    /** Отрисовка кривой как полинома y(x), полученного из кривой Безье */
    private void updateBezier() {
        double[] coeff = getBezierPolynomialCoeffs();
        bezierPath.getElements().clear();
        int steps = 200;
        boolean first = true;
        for (int i = 0; i <= steps; i++) {
            double x = xMin + (xMax - xMin) * i / steps;
            double y = evaluatePolynomial(coeff, x);
            double[] scr = logicalToScreen(x, y);
            if (isValidPoint(scr)) {
                if (first) {
                    bezierPath.getElements().add(new MoveTo(scr[0], scr[1]));
                    first = false;
                } else {
                    bezierPath.getElements().add(new LineTo(scr[0], scr[1]));
                }
            } else {
                first = true;
            }
        }
    }

    /** Вычисление коэффициентов полинома y(x) степени N_POINTS-1 для кривой Безье
     *  с фиксированными равномерными X-координатами контрольных точек.
     *  Возвращает массив coeff, где y = coeff[0] + coeff[1]*x + ... + coeff[degree]*x^degree
     */
    private double[] getBezierPolynomialCoeffs() {
        int n = N_POINTS;
        int degree = n - 1;
        double[] coeff = new double[degree + 1];
        double L = xMax - xMin;

        for (int k = 0; k < n; k++) {
            double yk = logicalY.get(k);
            double binomNk = binomial(degree, k);

            // Коэффициенты полинома Бернштейна в базисе {t^i}
            double[] bern = new double[degree + 1];
            for (int i = 0; i <= degree - k; i++) {
                int sign = (i % 2 == 0) ? 1 : -1;
                double c = binomNk * binomial(degree - k, i) * sign;
                bern[k + i] += c;
            }

            // Замена t = (x - xMin)/L
            for (int i = 0; i <= degree; i++) {
                double a = bern[i] * yk;
                // (x - xMin)^i = sum_{j=0}^i C(i,j) * x^j * (-xMin)^(i-j)
                for (int j = 0; j <= i; j++) {
                    double binomIJ = binomial(i, j);
                    double term = a * binomIJ * Math.pow(-xMin, i - j) / Math.pow(L, i);
                    coeff[j] += term;
                }
            }
        }
        return coeff;
    }

    private double evaluatePolynomial(double[] coeff, double x) {
        double result = 0;
        double pow = 1;
        for (int i = 0; i < coeff.length; i++) {
            result += coeff[i] * pow;
            pow *= x;
        }
        return result;
    }

    private int binomial(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        long res = 1;
        for (int i = 1; i <= k; i++) {
            res = res * (n - k + i) / i;
        }
        return (int) res;
    }

    /** Вывод уравнения в текстовое поле и вызов callback */
    private void showEquation() {
        double[] coeff = getBezierPolynomialCoeffs();
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (int i = 0; i < coeff.length; i++) {
            double c = coeff[i];
            if (Math.abs(c) < 1e-10) continue;
            if (!first) {
                sb.append(c > 0 ? " + " : " - ");
            } else {
                if (c < 0) sb.append("-");
                first = false;
            }
            double absC = Math.abs(c);
            boolean isOne = Math.abs(absC - 1.0) < 1e-10;

            if (i == 0) {
                sb.append(formatNumber(absC));
            } else {
                if (!isOne) {
                    sb.append(formatNumber(absC)).append("*");
                }
                sb.append("x");
                if (i > 1) {
                    sb.append("^").append(i);
                }
            }
        }
        if (first) sb.append("0");
        equationArea.setText(sb.toString());
        errorLabel.setText("");
        callback.accept(sb.toString());
    }

    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "0";
        String s = Double.toString(value);
        if (s.contains(".")) {
            s = s.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return s;
    }
}