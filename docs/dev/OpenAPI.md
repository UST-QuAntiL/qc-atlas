# OpenAPI 

## What is OpenAPI?
[OpenAPI](https://swagger.io/specification/) defines a standard, language agnostic interface to RESTful APIs. It allows the generation of API client libraries.

An example of the generated code can be found in the [qc-atlas-ui project](https://github.com/PlanQK/qc-atlas-ui/tree/master/generated/api). 


## How do I generate API client libraries?
Pick a generator for the programming language / framework of your choice, for example from [here](https://github.com/OpenAPITools/openapi-generator).
The generator used in the [qc-atlas-ui project](https://github.com/PlanQK/qc-atlas-ui/tree/master/generated/api) can be found in this [repository](https://github.com/cyclosproject/ng-openapi-gen). 
To obtain the OpenAPI file launch the backend, the file is located in `/atlas/v3/api-docs`.