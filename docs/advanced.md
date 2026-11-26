# Advanced usage

[Back to the README](../README.md)

## Reference model metadata

Archetype tools need metadata about the reference model to work. This can be the openEHR reference model, but also another model. For most users, `BuiltinReferenceModels.getMetaModelProvider()` is enough. This section is for when you want to add your own reference models or metadata.

Archie supports two types of metadata:

- **Reflection based metadata**, derived from a Java implementation of a reference model, in the form of a `ModelInfoLookup`. Two are included: `ArchieRMInfoLookup` for the openEHR reference model, and `TestRMInfoLookup` for the openEHR test models in the `test-rm` module. The built-in metadata only contains the lookups for the modules you depend on. If you depend on `archie-all`, the openEHR reference model is included.
- **BMM metadata**, which is not based on an implementation, but on a file in the BMM format defined by openEHR. See [the BMM specification](https://specifications.openehr.org/releases/LANG/latest/bmm.html).

A `MetaModel` combines both types of metadata, and a `MetaModelProvider` provides the `MetaModel` for a given archetype or reference model version. If a BMM model is available for an archetype, it is used where possible.

To use your own BMM files, parse them, then load the AOM profiles:

```java
BmmRepository bmmRepository = new BmmRepository();
for (String fileName : bmmFiles) {
    try (InputStream stream = new FileInputStream(fileName)) {
        bmmRepository.addPersistentSchema(BmmOdinParser.convert(stream));
    }
}
BmmSchemaConverter converter = new BmmSchemaConverter(bmmRepository);
converter.validateAndConvertRepository();

AomProfiles aomProfiles = new AomProfiles();
for (String fileName : aomProfileFiles) {
    try (InputStream stream = new FileInputStream(fileName)) {
        aomProfiles.add(stream);
    }
}

MetaModelProvider metaModelProvider = new SimpleMetaModelProvider(
        new ReferenceModels(ArchieRMInfoLookup.getInstance()), bmmRepository, aomProfiles);
```

This creates a `MetaModelProvider` with the Archie reference model implementation, the BMM models and the AOM profiles. The flattener and archetype validator use the BMM models and AOM profiles. Tools that work on reference model objects use the reflection based metadata.

After validating and converting, the `BmmRepository` gives access to the P_BMM schemas, the BMM models and all validation messages through the `BmmValidationResult` class. See the Javadoc for more information.

## Validating a single archetype

The [archetype repository](archetypes.md#archetype-repositories-and-validation) validates all archetypes at once. To validate a single archetype without any parent or included archetypes, use the `ArchetypeValidator` directly:

```java
ValidationResult validationResult = new ArchetypeValidator(BuiltinReferenceModels.getMetaModelProvider()).validate(archetype);
```

The result contains the validation messages, the validation results of any template overlays and the flattened archetype.

## BMM and P_BMM

Archie implements BMM version 2.2 and P_BMM version 2.3.0. See [Reference model metadata](#reference-model-metadata) on how to parse P_BMM and convert it to BMM. There are some differences with the specification, to simplify the implementation:

- The P_BMM implementation only contains the persisted fields, and does not keep any state about the conversion to BMM.
- BMM_SCHEMA and REFERENCE_MODEL_ACCESS are not used. Use `BmmValidationResult` instead of BMM_SCHEMA, and `BmmRepository` instead of REFERENCE_MODEL_ACCESS.
- BMM_SCHEMA_DESCRIPTOR and P_BMM_SCHEMA_DESCRIPTOR are not used, and have no direct replacement.

P_BMM is bound to Jackson, so it can be serialized to ODIN:

```java
String odin = new BmmOdinSerializer().serialize(pBmmSchema);
```

It can also be serialized to and parsed from JSON or YAML. Use `BmmJacksonUtil.getObjectMapper()` for JSON.

## Default constraints from the reference model

An archetype constrains instances of the reference model, but the reference model also has default constraints. For example, the `data` attribute of an OBSERVATION is single valued, not a list or set. This affects the default cardinality and existence, and methods such as `isMultiple()` and `isSingle()` on C_ATTRIBUTE. To set these values when parsing, pass a `MetaModelProvider` to the parser:

```java
Archetype archetype = new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(adlFile);
```

This is generally not needed when you use an archetype repository.

## Using a different reference model implementation

Archie uses its own openEHR reference model implementation by default, but it can work with other implementations. To do so, implement a `ModelInfoLookup` for your reference model, usually by extending `ReflectionModelInfoLookup` with your own `ModelNamingStrategy`. See `ArchieRMInfoLookup` and `TestRMInfoLookup` for examples. Then register it in a `ReferenceModels` instance and use that to create a `SimpleMetaModelProvider`.
