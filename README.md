# Archie: OpenEHR Library 

Archie is an openEHR Library written in Java, which can be used to implement openEHR tools and systems. See http://www.openehr.org for information about openEHR.
Archie works with the most recent versions of openEHR. This includes ADL version 2.
It contains an ADL 2 parser and an archetype object model implementation, as well as the EHR part of the reference model implementation.

It uses the ANTLR adl-grammar written by Thomas Beale at https://github.com/openehr/adl-antlr.

It is licensed under the Apache license.


# NOTE: THIS IS A DEVELOPMENT BRANCH

We are splitting Archie into separate modules. This causes a few API incompatibilities, which is not limited to the following list:

- Cardinality was moved to the om.nedap.archie.base package
- APathQuery is now changed into AOMPathQuery and RMPathQuery. If you used itemAtPath and itemsAtPath this will not affect your code.
- The default AOM methods no longer use ArchieRMInfoLookup by default. You need to supply it by hand. List of a few cases where this happens:
    - CAttributeTuple.isValidValue()
    - CAttributeTuple.isValid()
    - Flattener
    - ArchetypeValidator
    - RMQueries
    - RuleEvaluation
    - RMQueryContext
    - ... probably more
- RMQueryContext now needs a JAXBContext for the RM to function. You can use JAXBUtil.getArchieJaxbContext() for the openehr-rm 
- The ArchetypeRepository interface now has a getAll() method. It's likely this interface will be renamed or removed in the future in favor of what is now the FullArchetypeRepository
- OverridingArchetypeRepository.addArchetype has been renamed to addExtraArchetype
- ADLPArser.withConstrainsImposer has been removed. You can still do new ADLPArser(new RMConstraintsImposer()) if you want, or use it manually. 
- all datetime parsers are now located in com.nedap.archie.datetime.DateTimeParsers
- CObject is now an abstract class, as it should be according to specs
- ModelInfoLookup now also returns computed attributes, with isComputed() returning true. This can break lots of RM processing tools!


## dependency

You can depend on parts of Archie, or the entire library at once. If you want the entire library, including the Archetype Object Model, ADL parser, all tools and OpenEHR reference model implementation, you can do in gradle:

```gradle
dependencies {
    compile 'com.nedap.healthcare.archie:archie-utils:0.4.0'
}
```

or if you use maven, in your pom.xml

```xml
<dependency>
    <groupId>com.nedap.healthcare.archie</groupId>
    <artifactId>archie-utils</artifactId>
    <version>0.4.0</version>
</dependency>
```

If you use an alternative reference model such as CIMI, and do not want to depend on the OpenEHR Reference model, you can depend just on the AOM, ADL and BMM plus tools to be able to run the Flattener and Validation tools. Or to add your own reference model implementation. You can depend on:

```gradle
dependencies {
    compile 'com.nedap.healthcare.archie:tools:0.4.0'
}
```

or if you use maven, in your pom.xml

```xml
<dependency>
    <groupId>com.nedap.healthcare.archie</groupId>
    <artifactId>tools</artifactId>
    <version>0.4.0</version>
</dependency>
```



## Build

