# Running Database tests

Running Database tests requies a running PostrgeSQL 12 instance on localhost, which for example can be obtained by running using Docker:
```bash
docker run --name pgdb --rm -p 5432:5432 -e POSTGRES_DB=planqk_test -e POSTGRES_PASSWORD=planqk -e POSTGRES_USER=planqk -d postgres:12
```

Derived from this command you can see that the Database must be called `planqk_test` the corresponding user must be called `planqk` with the password `planqk`.

After the database has launched you can run `mvn clean verify` to run all tests.

## Running Database tests with an Application DB instance

If you are running postgres from the [planqk-docker](https://github.com/PlanQK/planqk-docker) Repository, or you are using a Postgres Instance to launch the `qc-atlas` application for example using this docker command: 
```
docker run --name pgdb --rm -p 5432:5432 -e POSTGRES_DB=planqk -e POSTGRES_PASSWORD=planqk -e POSTGRES_USER=planqk -d postgres:12
```

Since this instance misses the `planqk_test` database is must be created by running:
```
docker exec -it pgdb psql -U planqk postgres -c 'CREATE DATABASE planqk_test;'
```

After doing so the tests should be executed when running `mvn clean verify`.