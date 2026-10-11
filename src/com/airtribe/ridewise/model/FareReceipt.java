package com.airtribe.ridewise.model;

import com.airtribe.ridewise.exception.InvalidDataException;

import java.time.Instant;

public final class FareReceipt {
    private final String rideId;
    private final double amount;
    private final Instant generatedAt;

    public FareReceipt(String rideId, double amount, Instant generatedAt) {
        if (rideId == null || rideId.isBlank()) {
            throw new InvalidDataException("Receipt ride id cannot be blank");
        }
        if (!Double.isFinite(amount) || amount < 0) {
            throw new InvalidDataException("Receipt amount must be finite and non-negative");
        }
        if (generatedAt == null) {
            throw new InvalidDataException("Receipt generation time cannot be null");
        }

        this.rideId = rideId;
        this.amount = amount;
        this.generatedAt = generatedAt;
    }

    public String getRideId() {
        return rideId;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}
