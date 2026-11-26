# Converting ADL 1.4 archetypes to ADL 2

[Back to the README](../README.md)

Archie can parse ADL 1.4 archetypes and convert them to ADL 2:

```java
MetaModelProvider metaModelProvider = BuiltinReferenceModels.getMetaModelProvider();
ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
ADL14Converter converter = new ADL14Converter(metaModelProvider, conversionConfiguration);
ADL14Parser parser = new ADL14Parser(metaModelProvider);

List<Archetype> archetypes = new ArrayList<>();
for (String file : fileNames) {
    try (InputStream stream = new FileInputStream(file)) {
        archetypes.add(parser.parse(stream, conversionConfiguration));
    } catch (ADLParseException e) {
        // handle the parse error
    }
}

ADL2ConversionResultList resultList = converter.convert(archetypes);
for (ADL2ConversionResult adl2ConversionResult : resultList.getConversionResults()) {
    if (adl2ConversionResult.getException() == null) {
        // the ADL 2 result. Warnings are available in adl2ConversionResult.getLog()
        Archetype convertedArchetype = adl2ConversionResult.getArchetype();
    }
}
```

The converted archetypes are in differential ADL 2 form. Things to keep in mind:

- Run the converted archetypes through the archetype validator, especially specialized archetypes. ADL 1.4 tooling often has limited validation for specialized archetypes, so they often contain errors.
- To convert a specialized archetype, pass both the specialized archetype and its parent to the converter in the same call. Otherwise the `ADL2ConversionResult` will contain an exception.
- ADL 2 requires node ids at some places where they are optional in ADL 1.4, so the converter generates new node ids. How to do this has not been standardized, so other tools may generate different ADL 2 archetypes from the same input.

## Id-coded or at-coded output

The converter creates id-coded ADL 2 archetypes by default. To keep the ADL 1.4 at-codes as node ids, create [at-coded ADL 2 archetypes](archetypes.md#at-coded-adl-2-archetypes) instead:

```java
ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
conversionConfiguration.setNodeIdCodeSystem(ADL14ConversionConfiguration.NodeIdCodeSystem.AT_CODED);
```

The linked section also describes how to configure the validator for at-coded output. In both cases, the converted archetypes get `adl_version=2.4.0`.

## Conversion algorithm

Converting to at-coded ADL 2 keeps the ADL 1.4 codes. Converting to id-coded ADL 2 renumbers them:

| Part of the archetype | Id-coded output | At-coded output |
|---|---|---|
| Node ids, such as `ELEMENT[at0004]` | Renumbered to an id-code: `ELEMENT[id5]` | Unchanged: `ELEMENT[at0004]` |
| Local value codes, such as `[local::at0010]` | Renumbered to an at-code: `[at11]` | Unchanged: `[at0010]` |
| Value set codes, such as `[ac0001]` | Renumbered to an ac-code: `[ac2]` | Unchanged: `[ac0001]` |
| Paths, in term bindings and `use_node` | Node ids renumbered | Unchanged |
| A list of local codes, such as `[local::at0010, at0011]` | A new value set `ac9000` with members `at11` and `at12` | A new value set `ac9000` with members `at0010` and `at0011` |
| An external code, such as `[SNOMED-CT::123]` | A new value code `at9000` with a term binding | A new value code `at9000` with a term binding |
| A node that needs a node id in ADL 2, but has none in ADL 1.4 | A new node id `id9000` | A new node id `at9000` |

Generated codes start at 9000, so they do not collide with codes added later.

Renumbering to id-coded ADL 2 works as follows:

1. Strip the leading zeros: `at0004` becomes `4`.
2. Add one to the top-level number, because ADL 1.4 starts counting at `at0000` and ADL 2 at `id1`: `4` becomes `5`. Skip this step for codes that are new in a specialized archetype, such as `at0.5`.
3. Add the new prefix: `id` for node ids, `at` for value codes and `ac` for value set codes.

So `at0000` becomes `id1`, `at0004` becomes `id5` or `at5`, `at0004.1` becomes `id5.1` and `at0.5` becomes `id0.5`. An at-code that is used both as a node id and as a value code becomes two codes, for example `id5` and `at5`.

## Storing the conversion log

To use the converter in a modeling tool, where you edit in ADL 1.4 but want to output both ADL 1.4 and ADL 2, the converter must generate the same node ids after every edit. To do so, pass the conversion log of the previous conversion to the next one:

```java
ADL2ConversionResultList resultList = converter.convert(archetypes, previousResultList.getConversionLog());
```

The conversion log can be serialized with Jackson, so it can be stored alongside the ADL 1.4 archetypes.

A conversion log is only reused for the same code system. A log of the other code system is ignored, with the warning `WARNING_PREVIOUS_CONVERSION_LOG_CODE_SYSTEM_MISMATCH`. Logs stored by earlier Archie versions are id-coded.

## Conversion configuration

The `ADL14ConversionConfiguration` specifies how term codes as used in ADL 1.4 are mapped to the term URIs used in ADL 2. See [`ConversionConfigForTest.java`](../tools/src/test/java/com/nedap/archie/adl14/ConversionConfigForTest.java) for an example of how this works and how to serialize it to a file, and [`configuration.json`](../tools/src/test/resources/com/nedap/archie/adl14/configuration.json) for a simple serialized configuration with sane defaults for SNOMED CT, LOINC and openEHR URIs.

If you leave the configuration empty, the converter falls back to a default URI conversion.
