package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.util.DataStore;

import java.util.List;

public class DriverService {
    private final DataStore<Driver> driverStore;

    public DriverService() {
        this.driverStore = new DataStore<>();
    }

    public void register(Driver driver) {
        if (!driverStore.saveIfAbsent(driver)) {
            throw new InvalidDataException("Driver id already registered: " + driver.getId());
        }
    }

    public Driver findById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Driver id cannot be blank");
        }

        Driver driver = driverStore.findById(id);
        if (driver == null) {
            throw new EntityNotFoundException("Driver not found: " + id);
        }

        return driver;
    }

    public List<Driver> findAll() {
        return driverStore.findAll();
    }

    public List<Driver> findAvailableDrivers() {
        List<Driver> drivers = driverStore.findAll();

        return drivers.stream().filter(Driver::isAvailable).toList();
    }
}
