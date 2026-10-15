package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Ride;

import java.time.Clock;
import java.time.LocalTime;
import java.time.ZoneId;

public class PeakHourFareStrategy implements FareStrategy {
    private static final double PEAK_MULTIPLIER = 1.5;
    private static final LocalTime MORNING_START = LocalTime.of(8, 0);
    private static final LocalTime MORNING_END = LocalTime.of(10, 0);
    private static final LocalTime EVENING_START = LocalTime.of(17, 0);
    private static final LocalTime EVENING_END = LocalTime.of(20, 0);
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Kolkata");

    private final FareStrategy baseStrategy;
    private final Clock clock;
    private final ZoneId zoneId;

    public PeakHourFareStrategy(FareStrategy baseStrategy) {
        this(baseStrategy, Clock.system(DEFAULT_ZONE), DEFAULT_ZONE);
    }

    public PeakHourFareStrategy(FareStrategy baseStrategy, Clock clock, ZoneId zoneId) {
        if (baseStrategy == null) {
            throw new InvalidDataException("Base fare strategy cannot be null");
        }
        if (clock == null) {
            throw new InvalidDataException("Clock cannot be null");
        }
        if (zoneId == null) {
            throw new InvalidDataException("Time zone cannot be null");
        }

        this.baseStrategy = baseStrategy;
        this.clock = clock;
        this.zoneId = zoneId;
    }

    @Override
    public double calculateFare(Ride ride) {
        double baseFare = baseStrategy.calculateFare(ride);
        LocalTime currentTime = LocalTime.now(clock.withZone(zoneId));

        if (isPeakHour(currentTime)) {
            return baseFare * PEAK_MULTIPLIER;
        }
        return baseFare;
    }

    private boolean isPeakHour(LocalTime time) {
        boolean morningPeak = !time.isBefore(MORNING_START) && time.isBefore(MORNING_END);
        boolean eveningPeak = !time.isBefore(EVENING_START) && time.isBefore(EVENING_END);
        return morningPeak || eveningPeak;
    }
}
