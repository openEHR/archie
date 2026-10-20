# Archetypes

[Back to the README](../README.md)

Archetypes are the basis of openEHR. Archie can parse, validate, flatten, diff and serialize them.

## Parsing archetypes

```java
ADLParser parser = new ADLParser();
try {
    Archetype archetype = parser.parse(adlFile);
} catch (ADLParseException e) {
    e.getErrors().logToLogger();
    Archetype partial = e.getArchetype(); // a best effort partial result, may be null
}
```

The parser throws an `ADLParseException` when the ADL contains errors. The parser tries to continue after an error, so the exception contains all errors found, plus a partially parsed archetype when possible. This is useful for modeling tools that want to show as much as possible of an archetype with errors. By default, the parser also logs the errors. Call `parser.setLogEnabled(false)` to disable that.

## Archetype path queries

```java
AOMPathQuery query = new AOMPathQuery("/context[id1]/items[id2]/value");
ArchetypeModelObject object = query.find(archetype.getDefinition());

ArchetypeModelObject sameObject = archetype.getDefinition().itemAtPath("/context[id1]/items[id2]/value");

CAttribute attribute = archetype.getDefinition()
    .getAttribute("context").getChild("id1").getAttribute("items");
```

Path segments must use node ids, or a one-based index such as `items[2]`. Matching a segment on the
meaning of a node, as in `/context[systolic]/items`, is no longer supported. If you need to find a node
by its term text, compare the meaning yourself:

```java
CObject systolic = archetype.getDefinition()
    .getAttribute("context").getChildren().stream()
    .filter(child -> "systolic".equalsIgnoreCase(child.getMeaning()))
    .findFirst().orElse(null);
```

## Archetype repositories and validation

Archetypes are often related to other archetypes: they specialize a parent archetype, or include other archetypes. To work with a set of related archetypes, add them to an archetype repository and compile it:

```java
InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
for (Archetype archetype : allArchetypes) {
    repository.addArchetype(archetype);
}
repository.compile(BuiltinReferenceModels.getMetaModelProvider());

for (ValidationResult result : repository.getAllValidationResults()) {
    // your code here
}
```

Compiling validates all archetypes. Each `ValidationResult` lists the errors and warnings for the archetype and contains the source archetype. If validation got far enough, it also contains the flattened archetype. Flattened archetypes are also available through `repository.getFlattenedArchetype(archetypeId)`.

`BuiltinReferenceModels.getMetaModelProvider()` provides the reference model metadata that is needed to validate and flatten archetypes. See [Reference model metadata](advanced.md#reference-model-metadata) for more information.

## Operational templates

If you are building an EHR implementation, operational templates are likely the most useful form of archetypes to work with. An operational template is generated from an archetype or template. In an archetype, structures can be reused in several places and other archetypes can be embedded. The operational template replaces these references with a copy of the actual structure, so your tools do not need to resolve them. See [the openEHR OPT2 specification](https://specifications.openehr.org/releases/AM/latest/OPT2.html) for more information.

Note that ADL 2 operational templates are fundamentally different from the ADL 1.4 OET/OPT format. What used to be done with OET/OPT is now done with ADL 2 templates, in combination with operational template creation. See [the openEHR specification on templates](https://specifications.openehr.org/releases/AM/latest/ADL2.html#_templates) on how to work with them.

To create an operational template:

```java
Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider()).createOperationalTemplate(true);
OperationalTemplate template = (OperationalTemplate) flattener.flatten(sourceArchetype);
```

Pass the original, differential archetype, not a flattened version.

- A flattener can be used only once. To flatten another archetype, create a new `Flattener`.
- To keep only some languages in the terminology, call `flattener.keepLanguages("en", "nl")` before flattening. To also remove the other languages from the archetype metadata, call `flattener.removeLanguagesFromMetadata(true)`.
- Without the `createOperationalTemplate(true)` call, the flattener creates a flattened archetype instead of an operational template.

## Terminology

You can use `archetype.getTerminology()` to get the texts and descriptions of nodes in any language. Archie has some convenience methods to make this easier. You can set the language to use for the current thread, and get terms directly from the archetype constraints:

```java
ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");

CObject cobject = archetype.getDefinition().getAttribute("context").getChild("id1");
ArchetypeTerm term = cobject.getTerm();
logger.info("and the archetype term text in Dutch is: " + term.getText());
```

This works both with the terminology of a differential archetype and with the component terminologies of operational templates. Use `ArchieLanguageConfiguration.setDefaultMeaningAndDescriptionLanguage(...)` to change the default language for all threads.

You can also specify the language yourself:

```java
CObject cobject = archetype.getDefinition().getAttribute("context").getChild("id1");
ArchetypeTerm term = archetype.getTerm(cobject, "en");
logger.info("and the archetype term text in English is: " + term.getText());
```

Or access the terminology directly:

```java
archetype.getTerminology().getValueSets();
operationalTemplate.getComponentTerminologies();
```

For terminology constraints with locally defined values:

```java
CTerminologyCode cTerminologyCode = archetype.itemAtPath("/somePath...");
List<TerminologyCodeWithArchetypeTerm> terms = cTerminologyCode.getTerms();

// or for a single code:
ArchetypeTerm term = archetype.getTerm(cTerminologyCode, "at15", "en");
```

## Diffing archetypes

Diffing is the inverse of flattening. It is useful when writing a tool to edit archetypes: users can edit the flat form, which is much easier to understand, after which you store the output of the differentiator as the differential form.

```java
Archetype flatChild, flatParent; // see above for how to parse and flatten

Differentiator differentiator = new Differentiator(BuiltinReferenceModels.getMetaModelProvider());
Archetype diffed = differentiator.differentiate(flatChild, flatParent);
```

Use flattened archetypes for both the parent and the specialized archetype. Do not use operational templates.

The differentiator diffs the definition and the terminology of the archetype. It:

- adds sibling order markers to preserve the ordering of C_OBJECTs in the specialized archetype. It only adds them where necessary, and adds as few as possible
- removes default or unspecialized cardinality, occurrences and existence
- removes unspecialized C_OBJECTs, C_ATTRIBUTEs and tuples
- generates differential paths where possible
- generates a diff of the terminology, including term definitions, term bindings and value sets
- removes translations that exist in the parent archetype, but not in the specialized archetype
- removes terminology extracts

It returns a new archetype. The input archetypes are not changed.

The differentiator does not diff template overlays when given a template. You can however diff a flattened template overlay with its flat parent, and replace the template overlay in the template with the result yourself. The rules section is not diffed either.

## Walking the archetype tree

`getAttributes()` is defined on `CObject`, so walking the archetype tree takes just a few lines of code:

```java
public void walk(CObject cobject) {
    for (CAttribute attribute : cobject.getAttributes()) {
        for (CObject child : attribute.getChildren()) {
            walk(child);
        }
    }
}
```
