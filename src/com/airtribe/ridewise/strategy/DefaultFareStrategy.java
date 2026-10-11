package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.exception.InvalidRideStateException;
import com.airtribe.ridewise.model.Ride;

public class DefaultFareStrategy implements FareStrategy {
    @Override
    public double calculateFare(Ride ride) {
        if (ride == null) {
            throw new InvalidDataException("Ride cannot be null when calculating fare");
        }
        if (ride.getDriver() == null) {
            throw new InvalidRideStateException(
                    "Cannot calculate fare before a driver is assigned"
            );
        }

        return switch (ride.getDriver().getVehicleType()) {
            case BIKE -> 20.0 + ride.getDistance() * 8.0;
            case AUTO -> 30.0 + ride.getDistance() * 12.0;
            case CAR -> 50.0 + ride.getDistance() * 18.0;
        };
    }
}
