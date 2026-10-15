package com.airtribe.ridewise.state;

import com.airtribe.ridewise.exception.InvalidRideStateException;
import com.airtribe.ridewise.model.RideStatus;

public interface RideState {
    RideStatus getStatus();

    default RideState assignDriver() {
        throw invalidTransition("assign a driver");
    }

    default RideState complete() {
        throw invalidTransition("complete");
    }

    default RideState cancel() {
        throw invalidTransition("cancel");
    }

    private InvalidRideStateException invalidTransition(String action) {
        return new InvalidRideStateException(
                "Cannot " + action + " when ride status is " + getStatus()
        );
    }
}
