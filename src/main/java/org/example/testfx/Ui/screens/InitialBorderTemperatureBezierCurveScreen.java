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
 * Реализует интерфейс Screen, чтобы использоваться в многоэкранном приложении.
 */
//
public class InitialBorderTemperatureBezierCurveScreen implements Screen {

    // Параметры (можно менять через конструктор или сеттеры)
    private static final int N_POINTS = 6;               // количество контрольных точек
    private final double xMin = 0.0;                            // минимальный логический X
    private final double xMax;                            // максимальный логический X
    private final double yMin;                         // минимальный логический Y
    private final double yMax;                          // максимальный логический Y
    private final double margin = 50;                            // отступы от краёв панели (пиксели)

    // Компоненты интерфейса
    private Pane drawingPane;
    private Group gridGroup;
    private Group curveGroup;
    private List<Circle> points;
    private List<Double> logicalX;
    private List<Double> logicalY;
    private Path bezierPath;
    private TextArea equationArea;
    private Label errorLabel;

    // Корневой узел экрана
    private BorderPane root;

    private final Consumer<String> callback;


    public InitialBorderTemperatureBezierCurveScreen(double xMax, double tMax, double tMin, Consumer<String> callback) {
        this.callback = callback;
        this.xMax = xMax;
        this.yMax = tMax;
        this.yMin = tMin;

        // Создаём корневой BorderPane
        root = new BorderPane();

        // Центральная область с рисунком
        drawingPane = new Pane();
        drawingPane.setPrefSize(800, 500);
        drawingPane.setStyle("-fx-background-color: #ffffff;");

        // Слушатели изменения размера для перерисовки сетки и кривой
        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> refresh());
        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> refresh());

        gridGroup = new Group();
        curveGroup = new Group();
        drawingPane.getChildren().addAll(gridGroup, curveGroup);

        // Инициализация данных и точек
        initializePoints();

        // Создаём путь для кривой
        bezierPath = new Path();
        bezierPath.setStroke(Color.BLUE);
        bezierPath.setStrokeWidth(2);
        curveGroup.getChildren().add(bezierPath);

        // Добавляем кружки в группу
        for (Circle c : points) {
            curveGroup.getChildren().add(c);
        }

        root.setCenter(drawingPane);

        // Нижняя панель с кнопкой и текстовой областью
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

        // Первоначальная отрисовка будет выполнена автоматически
        // после того, как drawingPane получит реальные размеры (через слушатели)
    }

    @Override
    public Parent getView() {
        return root;
    }

    // ---------- Внутренние методы (без изменений, кроме удаления ссылок на Stage/Scene) ----------

    /** Полное обновление: сетка, позиции точек, кривая */
    private void refresh() {
        drawGridAndAxes();
        updatePointsPosition();
        updateBezier();
    }

    /** Создание/пересоздание точек с начальными значениями */
    private void initializePoints() {
        logicalX = new ArrayList<>();
        logicalY = new ArrayList<>();
        points = new ArrayList<>();

        double step = (xMax - xMin) / (N_POINTS - 1);
        for (int i = 0; i < N_POINTS; i++) {
            double lx = xMin + i * step;
            double ly = 0.0; // все на y=0
            logicalX.add(lx);
            logicalY.add(ly);
        }

        // Создаём кружки
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

    /** Преобразование логических координат в экранные */
    private double[] logicalToScreen(double lx, double ly) {
        double w = drawingPane.getWidth() - 2 * margin;
        double h = drawingPane.getHeight() - 2 * margin;
        if (w <= 0 || h <= 0) return new double[]{margin, margin}; // защита от нулевого размера
        double sx = margin + (lx - xMin) / (xMax - xMin) * w;
        double sy = drawingPane.getHeight() - margin - (ly - yMin) / (yMax - yMin) * h;
        return new double[]{sx, sy};
    }

    /** Преобразование экранных координат в логические */
    private double[] screenToLogical(double sx, double sy) {
        double w = drawingPane.getWidth() - 2 * margin;
        double h = drawingPane.getHeight() - 2 * margin;
        if (w <= 0 || h <= 0) return new double[]{xMin, yMin};
        double lx = xMin + (sx - margin) / w * (xMax - xMin);
        double ly = yMax - (sy - margin) / h * (yMax - yMin);
        return new double[]{lx, ly};
    }

    /** Обновление позиций кружков по текущим логическим координатам */
    private void updatePointsPosition() {
        for (int i = 0; i < N_POINTS; i++) {
            double[] scr = logicalToScreen(logicalX.get(i), logicalY.get(i));
            points.get(i).setCenterX(scr[0]);
            points.get(i).setCenterY(scr[1]);
        }
    }

    /** Настройка перетаскивания */
    private void setupMouseHandling(Circle circle, int index) {
        circle.setOnMousePressed(event -> {
            double deltaX = circle.getCenterX() - event.getSceneX();
            double deltaY = circle.getCenterY() - event.getSceneY();
            circle.setUserData(new double[]{deltaX, deltaY});
        });

        circle.setOnMouseDragged(event -> {
            double[] delta = (double[]) circle.getUserData();
            if (delta != null) {
                double newSx = event.getSceneX() + delta[0];
                double newSy = event.getSceneY() + delta[1];

                double[] newLog = screenToLogical(newSx, newSy);
                double newLx = newLog[0];
                double newLy = newLog[1];

                // Ограничения по X
                if (index == 0) {
                    newLx = logicalX.get(0); // левая крайняя фиксирована
                } else if (index == N_POINTS - 1) {
                    newLx = logicalX.get(N_POINTS - 1); // правая крайняя фиксирована
                } else {
                    double leftBound = logicalX.get(index - 1) + 0.01; // небольшой зазор
                    double rightBound = logicalX.get(index + 1) - 0.01;
                    if (newLx < leftBound) newLx = leftBound;
                    if (newLx > rightBound) newLx = rightBound;
                }

                // Ограничения по Y в пределах [yMin, yMax]
                if (newLy < yMin) newLy = yMin;
                if (newLy > yMax) newLy = yMax;

                logicalX.set(index, newLx);
                logicalY.set(index, newLy);

                double[] scr = logicalToScreen(newLx, newLy);
                circle.setCenterX(scr[0]);
                circle.setCenterY(scr[1]);

                updateBezier();
            }
        });

        circle.setOnMouseReleased(event -> circle.setUserData(null));
    }

    /** Рисование сетки и осей с адаптивным шагом */
    private void drawGridAndAxes() {
        gridGroup.getChildren().clear();

        double w = drawingPane.getWidth();
        double h = drawingPane.getHeight();
        if (w == 0 || h == 0) return;

        // Определяем разумный шаг сетки в зависимости от диапазона
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;

        // Хотим примерно 10 делений по каждой оси
        double xStep = roundStep(xRange / 10);
        double yStep = roundStep(yRange / 10);

        // Вертикальные линии
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

        // Горизонтальные линии
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

        // Ось X (y=0)
        double[] xAxisStart = logicalToScreen(xMin, 0);
        double[] xAxisEnd = logicalToScreen(xMax, 0);
        if (isValidPoint(xAxisStart) && isValidPoint(xAxisEnd)) {
            Line xAxis = new Line(xAxisStart[0], xAxisStart[1], xAxisEnd[0], xAxisEnd[1]);
            xAxis.setStroke(Color.BLACK);
            xAxis.setStrokeWidth(1.5);
            gridGroup.getChildren().add(xAxis);
        }

        // Ось Y (x=0)
        double[] yAxisStart = logicalToScreen(0, yMin);
        double[] yAxisEnd = logicalToScreen(0, yMax);
        if (isValidPoint(yAxisStart) && isValidPoint(yAxisEnd)) {
            Line yAxis = new Line(yAxisStart[0], yAxisStart[1], yAxisEnd[0], yAxisEnd[1]);
            yAxis.setStroke(Color.BLACK);
            yAxis.setStrokeWidth(1.5);
            gridGroup.getChildren().add(yAxis);
        }

        // Подписи делений (только если они попадают в видимую область)
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

    /** Округление шага до "красивых" чисел */
    private double roundStep(double rawStep) {
        double exponent = Math.floor(Math.log10(rawStep));
        double fraction = rawStep / Math.pow(10, exponent);
        if (fraction < 1.5) return Math.pow(10, exponent) * 1;
        else if (fraction < 3.5) return Math.pow(10, exponent) * 2;
        else if (fraction < 7.5) return Math.pow(10, exponent) * 5;
        else return Math.pow(10, exponent) * 10;
    }

    /** Проверка, что точка находится в разумных пределах экрана */
    private boolean isValidPoint(double[] p) {
        return p[0] >= 0 && p[0] <= drawingPane.getWidth() && p[1] >= 0 && p[1] <= drawingPane.getHeight();
    }

    /** Пересчёт кривой Безье */
    private void updateBezier() {
        int n = N_POINTS;
        if (n < 2) return;

        bezierPath.getElements().clear();
        int steps = 200;
        boolean first = true;
        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double xt = 0, yt = 0;
            for (int j = 0; j < n; j++) {
                double coeff = bernstein(n - 1, j, t);
                xt += coeff * logicalX.get(j);
                yt += coeff * logicalY.get(j);
            }
            double[] scr = logicalToScreen(xt, yt);
            if (isValidPoint(scr)) { // добавляем только если точка в пределах экрана
                if (first) {
                    bezierPath.getElements().add(new MoveTo(scr[0], scr[1]));
                    first = false;
                } else {
                    bezierPath.getElements().add(new LineTo(scr[0], scr[1]));
                }
            } else {
                // Если вышли за пределы, начинаем новый сегмент (разрыв линии)
                first = true;
            }
        }
    }

    /** Биномиальный коэффициент */
    private int binomial(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        int result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }

    /** Полином Бернштейна */
    private double bernstein(int n, int k, double t) {
        return binomial(n, k) * Math.pow(1 - t, n - k) * Math.pow(t, k);
    }

    /** Вывод уравнения зависимости y от x (интерполяционный полином) в формате, совместимом с ExpressionParser */
    private void showEquation() {
        int n = N_POINTS;
        if (n < 2) {
            equationArea.setText("Недостаточно точек для интерполяции");
            return;
        }

        // Проверка уникальности X
        for (int i = 1; i < n; i++) {
            if (Math.abs(logicalX.get(i) - logicalX.get(i - 1)) < 1e-6) {
                equationArea.setText("Ошибка: точки должны иметь разные X (возможно, слишком близки)");
                return;
            }
        }

        // Решаем систему для нахождения коэффициентов полинома степени n-1
        double[] coeff;
        try {
            coeff = solvePolynomial(logicalX, logicalY);
        } catch (Exception e) {
            equationArea.setText("Ошибка вычисления полинома: " + e.getMessage());
            return;
        }

        // Форматируем уравнение в виде, понятном ExpressionParser
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (int i = 0; i < coeff.length; i++) {
            double c = coeff[i];
            if (Math.abs(c) < 1e-10) continue; // пропускаем практически нулевые члены

            // Определяем знак
            if (!first) {
                sb.append(c > 0 ? " + " : " - ");
            } else {
                if (c < 0) sb.append("-");
                first = false;
            }

            double absC = Math.abs(c);
            boolean isOne = Math.abs(absC - 1.0) < 1e-10;

            if (i == 0) { // свободный член
                sb.append(formatNumber(absC));
            } else {
                // Коэффициент (опускаем, если равен 1 или -1)
                if (!isOne) {
                    sb.append(formatNumber(absC)).append("*");
                }
                sb.append("x");
                if (i > 1) {
                    sb.append("^").append(i);
                }
            }
        }

        if (first) sb.append("0"); // все коэффициенты нулевые
        equationArea.setText(sb.toString());
        errorLabel.setText("");
        callback.accept(sb.toString());
    }

    /** Вспомогательный метод для форматирования чисел без лишних нулей */
    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return "0";
        // Убираем trailing zeros после десятичной точки
        String s = Double.toString(value);
        if (s.contains(".")) {
            s = s.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return s;
    }


    /** Решение системы линейных уравнений для полинома степени n-1 методом Гаусса */
    private double[] solvePolynomial(List<Double> xList, List<Double> yList) {
        int n = xList.size();
        double[][] a = new double[n][n + 1]; // расширенная матрица

        for (int i = 0; i < n; i++) {
            double xi = xList.get(i);
            double pow = 1.0;
            for (int j = 0; j < n; j++) {
                a[i][j] = pow;
                pow *= xi;
            }
            a[i][n] = yList.get(i);
        }

        // Прямой ход с выбором главного элемента
        for (int col = 0; col < n; col++) {
            int maxRow = col;
            double maxVal = Math.abs(a[col][col]);
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(a[row][col]) > maxVal) {
                    maxVal = Math.abs(a[row][col]);
                    maxRow = row;
                }
            }
            if (maxVal < 1e-12) {
                throw new RuntimeException("Матрица вырождена (возможно, одинаковые X)");
            }
            // Меняем строки местами
            double[] temp = a[col];
            a[col] = a[maxRow];
            a[maxRow] = temp;

            // Нормализуем текущую строку
            double div = a[col][col];
            for (int j = col; j <= n; j++) {
                a[col][j] /= div;
            }

            // Исключаем элемент в нижних строках
            for (int row = col + 1; row < n; row++) {
                double factor = a[row][col];
                for (int j = col; j <= n; j++) {
                    a[row][j] -= factor * a[col][j];
                }
            }
        }

        // Обратная подстановка
        double[] coeff = new double[n];
        coeff[n - 1] = a[n - 1][n];
        for (int i = n - 2; i >= 0; i--) {
            double sum = a[i][n];
            for (int j = i + 1; j < n; j++) {
                sum -= a[i][j] * coeff[j];
            }
            coeff[i] = sum;
        }
        return coeff;
    }


}