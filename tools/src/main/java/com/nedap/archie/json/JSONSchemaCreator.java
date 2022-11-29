package com.nedap.archie.json;


import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.stream.JsonGenerator;

import java.util.*;
import java.util.function.Supplier;

public class JSONSchemaCreator {

    public static final String JSON_SCHEMA_FILE_EXTENSION = ".json";
    private Map<String, Supplier<JsonObjectBuilder>> primitiveTypeMapping;
    private List<String> rootTypes;
    private BmmModel bmmModel;
    private final JsonBuilderFactory jsonFactory;
    private Set<String> ignoredAttributes;
    private Set<String> ignoredClasses;

    private boolean fullReferences = false;

    /**
     * Whether to allow any additional properties in the openEHR json.
     */
    private boolean allowAdditionalProperties;

    private JsonSchemaUriProvider uriProvider = new SingleFileNameUriProvider();
    private String baseUri = "https://specifications.openehr.org/releases/ITS-JSON/latest/components/RM/Release-1.1.0/";


    public JSONSchemaCreator() {
        primitiveTypeMapping = new HashMap<>();
        primitiveTypeMapping.put("integer", () -> createType("integer"));
        primitiveTypeMapping.put("integer64", () -> createType("integer"));
        primitiveTypeMapping.put("boolean", () -> createType("boolean"));
        primitiveTypeMapping.put("real", () -> createType("number"));
        primitiveTypeMapping.put("double", () -> createType("number"));
        primitiveTypeMapping.put("octet", () -> createType("string"));//well, base64...
        primitiveTypeMapping.put("byte", () -> createType("string"));
        primitiveTypeMapping.put("character", () -> createType("string"));
        primitiveTypeMapping.put("hash", () -> createType("object"));
        primitiveTypeMapping.put("string", () -> createType("string"));
        primitiveTypeMapping.put("uri", () -> createType("string").add("format", "uri-reference"));
        primitiveTypeMapping.put("iso8601_date", () -> createType("string").add("format", "date"));
        primitiveTypeMapping.put("iso8601_date_time", () -> createType("string").add("format", "date-time"));
        primitiveTypeMapping.put("iso8601_time", () -> createType("string").add("format", "time"));
        primitiveTypeMapping.put("iso8601_duration", () -> createType("string"));
        primitiveTypeMapping.put("proportion_kind", () -> createType("integer"));//TODO: proper enum support

        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        jsonFactory = Json.createBuilderFactory(config);
    }

    /**
     * Creates one or more JSON Schemas from a BMM model. Depending on configuration, creates a file per BMM source schema id
     * , package, or just one file.
     * the main schema, containing the entry point, will always be the first file in the returned list
     *
     * @param bmm the BMM model to create the schema for
     * @return a map, keyed by file name, of JsonObjects containing the json schemas
     */
    public Map<JsonSchemaUri, JsonObject> create(BmmModel bmm) {
        this.bmmModel = bmm;
        JsonSchemaUri mainFileName = this.uriProvider.provideMainFileUri();
        //create the definitions and the root if/else base

        JsonArrayBuilder allOfArray = jsonFactory.createArrayBuilder();

        allOfArray.add(createRequiredArray("_type"));

        //for every root type, if the type is right, check that type
        //anyof does more or less the same, but this is faster plus it gives MUCH less errors!
        for(String rootType:rootTypes) {

            JsonObjectBuilder typePropertyCheck = createConstType(rootType);
            JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);

            JsonObjectBuilder typeReference = createRootlevelReference(mainFileName, rootType);
            //IF the type matches
            //THEN check the correct type from the definitions
            JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
                    .add("if", typeCheck)
                    .add("then", typeReference);
            allOfArray.add(ifObject);
        }

        Map<JsonSchemaUri, SchemaBuilder> schemaBuilders = new LinkedHashMap<>();

        SchemaBuilder mainSchemaBuilder = new SchemaBuilder(mainFileName);
        schemaBuilders.put(mainFileName, mainSchemaBuilder);
        mainSchemaBuilder.getSchema().add("allOf", allOfArray);

