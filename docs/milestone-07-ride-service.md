# Milestone 7: Ride requests, assignment, and dependency injection

Status: completed. RideService construction, collaborator validation, storage, ID generation, required lookup, request workflow, focused verification, and transaction discussion are complete.

## Problem

Requesting a ride coordinates several existing responsibilities: require a registered rider, create and store a REQUESTED ride, obtain available drivers, delegate selection to RideMatchingStrategy, assign a match, and update driver availability. Main should invoke this workflow rather than perform it itself.

RideService will depend on RiderService, DriverService, and RideMatchingStrategy. Receiving the matching strategy through its constructor keeps the workflow independent of NearestDriverStrategy and makes another contract-compatible algorithm substitutable.

The workflow must validate before mutation and avoid partially assigned state. In memory, driver assignment, ride status, driver availability, and ride storage cannot yet be protected by a database transaction. The exact operation order and rollback behavior must therefore be chosen carefully before code is added.

## ID generation decision

At the learner's request, RideWise uses an eagerly initialized singleton IdGenerator modeled after MediTrack. It owns independent atomic counters and produces RIDER-001, DRIVER-001, and RIDE-001 sequences. A private constructor prevents ordinary construction and getInstance returns the single process-wide object.

The shared instance prevents separate callers from accidentally starting competing in-process counters. AtomicInteger also makes each increment atomic, although the console MVP is single-threaded. The costs are global mutable state, tests that share counter progress, and counter reset after application restart. This is suitable only for the current in-memory MVP; persistent or distributed ID generation requires a database sequence, identity column, UUID, or another durable mechanism.

## RideService support

RideService receives RiderService, DriverService, and RideMatchingStrategy through its constructor and rejects null collaborators. The matching dependency is an interface, so the workflow will delegate without constructing or naming NearestDriverStrategy. Ride storage is private and required lookup distinguishes blank input from a well-formed missing ID.

The singleton IdGenerator is obtained once during RideService construction. The service will use it to create ride IDs, while callers supply only rider ID and trip distance to requestRide.

## Request workflow

At the learner's request, the agent implemented requestRide. It requires a registered rider, creates a validated REQUESTED ride with a generated ID, obtains available drivers, delegates selection to the injected strategy, and saves the ride before mutating assignment state. A null strategy result returns the stored REQUESTED ride. A match assigns the driver and then marks that driver unavailable.

The strategy runs before storage but is contractually free of mutation. The storage result is checked before ride or driver mutation, preventing a duplicate-storage failure from booking a driver without a stored ride. The final two in-memory mutations cannot currently fail under their validated preconditions; a real database implementation would still require a transaction for atomicity and concurrency control.

## Verification

Focused JShell scenarios verified that a no-driver request is stored as REQUESTED with a null driver, nearest matching produces ASSIGNED, the selected driver becomes unavailable, an unselected driver remains available, and unknown-rider or invalid-distance failures do not add rides. Required lookup returns the same stored ride object.

## Database transaction and concurrency

A database implementation must insert or update the ride and mark the selected driver unavailable in one transaction. If either write fails, rollback must undo both. Otherwise the database could contain an assigned ride whose driver remains available, or an unavailable driver with no corresponding ride.

The available boolean alone cannot guarantee exclusive assignment. Two concurrent requests can both read true before either writes false. The workflow would need a locking or conditional-update technique, such as selecting the driver row for update or updating it with a WHERE available = true condition and checking that exactly one row changed. This concurrency mechanism is discussed only; it is outside the single-threaded in-memory MVP.
