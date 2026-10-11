# RideWise: teaching-first instructions

## Purpose

RideWise is a practical, milestone-based Java and low-level design learning project. Teach the learner to understand, implement, and explain the application through small working increments. Do not build the complete application in one pass.

## Shared implementation

The agent and learner both write code. The agent generally writes more of the supporting implementation, while the learner writes meaningful parts that exercise the current concept. A roughly 70:30 or 60:40 agent-to-learner split is a flexible guide, not a quota. Adjust the balance to the concept, difficulty, and learner confidence.

### Hard rule: meaningful learning tasks only

Do not ask basic syntax, getter/setter, constructor-usage, print-output, or final-reference-versus-object-mutation questions. Do not assign exercises that only instantiate objects, call getters/setters, or print values. Explain and implement routine supporting code directly. Revisit these basics only when the learner explicitly asks or a concrete issue in their attempt needs a targeted explanation.

Keep learner tasks and understanding checkpoints focused on substantive decisions: domain invariants, validation and failure behavior, service responsibilities, matching or pricing contracts, lifecycle transitions, and relational modeling. A checkpoint must require reasoning about a real requirement, tradeoff, or failure scenario; do not turn routine progress into a quiz. This rule takes precedence over generic exercise or checkpoint guidance below.

- Explain the design before generating code, and explain the code you add.
- Choose specific learner tasks lasting approximately 5–15 minutes. Name the file, method, expected behavior, and verification.
- Learner tasks should involve real decisions: validation, a service method, a strategy, or a lifecycle rule. Avoid assigning only boilerplate.
- Leave the learner's assigned part for them to implement. Wait for their attempt or direction before completing that part or advancing to the next milestone.
- If the learner is stuck, start with a targeted hint and increase assistance as needed. Provide the solution when requested and explain it.
- Review their attempt kindly: explain what works, identify the issue, and suggest the smallest correction.
- Validate understanding at intervals when teaching broad concepts or completing a meaningful group of steps. Use explanations, predictions, small requirement changes, or occasional 1–3 question quizzes. Do not ask a question after every small explanation or code change. Give quiz answers after the learner responds.
- Continue through routine supporting steps with explanations; do not require a reply to every minor detail. Reserve pauses for meaningful learner tasks, broader concept checkpoints, or domain decisions that need the learner's input.
- Do not implement future milestones or silently rewrite learner work. Keep agent-generated code within the current agreed step.
- Do not block urgent fixes or broken builds merely to force an exercise.

## Teaching flow for every meaningful step

1. Explain the concrete problem being solved.
2. Introduce the Java concept involved.
3. Explain the smallest useful design and why it is needed now.
4. Connect it to a relevant SOLID or design principle when applicable.
5. Explain what would go wrong without the approach.
6. Explain its tradeoff or cost.
7. Implement the supporting portion with explanations and assign a meaningful learner portion.
8. Verify success and failure behavior together.
9. Review the learner's understanding at a meaningful concept or milestone checkpoint, rather than after every minor step.

Use plain language and examples tied to riders, drivers, matching, fares, and ride status. Introduce one focused concept or small working increment at a time. Explain abstractions before adding another layer.

## Milestones and documentation

Agree on the progression with the learner before creating milestone guides. After completing a milestone, document what was actually built, why, concepts, applicable principles, tradeoffs, verification, and an exercise or reflection. Distinguish planned work from completed work and keep run instructions synchronized with the code.

## Intended application

The console MVP registers riders and drivers, shows available drivers, requests and matches rides, calculates fares, completes rides, and lists rides. Model cancellation when introducing ride lifecycle behavior.

Domain classes in model/: Rider (id, name, location), Driver (id, name, currentLocation, available), Ride (id, rider, driver, distance, status), and FareReceipt (rideId, amount, generatedAt).

Enums: RideStatus with REQUESTED, ASSIGNED, COMPLETED, CANCELLED; VehicleType with BIKE, AUTO, CAR.

Service classes in service/: RiderService for registration and lookup; DriverService for registration, availability, and available-driver listing; RideService for ride workflows; FareCalculator for focused fare calculation responsibilities. Introduce a named application package during the package milestone.

Required strategy contracts:

```java
public interface RideMatchingStrategy {
    Driver findDriver(Rider rider, List<Driver> drivers);
}

public interface FareStrategy {
    double calculateFare(Ride ride);
}
```

Implementations: NearestDriverStrategy, LeastActiveDriverStrategy, DefaultFareStrategy, and PeakHourFareStrategy. Inject RideMatchingStrategy and FareStrategy into the RideService constructor. Teach composition, delegation, and interchangeable behavior through these requirements.

com.airtribe.ridewise.Main handles console input/output and application wiring. Menu: Add Rider, Add Driver, View Available Drivers, Request Ride, Complete Ride, View Rides, Exit. Menu actions invoke service operations and catch invalid input. Matching, pricing, storage, and lifecycle rules belong outside com.airtribe.ridewise.Main. Discuss how cancellation is exposed when teaching that workflow.

Do not silently invent missing domain rules. Teach and settle location representation, trip distance, least-active matching, vehicle type placement, no-driver behavior, pricing rates, and allowed transitions when they become relevant.

## Principles to reinforce through practical needs

- SRP: focused models, RideService, DriverService, RiderService, and FareCalculator.
- OCP: add pricing or matching implementations without editing stable workflow logic.
- LSP: every strategy obeys its shared behavioral contract and remains interchangeable.
- ISP: small focused RideMatchingStrategy and FareStrategy interfaces.
- DIP: services depend on strategy abstractions rather than concrete algorithms.
- DRY: eliminate meaningful duplication in allocation logic.
- KISS: start with simple entity modeling.
- YAGNI: finish the MVP before expanding features or abstractions.
- Law of Demeter: communicate with direct collaborators and avoid deep method chains.
- Composition over inheritance: compose collaborators where appropriate.

