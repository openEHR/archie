# JSON Schema generation

This module generates JSON schema files from the BMM files included in Archie. It is used to generate the official JSON Schema at https://github.com/openehr/specifications-ITS-JSON.

## Generating the JSON schema files

1. follow the Archie build instructions and make sure it builds
2. Go to the json-schema subdirectory and build it:

```shell
cd json-schema
../gradlew clean generateJson
```

The output will be in build/schemaOutput. It is already in the correct format and structure, so it can be directly copied to the specifications-ITS-JSON repository.