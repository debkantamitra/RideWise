# Milestone 8: Fare strategy, calculator, and receipt

Status: completed. FareStrategy, validated FareCalculator delegation, DefaultFareStrategy, and immutable FareReceipt are implemented and verified. Peak pricing is the next strategy milestone.

## Problem and smallest design

Fare rules will vary without changing the ride workflow. FareStrategy defines the required calculation contract, while FareCalculator delegates to the selected strategy and protects the shared output invariant: every fare must be finite and non-negative.

FareCalculator rejects a null strategy during construction and a null ride during calculation. It also rejects NaN, infinity, and negative strategy results. This validation is necessary because interchangeable implementations can contain mistakes or receive unusual data; sharing the check prevents every caller from trusting an invalid monetary result.

The interface alone does not provide OCP, LSP, ISP, or DIP. Those benefits depend on each implementation obeying the same contract and RideService receiving the abstraction rather than choosing a concrete pricing class. The extra interface and delegation layer add indirection, justified by the required default and peak-hour pricing behaviors.

## Default vehicle pricing

The learner chose vehicle-specific pricing and accepted these MVP rules: BIKE costs 20 plus 8 per distance unit, AUTO costs 30 plus 12 per unit, and CAR costs 50 plus 18 per unit. DefaultFareStrategy uses the assigned driver's VehicleType and throws InvalidRideStateException when a REQUESTED ride has no driver. It validates null input itself so the strategy also behaves safely when called outside FareCalculator.

The switch expression is exhaustive over VehicleType. Adding a new enum value forces this implementation to address its price at compile time. The rates are embedded for the small MVP; external configuration would be appropriate if operators needed to change them without a deployment.

## FareReceipt contract

The learner chose one receipt per completed ride, generated at completion. FareReceipt is final and stores final rideId, amount, and generatedAt fields. It rejects blank ride IDs, non-finite or negative amounts, and null timestamps. It accepts the Instant through its constructor rather than reading the clock itself, leaving workflow timing outside the value object and enabling deterministic verification.

Instant represents an unambiguous point on the UTC timeline. A database would normally store a time-zone-aware timestamp. The required FareStrategy contract uses double, so the MVP receipt also stores double; production monetary calculations should prefer BigDecimal and a fixed-scale DECIMAL column to avoid binary floating-point rounding surprises.

The proposed fare_receipts table uses ride_id as both its primary key and a foreign key to rides.id. That enforces at most one receipt per ride. Application workflow and database transaction logic would still be needed to ensure receipts are created only for completed rides.

## Verification

Focused JShell scenarios verified all three vehicle formulas, null and unassigned-ride failures, valid receipt values, and rejection of blank ride IDs, negative amounts, NaN, infinity, and null timestamps. All Java sources compile.
