package com.airtribe.ridewise.state;

import com.airtribe.ridewise.model.RideStatus;

public final class CancelledRideState implements RideState {
    public static final CancelledRideState INSTANCE = new CancelledRideState();

    private CancelledRideState() {
    }

    @Override
    public RideStatus getStatus() {
        return RideStatus.CANCELLED;
    }
}
