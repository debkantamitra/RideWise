package com.airtribe.ridewise.util;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private static final IdGenerator INSTANCE = new IdGenerator();

    private final AtomicInteger riderCounter;
    private final AtomicInteger driverCounter;
    private final AtomicInteger rideCounter;

    private IdGenerator() {
        this.riderCounter = new AtomicInteger(1);
        this.driverCounter = new AtomicInteger(1);
        this.rideCounter = new AtomicInteger(1);
    }

    public static IdGenerator getInstance() {
        return INSTANCE;
    }

    public String nextRiderId() {
        return nextId("RIDER", riderCounter);
    }

    public String nextDriverId() {
        return nextId("DRIVER", driverCounter);
    }

    public String nextRideId() {
        return nextId("RIDE", rideCounter);
    }

    private String nextId(String prefix, AtomicInteger counter) {
        return prefix + "-" + String.format("%03d", counter.getAndIncrement());
    }
}
