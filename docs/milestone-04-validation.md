# Milestone 4: Validation and exception handling

Status: completed. The unchecked exception contract, model validation, focused ride-state exception, verification, and database constraint mapping are complete. Console-boundary handling will be exercised when the menu exists.

## Problem

The current constructors accept invalid domain data such as blank IDs, blank names, null references, NaN or infinite locations, and non-positive trip distances. Once an invalid object exists, every service must defend against it. Models should reject invalid state at creation, before assigning any fields.

Console parsing and domain validation solve different problems. Main will eventually handle text that cannot be parsed as a number. Models will reject parsed values that are still invalid for RideWise. Services will later reject workflow failures such as duplicate registrations or unknown IDs.

## Intended model invariants

- Rider: non-blank id and name; finite location.
- Driver: non-blank id and name; non-null vehicle type; finite current location.
- Ride: non-blank id; non-null rider; finite positive trip distance.
- Ride assignment: non-null driver and status must be REQUESTED before mutation.

Negative locations remain valid because they are positions on the agreed reference road. NaN and positive or negative infinity are not real positions. Validation, tests, exception choices, and database constraint mapping remain to be completed.

## Exception contract

InvalidDataException extends IllegalArgumentException, so it is unchecked. Constructors and state-changing methods do not need throws declarations, while Main can later catch this focused type at the console boundary. A checked exception would make every construction site catch or declare expected domain-input failures. The unchecked approach is less ceremony, at the cost of compiler enforcement that callers handle the error.

## Implemented validation

Rider rejects null, empty, or whitespace-only IDs and names and rejects non-finite locations. Driver applies the same text and location rules and also rejects a null vehicle type. Driver.updateLocation validates before mutation, so a rejected update preserves the previous location. The repeated location rule is kept in one private helper inside Driver.

The learner added Ride constructor checks for a non-blank ID, non-null rider, finite distance, and distance greater than zero. The finite check is separate because comparisons such as NaN <= 0 are false and positive infinity is greater than zero; positivity alone would accept both values.

Compilation and focused JShell scenarios verified rejection of blank ride IDs, null riders, zero and negative distances, NaN, and both infinities. A valid positive distance still creates a REQUESTED ride with a null driver.

## Invalid data versus invalid state

Ride.assignDriver now throws InvalidDataException when its driver argument is null and InvalidRideStateException when the ride is no longer REQUESTED. InvalidRideStateException extends IllegalStateException and remains unchecked. The distinction lets a caller report the actual cause while keeping the model free of console output.

Neither custom exception requires a throws declaration because both inherit from RuntimeException. The later console menu can catch the focused domain types where it can recover and continue. Catching is deferred until that boundary exists; model classes throw rather than print or swallow failures.

## Database constraint mapping

Application validation protects objects created through the Java application. Database constraints protect stored data written through any client, import, or future application version. Both layers are needed.

- Primary keys on riders.id, drivers.id, and rides.id provide uniqueness and disallow null identifiers.
- NOT NULL applies to required names, locations, vehicle type, availability, ride distance, status, and rides.rider_id.
- CHECK constraints can reject blank trimmed names, non-positive ride distance, and unsupported vehicle-type or status values. Finite-number checks depend on the selected database and numeric type.
- Foreign keys ensure rides.rider_id references an existing rider and a non-null rides.driver_id references an existing driver.
- rides.driver_id remains nullable for a REQUESTED ride.

A cross-column rule tying ride status to driver nullability is deferred until cancellation semantics are agreed. A CANCELLED ride may eventually be allowed either before or after assignment, so adding a premature constraint could reject a valid lifecycle state.

## Milestone result and tradeoff

All current sources compile. Focused JShell scenarios cover blank text, null references, zero and negative distance, NaN and infinity, successful assignment, and unchanged state after rejected operations. Early validation prevents invalid models from spreading through services, at the cost of additional checks and an exception contract callers must understand.
