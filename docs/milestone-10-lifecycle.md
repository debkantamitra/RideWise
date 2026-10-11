# Milestone 10: Completion, cancellation, and lifecycle rules

Status: completed. State-pattern transition guards and RideService completion and cancellation coordination are implemented and verified.

## Agreed transitions

- ASSIGNED to COMPLETED is allowed.
- REQUESTED to CANCELLED is allowed and has no driver to release.
- ASSIGNED to CANCELLED is allowed and releases its driver later in RideService.
- Completing REQUESTED, COMPLETED, or CANCELLED is rejected.
- Cancelling COMPLETED or CANCELLED is rejected.

Ride.complete and Ride.cancel enforce status changes inside the model so no caller can bypass the same transition rules. Both validate before mutation and throw InvalidRideStateException on rejection. They change only ride status; coordination with driver availability, completed count, fare calculation, and receipt storage belongs in RideService.

An assigned then cancelled ride retains its driver reference as historical information, while a request cancelled before matching keeps a null driver. This is why a simple database rule saying every CANCELLED ride must have either a null or non-null driver would be incorrect.

## State pattern refactor

At the learner's request, Ride now holds a RideState object rather than implementing transition conditionals itself. RequestedRideState allows assignment and cancellation; AssignedRideState allows completion and cancellation; CompletedRideState and CancelledRideState reject every transition through the interface defaults. Each successful operation returns the next state object, and Ride applies that state only after validation succeeds.

The state objects are stateless singletons. RideStatus remains the external enum used for display, persistence, and assertions, while getStatus delegates to the current state. This preserves the existing Ride API and behavior.

The pattern gives each state one place to define allowed behavior and makes a new state easier to add without expanding conditionals in Ride. Its cost is five additional types and more indirection. The lifecycle is small enough that conditionals were also reasonable; the pattern is used here because the learner explicitly requested it and because completion and cancellation now make state-specific behavior substantial.

## RideService lifecycle coordination

RideService now receives both RideMatchingStrategy and FareStrategy through its constructor. It wraps fare behavior in FareCalculator and owns one receipt per ride in an in-memory map. A Clock overload supports deterministic receipt timestamps while normal application wiring uses the UTC system clock.

completeRide resolves the ride, calculates and validates fare, and constructs the receipt before mutation. It then asks the Ride state to complete, releases the driver, increments completed activity, and stores the receipt. Pricing or receipt validation failure therefore leaves ride and driver state unchanged. The state transition rejects repeated completion before a second receipt can be stored.

cancelRide asks the Ride state to transition before releasing any assigned driver. Cancelling a REQUESTED ride has no driver effect; cancelling an ASSIGNED ride makes its driver available without incrementing completed activity or creating a receipt.

## Verification

Focused scenarios verified successful completion, vehicle-specific receipt amount, deterministic receipt time, receipt lookup, driver release, completed-count increment, and rejection of repeated completion without a second increment. Both REQUESTED and ASSIGNED cancellation paths were verified, including no receipt and no completed-count increment. A deliberately invalid fare result proved that pricing failure leaves the ride ASSIGNED, its driver unavailable, activity unchanged, and no receipt stored.

## Transaction boundary

The in-memory ordering prevents expected validation failures from causing partial mutation. In a database, ride status, driver availability, completed count, and receipt insertion still belong in one transaction. Any failed write must roll back the entire group, and concurrent completion or cancellation attempts require locking or conditional updates.
