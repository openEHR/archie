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
- ADL 2 requires node ids at some places where they are optional in ADL 1.4, so the converter generates new id codes. How to do this has not been standardized, so other tools may generate different ADL 2 archetypes from the same input.

## Storing the conversion log

To use the converter in a modeling tool, where you edit in ADL 1.4 but want to output both ADL 1.4 and ADL 2, the converter must generate the same node ids after every edit. To do so, pass the conversion log of the previous conversion to the next one:

```java
ADL2ConversionResultList resultList = converter.convert(archetypes, previousResultList.getConversionLog());
```

The conversion log can be serialized with Jackson, so it can be stored alongside the ADL 1.4 archetypes.

## Conversion configuration

The `ADL14ConversionConfiguration` specifies how term codes as used in ADL 1.4 are mapped to the term URIs used in ADL 2. See [`ConversionConfigForTest.java`](../tools/src/test/java/com/nedap/archie/adl14/ConversionConfigForTest.java) for an example of how this works and how to serialize it to a file, and [`configuration.json`](../tools/src/test/resources/com/nedap/archie/adl14/configuration.json) for a simple serialized configuration with sane defaults for SNOMED CT, LOINC and openEHR URIs.

If you leave the configuration empty, the converter falls back to a default URI conversion.
