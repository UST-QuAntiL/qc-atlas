# Use Postgres database for service tests

* Status: accepted
* Deciders: [Q-tal Enpro Team]

## Context and Problem Statement

To ensure high test coverage within the project, functionalities that interact with the database must be tested as well. 
There are different approaches to testing these functionalities that require a database running.

## Considered Options

* Use In-Memory database (like H2)
* Use Production-like [Postgres](http://www.postgresql.org) database

## Decision Outcome

Chosen option: "Use Production-like [Postgres](http://www.postgresql.org) database", because it ensures a production-like behavior.

### Positive Consequences <!-- optional -->

* Almost same configuration for test and runtime.
* Less database technologies used.
* Guaranteed that production and tests behave the same.
