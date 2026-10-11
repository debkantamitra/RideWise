# Milestone 2: Packages and project structure

Status: completed.

## Problem and what was built

Growing RideWise needs clear homes for different responsibilities. The learner moved Main into src/com/airtribe/ridewise/Main.java and declared package com.airtribe.ridewise. Main now explicitly declares a public class and public static void main(String[] args).

## Concepts and design

Packages organize class names and access boundaries. Imports allow use of classes in other packages. Main's fully qualified name is com.airtribe.ridewise.Main. Conventional source folders match the package structure.

Planned responsibility groups are model, service, strategy, and exception. Only model has been added so far. Packages do not themselves enforce SRP; classes must still have focused responsibilities.

## Without this design and tradeoff

Leaving all code in one default-package file makes responsibilities and dependencies harder to understand as features grow. Named packages add paths and imports, but make organization explicit. KISS: no framework or build-system setup was required.

## Verification and learner participation

Learner reported completing the move. Agent reviewed the package declaration and explicit entry point. Main and Rider subsequently compiled together successfully. Current run commands are in learning-plan.md.

## Reflection

The filesystem path is src/com/airtribe/ridewise/Main.java; the runtime class name is com.airtribe.ridewise.Main. Explain why the run command uses the latter rather than a source filename.
