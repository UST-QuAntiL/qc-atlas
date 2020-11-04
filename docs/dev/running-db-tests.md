# Running Database tests

To run database (service) tests it sufficient to have a recent version of `docker` installed on the machine.

The [testcontainers library](https://www.testcontainers.org/) will take care of launching and deconstructing the containers
as needed. 

To run the tests just call:
```
mvn clean verify
```
