# Milestone 11: Console menu and error recovery

Status: completed. The required seven-action menu, application wiring, parsing loops, domain-error boundary, display actions, and end-to-end verification are complete.

## Responsibilities

Main constructs services and selects NearestDriverStrategy and DefaultFareStrategy. It reads text, retries malformed integer and double input, parses VehicleType, prints results, and catches the focused recoverable domain exceptions so the menu continues.

Models still protect valid object state. Services still own registration, storage, matching, pricing, and lifecycle workflows. Main does not reproduce these rules. This separation allows another interface to call the same services later.

The initial menu exposes Add Rider, Add Driver, View Available Drivers, Request Ride, Complete Ride, View Rides, and Exit. Cancellation remains implemented in RideService but is not exposed in the required initial menu. Adding a cancellation action later needs only console wiring, not lifecycle redesign.

Main catches InvalidDataException, EntityNotFoundException, and InvalidRideStateException because the user can correct those inputs and continue. It does not catch Exception or Throwable, which would hide programming errors. Scanner is closed with try-with-resources at the application boundary.

## Relational display equivalent

The in-memory View Rides action follows Ride references to show rider and optional driver IDs. A database implementation would use a required join from rides to riders and a left join from rides to drivers because REQUESTED rides may have a null driver_id. Receipt display would use another optional join until completion.

## Verification

A scripted console session registered a rider and AUTO driver, listed availability, requested a five-unit ride, displayed it as ASSIGNED, completed it with fare 90.00, confirmed the driver became available again, and displayed the ride as COMPLETED.

A failure session verified retry loops for malformed menu and decimal input and invalid vehicle type. Unknown rider and ride IDs produced focused messages and returned to the menu. Both sessions exited normally, and all Java sources compile.
