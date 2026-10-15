package com.airtribe.ridewise.model;

import com.airtribe.ridewise.exception.InvalidDataException;

public class Rider implements Identifiable {
    private final String id;
    private final String name;
    private final double location;

    public Rider(String id, String name, double location) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Rider id cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("Rider name cannot be blank");
        }
        if (!Double.isFinite(location)) {
            throw new InvalidDataException("Rider location must be finite");
        }

        this.id = id;
        this.name = name;
        this.location = location;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLocation() {
        return location;
    }
}
