package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.DataStore;
import com.airtribe.ridewise.util.IdGenerator;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RideService {
    private final RiderService riderService;
    private final DriverService driverService;
    private final RideMatchingStrategy matchingStrategy;
    private final FareCalculator fareCalculator;
    private final DataStore<Ride> rideStore;
    private final Map<String, FareReceipt> receiptsByRideId;
    private final IdGenerator idGenerator;
    private final Clock clock;

    public RideService(
            RiderService riderService,
            DriverService driverService,
            RideMatchingStrategy matchingStrategy,
            FareStrategy fareStrategy
    ) {
        this(riderService, driverService, matchingStrategy, fareStrategy, Clock.systemUTC());
    }

    public RideService(
            RiderService riderService,
            DriverService driverService,
            RideMatchingStrategy matchingStrategy,
            FareStrategy fareStrategy,
            Clock clock
    ) {
        if (riderService == null) {
            throw new InvalidDataException("Rider service cannot be null");
        }
        if (driverService == null) {
            throw new InvalidDataException("Driver service cannot be null");
        }
        if (matchingStrategy == null) {
            throw new InvalidDataException("Ride matching strategy cannot be null");
        }
        if (fareStrategy == null) {
            throw new InvalidDataException("Fare strategy cannot be null");
        }
        if (clock == null) {
            throw new InvalidDataException("Clock cannot be null");
        }

        this.riderService = riderService;
        this.driverService = driverService;
        this.matchingStrategy = matchingStrategy;
        this.fareCalculator = new FareCalculator(fareStrategy);
        this.rideStore = new DataStore<>();
        this.receiptsByRideId = new HashMap<>();
        this.idGenerator = IdGenerator.getInstance();
        this.clock = clock;
    }

    public Ride requestRide(String riderId, double distance) {
        Rider rider = riderService.findById(riderId);
        Ride ride = new Ride(idGenerator.nextRideId(), rider, distance);

        List<Driver> availableDrivers = driverService.findAvailableDrivers();
        Driver matchedDriver = matchingStrategy.findDriver(rider, availableDrivers);

        if (!rideStore.saveIfAbsent(ride)) {
            throw new InvalidDataException("Ride id already registered: " + ride.getId());
        }

        if (matchedDriver == null) {
            return ride;
        }

        ride.assignDriver(matchedDriver);
        matchedDriver.setAvailable(false);
        return ride;
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = findById(rideId);

        double fare = fareCalculator.calculateFare(ride);
        FareReceipt receipt = new FareReceipt(rideId, fare, Instant.now(clock));

        ride.complete();
        Driver driver = ride.getDriver();
        driver.setAvailable(true);
        driver.recordCompletedRide();
        receiptsByRideId.put(rideId, receipt);

        return receipt;
    }

    public void cancelRide(String rideId) {
        Ride ride = findById(rideId);
        Driver assignedDriver = ride.getDriver();

        ride.cancel();

        if (assignedDriver != null) {
            assignedDriver.setAvailable(true);
        }
    }

    public FareReceipt findReceiptByRideId(String rideId) {
        if (rideId == null || rideId.isBlank()) {
            throw new InvalidDataException("Ride id cannot be blank");
        }

        FareReceipt receipt = receiptsByRideId.get(rideId);
        if (receipt == null) {
            throw new EntityNotFoundException("Fare receipt not found for ride: " + rideId);
        }

        return receipt;
    }

    public Ride findById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Ride id cannot be blank");
        }

        Ride ride = rideStore.findById(id);
        if (ride == null) {
            throw new EntityNotFoundException("Ride not found: " + id);
        }

        return ride;
    }

    public List<Ride> findAll() {
        return rideStore.findAll();
    }
}
