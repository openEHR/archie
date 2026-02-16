# Archie: openEHR Library

Archie is an openEHR Library written in Java, which can be used to implement openEHR tools and systems. See http://www.openehr.org for information about openEHR.
Archie works with the most recent versions of openEHR. This includes ADL version 2.
It contains an ADL 2 parser and an archetype object model implementation, as well as the EHR part of the reference model implementation.

It uses the ANTLR adl-grammar written by Thomas Beale at https://github.com/openehr/adl-antlr.

It is licensed under the Apache license.


## Migrating from the previous 0.3 version of archie
Version 0.4 of archie is a major update that requires changes in your code to upgrade. See https://github.com/openEHR/archie/wiki/Migration-from-Archie-0.3.x on how to migrate from archie version 0.3.x.

## Dependency

You can depend on parts of Archie, or the entire library at once. If you want the entire library, including the Archetype Object Model, ADL parser, all tools and openEHR reference model implementation, you can do in gradle:

```gradle
dependencies {
    compile 'com.nedap.healthcare.archie:archie-all:3.17.0'
}
```

or if you use maven, in your pom.xml

```xml
<dependency>
    <groupId>com.nedap.healthcare.archie</groupId>
    <artifactId>archie-all</artifactId>
    <version>3.17.0</version>
</dependency>
```

If you want to depend on just the AOM and BMM, without any reference model implementation, depend on com.nedap.healthcare.archie:tools:3.17.0 and com.nedap.healthcare.archie:referencemodels:3.17.0 instead


## Build

