package com.airtribe.ridewise.state;

import com.airtribe.ridewise.model.RideStatus;

public final class RequestedRideState implements RideState {
    public static final RequestedRideState INSTANCE = new RequestedRideState();

    private RequestedRideState() {
    }

    @Override
    public RideStatus getStatus() {
        return RideStatus.REQUESTED;
    }

    @Override
    public RideState assignDriver() {
        return AssignedRideState.INSTANCE;
    }

    @Override
    public RideState cancel() {
        return CancelledRideState.INSTANCE;
    }
}
