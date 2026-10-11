# Milestone 3: Domain models and encapsulation

Status: completed. Rider, Driver, Ride, RideStatus, and VehicleType are implemented; the assignment invariant and initial relational model are verified.

## Problem and smallest current implementation

A rider's id, name, and location belong together. Rider groups them into one object instead of passing unrelated values separately. Agent created src/com/airtribe/ridewise/model/Rider.java with three private final fields, a constructor, and getters. Main creates a Rider and prints its name and location.

## Concepts and design choice

A class defines a type; new creates an instance. The constructor initializes state. private prevents direct field access from other classes. final prevents field reassignment after initialization; it does not validate values or guarantee deep immutability in general. These particular fields are String and double, so this Rider exposes no mutable nested state.

Constructor parameters shadow identically named fields. this.id = id assigns the parameter to the current object's field; id = id assigns the parameter to itself. The learner correctly explained the naming collision.

SRP: Rider represents rider information. Registration, storage, matching, and pricing will belong elsewhere. KISS/YAGNI: no parent entity or interface is needed now. The current immutable Rider is simple; moving a rider later will require a deliberate update design.

## Without the approach and its cost

Disconnected values are easier to mix up, and public mutable fields allow uncontrolled changes. Encapsulation adds a constructor and getters. It does not yet stop new Rider(null, "", Double.NaN); validation is the next milestone's problem.

## Agreed location model

location is a double representing kilometers along a shared reference road. For a rider at 5 and drivers at 2 and 9, absolute differences are 3 and 4. This avoids geographic calculations while learning matching. Real applications distinguish current locations from historical ride pickup/drop-off snapshots.

## Verification so far

Rider and Main compiled together successfully. The learner added the import and Rider construction. Agent supplied the routine print statements and compiled/ran Main, verifying Asha and 5.0. Driver was subsequently compiled successfully. Its state-changing behavior is not yet exercised.

## Rider usage exercise (completed together)

In src/com/airtribe/ridewise/Main.java, import com.airtribe.ridewise.model.Rider. Inside main, create new Rider("R1", "Asha", 5.0), then print its name and location through getters. Run in IntelliJ or use learning-plan.md commands. Expected values: Asha and 5.0. This demonstration was completed together; meaningful design tasks take priority over boilerplate exercises.

## Remaining discussion and work in this milestone

Introduce Driver, Ride, and enums in small shared steps, with learner tasks. Discuss references, optional assignment, and relationships before implementing them. Validation and workflows remain subsequent milestones.

Begin relational modeling by comparing a repeated ride record containing rider/driver details with references to separate rider/driver records. Teach primary keys, foreign keys and one-to-many relationships. A Java reference is not a database foreign key, and a class need not map directly to one table. Schema choices and normalization exercises are still pending; no database is implemented.

## Driver: fixed identity and changing state

The learner identified id and name as final. Agent added Driver with private final id/name, mutable currentLocation/available, constructor, getters, updateLocation, and setAvailable. Availability is provided explicitly in the constructor rather than assuming a registration policy.

Identity stays fixed. Name is fixed for this MVP, not a universal rule: real applications may allow profile updates. A final Driver reference would prevent reassignment of that reference but would not prevent updateLocation or setAvailable from changing the object.

SRP: Driver owns driver state; allocation belongs to services. Private mutable fields expose controlled operations, but workflow guards and validation are not implemented yet. Setters add flexibility and the cost of managing valid transitions.

The basic Driver mutation exercise was withdrawn at the learner's request; it was not completed. Routine constructor/getter/setter demonstrations and final-reference questions must not be assigned under the hard rule in AGENTS.md.

## Ride relationships and initial state

The learner chose to retain an unmatched request. A new Ride therefore requires an id, rider, and requested trip distance, starts in REQUESTED, and has a null driver until assignment. getDriver() deliberately exposes that optional relationship as null. The console and later workflow code must not dereference it without accounting for REQUESTED rides.

Ride stores Rider and Driver object references. These connect objects in memory; they are not database foreign keys. In a relational schema, rides.rider_id would be required while rides.driver_id would initially be nullable. One rider can have many rides, and one driver can have many assigned rides over time.

The constructor does not accept driver or status, so callers cannot create combinations such as REQUESTED with a driver or ASSIGNED without one. This is a small use of encapsulation to make the valid initial state easy to create. It costs flexibility: restored historical data may eventually need a separate persistence mapping approach.

Validation of ids, references, finite positive distance, and later lifecycle transitions remains for the relevant validation and lifecycle steps.

## Verified assignment invariant

The learner implemented Ride.assignDriver. It rejects null with IllegalArgumentException and rejects assignment outside REQUESTED with IllegalStateException. A successful assignment stores the driver and changes the status to ASSIGNED. Compilation and focused JShell checks verified initial state, null rejection without mutation, successful assignment, and rejection of a second assignment without replacing the original driver.

## Vehicle type placement

The learner chose the MVP design in which Driver directly stores one required VehicleType: BIKE, AUTO, or CAR. This avoids a Vehicle abstraction before the application needs vehicle history or multiple vehicles per driver. The tradeoff is that the model cannot represent those requirements without later refactoring. Validation for a null vehicle type belongs to the next milestone.

## Relational model discussed

The learner proposed separate riders, drivers, and rides tables and correctly identified the many-to-one relationships from rides to riders and drivers. The back-reference collections sometimes exposed by an ORM are not physical riders or drivers columns; related rides are found through rides.rider_id and rides.driver_id.

riders.id, drivers.id, and rides.id are primary keys. rides.rider_id is a required foreign key because every ride belongs to a rider. rides.driver_id is a nullable foreign key because a REQUESTED ride has no assigned driver. Assignment changes the in-memory null reference, and a later persistence implementation would change the SQL NULL to a driver ID.

The repeated ride-record example demonstrated update, insertion, and deletion anomalies caused by storing rider and driver details in every ride row. Separating the entities avoids those anomalies. This is a proposed schema discussion only; the application still stores objects in memory and has no ORM or database.

## Milestone verification

All Java sources compile and Main still runs. Focused JShell scenarios verified the Ride initial state, successful assignment, null rejection without mutation, and second-assignment rejection without replacing the original driver. The learner explained the table relationships and the nullable driver foreign key was clarified using the REQUESTED state.
