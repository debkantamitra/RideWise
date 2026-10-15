package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.List;

public class NearestDriverStrategy implements RideMatchingStrategy {
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        if (rider == null) {
            throw new InvalidDataException("Rider cannot be null when matching a driver");
        }
        if (drivers == null) {
            throw new InvalidDataException("Driver list cannot be null");
        }

        Driver nearestDriver = null;
        double nearestDistance = Double.POSITIVE_INFINITY;

        for (Driver driver : drivers) {
            if (!driver.isAvailable()) {
                continue;
            }

            double distance = Math.abs(driver.getCurrentLocation() - rider.getLocation());
            boolean isCloser = distance < nearestDistance;
            boolean winsTie = Double.compare(distance, nearestDistance) == 0
                    && nearestDriver != null
                    && driver.getId().compareTo(nearestDriver.getId()) < 0;

            if (isCloser || winsTie) {
                nearestDriver = driver;
                nearestDistance = distance;
            }
        }

        return nearestDriver;
    }
}
