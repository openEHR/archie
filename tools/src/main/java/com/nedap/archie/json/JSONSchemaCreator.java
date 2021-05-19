package com.nedap.archie.json;


import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JSONSchemaCreator {

    private Map<String, Supplier<JsonObjectBuilder>> primitiveTypeMapping;
    private List<String> rootTypes;
    private BmmModel bmmModel;
    private final JsonBuilderFactory jsonFactory;

    private boolean allowAdditionalProperties;


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
        primitiveTypeMapping.put("iso8601_date", () -> createType("string").add("format", "date"));
        primitiveTypeMapping.put("iso8601_date_time", () -> createType("string").add("format", "date-time"));
        primitiveTypeMapping.put("iso8601_time", () -> createType("string").add("format", "time"));
        primitiveTypeMapping.put("iso8601_duration", () -> createType("string"));
        primitiveTypeMapping.put("proportion_kind", () -> createType("integer"));//TODO: proper enum support

        rootTypes = new ArrayList<>();
        rootTypes.add("COMPOSITION");
        rootTypes.add("OBSERVATION");
        rootTypes.add("EVALUATION");
        rootTypes.add("ACTIVITY");
        rootTypes.add("ACTION");
        rootTypes.add("SECTION");
        rootTypes.add("INSTRUCTION");
        rootTypes.add("INSTRUCTION_DETAILS");
        rootTypes.add("ADMIN_ENTRY");
        rootTypes.add("CLUSTER");
        rootTypes.add("CAPABILITY");
        rootTypes.add("PERSON");
        rootTypes.add("ADDRESS");
        rootTypes.add("ROLE");
        rootTypes.add("ORGANISATION");
        rootTypes.add("PARTY_IDENTITY");
        rootTypes.add("ITEM_TREE");
        rootTypes.add("CONTRIBUTION");
        rootTypes.add("EHR");
        rootTypes.add("EHR_STATUS");
        rootTypes.add("VERSIONED_EHR_STATUS");
        rootTypes.add("VERSIONED_COMPOSITION");
        rootTypes.add("ORIGINAL_VERSION");
        rootTypes.add("IMPORTED_VERSION");
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        jsonFactory = Json.createBuilderFactory(config);
    }

    public JsonObject create(BmmModel bmm) {
        this.bmmModel = bmm;

        //create the definitions and the root if/else base

        JsonArrayBuilder allOfArray = jsonFactory.createArrayBuilder();
        JsonObjectBuilder definitions = jsonFactory.createObjectBuilder();


        allOfArray.add(createRequiredArray("_type"));

        //for every root type, if the type is right, check that type
        //anyof does more or less the same, but this is faster plus it gives MUCH less errors!
        for(String rootType:rootTypes) {

            JsonObjectBuilder typePropertyCheck = createConstType(rootType);
            JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);

            JsonObjectBuilder typeReference = createReference(rootType);
            //IF the type matches
            //THEN check the correct type from the definitions
            JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
                    .add("if", typeCheck)
                    .add("then", typeReference);
            allOfArray.add(ifObject);
        }
        for(BmmClass bmmClass: bmm.getClassDefinitions().values()) {
            if (!bmmClass.isAbstract() && !primitiveTypeMapping.containsKey(bmmClass.getName().toLowerCase())) {
                addClass(definitions, bmmClass);
            }
        }
        return jsonFactory.createObjectBuilder()
                .add("$schema", "http://json-schema.org/draft-07/schema")
                .add("allOf", allOfArray)
                .add("definitions", definitions)
                .build();
    }

    private void addClass(JsonObjectBuilder definitions, BmmClass bmmClass) {
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
            } else if((typeName.equalsIgnoreCase("POINT_EVENT") || typeName.equalsIgnoreCase("INTERVAL_EVENT")) &&
                    propertyName.equalsIgnoreCase("data")) {
                //we don't handle generics yet, and it's very tricky with the current BMM indeed. So, just manually hack this
                JsonObjectBuilder propertyDef = createPolymorphicReference(bmmModel.getClassDefinition("ITEM_STRUCTURE"));
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                }
                atLeastOneProperty = true;
            } else if ((typeName.equalsIgnoreCase("DV_URI") || typeName.equalsIgnoreCase("DV_EHR_URI")) && propertyName.equalsIgnoreCase("value")) {
                JsonObjectBuilder propertyDef = createPropertyDef(bmmProperty.getType());
                propertyDef.add("format", "uri-reference");
                properties.add(propertyName, propertyDef);
                atLeastOneProperty = true;
            } else {
                JsonObjectBuilder propertyDef = createPropertyDef(bmmProperty.getType());
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                }
                atLeastOneProperty = true;
            }
        }

        properties.add("_type", jsonFactory.createObjectBuilder().add("type", "string").add("pattern", "^" + typeName ));
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
        definitions.add(typeName, definition);
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

    private JsonObjectBuilder createPropertyDef(BmmType type) {

        if (type instanceof BmmParameterType) {
            return createType("object");
            //nothing more to be done
        } else if (type instanceof BmmSimpleType) {
            BmmSimpleType simpleType = (BmmSimpleType) type;
            if (isJSPrimitive(type)) {
                return getJSPrimitive(simpleType);
            } else {
                return createPolymorphicReference(simpleType.getBaseClass());
            }
        } else if (type instanceof BmmContainerType) {
            BmmContainerType containerType = (BmmContainerType) type;
            if(containerType.getBaseType().getTypeName().equalsIgnoreCase("Octet")) {
                //binary data will be base64 encoded, so express that here
                JsonObjectBuilder string = createType("string");
                string.add("contentEncoding", "base64");
                return string;
            }
            return jsonFactory.createObjectBuilder()
                .add("type", "array")
                .add("items", createPropertyDef(containerType.getBaseType()));
        } else if (type instanceof BmmGenericType) {
            BmmGenericType genericType = (BmmGenericType) type;
            if (isJSPrimitive(genericType)) {
                return getJSPrimitive(genericType);
            } else {
                return createPolymorphicReference(genericType.getBaseClass());
            }

        }
        throw new IllegalArgumentException("type must be a BmmType, but was " + type.getClass().getSimpleName());

    }

    /**
     * Create a reference to a given type, plus all its descendants.
     * @param type the type to refer to
     * @return the json schema that is a reference to this type, plus all of its descendants
     */
    private JsonObjectBuilder createPolymorphicReference(BmmClass type) {

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

                JsonObjectBuilder typeReference = createReference(descendant);
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
                        .add("then", createReference(type.getName()));
                array.add(elseObject);
            }

            return jsonFactory.createObjectBuilder().add("allOf", array);
        } else {
            return createReference(descendants.get(0));
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

    private JsonObjectBuilder createReference(String rootType) {
        return jsonFactory.createObjectBuilder().add("$ref", "#/definitions/" + rootType);
    }

    public JSONSchemaCreator allowAdditionalProperties(boolean allowAdditionalProperties) {
        this.allowAdditionalProperties = allowAdditionalProperties;
        return this;
    }
}
