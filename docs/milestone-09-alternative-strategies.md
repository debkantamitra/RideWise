# Milestone 9: Alternative strategies

Status: completed. PeakHourFareStrategy and LeastActiveDriverStrategy are implemented, verified, and reviewed for substitution and data tradeoffs.

## Peak pricing problem and design

Peak pricing should reuse vehicle-specific default pricing rather than duplicate its rates. PeakHourFareStrategy composes another FareStrategy, delegates the base calculation, and multiplies the result by 1.5 during 08:00–10:00 and 17:00–20:00. End boundaries are exclusive. The application default zone is Asia/Kolkata.

The strategy receives a Clock and ZoneId through its test-oriented constructor. Production wiring can use the convenience constructor with the system clock and default zone, while verification can freeze time at exact boundaries. Reading the system clock directly throughout the method would make boundary behavior difficult and unreliable to test.

This is composition over inheritance: peak pricing has a base pricing collaborator rather than extending DefaultFareStrategy. It can wrap any FareStrategy that obeys the contract. The cost is another object and delegation layer. The decorator must also preserve the shared finite, non-negative fare contract, which FareCalculator checks at the boundary.

Focused checks froze the clock immediately before, at the start of, immediately before the end of, and at the end of both windows. They verified the 1.5 multiplier only inside the half-open intervals and rejection of null collaborators.

## Least-active matching

The learner accepted completed ride count as the definition of driver activity. Cancelled rides will not count. Driver starts with zero completed rides and exposes recordCompletedRide for the later completion workflow. LeastActiveDriverStrategy filters unavailable drivers and chooses by completed count, then distance from the rider, then driver ID.

The comparator makes all priorities explicit and performs no mutation. The strategy obeys the same null-input, unavailable-driver, no-match, and no-mutation contract as nearest matching, so RideService can substitute it through RideMatchingStrategy.

Focused scenarios verified that completed count outranks distance, distance resolves equal counts, driver ID resolves equal counts and distances, unavailable drivers are excluded, empty candidates return null, invalid inputs fail, and matching does not change activity counts.

## Stored versus derived activity

The in-memory MVP stores completedRideCount on Driver because matching can read it directly through the required strategy signature. This duplicates information that could otherwise be derived by counting COMPLETED rides. The stored counter makes reads cheap but creates a consistency obligation: completing a ride and incrementing its driver's count must happen together.

In a database, deriving COUNT(*) from indexed completed rides avoids duplicated state but costs query work. Storing a counter speeds matching but requires the ride update and counter increment in the same transaction, with rollback on failure. The current choice favors simple in-memory matching; the later completion workflow must update both pieces of state.
