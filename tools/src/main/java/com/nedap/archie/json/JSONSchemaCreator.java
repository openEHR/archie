package com.nedap.archie.json;


import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmContainerType;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmOpenType;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmSimpleType;
import org.openehr.bmm.core.BmmType;
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
        Map<String, Object> config = new HashMap();
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
            if (!bmmClass.isAbstract() && !primitiveTypeMapping.containsKey(bmmClass.getTypeName().toLowerCase())) {
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
        String typeName = BmmDefinitions.typeNameToClassKey(bmmClass.getTypeName());

        BmmClass flatBmmClass = bmmClass.flattenBmmClass();
        JsonArrayBuilder required = jsonFactory.createArrayBuilder();
        JsonObjectBuilder properties = jsonFactory.createObjectBuilder();

        boolean atLeastOneProperty = false;
        for (String propertyName : flatBmmClass.getProperties().keySet()) {
            BmmProperty bmmProperty = flatBmmClass.getProperties().get(propertyName);
            if(bmmProperty.getComputed()) {
                continue;//don't output this
            } else if((bmmClass.getTypeName().startsWith("POINT_EVENT") || bmmClass.getTypeName().startsWith("INTERVAL_EVENT")) &&
                    propertyName.equalsIgnoreCase("data")) {
                //we don't handle generics yet, and it's very tricky with the current BMM indeed. So, just manually hack this
                JsonObjectBuilder propertyDef = createPolymorphicReference(bmmModel.getClassDefinition("ITEM_STRUCTURE"));
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                }
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

        properties.add("_type", jsonFactory.createObjectBuilder().add("type", "string").add("pattern", "^" + typeName + "(<.*>)?$"));
        JsonObjectBuilder definition = jsonFactory.createObjectBuilder()
                .add("type", "object")
                .add("required", required)
                .add("properties", properties);

        if(!allowAdditionalProperties && atLeastOneProperty) {
            definition.add("additionalProperties", false);
        }
        definitions.add(typeName, definition);
    }

    private void extendPropertyDef(JsonObjectBuilder propertyDef, BmmProperty bmmProperty) {
        if(bmmProperty instanceof BmmContainerProperty) {
            BmmContainerProperty containerProperty = (BmmContainerProperty) bmmProperty;
            if(containerProperty.getCardinality() != null && containerProperty.getCardinality().getLower() > 0) {
                propertyDef.add("minItems", containerProperty.getCardinality().getLower());
            }
        }
    }

    private JsonObjectBuilder createPropertyDef(BmmType type) {


        if(type instanceof BmmOpenType) {
            return createType("object");
            //nothing more to be done
        } else if (type instanceof BmmSimpleType) {
            if(isJSPrimitive(type)) {
                return getJSPrimitive(type);
            } else {
                return createPolymorphicReference(type.getBaseClass());
            }
        } else if (type instanceof BmmContainerType) {

            BmmContainerType containerType = (BmmContainerType) type;
            return jsonFactory.createObjectBuilder()
                .add("type", "array")
                .add("items", createPropertyDef(containerType.getBaseType()));
        } else if (type instanceof BmmGenericType) {
            if(isJSPrimitive(type)) {
                return getJSPrimitive(type);
            } else {
                return createPolymorphicReference(type.getBaseClass());
            }

        }
        throw new IllegalArgumentException("type must be a BmmType, but was " + type.getClass().getSimpleName());

    }

    private JsonObjectBuilder createPolymorphicReference(BmmClass type) {

        List<String> descendants = getAllNonAbstractDescendants( type);
        if(!type.isAbstract()) {
            descendants.add(BmmDefinitions.typeNameToClassKey(type.getTypeName()));
        }

        if(descendants.isEmpty()) {
            //this is an object of which only an abstract class exists.
            //it cannot be represented as standard json, one would think. this is mainly access control and authored
            //resource in the RM
            return createType("object");
        } else if (descendants.size() > 1) {
            JsonArrayBuilder array = jsonFactory.createArrayBuilder();
            array.add(createRequiredArray("_type"));
            for(String descendant:descendants) {
                JsonObjectBuilder typePropertyCheck = createConstType(descendant);
                JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);

                JsonObjectBuilder typeReference = createReference(descendant);
                //IF the type matches
                //THEN check the correct type from the definitions
                JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
                        .add("if", typeCheck)
                        .add("then", typeReference);
                array.add(ifObject);

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
            if(!bmmClass.getTypeName().equalsIgnoreCase(desc)) {//TODO: fix getImmediateDescendants in BMM so this check is not required
                BmmClass classDefinition = bmmModel.getClassDefinition(desc);
                if (!classDefinition.isAbstract()) {
                    result.add(BmmDefinitions.typeNameToClassKey(classDefinition.getTypeName()));
                }
                result.addAll(getAllNonAbstractDescendants(classDefinition));
            }
        }
        return result;
    }

    private boolean isJSPrimitive(BmmType bmmType) {
        return primitiveTypeMapping.containsKey(bmmType.getTypeName().toLowerCase());
    }

    private JsonObjectBuilder getJSPrimitive(BmmType bmmType) {
        return primitiveTypeMapping.get(bmmType.getTypeName().toLowerCase()).get();
    }

    private JsonObjectBuilder createConstType(String rootType) {

        return jsonFactory.createObjectBuilder()
                .add("_type", jsonFactory.createObjectBuilder()
                    .add("type", "string").add("pattern", "^" + rootType + "(<.*>)?$")
                    //.add("const", rootType)
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
