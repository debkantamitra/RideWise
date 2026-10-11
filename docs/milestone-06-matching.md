# Milestone 6: Matching contract and nearest-driver strategy

Status: completed. The matching interface and NearestDriverStrategy are implemented and verified; the database query/index tradeoff is documented.

## Problem

RideWise needs to choose a driver without placing matching logic in Main, DriverService, or RideService. A RideMatchingStrategy contract will allow the workflow to delegate that choice and later substitute least-active matching without rewriting ride-request logic.

The required method accepts a rider and a list of candidate drivers and returns a driver. Under the previously agreed no-driver behavior, no match leaves the ride REQUESTED. Because the required return type is Driver rather than Optional<Driver>, the natural no-match result is null. The strategy must not mutate a driver or ride.

## Agreed contract

- Reject a null rider or null driver list as invalid input.
- Ignore unavailable drivers.
- Compare distance using the absolute difference between driver and rider locations.
- Return null for an empty list or when every driver is unavailable.
- For equal distances, return the driver with the lexicographically smaller ID so the outcome does not depend on HashMap iteration order.
- Do not mutate the supplied rider, drivers, or list.

RideMatchingStrategy contains only the behavior that all matching algorithms must provide. It does not guarantee OCP, LSP, ISP, or DIP by existing alone. Those principles become relevant when RideService depends on this contract, delegates matching without knowing the algorithm, and can safely substitute another implementation that obeys the same no-match and mutation rules.

## NearestDriverStrategy implementation

At the learner's request, the agent implemented the strategy. It scans candidates once, skips unavailable drivers, and tracks the closest driver and distance seen so far. Equal distances are resolved by the agreed driver-ID comparison. The method returns null when no candidate qualifies and does not mutate its inputs. Its time cost grows linearly with the number of candidates and it uses constant extra space.

Focused JShell scenarios verified nearest selection on either side of the rider, exclusion of a closer unavailable driver, an equal-distance tie presented in reverse ID order, empty and all-unavailable no-match results, null-input rejection, and unchanged availability after matching.

## Database lookup and index tradeoff

A direct SQL expression could filter available drivers and order by the absolute difference from the rider location. On a large table, ordering every available row by a calculated ABS expression may still scan and sort many rows. An index on current_location can support looking for the closest candidate immediately below and above the rider position; availability must also be considered in the query or a suitable composite or partial index where the database supports it.

An index only on the boolean available column may have low selectivity because it has two possible values. Every index consumes storage and makes inserts and location or availability updates more expensive. RideWise remains an in-memory, small-data application, so no database or index is implemented.

## Milestone result

The strategy contract separates the matching decision from later ride workflow orchestration. A future LeastActiveDriverStrategy can be substituted only if it obeys the same availability, no-match, validation, and no-mutation behavior.