[![Build Status](https://travis-ci.org/openEHR/archie.svg?branch=master)](https://travis-ci.org/openEHR/archie)


the following is from the old repository and not up to date, but gives an impression:
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/dda8ce2b837a40b5b50cf52dae95764d)](https://www.codacy.com/app/pieter-bos/archie?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=nedap/archie&amp;utm_campaign=Badge_Grade)
[![Codecov Badge](https://img.shields.io/codecov/c/github/nedap/archie.svg)](https://codecov.io/gh/nedap/archie)

You need Java 8 to build. Check out the source and type:

```sh
./gradlew build
```

To install to your local maven repository for use in other gradle or maven projects:

```sh
./gradlew install
```

## Usage

### Parsing and Archetype queries

```java
Archetype archetype = new ADLParser().parse(adlFile);

APathQuery query = new APathQuery("/context[id1]/items[id2]/value");
ArchetypeModelObject object = query.find(archetype.getDefinition());

ArchetypeModelObject sameObject = archetype.getDefinition().itemAtPath("/context[id1]/items[id2]/value")

CAttribute attribute = archetype.getDefinition()
    .getAttribute("context").getChild("id1").getAttribute("items");

```

Or, if you prefer your paths to be human readable, you could do:
```java
APathQuery query = new APathQuery("/context[systolic]/items");

CAttribute attribute = archetype.getDefinition()
    .getAttribute("context").getChildByMeaning("systolic").getAttribute("items");
    attribute.getLogicalPath(); // is 'context[systolic]/items'
```

### Archetype Validation

After parsing you can check if you archetype is valid. The parser will try to continue parsing as much as possible without throwing Exceptions. This means you will have to check the errors manually. This happens even with some syntax errors, so checking errors is important!

```
if(!parser.getErrors().hasNoErrors()) {
    parser.getErrors().logToLogger();
}
```

The second round of checks comes after parsing, in the form of running the ArchetypeValidator:

```
ReferenceModels models = new ReferenceModels(ArchieRMInfoLookup.getInstance());
MetaModels metaModels = new MetaModels(models, null);
ValidationResult validationResult = new ArchetypeValidator(metaModels).validate(archetype);
```

This runs all the implemented archetype validation and returns a result. This result contains the validation results, the validation results of any template overlays and a flattened version of the input archetype.

Note that it requires a MetaModels class. This contains the Metadata of the reference models used. 

### Reference Model Metadata

Archetype tools require metadata about the used reference model to operate. This can be the OpenEHR reference model, but it can also be something else. Archie has two concepts to define this metadata: Reflection based metadata and BMM metadata.

The MetaModels class is an abstraction over these two types of models. Construct this, and it will automatically select the available metadata model. Note that if a BMM model is present for your archetype, it will use that if possible.

Reflection based metadata bases its metadata on an actual reference model implementation in Java. Two are included, the ArchieRMInfoLookup, for the OpenEHR Reference Model, and the TestRMInfoLookup, for the openEHR Test models. You can register these on a ReferenceModels class, see the code in the previous paragraph for an example

The BMM model is not based on an actual implementation, but on a file containing the metadata. The specifications of BMM are part of OpenEHR and can be found at http://www.openehr.org/releases/BASE/latest/docs/bmm/bmm.html.

To use, include your BMM files and point the code to the correct directory:

```
List<String> schemaDirectories = new ArrayList<>();
schemaDirectories.add("/myBmmDirectory");
ReferenceModelAccess access = new ReferenceModelAccess();
access.initializeAll(schemaDirectories);
MetaModels models = new MetaModels(new ReferenceModels(ArchieRMInfoLookup.getInstance()), access);
//now parse the AOM profiles
String[] resourceNames = {"first aom profile", "second aom profile"};
for(String resource:resourceNames) {
    try(InputStream odin = TestUtil.class.getResourceAsStream(resource)){
        models.getAomProfiles().add(odin);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}        
```

This instantiates a MetaModels class that has both the archie reference model implementation, the BMM models and AOM profiles. The BMM models and AOM profileswill be used for the flattener and archetype validator, the other models for tools that work on reference models.

A next version of archie will likely include BMM profiles and an easy way to obtain these models, as well as AOM profiles. See the ```TestUtilgetBMMReferenceModels()``` class for how the tests currently do this.

### Serializing

Archetype models can be serialized to ADL thanks the the serializer written by @markopi64 from Marand Labs. To use:

```java
String serialized = ADLArchetypeSerializer.serialize(archetype);
```

### Archetype repositories

The previous examples show how to validate a single standalone archetype. But archetypes are usually not single standalone artifacts - they can specialize or link to others. To do that, create a FullArchetypeRepository. The easiest way to do so is the InMemoryFullArchetypeRepository:

```
InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
for(Archetype archetype:allArchetypes) {
    repository.addArchetype(archetype);
}
...
```

Now you can compile the repository:

```
repository.compile(metaModels);
List<ValidationResult> allValidationResults = getAllValidationResults();
```

This will validate and flatten all archetypes, giving you access to the results. 


### Flattener and Operational Template creation

The FullArchetypeRepository already flattens archetypes on the compile operation, but it does not yet create OperationTemplates. To do so, you can use the Flattener manually:

```java
Archetype flattened = new Flattener(yourArchetypeRepository)
    .createOperationalTemplate(true)
    .flatten(yourArchetypeRepository.getArchetype("openEHR-EHR-COMPOSITION.your_archetype.v1"));
```

Pass the original archetype, NOT the flattened variant.

You will get a flattened operational template of the original archetype. The original archetype will not be changed, the result is a copy.
If you opted to create an operational template, the occurrences of use_archetype and use_node will have been replaced with a copy of the contents of the specified archetype and node, and the component terminologies will have been added to the operational template.

### Terminology: texts and descriptions for your archetypes

You can directly use archetype.terminology() to get the meaning in any desired language. Archie contains convenience methods to make this easier for you in several ways. Since you likely will want to use the same language for many calls in the same thread, you can specify the language you are using as a thread local variable and just directly get terms for the Archetype Constraints.

```java
ArchieLanguageConfig.setThreadLocalMeaningAndDescriptionLanguage("nl");

CObject cobject = archetype.getDefinition().getAttribute("context").getChild("id1");
ArchetypeTerm term = cobject.getTerm();
logger.info("and the archetype term text in Dutch is: " + term.getText());
```

This just works in all cases - with the normal terminology of an unflattened archetype and with component terminologies from operational templates.

Of couse, you can also specify the language yourself:

```java
CObject cobject = archetype.getDefinition().getAttribute("context").getChild("id1");
ArchetypeTerm term = archetype.getTerm(cobject, "en");
logger.info("and the archetype term text in English is: " + term.getText());
```

Or you could access the terminology itself:

```java
archetype.getTerminology().getValueSets();
operationalTemplate.getComponentTerminologies()
```

For terminology constraints with locally defined values:

```java
CTerminologyCode cTerminologyCode = object.itemAtPath("/somePath...");
List<TerminologyCodeWithArchetypeTerm> = cTerminologyCode.getTerms();
```

or

```java
archetype.getTerm(cTerminologyCode, "at15", "en");
```

//TODO: old documentation starts below
### Default constraints from the reference model

An archetype constrains instances of the reference model. But a reference model also has some default constraints. For example, an observation has a data field, which is a single field - not a list or set. This has implications for default cardinality and existence constraints, and for example the ```isMultiple()``` and ```isSingle()``` methods. You can apply these constraints so you get them set explicitly in the resulting archetype-object. To do so:

```java
Archetype archetype = ADLParser.withRMConstraintsImposer().parse(adlFile);
```

Or use the RMConstraintsImposer class yourself instead of directly from the parser.


### Use a different reference model implementation

Archie uses its own reference model by default, but it can use any reference model you like. To do so, you can implement your own Constraints imposer, your own reference model info lookup and some other classes, and use those in the right places. To do so, create an implementation of each of the following abstract classes and interfaces:

- ModelNamingStrategy
- ModelInfoLookup
- ModelConstraintImposer or ReflectionConstraintImposer

### Intervals and primitive objects

The Interval has its ```has(value)``` method implemented. So you can do:

```java
new Interval<>(2,4).has(3); //returns true
Interval.upperUnbounded(2).has(1); //returns false
```

CPrimitiveObject has an ```isValidValue(value)``` method, so you can do:

```java
CString cString = new CString();
cString.addConstraint("test");
cString.addConstraint("/more te+st/");
cString.isValidValue("test"); //returns true
cString.isValidValue("more teeeeest"); //returns true - regexp matching
cString.isValidValue("mooooore test"); //returns false
```

ISO-8601 date constraint patterns are not yet implemented.

### Archetype tree walking for openEHR reference models

Because the getAttributes() has been implemented on the CObject-class instead of CComplexObject, creating a tree walker is just a few lines of code:

```java
public void walk(CObject cobject) {
	for(CAttribute attribute:cobject.getAttributes()) {
		for(CObject child:attributes.getChildren()) {
			walk(child);
		}
	}
}
```

### JSON and XML (de)serialization

Both the Archetype and Reference Model objects serialize and deserialize to JSON and XML. JSON serialization works using Jackson. You can get a fully configured ObjectMapper using JacksonUtil:

```java
String json = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
Archetype parsedArchetype = JacksonUtil.getObjectMapper().readValue(json, Archetype.class);
```

The JSON output contains some features that make it easy to use in javascript, such as path attributes and translations in the language of your choice directly in the JSON. These features are ignored when parsing - as they should be.

XML works using JAXB. The XML output for the reference model conforms to the official openEHR RM XSD. For the AOM this is a bit tricky, because there is no finished XSD yet. It currently conforms to the development AOM 2.0 format. This format is still subject to change. To use:

```java
Marshaller marshaller = JAXBUtil.getArchieJAXBContext().createMarshaller();
StringWriter writer = new StringWriter();
marshaller.marshal(archetype, writer);
String xml = writer.toString();

Unmarshaller unmarshaller = JAXBUtil.getArchieJAXBContext().createUnmarshaller();
Archetype parsedArchetype = (Archetype) unmarshaller.unmarshal(new StringReader(xml));
```

### ODIN

ODIN is a JSON/YAML like notation used as part of ADL, for meta-data, terminologies and annotations. See https://github.com/openehr/odin for what more it can do. To our knowledge it's not used widely outside of ADL/openEHR. Archie can map ODIN data directly to Java-objects using Jackson. 

```
	YourType type = OdinObjectParser.convert(odinText, YourType.class);
```

It works by first converting to JSON, then binding that to objects with Jackson.
This means you can also convert ODIN to JSON. 

```
	String json = new OdinToJsonConverter().convert(odinText);
```

Converting to JSON is a great way to get ODIN object mapping with very little code and it parses enough to parse the ODIN used in ADL 2. Odin has a few advanced features not generally used in archetype metadata, which are object references and intervals. These are currently not yet supported, although it is very possible to implement them using a custom deserializer for the interval and JSON references for the object references.

## Reference Model

### Reference model object creation

The RMObjectCreator creates empty reference model objects based on constraints. It can also set values based on attribute names. You can use it to create a reference model based on an archetype and user input. To create an empty reference model based on an archetype, you could work further on this example:


```java
 public RMObject constructEmptyRMObject(CObject object) {
        RMObject result = creator.create(object);
        for(CAttribute attribute: object.getAttributes()) {
            List<Object> children = new ArrayList<>();
            for(CObject childConstraint:attribute.getChildren()) {
                if(childConstraint instanceof CComplexObject) {
                    RMObject childObject = constructEmptyRMObject(childConstraint);
                    children.add(childObject);
                }
            }
            //will fail when a single valued attribute has two values, check code in TestUtil.java for how to solve.
            creator.set(result, attribute.getRmAttributeName(), children);
        }
        return result;
    }
```

Setting primitive object values works in a similar way, with ```creator.set(...)```, or by setting them explicitly on the reference model object directly.

### Reference model APath queries

```java
List<Object> items = rmObject.itemsAtPath("/data[id2]/items");
```
Or if you want apath-expressions resolving to that single item together with every object returned, you can use the low-level method:

```java
List<RMObjectWithPath> itemsWithUniquePaths = new APathQuery("/data[id2]/items").findList(ArchieRMInfoLookup.getInstance(), rmObject);
```

#Experimental features

The following features are experimental. This means its working will likely change somewhat in the near future, but they can already be used.

## Full XPath support on reference model

The RMQueryContext provides full XPath 1-support on reference model instances. APath shorthand notations like /items[id2] will automatically be converted to the corresponding xpath. JAXB's Binder is used to get a DOM on which to run XPath. You can choose your own JAXB-implementation with the usual mechanisms, although only the internal java one has been tested.

## Rule evaluation

Basic rule evaluation is implemented, but the implementation is still experimental. See RuleEvaluation.java on how to use.

The rule evaluation means assertions can be defined, then passed through the ```RuleEvaluation``` class. The result is:

- a list of assertions, each noting:
    - whether it evaluated to true or false
    - whether it can be automatically fixed or a step by the user is needed
    - what must be done to automatically fix this rule, by setting paths to values and paths that must exist or not exist
    - which fields were used to evaluate to the result so errors can be shown

This means you can link the RuleEvaluation to a user interface, implement a way to set paths to a value, hide or show some fields and display validations, and you have a working rule evaluation.

Automatically fixing rules means that rules in the following form can be used for score calculations:

```
/path/to[id12]/score_result = /path/to[id12]/field_one + /path/to[id12]/field_two
```

Or for conditionally hiding fields:

```
/path/to[id12]/score_result > 3 implies exists /path/to/hidden/field
```

Or for validations that a user must fix themselves:

```
total_score_must_be_high: /path/to[id12]/score_result + /some_other_score > 24
```

And many more things. Currently supported is comparison and arithmetic operators, path retrieval, exists and not exists, implies, variable declaration and use, for_all. integer, string and boolean literals are implemented, although currently the string literal support is limited to the != and = operator. That means you can set fields with string values, including code_strings of DV_CODED_TEXT and DV_ORDINAL fields.

Rules are very similar to XPath expressions, with a few exceptions. However, the specification is lacking a formal definition of how to handle several kind of multiplicities. Where multiplicities occur in the paths in rules outside of a for_all rule, the W3C XPath 1 specification is used.

## Status

The project is quite usable for when you want to create an EHR implementation, but it is still missing many openEHR features which could make this library easier to use and more complete.

What we want this to do in the future:
- Date Constraint parsing with patterns, not just intervals
- More tests
- Validating if an RM object conforms to a certain archetype, apart from the rules
- Validating archetypes to see if they are valid
- ...

## Contributions

Feel free to create a pull request or use the github issue tracker to report issues.

## License

This software is licensed under the Apache license. See the LICENSE and NOTICE files.
