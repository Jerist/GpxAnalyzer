package ru.bulavin;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.WayPoint;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GpxStatistics {
    private Duration totalTimeMoving = Duration.ZERO;
    private Duration totalTimeStopped = Duration.ZERO;

    private double totalDistanceToKilometers = 0.0;

    private double dirtyAverageSpeed = 0.0;
    private double cleanAverageSpeed = 0.0;

    private Optional<Double> minElevation = Optional.empty();
    private Optional<Double> maxElevation = Optional.empty();

    private Optional<Double> maxAscent = Optional.empty();
    private Optional<Double> maxDescent = Optional.empty();
    private double currentAscent = 0.0;
    private double currentDescent = 0.0;


    private Optional<Double> minTemperature = Optional.empty();
    private Optional<Double> maxTemperature = Optional.empty();

    private Optional<Integer> minHeartRate = Optional.empty();
    private Optional<Integer> maxHeartRate = Optional.empty();
    private Optional<Double> averageHeartRate = Optional.empty();
    private int totalHeartRate = 0;
    private int heartRateCount = 0;



    private static final double STOP_SPEED_THRESHOLD = 1.0 / 3.6; // 1 км/ч в м/с

    public GpxStatistics(List<WayPoint> points) {
        calculateStatistics(points);
    }

    public Duration getTotalTimeMoving() {
        return totalTimeMoving;
    }

    public Duration getTotalTimeStopped() {
        return totalTimeStopped;
    }

    public double getTotalDistance() {
        return totalDistanceToKilometers;
    }

    public double getDirtyAverageSpeed() {
        return dirtyAverageSpeed;
    }

    public double getCleanAverageSpeed() {
        return cleanAverageSpeed;
    }

    public Optional<Double> getMinElevation() {
        return minElevation;
    }

    public Optional<Double> getMaxElevation() {
        return maxElevation;
    }

    public Optional<Double> getMaxAscent() {
        return maxAscent;
    }

    public Optional<Double> getMaxDescent() {
        return maxDescent;
    }

    public Optional<Double> getMinTemperature() {
        return minTemperature;
    }

    public Optional<Double> getMaxTemperature() {
        return maxTemperature;
    }

    public Optional<Integer> getMinHeartRate() {
        return minHeartRate;
    }

    public Optional<Integer> getMaxHeartRate() {
        return maxHeartRate;
    }

    public Optional<Double> getAverageHeartRate() {
        return averageHeartRate;
    }


    private void calculateStatistics(List<WayPoint> points) {
        WayPoint firstPoint = points.getFirst();
        initElevationStats(firstPoint);
        initTemperatureStats(firstPoint);
        initHeartRateStats(firstPoint);
        initAscentAndDescent();

        for (int i = 1; i < points.size(); i++) {
            WayPoint currentPoint = points.get(i);
            WayPoint prevPoint = points.get(i - 1);

            calculateTotalDistance(currentPoint, prevPoint);
            calculateTimeStats(currentPoint, prevPoint);
            calculateElevationStats(currentPoint, prevPoint);
            calculateTemperatureStats(currentPoint);
            calculateHeartRateStats(currentPoint);
        }

        calculateAverageSpeeds();
        if (heartRateCount > 0) {
            averageHeartRate = Optional.of((double) totalHeartRate / heartRateCount);
        }
    }

    private void calculateTotalDistance(WayPoint currentPoint, WayPoint prevPoint) {
        double distanceToMeters = currentPoint.distance(prevPoint).doubleValue();
        double distanceToKilometers = distanceToMeters / 1000;
        totalDistanceToKilometers += distanceToKilometers;
    }

    private void calculateTimeStats(WayPoint currentPoint, WayPoint prevPoint) {
        Optional<Instant> currentTime = currentPoint.getTime();
        Optional<Instant> prevTime = prevPoint.getTime();
        if (currentTime.isPresent() && prevTime.isPresent()) {
            Duration duration = Duration.between(prevTime.get(), currentTime.get());
            double distanceToMeters = currentPoint.distance(prevPoint).doubleValue();
            double timeInSeconds = duration.toSeconds();
            double speed = distanceToMeters / timeInSeconds;

            if (speed > STOP_SPEED_THRESHOLD) {
                totalTimeMoving = totalTimeMoving.plus(duration);
            } else {
                totalTimeStopped = totalTimeStopped.plus(duration);
            }
        }
    }

    private void initElevationStats(WayPoint firstPoint) {
        Optional<Double> currentElevation = firstPoint.getElevation().map(Length::doubleValue);
        if (currentElevation.isPresent()) {
            maxElevation = currentElevation;
            minElevation = currentElevation;
        }
    }

    private void initAscentAndDescent() {
        maxAscent = Optional.of(0.0);
        maxDescent = Optional.of(0.0);
    }

    private void calculateElevationStats(WayPoint currentPoint, WayPoint prevPoint) {
        Optional<Double> currentElevation = currentPoint.getElevation().map(Length::doubleValue);
        Optional<Double> prevElevation = prevPoint.getElevation().map(Length::doubleValue);
        if (currentElevation.isPresent() && prevElevation.isPresent()) {
            double elevationDifference = currentElevation.get() - prevElevation.get();
            if (elevationDifference > 0) {
                currentAscent += elevationDifference;
                if(maxAscent.isEmpty() || currentAscent > maxAscent.get()) {
                    maxAscent = Optional.of(currentAscent);
                }
                currentDescent = 0;
            }
            else
                if (elevationDifference < 0) {
                    currentDescent -= elevationDifference;
                    if(maxDescent.isEmpty() || currentDescent > maxDescent.get()) {
                        maxDescent = Optional.of(currentDescent);
                    }
                    currentAscent = 0;

                }
        }
        if (currentElevation.isPresent()) {
            if(maxElevation.isEmpty() || currentElevation.get() > maxElevation.get()) {
                maxElevation = currentElevation;
            }
            if(minElevation.isEmpty() || currentElevation.get() < minElevation.get()) {
                minElevation = currentElevation;
            }
        }
    }

    private void initTemperatureStats(WayPoint firstPoint) {
        Optional<Double> temperature = GpxHelper.getExtensionValueAsDouble(firstPoint, "temp");
        if (temperature.isPresent()) {
            minTemperature = temperature;
            maxTemperature = temperature;
        }
    }

    private void calculateTemperatureStats(WayPoint currentPoint) {
        Optional<Double> temperature = GpxHelper.getExtensionValueAsDouble(currentPoint, "temp");
        if (temperature.isPresent()) {
            if (minTemperature.isEmpty() || temperature.get() < minTemperature.get()) {
                minTemperature = temperature;
            }
            if (maxTemperature.isEmpty() || temperature.get() > maxTemperature.get()) {
                maxTemperature = temperature;
            }
        }
    }

    private void initHeartRateStats(WayPoint firstPoint) {
        Optional<Integer> heartRate = GpxHelper.getExtensionValueAsInt(firstPoint, "hr");
        if (heartRate.isPresent()) {
            minHeartRate = heartRate;
            maxHeartRate = heartRate;
            totalHeartRate += heartRate.get();
            heartRateCount++;
        }
    }

    private void calculateHeartRateStats(WayPoint currentPoint) {
        Optional<Integer> heartRate = GpxHelper.getExtensionValueAsInt(currentPoint, "hr");
        if (heartRate.isPresent()) {
            totalHeartRate += heartRate.get();
            heartRateCount++;
            if (minHeartRate.isEmpty() || heartRate.get() < minHeartRate.get()) {
                minHeartRate = heartRate;
            }
            if (maxHeartRate.isEmpty() || heartRate.get() > maxHeartRate.get()) {
                maxHeartRate = heartRate;
            }
        }
    }

    private void calculateAverageSpeeds() {
        if (!totalTimeMoving.isZero()) {
            dirtyAverageSpeed = totalDistanceToKilometers / (totalTimeMoving.getSeconds() / 3600.0); // km/h
        }
        if (!(totalTimeMoving.plus(totalTimeStopped)).isZero()) {
            cleanAverageSpeed = totalDistanceToKilometers / ((totalTimeMoving.plus(totalTimeStopped)).getSeconds() / 3600.0); // km/h
        }
    }
}