[![Build Status](https://travis-ci.org/openEHR/archie.svg?branch=master)](https://travis-ci.org/openEHR/archie)
[![codecov](https://codecov.io/gh/openEHR/archie/branch/master/graph/badge.svg)](https://codecov.io/gh/openEHR/archie)

You need Java 11 or higher to build. When compiled, the library works on JDK 8, but building is not supported on that version.

To build, Check out the source and type:

```sh
./gradlew build
```

To install to your local maven repository for use in other gradle or maven projects:

```sh
./gradlew publishToMavenLocal
```

## Contents

- [Usage](#usage)
  * [Archetypes](#archetypes)
    + [Parsing and Archetype queries](#parsing-and-archetype-queries)
    + [Checking for parse errors](#checking-for-parse-errors)
    + [Creating an Archetype Repository](#creating-an-archetype-repository)
    + [Reference model metadata](#reference-model-metadata)
    + [Operational templates](#operational-templates)
    + [Terminology: texts, term bindings and descriptions for your archetypes](#terminology-texts-term-bindings-and-descriptions-for-your-archetypes)
    + [Diffing archetypes](#diffing-archetypes)
    + [Serializing to ADL](#serializing-to-adl)
    + [JSON and XML (de)serialization](#json-and-xml-deserialization)
    + [ODIN](#odin)
    + [Intervals and primitive objects](#intervals-and-primitive-objects)
    + [Archetype tree walking for openEHR reference models](#archetype-tree-walking-for-openehr-reference-models)
  * [Reference Model tools](#reference-model-tools)
    + [Reference model object creation](#reference-model-object-creation)
    + [Reference model APath queries](#reference-model-apath-queries)
    + [Validating RM Objects against an archetype](#validating-rm-objects-against-an-archetype)
- [Experimental Features](#experimental-features)
  * [Importing and converting ADL 1.4 files](#importing-and-converting-adl-14-files)
  * [Full XPath support on reference model](#full-xpath-support-on-reference-model)
  * [Rule evaluation](#rule-evaluation)
  * [Lower level APIs](#lower-level-apis)
    + [Archetype Validation](#archetype-validation)
    + [Reference Model Metadata](#reference-model-metadata)
    + [Default constraints from the reference model](#default-constraints-from-the-reference-model)
    + [Use a different reference model implementation](#use-a-different-reference-model-implementation)
  * [Status](#status)
  * [Contributions](#contributions)
  * [License](#license)

## Usage

## Archetypes

The base of openEHR are archetypes. Archie allows you to work with them, and defines tools to create and validate them.

### Parsing and Archetype queries

```java
ADLParser parser = new ADLParser();
Archetype archetype = parser.parse(adlFile);

AOMPathQuery query = new AOMPathQuery("/context[id1]/items[id2]/value");
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

### Checking for parse errors

The parser is setup to continue parsing even if there are errors. This means you will manually have to check for parse errors in your code, after parsing each file:

```java
if(!parser.getErrors().hasNoErrors()) {
    parser.getErrors().logToLogger();
}
```

### Creating an Archetype Repository

A single archetype can be useful, but archetypes often are related to other archetypes: They specialize a parent archetype, or link to another archetype. To easily use archetypes, you can create an ArchetypeRepository:

```java
InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
for(Archetype archetype:allArchetypes) {
    repository.addArchetype(archetype);
}
repository.compile(BuiltinReferenceModels.getMetaModelProvider());

for(ValidationResult result:repository.getAllValidationResults()) {
    ... your code here
}

```

The validation result contains the validation result, listing any errors in the archetype. It also contains the source archetype and if validation passed far enough to allow it to flatten, the flattened form of the archetype.

### Reference model metadata

You may have noticed a call to ```BuiltinReferenceModels.getMetaModelProvider()```. This retrieves the metadata for the reference models, which are needed to validate and flatten archetypes. Archie contains two types of metamodels: BMM, and reflection based metadata.
The BMM models are a file containing metadata in a form defined by the openEHR specifications.
The reflection based metadata contains ModelInfoLookup classes. They are derived from an implementation of a reference model. Note that the ModelInfoLookup classes are only added if you depended on them. If you depended on archie-all, you're all set.

### Operational templates

You can create operational templates of your archetypes. Think of operational templates as something you generate from an archetype or template to more easily work with it. If you are creating an EHR implementation, these will likely be the most useful form of archetypes to work with.
openEHR Archetypes allow you to reuse structures inside your archetypes at several places. It also allows to embed other archetypes inside your archetype. Without operational templates, you would need to build support for these structures into all of your tools. Operational templates fix this for you, by replacing the proxies for structure and embedded archetypes in the archetype with a copy of the actual embedded structure. For more information about operational templates, see (the documentation at the openEHR website)[http://openehr.org/releases/AM/latest/docs/OPT2/OPT2.html].
Note that ADL 2 operational templates is fundamentally different from the ADL 1.4 OET/OPT format. What you used to achieve with OET/OPT is now built into ADL 2 as templates in combination with the operational template creation. See (the openEHR specification on templates)[http://openehr.org/releases/AM/latest/docs/ADL2/ADL2.html#_templates] on how to work with them.

To create an Operational Template:

```java
Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider()).createOperationalTemplate(true);
OperationalTemplate template = (OperationalTemplate) flattener.flatten(sourceArchetype);
```

Make sure you pass an original archetype, not a flattened version.

Note that a flattener is single use: to flatten another archetype after use, you will have to create another Flattener.
You can also setup the flattener to remove languages from the terminology by specifying the languages you wish to keep, by calling ```flattener.keepLanguages("en", "nl")```, with a list of language strings before flattening. Additionally you can also remove the languages from the metadata of the archetype by calling ```flattener.removeLanguagesFromMetadata(true)``` before flattening. Of course, if you leave out the ```flattener.createOperationalTemplate(true)``` call, the flattener will flatten the archetype, but not create an OperationalTemplate.


### Terminology: texts, term bindings and descriptions for your archetypes

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

### Diffing archetypes

The inverse operation of flattening archetypes is diffing. This is useful if you write a tool to edit archetypes. For end users, it's much less complicated to edit flat forms of archetypes instead of differential forms. This operation lets users edit the flat form, after which you can save the output from the differentiator as the differential form. To use the Differentiator:

```java
Archetype flatChild, flatParent; //for how to parse and flatten, see elsewhere in the readme

Differentiator differentiator = new Differentiator(BuiltinReferenceModels.getMetaModelProvider());
Archetype diffed = differentiator.differentiate(flatChild, flatParent);
```

Use a flattened archetype both for the parent and the specialized archetype. Do not use OperationalTemplates.

The Differentiator supports differentiating the definition of the archetype, plus the terminology. It performs the following operations:

- adding sibling order markers to preserve the ordering of cObjects in the specialized archetype
- removing default or unspecialized cardinality, occurrences and existence
- removing unspecialized CObjects
- removing unspecialized CAttributes
- removing unspecialized tuples
- generating differential paths where possible
- generating a diff of the terminology, including term definitions, term bindings and value sets
- removes any translations that exist in the parent archetype, but not in the specialised archetype
- removing terminology extracts

It returns a new archetype as its result, the input archetypes are never altered.
The sibling order algorithm is written so that it creates a very small number of sibling order markers, and only generates them if necessary.
The diff operation does not diff any template overlays if presented with a Template. However, you can use a flattened TemplateOverlay plus its flat parent as arguments and obtain the differential form of the template overlay. You can then replace the edited template overlay in the template yourself. It is possible that this feature will be added in a later version.
It also does not yet diff the rules section, this must be manually performed.

### Serializing to ADL

Archetype models can be serialized to ADL thanks the the serializer written by @markopi64 from Marand Labs. To use:

```java
String serialized = ADLArchetypeSerializer.serialize(archetype);
```

This can be done on all kinds of Archetypes, including OperationalTemplates

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

```java
    YourType type = OdinObjectParser.convert(odinText, YourType.class);
```

It works by first converting to JSON, then binding that to objects with Jackson.
This means you can also convert ODIN to JSON.

```java
    String json = new OdinToJsonConverter().convert(odinText);
```

The JSON conversion plus object mapping currently supports most ODIN features, except:

- Intervals of anything other than integers
- object references

To serialize your own object into ODIN, a Jackson object mapper is present. Note that unlike most Jackson Object Mappers, this mapper currently cannot parse ODIN, only serialize it. Trying to parse with it will result in exceptions. To use:

```java
ODINMapper mapper = new ODINMapper();
String result = mapper.writeValueAsString(yourObject);
```

Like any other Jackson ObjectMapper, many options in the mapping are configurable, such as custom type mapping.


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

## Reference Model tools

Archie contains an implementation of the openEHR reference model version 1.1.0. It does not yet contain the task planning part. It also contains tools to work with reference model objects.

### Reference model object creation

The ```ExampleJsonInstanceGenerator``` generates valid JSON instances from Operational Templates. They can be parsed and used as examples of RM Object instances, or empty RM Object instances.

If you want to do this yourself, The RMObjectCreator creates empty reference model objects based on constraints. It can also set values based on attribute names. You can use it to create a reference model based on an archetype and user input. To create an empty reference model based on an archetype, you could work further on this example:


```java
  public void createEmptyRMObject(CObject object) {
    RMObjectCreator creator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
    constructEmptyRMObject(ArchieRMInfoLookup.getInstance(), creator, cObject);
  }


 private RMObject constructEmptyRMObject(ModelInfoLookup lookup, RMObjectCreator creator, CObject object) {
        RMObject result = creator.create(object);
        for(CAttribute attribute: object.getAttributes()) {
            List<Object> children = new ArrayList<>();
            for(CObject childConstraint:attribute.getChildren()) {
                if(childConstraint instanceof CComplexObject) {
                    //in case of abstract classes used in the archetype, this will pick a non-abstract descendant class
                    RMObject childObject = constructEmptyRMObject(lookup, creator, childConstraint);
                    children.add(childObject);
                }
            }
            if(!children.isEmpty()) {
                //this is not BMM, but access to the actual RM implementation because that is what we need here
                RMAttributeInfo attributeInfo = lookup.getAttributeInfo(result.getClass(), attribute.getRmAttributeName());
                if(attributeInfo != null) {
                    if(attributeInfo.isMultipleValued()) {
                        creator.set(result, attribute.getRmAttributeName(), children);
                    } else if(!children.isEmpty()){
                        //set the first possible result in case of multiple children for a single valued value
                        creator.set(result, attribute.getRmAttributeName(), Lists.newArrayList(children.get(0)));
                    }
                }
            }
        }
        return result;
    }
```

Setting primitive object values works in a similar way, with ```creator.set(...)```, or by setting them explicitly on the reference model object directly.

Notice the call to ```ArchieRMInfoLookup.getInstance()```, which obtains the metadata about the reference model implementation. It can also be obtained from the result of ```BuiltinReferenceModels.getMetaModelProvider()```, or you can define your own to make Archie work with your own reference model implementation.

### Parsing JSON

Parsing Reference Model objects in JSON is done the same as with parsing JSON archetypes:

```java
String json = JacksonUtil.getObjectMapper().writeValueAsString(archetype);
RmObject rmObject = JacksonUtil.getObjectMapper().readValue(json, RmObject.class);
```

or if you already know which RM class you are expecting:

```java
Composition composition = JacksonUtil.getObjectMapper().readValue(compositionJson, Composition.class);
```

### Reference model APath queries

```java
List<Object> items = rmObject.itemsAtPath("/data[id2]/items");
```

Or if you want apath-expressions resolving to that single item together with every object returned, you can use the low-level method:

```java
List<RMObjectWithPath> itemsWithUniquePaths = new RMPathQuery("/data[id2]/items").findList(ArchieRMInfoLookup.getInstance(), rmObject);
```

## Validating RM Objects against an archetype

The RMObjectValidator validates RMObjects against an archetype. It validates:

- cardinality
- occurrences
- existence
- data types
- primitive object constraints
- tuples

Cardinality and existence are validated both from the archetype and the underlying reference model.

To use:

```java
OperationalTemplate operationalTemplate;

RMObjectValidator validator = new RMObjectValidator(ArchieRMInfoLookup.getInstance()); //or a different ModelInfoLookup for other RMs
List<RMObjectValidationMessage> validationMessages = validator.validate(operationalTemplate, rmObject);
```

The RM Object validator only works on operational templates, not on differential or flattened forms of archetypes. You can create an operational template from any archetype see [Operational templates](#operational-templates).

Note that it is possible the API of the messages is still unstable and will likely change in future versions.

# Experimental features

The following features are experimental. This means its working or API will likely change somewhat in the near future, but they can already be used.

## Importing and converting ADL 1.4 files

Starting from version 0.7, Archie can import ADL 1.4 files, and convert them to ADL 2. To do so, do the following:

```java
ADL14ConversionConfiguration conversionConfiguration = new ADL14ConversionConfiguration();
ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), conversionConfiguration);

List<Archetype> archetypes = new ArrayList<>();
ADL14Parser parser = new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider());

for(String file:fileNames) {
   try(InputStream stream = new FileInputStream(file)) {
       Archetype archetype = parser.parse(stream, conversionConfiguration);
       if(parser.getErrors().hasNoErrors()) {
           archetypes.add(archetype);
       } else {
           //handle parse error
       }
   }
}

ADL2ConversionResultList resultList = converter.convert(archetypes);
for(ADL2ConversionResult adl2ConversionResult:resultList.getConversionResults()) {
   if(adl2ConversionResult.getException() == null) {
       // convertedArchetype is the ADL 2 conversion result. Additional warning messages in adl2ConversionResult.getLog()
       Archetype convertedArchetype = adl2ConversionResult.getArchetype();
   }
}
```

The converted archetypes are now in ADL 2, differential form. Note that it's a good idea to run these trough the archetype validator, especially if they are specialized archetypes. tooling for ADL 1.4 specialized archetypes often has limited validations, meaning that they often contain errors.

In case an archetype has been specialized, you will need to supply both the specialized archetype and its parent to the converter at the same time to allow for conversion to succeed, otherwise there will be an exception in the ADL2ConversionResult.

In ADL 2, at some nodes node ids are required where they are optional in ADL 1.4. As a result, the converter needs to generate new id codes. This has not yet been standardized. This is the reason you cannot yet expect all tools to generate the exact same or even entirely compatible ADL 2 archetypes. So use this tool carefully.


### Storing the conversion log for later use

One use case of this converter is to integrate it in modeling tools, so that modeling can still be ADL 1.4, but the output can be both ADL 1.4 and ADL 2. To ensure the tool generates the exact same node ids even after edits in ADL 1.4, a conversion log is generated that is output as the result of the conversion. It can be used as the input for a next conversion of the same archetypes, to ensure the same node ids are generated for the same nodes. To do so:

```
ADL2ConversionResultList resultList = converter.convert(archetypes, resultList.getConversionLog());
```

The conversion log can be serialized to a file for storage using Jackson, so it can be stored alongside the ADL 1.4 source archetypes.

### Conversion Configuration

You may have noticed an instance of `ADL14ConversionConfiguration` in the previous example. In this configuration the mapping from term codes as used in ADL 1.4 to term URIs as used in ADL 2 can be specified. See the `ConversionConfigForTest.java` file for an example on how this works, and how this can be serialized to a file, and the file `/tools/src/test/java/com/nedap/archie/adl14/configuration.json` for an example of a simple serialized configuration that contains sane defaults for snomed, loinc and openehr URIs.

If you leave the configuration empty, the converter will fall back to a default URI conversion.

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

This means you can link the RuleEvaluation to a user interface, implement a way to set paths to a value, hide or show some fields and display validations, and you have a working rule evaluation with which you can create things like score calculations and conditional fields very easily.

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

## Lower level APIs

### Archetype Validation

After parsing you can check if you archetype is valid. The parser will try to continue parsing as much as possible without throwing Exceptions. This means you will have to check the errors manually. This happens even with some syntax errors, so checking errors is important!

```java
if(!parser.getErrors().hasNoErrors()) {
    parser.getErrors().logToLogger();
}
```

The second round of checks comes after parsing, in the form of running the ArchetypeValidator:

```java
ReferenceModels models = new ReferenceModels(ArchieRMInfoLookup.getInstance());
MetaModels metaModels = new MetaModels(models, null);
ValidationResult validationResult = new ArchetypeValidator(metaModels).validate(archetype);
```

This runs all the implemented archetype validation and returns a result. This result contains the validation results, the validation results of any template overlays and a flattened version of the input archetype.

Note that it requires a MetaModels class. This contains the Metadata of the reference models used.

### Reference Model Metadata

Archetype tools require metadata about the used reference model to operate. This can be the openEHR reference model, but it can also be something else. Archie has two concepts to define this metadata: Reflection based metadata and BMM metadata. Above is a short description of how to load the built-in metadata. This should be enough for most users. However, if you want to add new reference models or reference model metadata, you need this paragraph.

The MetaModels class is an abstraction over these two types of models. Construct this, and it will automatically select the available metadata model. Note that if a BMM model is present for your archetype, it will use that if possible.

Reflection based metadata bases its metadata on an actual reference model implementation in Java. Two are included, the ArchieRMInfoLookup, for the openEHR Reference Model, and the TestRMInfoLookup, for the openEHR Test models. You can register these on a ReferenceModels class, see the code in the previous paragraph for an example

The BMM model is not based on an actual implementation, but on a file containing the metadata. The specifications of BMM are part of openEHR and can be found at http://www.openehr.org/releases/BASE/latest/docs/bmm/bmm.html.

To use, parse your own BMM files, then load the AOM Profiles.

```java
BmmRepository repository = new BmmRepository();
String[] bmmFiles = {"file1", "file2"};
for(String fileName:bmmFiles) {
    try(InputStream stream = new FileInputStream(fileName)) {
        repository.addPersistentSchema(BmmOdinParser.convert(stream));
    } catch (IOException e) {
        e.printStackTrace();
    }
}
BmmSchemaConverter converter = new BmmSchemaConverter(repo);
converter.validateAndConvertRepository();

MetaModels models = new MetaModels(new ReferenceModels(ArchieRMInfoLookup.getInstance()), repository);

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

This instantiates a MetaModels class that has both the archie reference model implementation, the BMM models and AOM profiles. The BMM models and AOM profiles will be used for the flattener and archetype validator, the other models for tools that work on reference models.

After validating and converting, the BmmRepository allows access to the PBmmSchema, the BmmModels, all validation messages and more, through the BmmValidationResult class. See Javadoc for more information.B

### BMM and P_BMM implementation

A BMM and P_BMM implementation is present. See the previous paragraph on how to parse P_BMM and convert it to Bmm, using the BmmRepository and BmmSchemaConverter.
BMM Version 2.2 is implemented, plus P_BMM version 2.3.0. There are some changes with regards to the BMM documentation:

- The archie P_BMM implementation does not maintain any state regarding the conversion to BMM, only the persisted fields
- BMM_SCHEMA and REFERENCE_MODEL_ACCESS are not used. Instead of BmmSchema there is BmmValidationResult, instead of ReferenceModelAccess there is BmmRepository
- BmmSchemaDescriptor and PBmmSchemaDescriptor is not used, without direct replacement

These changes have been made to simplify the implementation of Bmm. There should be a direct replacement of all functionality.

Note that archie still contains a deprecated older P_BMM implementation which conforms directly with the specifications. It will be removed in a future version.


### Serializing P_BMM

The P_BMM has a direct binding to Jackson. To serialize to ODIN:

```java
PBmmSchema schema = ... ;
String result = BmmOdinSerializer.serialize(schema);
```

Because the implementation is bound to Jackson, it can also be easily serialized and parsed to JSON and YAML. Use ```BmmJacksonUtil.getObjectMapper()``` for the JSON implementation.

### Default constraints from the reference model

An archetype constrains instances of the reference model. But a reference model also has some default constraints. For example, an observation has a data field, which is a single field - not a list or set. This has implications for default cardinality and existence constraints, and for example the ```isMultiple()``` and ```isSingle()``` methods. You can apply these constraints so you get them set explicitly in the resulting archetype-object. To do so:

```java
Archetype archetype = ADLParser.withRMConstraintsImposer().parse(adlFile);
```

Or use the RMConstraintsImposer class yourself instead of directly from the parser. Generally this should not be required if you use the FullArchetypeRepository.


### Use a different reference model implementation

Archie uses its own reference model by default, but it can use any reference model you like. To do so, you can implement your own Constraints imposer, your own reference model info lookup and some other classes, and use those in the right places. To do so, create an implementation of each of the following abstract classes and interfaces:

- ModelNamingStrategy
- ModelInfoLookup

## Status

The project is used in production in an EHR systems and completely or nearly completely implements the standards for all described features. Of course there are many parts that can be extended and improved.

## Contributions

Feel free to create a pull request or use the github issue tracker to report issues.

## License

This software is licensed under the Apache license. See the LICENSE and NOTICE files.
