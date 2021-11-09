#JSON Schema and OpenAPI generators

This directory contains generation tasks for OpenEHR JSON Schema and OpenAPI models.

To generate, go to this directory in the commandline and run:

```shell script
../gradlew clean generateJsonSchema generateOpenAPI
```

The build directory will contain the output, which is currently a JSON Schema for RM 1.0.3, 1.0.4 and 1.1.0, and an OpenAPI 
file containing the models of RM 1.1.0, plus one example API to make it a valid file.