package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.exception.InvalidRideStateException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.util.IdGenerator;

import java.util.List;
import java.util.Scanner;

public class Main {
    private final RiderService riderService;
    private final DriverService driverService;
    private final RideService rideService;
    private final IdGenerator idGenerator;
    private final Scanner scanner;

    public Main(Scanner scanner) {
        this.riderService = new RiderService();
        this.driverService = new DriverService();
        this.rideService = new RideService(
                riderService,
                driverService,
                new NearestDriverStrategy(),
                new DefaultFareStrategy()
        );
        this.idGenerator = IdGenerator.getInstance();
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new Main(scanner).run();
        }
    }

    public void run() {
        boolean running = true;

        System.out.println("Welcome to RideWise");
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            try {
                switch (choice) {
                    case 1 -> addRider();
                    case 2 -> addDriver();
                    case 3 -> viewAvailableDrivers();
                    case 4 -> requestRide();
                    case 5 -> completeRide();
                    case 6 -> viewRides();
                    case 7 -> running = false;
                    default -> System.out.println("Please choose an option from 1 to 7.");
                }
            } catch (InvalidDataException
                     | EntityNotFoundException
                     | InvalidRideStateException exception) {
                System.out.println("Could not complete action: " + exception.getMessage());
            }
        }

        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View Available Drivers");
        System.out.println("4. Request Ride");
        System.out.println("5. Complete Ride");
        System.out.println("6. View Rides");
        System.out.println("7. Exit");
    }

    private void addRider() {
        String name = readText("Rider name: ");
        double location = readDouble("Rider location: ");
        Rider rider = new Rider(idGenerator.nextRiderId(), name, location);
        riderService.register(rider);
        System.out.println("Added rider " + rider.getId() + ": " + rider.getName());
    }

    private void addDriver() {
        String name = readText("Driver name: ");
        double location = readDouble("Driver location: ");
        VehicleType vehicleType = readVehicleType();
        Driver driver = new Driver(
                idGenerator.nextDriverId(),
                name,
                vehicleType,
                location,
                true
        );
        driverService.register(driver);
        System.out.println("Added driver " + driver.getId() + ": " + driver.getName());
    }

    private void viewAvailableDrivers() {
        List<Driver> drivers = driverService.findAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers are currently available.");
            return;
        }

        for (Driver driver : drivers) {
            System.out.println(
                    driver.getId() + " | "
                            + driver.getName() + " | "
                            + driver.getVehicleType() + " | location "
                            + driver.getCurrentLocation()
            );
        }
    }

    private void requestRide() {
        String riderId = readText("Rider ID: ");
        double distance = readDouble("Trip distance: ");
        Ride ride = rideService.requestRide(riderId, distance);

        if (ride.getDriver() == null) {
            System.out.println("Created " + ride.getId() + "; waiting for an available driver.");
            return;
        }

        System.out.println(
                "Created " + ride.getId() + "; assigned driver " + ride.getDriver().getId() + "."
        );
    }

    private void completeRide() {
        String rideId = readText("Ride ID: ");
        FareReceipt receipt = rideService.completeRide(rideId);
        System.out.printf(
                "Completed %s; fare %.2f; receipt generated at %s.%n",
                receipt.getRideId(),
                receipt.getAmount(),
                receipt.getGeneratedAt()
        );
    }

    private void viewRides() {
        List<Ride> rides = rideService.findAll();
        if (rides.isEmpty()) {
            System.out.println("No rides found.");
            return;
        }

        for (Ride ride : rides) {
            String driverId = ride.getDriver() == null ? "unassigned" : ride.getDriver().getId();
            System.out.println(
                    ride.getId() + " | rider " + ride.getRider().getId()
                            + " | driver " + driverId
                            + " | distance " + ride.getDistance()
                            + " | " + ride.getStatus()
            );
        }
    }

    private VehicleType readVehicleType() {
        while (true) {
            String value = readText("Vehicle type (BIKE/AUTO/CAR): ");
            try {
                return VehicleType.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException exception) {
                System.out.println("Please enter BIKE, AUTO, or CAR.");
            }
        }
    }

    private String readText(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readInt(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            String value = readText(prompt);
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
