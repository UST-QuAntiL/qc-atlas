# Use OpenAPI specification

* Status: accepted
* Deciders: [StoneOne, USTUTT team]

## Context and Problem Statement

Since we use the OpenAPI for frontend client code generations, the HATEOAS links are no longer needed or used.  

## Decision Drivers

* readable API documentation
* development effort to test/gather the HATEOAS links

## Considered Options

* continue to provide HATEOAS links
* remove the HATEOAS links from all entities

## Decision Outcome

Chosen option: remove the HATEOAS links from all entities, because this further simplifies the DTO entities

