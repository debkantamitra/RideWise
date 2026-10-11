package com.airtribe.ridewise.state;

import com.airtribe.ridewise.model.RideStatus;

public final class AssignedRideState implements RideState {
    public static final AssignedRideState INSTANCE = new AssignedRideState();

    private AssignedRideState() {
    }

    @Override
    public RideStatus getStatus() {
        return RideStatus.ASSIGNED;
    }

    @Override
    public RideState complete() {
        return CompletedRideState.INSTANCE;
    }

    @Override
    public RideState cancel() {
        return CancelledRideState.INSTANCE;
    }
}
