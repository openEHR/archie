# Reference model

[Back to the README](../README.md)

Archie contains an implementation of the openEHR reference model version 1.1.0, without the task planning part, plus tools to work with reference model objects.

## Creating reference model objects

The `ExampleJsonInstanceGenerator` generates example JSON instances from an operational template. They can be parsed and used as examples, or as a starting point for new reference model objects:

```java
ExampleJsonInstanceGenerator generator = new ExampleJsonInstanceGenerator(BuiltinReferenceModels.getMetaModelProvider(), "en");
Map<String, Object> example = generator.generate(operationalTemplate);
String json = JacksonUtil.getObjectMapper().writeValueAsString(example);
```

To build reference model objects yourself, the `RMObjectCreator` creates an empty reference model object for a given constraint. If the constraint uses an abstract class, it picks a non-abstract descendant class:

```java
RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
RMObject rmObject = creator.create(cComplexObject);
```

You can then set attribute values directly on the created objects, or with the `ModelInfoLookup`.

`ArchieRMInfoLookup.getInstance()` provides the metadata about the openEHR reference model implementation. See [Using a different reference model implementation](advanced.md#using-a-different-reference-model-implementation) to use your own.

## Parsing reference model JSON

Reference model JSON is parsed the same way as archetype JSON:

```java
RMObject rmObject = JacksonUtil.getObjectMapper().readValue(json, RMObject.class);
```

or if you know which class to expect:

```java
Composition composition = JacksonUtil.getObjectMapper().readValue(compositionJson, Composition.class);
```

## Reference model path queries

```java
List<Object> items = rmObject.itemsAtPath("/data[id2]/items");
```

To get the unique path of every returned object as well, use the lower level `RMPathQuery`:

```java
List<RMObjectWithPath> itemsWithUniquePaths = new RMPathQuery("/data[id2]/items").findList(ArchieRMInfoLookup.getInstance(), rmObject);
```

## Validating reference model objects against an archetype

The `RMObjectValidator` validates reference model objects against an operational template. It validates:

- cardinality
- occurrences
- existence
- data types
- primitive object constraints
- tuples

Cardinality and existence are validated against both the archetype and the reference model.

```java
RMObjectValidator validator = new RMObjectValidator(
        ArchieRMInfoLookup.getInstance(), // or a different ModelInfoLookup for other reference models
        operationalTemplateProvider,
        new ValidationConfiguration.Builder().build());
List<RMObjectValidationMessage> validationMessages = validator.validate(operationalTemplate, rmObject);
```

The validator only works with operational templates, not with differential or flattened archetypes. See [Operational templates](archetypes.md#operational-templates) on how to create them. The `OperationalTemplateProvider` is used to get the operational templates of archetypes that are used in archetype slots. `InMemoryFullArchetypeRepository` implements this interface. It only returns operational templates that were stored in it with `setOperationalTemplate(...)`.

`ValidationConfiguration.Builder` has options to also validate the reference model invariants, and to fail on unknown terminology ids.

## Intervals and primitive object constraints

`Interval` implements the `has(value)` method:

```java
new Interval<>(2, 4).has(3); // returns true
Interval.upperUnbounded(2, true).has(1); // returns false
```

To check whether a value conforms to a C_PRIMITIVE_OBJECT constraint, use the `RMObjectValidator`, or the
`ValidationHelper` it is built on:

```java
CString cString = new CString();
cString.addConstraint("test");
cString.addConstraint("/more te+st/");

ValidationHelper helper = new ValidationHelper(ArchieRMInfoLookup.getInstance(), new ValidationConfiguration.Builder().build());
helper.isValidValue(cString, "test"); // returns true
helper.isValidValue(cString, "more teeeeest"); // returns true - regexp matching
helper.isValidValue(cString, "mooooore test"); // returns false
```

ISO 8601 date constraint patterns are not yet supported.
