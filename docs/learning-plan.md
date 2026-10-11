# RideWise learning plan

Learn through small working increments, with explanations, shared implementation, learner exercises, verification, and review before advancing. The coding split is flexible. Follow ../AGENTS.md.

## Milestones and progress

| # | Milestone | Status | Database discussion |
| --- | --- | --- | --- |
| 1 | Java compilation and execution | Completed: learner verified stale bytecode behavior | None yet |
| 2 | Packages and project structure | Completed: learner moved Main; code reviewed and compiled with Rider | None yet |
| 3 | Domain models, constructors, encapsulation, relationships | Completed: Rider, Driver, Ride, enums, assignment invariant, and relationship model verified | Object references versus foreign keys, primary/foreign keys, nullable assignment, one-to-many cardinality, and repeated-data anomalies |
| 4 | Validation and exception handling | Completed: model invariants and focused unchecked exceptions implemented and verified | Mapped model rules to NOT NULL, primary/unique keys, CHECK, and foreign keys; cross-column lifecycle constraint deferred |
| 5 | Collections and rider/driver services | Completed: shared DataStore, duplicate-safe registration, required lookup, defensive listing, and availability filtering verified | Table separation, anomalies, dependencies, and 2NF discussed; learner normalization assignment skipped by request |
| 6 | Matching interface and nearest-driver strategy | Completed: contract, nearest algorithm, deterministic ties, no-match behavior, and mutation safety verified | Available-driver query and location-index tradeoffs discussed |
| 7 | Ride requests, assignment, dependency injection | Completed: generated IDs, injected matching, stored no-match requests, assignment, and driver availability verified | Nullable driver foreign key, transaction rollback, row locking, and exclusive assignment discussed |
| 8 | Fare strategy, calculator, immutable receipt | Completed: vehicle pricing, validated delegation, and immutable receipt implemented and verified | One-to-one receipt key, DECIMAL/BigDecimal tradeoff, Instant/time-zone choice, and completion timing documented |
| 9 | Alternative matching and pricing strategies | Completed: peak pricing and least-active matching implemented and verified through shared contracts | Completed-ride counter chosen for memory; derivation, consistency, and transactional update tradeoffs documented |
| 10 | Completion, cancellation, lifecycle rules | Completed: State-pattern transitions, completion/cancellation coordination, driver release, activity, fare receipt, and failure ordering verified | Atomic updates, rollback, stored counters, and concurrency consistency documented |
| 11 | Console menu and error recovery | Completed: seven-action menu, wiring, parsing recovery, focused domain catches, and end-to-end workflow verified | Required and optional joins related to in-memory ride display |
| 12 | End-to-end verification and design review | Planned | Complete 3NF review, schema sketch and deliberate denormalization tradeoffs |

Only guides for completed/current milestones are created now. Future guides are added when their milestone starts and updated after completion; they must not imply future features exist.

## Working agreement

Explain the problem, concept, design, principle, failure without it, and cost. Agent writes supporting code; learner implements a meaningful portion. Verify together, review the attempt and understanding, then advance. A milestone may contain multiple small steps and discussions.

Hard rule: no basic syntax, getter/setter, constructor-usage, print-output, or final-reference quizzes or exercises. Handle routine supporting code directly. Reserve learner tasks and checkpoints for substantive domain, validation, service, strategy, lifecycle, or database decisions. Revisit basics only on explicit request or to explain a concrete issue in the learner's attempt.

## Decisions and open questions

Agreed: package base com.airtribe.ridewise; location is a numeric kilometer position along a shared road reference. Distance between positions uses absolute difference. Negative positions are valid; NaN and infinity are invalid. A ride request is stored even when no driver is available: it remains REQUESTED and its driver reference is null until assignment. Ride distance represents the requested trip distance, not the driver's distance from the rider. For the MVP, each Driver directly stores one VehicleType; vehicle history and multiple vehicles per driver are outside the current scope. Nearest matching ignores unavailable drivers, returns null when no match exists, and breaks equal-distance ties using the lexicographically smaller driver ID. A singleton IdGenerator creates process-local RIDER-001, DRIVER-001, and RIDE-001 sequences; counters reset on application restart. Default fares are vehicle-specific: BIKE 20 + 8 per distance unit, AUTO 30 + 12 per unit, and CAR 50 + 18 per unit. A FareReceipt is generated only on completion, with one receipt per completed ride and an Instant timestamp.

Still to discuss when relevant: trip distance input, least-active definition, prices, receipt timing/cardinality, cancellation UI, and transition rules. Do not silently settle these through code.

## Run current code

From the project root:

```sh
javac -d out $(rg --files src -g '*.java')
java -cp out com.airtribe.ridewise.Main
```

The application opens the seven-action RideWise menu. Data remains in memory for the current process. Database work is discussion and illustrative schema/SQL; persistence implementation requires a separately agreed step.
