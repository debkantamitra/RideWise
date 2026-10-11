# Milestone 1: Java compilation and execution

Status: completed.

## Problem and implementation

Establish how to run Java before building ride-booking behavior. Used the existing Main program to observe compiled output. No services or models were needed.

## Concepts and rationale

The JDK provides development tools and runtime components. javac compiles .java source into .class bytecode. The JVM executes bytecode; the JRE consists of the JVM and supporting runtime libraries. The learner correctly identified that changing source without recompilation leaves the old compiled message unchanged.

## Principle and tradeoff

KISS and YAGNI: a simple console program is enough to learn execution. The compilation step adds a build action but makes source versus runtime behavior explicit. No design pattern is needed.

## Verification and exercise completed

The learner changed the printed message, ran the existing compiled class, then recompiled and observed the updated message. Without recompilation, source and executed behavior diverge. The learner reported that the observed result matched their prediction.

## Current commands

Main has since moved into a package. Use the commands in learning-plan.md; the earlier default-package command java -cp out Main is no longer the current entry point.

## Reflection

Which tool produces bytecode, and which component executes it? Discussed and answered: javac produces bytecode; the JVM executes it.
