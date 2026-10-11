package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;

public class LeastActiveDriverStrategy implements RideMatchingStrategy {
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        if (rider == null) {
            throw new InvalidDataException("Rider cannot be null when matching a driver");
        }
        if (drivers == null) {
            throw new InvalidDataException("Driver list cannot be null");
        }

        Comparator<Driver> byActivityThenDistanceThenId = Comparator
                .comparingInt(Driver::getCompletedRideCount)
                .thenComparingDouble(driver -> Math.abs(
                        driver.getCurrentLocation() - rider.getLocation()
                ))
                .thenComparing(Driver::getId);

        return drivers.stream()
                .filter(Driver::isAvailable)
                .min(byActivityThenDistanceThenId)
                .orElse(null);
    }
}
