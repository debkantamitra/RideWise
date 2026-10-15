package com.airtribe.ridewise.model;

import com.airtribe.ridewise.exception.InvalidDataException;

public class Driver implements Identifiable {
    private final String id;
    private final String name;
    private final VehicleType vehicleType;
    private double currentLocation;
    private boolean available;
    private int completedRideCount;

    public Driver(String id, String name, VehicleType vehicleType, double currentLocation, boolean available) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Driver id cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Driver name cannot be blank");
        }
        if (vehicleType == null) {
            throw new InvalidDataException("Driver vehicle type cannot be null");
        }
        validateLocation(currentLocation);

        this.id = id;
        this.name = name;
        this.vehicleType = vehicleType;
        this.currentLocation = currentLocation;
        this.available = available;
        this.completedRideCount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public double getCurrentLocation() {
        return currentLocation;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getCompletedRideCount() {
        return completedRideCount;
    }

    public void updateLocation(double currentLocation) {
        validateLocation(currentLocation);
        this.currentLocation = currentLocation;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void recordCompletedRide() {
        completedRideCount++;
    }

    private static void validateLocation(double location) {
        if (!Double.isFinite(location)) {
            throw new InvalidDataException("Driver location must be finite");
        }
    }
}