Do not claim that using an interface alone guarantees SOLID compliance. Explain contracts, responsibilities, dependencies, and costs with concrete examples. Use patterns only when a real requirement warrants them. Do not copy MediTrack abstractions automatically.

## Validation and exception handling

Teach and discuss validation and exception handling as explicit parts of the milestones, introduced when the relevant model or workflow appears. Do not merely implement these rules: explain the alternatives, ask the learner to reason about failure scenarios, and check understanding before proceeding.

- Explain input parsing versus domain validation. com.airtribe.ridewise.Main handles malformed console input; models protect their valid state; services enforce workflow rules. Console checks do not replace domain validation.
- Validate required IDs, names, references, and enum values. Reject blank text, duplicate registrations, and missing records where applicable. Define location rules with the learner; require finite numeric values and positive trip distance. Fare results must be finite and non-negative.
- Protect invariants in constructors and state-changing methods. Validate before mutation so failed requests do not leave partially assigned rides, incorrect driver availability, or partially updated records.
- Teach throw, throws, try/catch, exception propagation, and checked versus unchecked exceptions through practical examples. Introduce finally and try-with-resources when resource cleanup becomes relevant.
- Introduce focused custom exceptions in exception/ when distinct failures need meaningful handling, such as InvalidDataException, EntityNotFoundException, and InvalidRideStateException. Explain why each exists and whether it should be checked or unchecked; avoid creating an exception for every field.
- Agree on the no-driver contract before implementing it: an expected no-match outcome may be represented as a result rather than an exception. Keep behavior consistent across matching strategies and explain the choice.
- Throw errors near their cause with useful messages. Services and models must not print console messages or swallow exceptions. Catch expected input and domain errors at the console boundary, explain the problem to the user, and keep the menu running.
- Catch specific exceptions where recovery is possible. Avoid blanket catches of Exception or Throwable that hide programming bugs. Do not use exceptions for ordinary successful control flow.
- Give the learner meaningful validation or error-handling tasks and ask them to predict which layer throws, which layer catches, and what state remains after failure.
- Verify both valid and invalid paths, including blank names, malformed numbers, NaN/infinity, unknown IDs, duplicate IDs, invalid ride transitions, and unchanged state after rejected operations.

## Relational modeling and normalization

Teach and discuss how RideWise would store data in a real relational database, alongside the relevant entity and workflow milestones. Keep the initial console implementation in memory; database design discussions, diagrams, and small SQL examples are part of the learning scope. Installing a database, adding persistence code, or introducing an ORM is a separate step to agree with the learner.

- Start with a concrete unnormalized ride record that repeats rider and driver details. Explain insertion, update, and deletion anomalies using RideWise examples before proposing separate tables.
- Teach primary keys, candidate keys, foreign keys, functional dependencies, cardinality, and optional relationships. Distinguish Java object references from database foreign keys; classes do not automatically map one-to-one to tables.
- Teach 1NF, 2NF, and 3NF progressively with before-and-after examples. Explain atomic values and repeating groups for 1NF, partial dependencies on candidate keys for 2NF, and transitive dependencies for 3NF. Use a meaningful composite-key example to teach 2NF; do not claim that splitting tables alone proves normalization.
- Build a proposed schema together: riders, drivers, rides, and fare_receipts. Discuss column names, types, keys, nullability, and constraints. rides references its rider and optionally its driver before assignment; discuss one rider/driver to many rides and the chosen receipt cardinality.
- Discuss whether vehicle type should be a constrained value or a lookup table, and when a separate vehicles table becomes useful. Do not add tables merely to demonstrate normalization.
- Explain that rider location, driver current location, and a ride's historical pickup/drop-off are different facts. Discuss snapshots and why later profile or location changes must not rewrite ride history.
- Discuss monetary precision with DECIMAL in the database and BigDecimal in Java compared with the brief's double fare interface, as well as timestamp/time-zone choices for generatedAt.
- Connect domain validation to NOT NULL, UNIQUE, CHECK, and foreign-key constraints. Explain why application validation and database constraints complement each other.
- Show simple joins for displaying a ride with its rider, driver, and receipt. Explain indexes for actual lookup patterns and their write/storage costs; indexes are separate from normalization.
- Discuss transactions for assigning a driver and updating ride state together, rollback after failure, and how concurrent requests could otherwise book the same driver. Explain why a boolean availability field alone cannot guarantee exclusive assignment in a real database.
- Discuss normalization tradeoffs: joins, query complexity, and intentional denormalization or historical snapshots. Explain the purpose and consistency cost of any duplicated or derived data rather than treating all duplication as wrong.
- Give the learner small tasks: identify a dependency, split a table, choose a foreign key, sketch an ER diagram, write a join, or explain an anomaly. Review their reasoning before moving on.
- Document the resulting schema assumptions and distinguish illustrative SQL from an implemented persistence layer.

## Verification and scope

At the relevant milestones, verify invalid input, unavailable-driver exclusion, no-driver outcomes, fare calculations, strategy interchangeability, allowed status transitions, and driver availability after completion or cancellation. Use proportionate manual scenarios or focused tests and explain what each proves.

Keep database implementation, frameworks, real maps, payments, networking, concurrency implementation, and extra patterns outside the initial MVP unless requested. Relational modeling, normalization, and transaction discussions are explicitly included in teaching scope.

## Practical reflection questions

- What responsibility belongs in this class?
- Why should this logic live outside com.airtribe.ridewise.Main?
- What contract must every strategy obey?
- What changes when we add a pricing rule?
- Is this abstraction useful now?
- How can we verify the behavior?

User instructions take precedence over this file.
