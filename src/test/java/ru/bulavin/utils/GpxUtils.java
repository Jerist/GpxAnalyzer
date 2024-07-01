package ru.bulavin.utils;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

public class GpxUtils {
    public static List<WayPoint> getPoints() {
        return List.of(
                WayPoint.builder()
                        .lat(51.6750)
                        .lon(39.2089)
                        .ele(150.0)
                        .time(Instant.parse("2022-01-01T10:00:00Z"))
                        .build(),
                WayPoint.builder()
                        .lat(51.6615)
                        .lon(39.2003)
                        .ele(160.0)
                        .time(Instant.parse("2022-01-01T10:30:00Z"))
                        .build(),
                WayPoint.builder()
                        .lat(51.6580)
                        .lon(39.2105)
                        .ele(140.0)
                        .time(Instant.parse("2022-01-01T11:00:00Z"))
                        .build()
        );
    }

    public static List<WayPoint> getPoints(Path path) throws IOException {
        GPX gpx = GPX.read(path);
        return gpx.tracks()
                .flatMap(Track::segments)
                .flatMap(TrackSegment::points)
                .toList();
    }

}
