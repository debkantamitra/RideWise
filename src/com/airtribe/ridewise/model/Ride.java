package com.airtribe.ridewise.model;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.state.RequestedRideState;
import com.airtribe.ridewise.state.RideState;

public class Ride implements Identifiable {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final double distance;
    private RideState state;

    public Ride(String id, Rider rider, double distance) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Ride id cannot be blank");
        }

        if (rider == null) {
            throw new InvalidDataException("Rider cannot be null");
        }

        if (!Double.isFinite(distance)) {
            throw new InvalidDataException("Ride distance must be finite");
        }

        if (distance <= 0) {
            throw new InvalidDataException("Ride distance should be greater than zero");
        }

        this.id = id;
        this.rider = rider;
        this.distance = distance;
        this.driver = null;
        this.state = RequestedRideState.INSTANCE;
    }

    @Override
    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getDistance() {
        return distance;
    }

    public RideStatus getStatus() {
        return state.getStatus();
    }

    public void assignDriver(Driver driver) {
        if (driver == null) {
            throw new InvalidDataException("Assigned driver cannot be null");
        }

        RideState nextState = state.assignDriver();
        this.driver = driver;
        this.state = nextState;
    }

    public void complete() {
        state = state.complete();
    }

    public void cancel() {
        state = state.cancel();
    }
}
