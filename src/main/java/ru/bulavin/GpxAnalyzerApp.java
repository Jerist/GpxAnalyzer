package ru.bulavin;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class GpxAnalyzerApp extends Application {

    private TextArea outputArea;
    private WebEngine webEngine;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("GPX Analyzer");

        Label label = new Label("Выберите GPX файл для анализа:");
        Button button = new Button("Выбрать файл");
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setMinHeight(210.);

        WebView mapView = new WebView();
        webEngine = mapView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("/map.html")).toExternalForm());

        button.setOnAction(_ -> chooseFile(primaryStage));

        VBox vbox = new VBox(10, label, button, outputArea);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(vbox);
        borderPane.setCenter(mapView);

        Scene scene = new Scene(borderPane, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void chooseFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GPX Files", "*.gpx"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            analyzeGpxFile(file);
        }
    }

    private void analyzeGpxFile(File file) {
        try {
            GPX gpx = GPX.read(Paths.get(file.getAbsolutePath()));
            List<WayPoint> points = gpx.tracks()
                    .flatMap(Track::segments)
                    .flatMap(TrackSegment::points)
                    .toList();

            GpxStatistics statistics = new GpxStatistics(points);

            StringBuilder result = new StringBuilder();
            result.append("Общая дистанция: ").append(String.format("%.3f", statistics.getTotalDistance())).append(" км\n");
            result.append("Общее время движения: ").append(statistics.getTotalTimeMoving().toMinutes()).append(" минут\n");
            result.append("Общее время остановок: ").append(statistics.getTotalTimeStopped().toMinutes()).append(" минут\n");
            result.append("Грязная средняя скорость: ").append(String.format("%.3f", statistics.getDirtyAverageSpeed())).append(" км/ч\n");
            result.append("Чистая средняя скорость: ").append(String.format("%.3f", statistics.getCleanAverageSpeed())).append(" км/ч\n");


            Optional<Double> minElevation = statistics.getMinElevation();
            if (minElevation.isPresent()) {
                result.append("Минимальная высота: ").append(String.format("%.3f", minElevation.get())).append(" м\n");
            }

            Optional<Double> maxElevation = statistics.getMaxElevation();
            if (maxElevation.isPresent()) {
                result.append("Максимальная высота: ").append(String.format("%.3f", maxElevation.get())).append(" м\n");
            }

            Optional<Double> maxAscent = statistics.getMaxAscent();
            if (maxAscent.isPresent()) {
                result.append("Максимальный подъём: ").append(String.format("%.3f", maxAscent.get())).append(" м\n");
            }

            Optional<Double> maxDescent = statistics.getMaxDescent();
            if (maxDescent.isPresent()) {
                result.append("Максимальный спуск: ").append(String.format("%.3f", maxDescent.get())).append(" м\n");
            }

            Optional<Double> minTemperature = statistics.getMinTemperature();
            if (minTemperature.isPresent()) {
                result.append("Минимальная температура: ").append(String.format("%.3f", minTemperature.get())).append(" °C\n");
            }

            Optional<Double> maxTemperature = statistics.getMaxTemperature();
            if (maxTemperature.isPresent()) {
                result.append("Максимальная температура: ").append(String.format("%.3f", maxTemperature.get())).append(" °C\n");
            }

            Optional<Integer> minHeartRate = statistics.getMinHeartRate();
            if (minHeartRate.isPresent()) {
                result.append("Минимальное значение пульса: ").append(minHeartRate.get()).append(" уд/м\n");
            }

            Optional<Integer> maxHeartRate = statistics.getMaxHeartRate();
            if (maxHeartRate.isPresent()) {
                result.append("Максимальное значение пульса: ").append(maxHeartRate.get()).append(" уд/м\n");
            }

            Optional<Double> averageHeartRate = statistics.getAverageHeartRate();
            if (averageHeartRate.isPresent()) {
                result.append("Среднее значение пульса: ").append(String.format("%.3f", averageHeartRate.get())).append(" уд/м\n");
            }

            outputArea.setText(result.toString());

            StringBuilder jsPoints = new StringBuilder();
            points.forEach(p -> {
                jsPoints.append("addPoint(")
                        .append(p.getLatitude().doubleValue()).append(", ")
                        .append(p.getLongitude().doubleValue()).append(");");
            });



            webEngine.executeScript("clearMap();");
            webEngine.executeScript(jsPoints.toString());

            if (!points.isEmpty()) {
                WayPoint firstPoint = points.getFirst();
                webEngine.executeScript("setCenter(" +
                        firstPoint.getLatitude().doubleValue() + ", " +
                        firstPoint.getLongitude().doubleValue() + ");");
            }

        } catch (IOException e) {
            outputArea.setText("Ошибка чтения GPX файла: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
