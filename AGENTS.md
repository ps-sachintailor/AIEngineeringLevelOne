# Agent Guide

## Scope

This repository contains a Java 21 Maven application built with Spring Boot and Spring AI. Keep changes focused on the Jira acceptance criteria and preserve environment-based secret handling.

## Required checks

Run `mvn clean verify` before committing. For runtime changes, also start the packaged application and confirm `GET /actuator/health` reports `UP`.

## Conventions

- Use package root `com.productsquads.aiengineering`.
- Prefer constructor injection and immutable configuration records.
- Return errors through the centralized `GlobalExceptionHandler`.
- Never add credentials, populated `.env` files, private keys, or generated `target` content.
- Update `README.md` and `MEMORY.md` when commands or durable architecture decisions change.
