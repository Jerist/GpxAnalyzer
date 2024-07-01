package ru.bulavin;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import io.jenetics.jpx.WayPoint;
import ru.bulavin.utils.GpxUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GpxStatisticsTest {

    private static final List<WayPoint> points = GpxUtils.getPoints();
    private static List<WayPoint> pointsWithTempAndHr = List.of();
    private static final String pathToFileWithTempAndHr = "src\\test\\java\\ru\\bulavin\\utils\\test_sample.gpx";

    @BeforeAll
    public static void init(){
        try {
            Path currentDir = Paths.get(".").toAbsolutePath();
            pointsWithTempAndHr = GpxUtils.getPoints(Paths.get(currentDir.toString(), pathToFileWithTempAndHr));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCalculateTotalDistance() {
        GpxStatistics statistics = new GpxStatistics(points);

        assertEquals(2.42, statistics.getTotalDistance(), 0.01);
    }

    @Test
    public void testCalculateTotalMovingTime() {
        GpxStatistics statistics = new GpxStatistics(points);

        assertEquals(1, statistics.getTotalTimeMoving().toHours());
    }

    @Test
    public void testCalculateTotalStoppedTime() {
        GpxStatistics statistics = new GpxStatistics(points);

        assertEquals(0, statistics.getTotalTimeStopped().toHours());
    }

    @Test
    public void testCalculateAverageSpeed() {
        GpxStatistics statistics = new GpxStatistics(points);

        assertEquals(2.42, statistics.getDirtyAverageSpeed(), 0.01);
        assertEquals(2.42, statistics.getCleanAverageSpeed(), 0.01);
    }

    @Test
    public void testCalculateMinAndMaxElevation() {
        GpxStatistics statistics = new GpxStatistics(points);
        Optional<Double> minElevation = statistics.getMinElevation();
        Optional<Double> maxElevation = statistics.getMaxElevation();
        assertTrue(minElevation.isPresent());
        assertTrue(maxElevation.isPresent());
        assertEquals(140.0, minElevation.get(), 0.01);
        assertEquals(160.0, maxElevation.get(), 0.01);
    }

    @Test
    public void testCalculateTemperatureStatistics() {
        GpxStatistics statistics = new GpxStatistics(points);

        Optional<Double> minTemperature = statistics.getMinTemperature();
        Optional<Double> maxTemperature = statistics.getMaxTemperature();
        assertFalse(minTemperature.isPresent());
        assertFalse(maxTemperature.isPresent());

    }

    @Test
    public void testCalculateHeartRateStatistics() {
        GpxStatistics statistics = new GpxStatistics(points);

        Optional<Integer> minHeartRate = statistics.getMinHeartRate();
        Optional<Integer> maxHeartRate = statistics.getMaxHeartRate();
        Optional<Double> averageHeartRate = statistics.getAverageHeartRate();

        assertFalse(minHeartRate.isPresent());
        assertFalse(maxHeartRate.isPresent());
        assertFalse(averageHeartRate.isPresent());
    }








    @Test
    public void testTotalMovingTime() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Duration movingTime = stats.getTotalTimeMoving();
        assertEquals(Duration.ofMinutes(0), movingTime); // assuming total moving time should be 30 minutes
    }
    @Test
    public void testTotalStoppedTime() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Duration stoppedTime = stats.getTotalTimeStopped();
        assertEquals(Duration.ofMinutes(30), stoppedTime); // assuming total stopped time should be 30 minutes
    }

    @Test
    public void testAverageSpeed() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        double clearAverageSpeed = stats.getCleanAverageSpeed();
        double dirtyAverageSpeed = stats.getDirtyAverageSpeed();
        assertEquals(0.4, clearAverageSpeed, 0.1);
        assertEquals(0.1, dirtyAverageSpeed, 0.1);
    }

    @Test
    public void testTotalDistance() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        double totalDistance = stats.getTotalDistance();
        assertEquals(0.2, totalDistance, 0.01);
    }

    @Test
    public void testMinElevation() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Optional<Double> minElevation = stats.getMinElevation();
        assertTrue(minElevation.isPresent());
        assertEquals(150, minElevation.get());
    }

    @Test
    public void testMaxElevation() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Optional<Double> maxElevation = stats.getMaxElevation();

        assertTrue(maxElevation.isPresent());
        assertEquals(165, maxElevation.get());
    }

    @Test
    public void testAscentAndDescent() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Optional<Double> maxAscent = stats.getMaxAscent();
        Optional<Double> maxDescent = stats.getMaxDescent();

        assertTrue(maxAscent.isPresent());
        assertTrue(maxDescent.isPresent());

        assertEquals(maxAscent.get(), 15);
        assertEquals(maxDescent.get(), 0);
    }

    @Test
    public void testTemperature() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Optional<Double> maxTemperature = stats.getMaxTemperature();
        Optional<Double> minTemperature = stats.getMinTemperature();

        assertTrue(maxTemperature.isPresent());
        assertTrue(minTemperature.isPresent());

        assertEquals(maxTemperature.get(), 26);
        assertEquals(minTemperature.get(), 20);
    }

    @Test
    public void testHeartRate() {
        GpxStatistics stats = new GpxStatistics(pointsWithTempAndHr);
        Optional<Integer> maxHeartRate = stats.getMaxHeartRate();
        Optional<Integer> minHeartRate = stats.getMinHeartRate();
        Optional<Double> averageHeartRate = stats.getAverageHeartRate();

        assertTrue(maxHeartRate.isPresent());
        assertTrue(minHeartRate.isPresent());
        assertTrue(averageHeartRate.isPresent());

        assertEquals(maxHeartRate.get(), 95);
        assertEquals(minHeartRate.get(), 80);
        assertEquals(averageHeartRate.get(), 87.5, 0.01);

    }
}
