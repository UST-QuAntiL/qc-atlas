# Use a Model Mapper to convert entities to DTOs and vice versa

* Status: accepted
* Deciders: [Q-tal Enpro Team]
* Date: [2020-06-04] <!-- optional -->

## Context and Problem Statement

Due to the use of two different object types for transfering and storing data, the conversion between the types becomes a necessity. Therefore we need means to perform the respective conversion.

## Considered Options

* Manually written conversion methods
* [Model Mapper](http://www.modelmapper.org)

## Decision Outcome

Chosen option: "[Model Mapper](http://www.modelmapper.org)", because it fulfils our requirements, is highly configurable and less error prone then manually written converters.

### Positive Consequences <!-- optional -->

* Less boilerplate code
