# Milestone 5: Collections and registration services

Status: completed. A reusable in-memory DataStore, duplicate-safe RiderService and DriverService operations, and available-driver filtering are implemented and verified. Normalization was discussed; the learner assignment was skipped by request.

## Problem

Main must not own rider and driver storage or duplicate-registration rules. RiderService and DriverService will provide focused operations over in-memory collections. This keeps console input/output separate from domain workflows and lets the same services support a different UI later.

The smallest useful increment is RiderService registration and lookup. DriverService and availability filtering follow after the rider behavior is verified.

## Storage and lookup contract

RiderService owns a DataStore backed by a map keyed by rider ID. Key-based storage gives direct lookup and makes duplicate detection explicit. Main should not reach into this collection.

findById rejects a blank lookup ID with InvalidDataException. A valid but unregistered ID throws EntityNotFoundException, an unchecked focused exception, because an operation requiring that rider cannot continue. Returning null would defer the failure and risk an unrelated NullPointerException. Optional remains useful for queries where absence is an expected answer, but that is not the chosen required-lookup contract.

## Shared DataStore refactor

At the learner's request, the MediTrack DataStore design was reviewed and adapted. RideWise uses a small Identifiable interface rather than requiring Rider, Driver, and Ride to inherit from a common base class solely for storage. DataStore<T extends Identifiable> owns the generic map and provides save-if-absent, ID lookup, and a defensive list copy.

DataStore reports duplicate storage through a boolean. RiderService translates that storage result into a rider-specific InvalidDataException and owns the required-lookup EntityNotFoundException contract. This keeps generic storage free of rider wording and prevents duplicate registration from silently replacing the original object.

The abstraction adds an interface and another class, but it will be reused by RiderService, DriverService, and later RideService. It removes repeated map plumbing while leaving domain workflows in their services.

## DriverService support

DriverService reuses DataStore<Driver> and follows the same explicit constructor initialization as RiderService. It owns driver-specific duplicate-registration and required-lookup messages. Availability filtering belongs here because it is a driver query; generic DataStore should not know what availability means.

The learner implemented findAvailableDrivers with a stream filter and Driver::isAvailable method reference. Stream.toList returns an unmodifiable result list. This protects the result structure from caller additions or removals, although the Driver objects inside remain mutable. Focused checks verified empty storage, mixed availability, and updated results after a driver's availability changes.

## Relational discussion

The earlier repeated ride record was already separated into riders, drivers, and rides. A daily driver-availability example introduced a meaningful composite key of driver_id and service_date. The availability value depends on the complete pair, while driver_name depends only on driver_id; repeating the name is a partial dependency and violates 2NF. Splitting driver facts from daily availability removes that dependency. This was an illustrative database example only, and the learner chose to skip the related assignment.

## Milestone verification

Compilation and focused JShell scenarios verified registration, duplicate preservation, required lookup failures, defensive list copying, empty availability results, unavailable-driver exclusion, and changes in availability after registration.
