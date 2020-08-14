# Use Postgres databse for service tests

* Status: accepted
* Deciders: [Q-tal Enpro Team]

## Context and Problem Statement

To ensure high test coverage within the project, the database must be tested as well. There are different approaches to test the database.

## Considered Options

* Use In-Memory database
* Use Production-like [Postgres](http://www.postgresql.org) database

## Decision Outcome

Chosen option: "Use Production-like [Postgres](http://www.postgresql.org) database", because it ensures a production-like behavior.

### Positive Consequences <!-- optional -->

* Almost same configuration for test and runtime.
* Less database technologies used.
