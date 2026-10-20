# Serialization

[Back to the README](../README.md)

## ADL

Archetypes can be serialized to ADL, thanks to the serializer written by @markopi64 from Marand Labs:

```java
String serialized = ADLArchetypeSerializer.serialize(archetype);
```

This works for all kinds of archetypes, including operational templates.

## JSON

Both archetypes and reference model objects can be serialized to and parsed from JSON using Jackson. `JacksonUtil` provides a fully configured `ObjectMapper`:

```java
String json = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
Archetype parsedArchetype = JacksonUtil.getObjectMapper().readValue(json, Archetype.class);
```

The JSON output contains some extra fields that make it easier to use in JavaScript, such as paths and the texts of nodes in the language of your choice. These fields are ignored when parsing.

## XML

XML serialization uses JAXB. The XML output for the reference model conforms to the official openEHR RM XSD. There is no official XSD for the AOM 2 yet, so the AOM XML output conforms to the development AOM 2.0 format, which may still change.

```java
Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
StringWriter writer = new StringWriter();
marshaller.marshal(archetype, writer);
String xml = writer.toString();

Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
Archetype parsedArchetype = (Archetype) unmarshaller.unmarshal(new StringReader(xml));
```

## Jackson 3

Parsing, ADL serialization and JSON can also be used with Jackson 3:

```java
ADLParser3 parser = new ADLParser3();
Archetype archetype = parser.parse(adlFile);

String serialized = ADLArchetypeSerializer.serializeWithJackson3(archetype, null, new ArchieRMObjectMapperProvider());

String json = JacksonUtil3.getObjectMapper().writeValueAsString(archetype);
Archetype parsedArchetype = JacksonUtil3.getObjectMapper().readValue(json, Archetype.class);
```

The ODIN classes have Jackson 3 variants as well, such as `OdinObjectParser3` and `ODINMapper3`.

## ODIN

ODIN is a JSON/YAML-like notation used in ADL for metadata, terminologies and annotations. See https://github.com/openehr/odin for more information. Archie can map ODIN directly to Java objects using Jackson:

```java
YourType type = OdinObjectParser.convert(odinText, YourType.class);
```

This works by first converting ODIN to JSON, then binding that JSON to objects with Jackson. Most ODIN features are supported, except:

- intervals of anything other than integers
- object references

To serialize your own objects to ODIN, use the `ODINMapper`. Unlike most Jackson object mappers, it can only serialize ODIN, not parse it:

```java
ODINMapper mapper = new ODINMapper();
String result = mapper.writeValueAsString(yourObject);
```

Like any other Jackson `ObjectMapper`, the mapping is configurable, for example with custom type mappings.
