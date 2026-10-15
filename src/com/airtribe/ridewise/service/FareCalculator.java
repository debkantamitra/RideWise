package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.strategy.FareStrategy;

public class FareCalculator {
    private final FareStrategy fareStrategy;

    public FareCalculator(FareStrategy fareStrategy) {
        if (fareStrategy == null) {
            throw new InvalidDataException("Fare strategy cannot be null");
        }
        this.fareStrategy = fareStrategy;
    }

    public double calculateFare(Ride ride) {
        if (ride == null) {
            throw new InvalidDataException("Ride cannot be null when calculating fare");
        }

        double fare = fareStrategy.calculateFare(ride);
        if (!Double.isFinite(fare) || fare < 0) {
            throw new InvalidDataException("Calculated fare must be finite and non-negative");
        }

        return fare;
    }
}
