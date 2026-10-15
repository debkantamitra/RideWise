package com.airtribe.ridewise.state;

import com.airtribe.ridewise.model.RideStatus;

public final class CompletedRideState implements RideState {
    public static final CompletedRideState INSTANCE = new CompletedRideState();

    private CompletedRideState() {
    }

    @Override
    public RideStatus getStatus() {
        return RideStatus.COMPLETED;
    }
}