        for(BmmClass bmmClass: bmm.getClassDefinitions().values()) {
            if (!bmmClass.isAbstract() &&
                    !primitiveTypeMapping.containsKey(bmmClass.getName().toLowerCase()) &&
                !ignoredClasses.contains(bmmClass.getName().toLowerCase())) {
                SchemaBuilder schema = getOrCreateDefinitions(schemaBuilders, bmmClass);
                schema.getDefinitions().add(BmmDefinitions.typeNameToClassKey(bmmClass.getName()), createClass(bmmClass));
            }
        }

        Map<JsonSchemaUri, JsonObject> result = new LinkedHashMap<>();
        //put the main schema first
        for(SchemaBuilder schemaBuilder:schemaBuilders.values()) {
            result.put(schemaBuilder.getUri(), schemaBuilder.build());
        }

        return result;
    }

    private SchemaBuilder getOrCreateDefinitions(Map<JsonSchemaUri, SchemaBuilder> result, BmmClass bmmClass) {
        JsonSchemaUri uri = uriProvider.provideJsonSchemaUrl(bmmClass);
        SchemaBuilder schemaBuilder = result.get(uri);
        if(schemaBuilder == null) {
            //file not yet found. Create it
            schemaBuilder = new SchemaBuilder(uri);
            result.put(uri, schemaBuilder);
        }
        return schemaBuilder;

    }

    private JsonObjectBuilder createClass(BmmClass bmmClass) {
        String bmmClassName = bmmClass.getName();
        String typeName = BmmDefinitions.typeNameToClassKey(bmmClassName);

        JsonArrayBuilder required = jsonFactory.createArrayBuilder();
        JsonObjectBuilder properties = jsonFactory.createObjectBuilder();

        boolean atLeastOneProperty = false;
        Map<String, BmmProperty<?>> flatProperties = bmmClass.getFlatProperties();
        for (String propertyName : flatProperties.keySet()) {
            BmmProperty<?> bmmProperty = flatProperties.get(propertyName);
            if(bmmProperty.getComputed()) {
                continue;//don't output this
            } else if (ignoredAttributes.contains(typeName + "." + propertyName)) {
                continue; //don't output this either
            } if((typeName.equalsIgnoreCase("POINT_EVENT") || typeName.equalsIgnoreCase("INTERVAL_EVENT")) &&
                    propertyName.equalsIgnoreCase("data")) {
                //we don't handle generics yet, and it's very tricky with the current BMM indeed. So, just manually hack this
                JsonObjectBuilder propertyDef = createPolymorphicReference(bmmClass, bmmModel.getClassDefinition("ITEM_STRUCTURE"));
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                }
                atLeastOneProperty = true;
            } else if ((typeName.equalsIgnoreCase("DV_URI") || typeName.equalsIgnoreCase("DV_EHR_URI")) && propertyName.equalsIgnoreCase("value")) {
                JsonObjectBuilder propertyDef = createPropertyDef(bmmClass, bmmProperty.getType());
                propertyDef.add("format", "uri-reference");
                properties.add(propertyName, propertyDef);
                required.add(propertyName);
                atLeastOneProperty = true;
            } else {
                JsonObjectBuilder propertyDef = createPropertyDef(bmmClass, bmmProperty.getType());
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                }
                atLeastOneProperty = true;
            }
        }

        properties.add("_type", jsonFactory.createObjectBuilder().add("type", "string").add("const",  typeName ));
        JsonObjectBuilder definition = jsonFactory.createObjectBuilder()
                .add("type", "object")
                .add("required", required)
                .add("properties", properties);

        if(bmmClass.getDocumentation() != null) {
            definition.add("description", bmmClass.getDocumentation());
        }

        if(!allowAdditionalProperties && atLeastOneProperty) {
            definition.add("additionalProperties", false);
        }
        return definition;
    }

    private void extendPropertyDef(JsonObjectBuilder propertyDef, BmmProperty<?> bmmProperty) {
        if(bmmProperty instanceof BmmContainerProperty) {
            BmmContainerProperty containerProperty = (BmmContainerProperty) bmmProperty;
            if(containerProperty.getCardinality() != null && containerProperty.getCardinality().getLower() > 0) {
                propertyDef.add("minItems", containerProperty.getCardinality().getLower());
            }
        }
        if(bmmProperty.getDocumentation() != null) {
            propertyDef.add("description", bmmProperty.getDocumentation());
        }
    }

    private JsonObjectBuilder createPropertyDef(BmmClass classContainingProperty, BmmType type) {

        if (type instanceof BmmParameterType) {
            return createType("object");
            //nothing more to be done
        } else if (type instanceof BmmSimpleType) {
            BmmSimpleType simpleType = (BmmSimpleType) type;
            if (isJSPrimitive(type)) {
                return getJSPrimitive(simpleType);
            } else {
                return createPolymorphicReference(classContainingProperty, simpleType.getBaseClass());
            }
        } else if (type instanceof BmmContainerType) {
            BmmContainerType containerType = (BmmContainerType) type;
            if(containerType.getBaseType().getTypeName().equalsIgnoreCase("Octet")) {
                //binary data will be base64 encoded, so express that here
                JsonObjectBuilder string = createType("string");
                string.add("contentEncoding", "base64");
                return string;
            } else  if (containerType.getContainerType().getName().equalsIgnoreCase("hash")) {
                return getHash(classContainingProperty, containerType);
            }
            return jsonFactory.createObjectBuilder()
                .add("type", "array")
                .add("items", createPropertyDef(classContainingProperty, containerType.getBaseType()));
        } else if (type instanceof BmmGenericType) {
            BmmGenericType genericType = (BmmGenericType) type;
            if (genericType.getBaseClass().getName().equalsIgnoreCase("hash")) {
                return getHash(classContainingProperty, genericType);
            } else if (isJSPrimitive(genericType)) {
                return getJSPrimitive(genericType);
            } else {
                return createPolymorphicReference(classContainingProperty, genericType.getBaseClass());
            }

        }
        throw new IllegalArgumentException("type must be a BmmType, but was " + type.getClass().getSimpleName());

    }

    private JsonObjectBuilder getHash(BmmClass classContainingProperty, BmmContainerType containerType) {
        BmmType valueType = containerType.getBaseType();
        JsonObjectBuilder object = createType("object");
        object.add("additionalProperties", createPropertyDef(classContainingProperty, valueType));
        return object;
    }

    private JsonObjectBuilder getHash(BmmClass classContainingProperty, BmmGenericType genericType) {
        if(genericType.getGenericParameters().size() == 2) {
            BmmType keyType = genericType.getGenericParameters().get(0);
            BmmType valueType = genericType.getGenericParameters().get(1);
            JsonObjectBuilder object = createType("object");
            object.add("additionalProperties", createPropertyDef(classContainingProperty, valueType));
            return object;
        } else {
            return getJSPrimitive(genericType);
        }
    }

    /**
     * Create a reference to a given type, plus all its descendants.
     * @param classContainingTypeReference the class containing the type reference - used to check to refer to just #definitions, or prepended with a filename
     * @param type the type to refer to
     * @return the json schema that is a reference to this type, plus all of its descendants
     */
    private JsonObjectBuilder createPolymorphicReference(BmmClass classContainingTypeReference, BmmClass type) {

        List<String> descendants = getAllNonAbstractDescendants( type);
        //if the type to refer to is abstract, a _type field is required, because there is no class to fall back on
        //if the type to refer to is concrete, a _type field is not required. If it is missing,
        //the concrete type should be used instead
        //this boolean indicates that difference.
        boolean isConcreteType = false;
        if(!type.isAbstract()) {
            descendants.add(BmmDefinitions.typeNameToClassKey(type.getName()));
            isConcreteType = true;
        }

        boolean genericType = type instanceof BmmGenericClass;
        for(String descendant:descendants) {
            if(bmmModel.getClassDefinition(descendant) instanceof BmmGenericClass) {
                genericType = true; // it would be better to generate either an enum OR a couple of patterns, but not now
            }
        }

        if(descendants.isEmpty()) {
            //this is an object of which only an abstract class exists.
            //it cannot be represented as standard json, one would think. this is mainly access control and authored
            //resource in the RM
            return createType("object");
        } else if (descendants.size() > 1) {
            JsonArrayBuilder array = jsonFactory.createArrayBuilder();

            //if an abstract type, _type is required
            JsonObjectBuilder requiredType = isConcreteType?
                    jsonFactory.createObjectBuilder() :
                    createRequiredArray("_type");

            if(!genericType) {
                JsonObjectBuilder typeDefinition = jsonFactory.createObjectBuilder()
                        .add("type", "string");

                JsonArrayBuilder enumValues = jsonFactory.createArrayBuilder();
                for (String descendant : descendants) {
                    enumValues.add(descendant);
                }
                typeDefinition.add("enum", enumValues);
                JsonObjectBuilder typePropertyCheck = jsonFactory.createObjectBuilder()
                        .add("_type", typeDefinition);
                requiredType.add("properties", typePropertyCheck);
            }
            array.add(requiredType);

            for(String descendant:descendants) {
                JsonObjectBuilder typePropertyCheck = createConstType(descendant);
                JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);
                if(isConcreteType) {
                    //inside the if-block, make type required, or it will match this block if _type is not present
                    typeCheck.addAll(createRequiredArray("_type"));
                }

                JsonObjectBuilder typeReference = createReference(classContainingTypeReference, descendant);
                //IF the type matches
                //THEN check the correct type from the definitions
                JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
                        .add("if", typeCheck)
                        .add("then", typeReference);
                array.add(ifObject);

            }

            if(isConcreteType) {
                //fallback to the base type if it is concrete, and not if it is abstract
                JsonObjectBuilder elseObject = jsonFactory.createObjectBuilder()
                        .add("if", jsonFactory.createObjectBuilder().add("not", createRequiredArray("_type")))
                        .add("then", createReference(classContainingTypeReference, type.getName()));
                array.add(elseObject);
            }

            return jsonFactory.createObjectBuilder().add("allOf", array);
        } else {
            return createReference(classContainingTypeReference, descendants.get(0));
        }

    }


    private List<String> getAllNonAbstractDescendants(BmmClass bmmClass) {
        List<String> result = new ArrayList<>();
        List<String> descs = bmmClass.getImmediateDescendants();
        for(String desc:descs) {
            if(!bmmClass.getName().equalsIgnoreCase(desc)) {//TODO: fix getImmediateDescendants in BMM so this check is not required
                BmmClass classDefinition = bmmModel.getClassDefinition(desc);
                if (!classDefinition.isAbstract()) {
                    result.add(BmmDefinitions.typeNameToClassKey(classDefinition.getName()));
                }
                result.addAll(getAllNonAbstractDescendants(classDefinition));
            }
        }
        return result;
    }

    private boolean isJSPrimitive(BmmType bmmType) {
        return primitiveTypeMapping.containsKey(BmmDefinitions.typeNameToClassKey(bmmType.getTypeName()).toLowerCase());
    }

    private JsonObjectBuilder getJSPrimitive(BmmType bmmType) {
        return getJSPrimitive(BmmDefinitions.typeNameToClassKey(bmmType.getTypeName()).toLowerCase());
    }

    private JsonObjectBuilder getJSPrimitive(String classKey) {
        return primitiveTypeMapping.get(classKey.toLowerCase()).get();
    }

    private JsonObjectBuilder createConstType(String rootType) {

        BmmClass classDefinition = bmmModel.getClassDefinition(rootType);
        return jsonFactory.createObjectBuilder()
                .add("_type", jsonFactory.createObjectBuilder()
                        .add("const", rootType)
                );

    }

    private JsonObjectBuilder createRequiredArray(String... requiredFields) {
        JsonArrayBuilder requiredArray = jsonFactory.createArrayBuilder();
        for(String requiredProperty: requiredFields) {
            requiredArray.add(requiredProperty);
        }
        return jsonFactory.createObjectBuilder().add("required", requiredArray);
    }


    private JsonObjectBuilder createType(String jsPrimitive) {
        return jsonFactory.createObjectBuilder().add("type", jsPrimitive);
    }

    private JsonObjectBuilder createRootlevelReference(JsonSchemaUri packageFileName, String type) {
        BmmClass bmmClass = bmmModel.getClassDefinitions().get(type);
        if(bmmClass == null) {
            //TODO: a warning!
            return jsonFactory.createObjectBuilder().add("$ref", "#/definitions/" + type);
        } else {
            JsonSchemaUri typeFileName = uriProvider.provideJsonSchemaUrl(bmmClass);
            if(typeFileName == null) {
                throw new RuntimeException("The URI Provider in the json schema creator returned null; that is not allowed");
            }
            if(typeFileName.equals(packageFileName)) {
                return jsonFactory.createObjectBuilder().add("$ref", "#/definitions/" + type);
            } else {
                if(fullReferences) {
                    return jsonFactory.createObjectBuilder().add("$ref", typeFileName.getBaseUri() + typeFileName.getFilename() + "#/definitions/" + type);
                } else {
                    if (typeFileName.getBaseUri().equals(packageFileName.getBaseUri())) {
                        return jsonFactory.createObjectBuilder().add("$ref", typeFileName.getFilename() + "#/definitions/" + type);
                    } else {
                        return jsonFactory.createObjectBuilder().add("$ref", typeFileName.getId() + "#/definitions/" + type);
                    }
                }
            }
        }

    }


    private JsonObjectBuilder createReference(BmmClass classContainingReference, String type) {
        JsonSchemaUri packageFileName = classContainingReference == null ? new JsonSchemaUri("", "") : uriProvider.provideJsonSchemaUrl(classContainingReference);
        return createRootlevelReference(packageFileName, type);
    }

    public JSONSchemaCreator setRootTypes(List<String> rootTypes) {
        this.rootTypes = rootTypes;
        return this;
    }

    public JSONSchemaCreator allowAdditionalProperties(boolean allowAdditionalProperties) {
        this.allowAdditionalProperties = allowAdditionalProperties;
        return this;
    }

    public JSONSchemaCreator splitInMultipleFiles(boolean splitInPackages) {
        if(splitInPackages) {
            this.uriProvider = new BmmPackageNameUriProvider();
        } else {
            this.uriProvider = new SingleFileNameUriProvider();
        }
        return this;
    }

    public JSONSchemaCreator withJsonSchemaUriProvider(JsonSchemaUriProvider uriProvider) {
        this.uriProvider = uriProvider;
        return this;
    }

    public JSONSchemaCreator withBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public JSONSchemaCreator withFullReferences(boolean fullReferences) {
        this.fullReferences = fullReferences;
        return this;
    }

    public void setIgnoredAttributes(HashSet<String> ignoredAttributes) {
        this.ignoredAttributes = ignoredAttributes;
    }

    public void setIgnoredClasses(HashSet<String> ignoredClasses) {
        this.ignoredClasses = new HashSet<>();
        for(String ignoredClass:ignoredClasses) {
            this.ignoredClasses.add(ignoredClass.toLowerCase());
        }
    }


    private class SingleFileNameUriProvider implements JsonSchemaUriProvider {

        @Override
        public JsonSchemaUri provideJsonSchemaUrl(BmmClass clazz) {
            return new JsonSchemaUri(baseUri, clazz.getBmmModel().getSchemaId() + "_all" + JSON_SCHEMA_FILE_EXTENSION);
        }

        @Override
        public JsonSchemaUri provideMainFileUri() {
            return  new JsonSchemaUri(baseUri,
                    bmmModel.getSchemaId() + "_all" + JSON_SCHEMA_FILE_EXTENSION);
        }
    }

    private class BmmPackageNameUriProvider implements JsonSchemaUriProvider {

        @Override
        public JsonSchemaUri provideJsonSchemaUrl(BmmClass clazz) {
            return new JsonSchemaUri(baseUri,
                    clazz.getSourceSchemaId() + JSON_SCHEMA_FILE_EXTENSION);
        }

        @Override
        public JsonSchemaUri provideMainFileUri() {
            return new JsonSchemaUri(baseUri, bmmModel.getSchemaId() + JSON_SCHEMA_FILE_EXTENSION);
        }
    }

    private class SchemaBuilder {
        private JsonSchemaUri uri;
        private JsonObjectBuilder schema;
        private JsonObjectBuilder definitions;

        public SchemaBuilder(JsonSchemaUri uri) {
            this.uri = uri;
            definitions = jsonFactory.createObjectBuilder();
            schema = jsonFactory.createObjectBuilder()
                    .add("$schema", "http://json-schema.org/draft-07/schema")
                    .add("$id", uri.getId());
        }

        public JsonSchemaUri getUri() {
            return uri;
        }

        public JsonObjectBuilder getSchema() {
            return schema;
        }
        public JsonObjectBuilder getDefinitions() {
            return definitions;
        }

        public JsonObject build() {
            return schema.add("definitions", definitions).build();
        }

    }
}
