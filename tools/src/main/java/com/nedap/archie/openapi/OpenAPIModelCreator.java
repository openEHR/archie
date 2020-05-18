package com.nedap.archie.openapi;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.openehr.bmm.core.*;
import org.openehr.bmm.persistence.validation.BmmDefinitions;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class OpenAPIModelCreator {


    public static final PropertyNamingStrategy.UpperCamelCaseStrategy camelCaseStrategy = new PropertyNamingStrategy.UpperCamelCaseStrategy();
    private Map<String, Supplier<JsonObjectBuilder>> primitiveTypeMapping;
    private Set<String> ignoredTypes;
    private String baseType;//'any'
    private List<String> rootTypes;
    private Map<String, List<String>> ancestorOverrides;
    private BmmModel bmmModel;
    private final JsonBuilderFactory jsonFactory;
    private String typeNameProperty = "_type";

    private boolean allowAdditionalProperties;


    public OpenAPIModelCreator() {
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

        ancestorOverrides = new LinkedHashMap<>();
        ancestorOverrides.put("DV_INTERVAL", Lists.newArrayList("DATA_VALUE", "INTERVAL"));

        baseType = "Any";
        ignoredTypes = Sets.newHashSet("ORDERED", "HASH", "CONTAINER", "SET", "ARRAY", "LIST",
                "DATE_TIME", "DATE", "TIME", "ISO8601_TYPE", "TEMPORAL", "NUMERIC", "ORDERED_NUMERIC");




        Map<String, Object> config = new HashMap();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        jsonFactory = Json.createBuilderFactory(config);
    }

    public JsonObject create(BmmModel bmm) {
        this.bmmModel = bmm;

        //create the definitions and the root if/else base

        JsonArrayBuilder allOfArray = jsonFactory.createArrayBuilder();
        JsonObjectBuilder definitions = jsonFactory.createObjectBuilder();


        allOfArray.add(createRequiredArray(typeNameProperty));

        //for every root type, if the type is right, check that type
        //anyof does more or less the same, but this is faster plus it gives MUCH less errors!
//        for(String rootType:rootTypes) {
//
//            JsonObjectBuilder typePropertyCheck = createConstType(rootType);
//            JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);
//
//            JsonObjectBuilder typeReference = createReference(rootType);
//            //IF the type matches
//            //THEN check the correct type from the definitions
//            JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
//                    .add("if", typeCheck)
//                    .add("then", typeReference);
//            allOfArray.add(ifObject);
//        }
        for(BmmClass bmmClass: bmm.getClassDefinitions().values()) {
            if (shouldClassBeIncluded(bmmClass)) {
                addClass(definitions, bmmClass);
            }
        }
        return jsonFactory.createObjectBuilder()
                .add("openapi", "3.0.1")
//        openapi: 3.0.0
//        info:
//        title: Sample API
//        description: Optional multiline or single-line description in [CommonMark](http://commonmark.org/help/) or HTML.
//        version: 0.1.9
                .add("info", jsonFactory.createObjectBuilder()
                        .add("title", "OpenEHR API stub")
                        .add("description", "empty OpenAPI model file for RM 1.0.4")
                        .add("contact", jsonFactory.createObjectBuilder().add("name", "Pieter Bos").add("email", "pieter.bos@nedap.com"))
                        .add("version", "0.0.1")
                )
                .add("paths", jsonFactory.createObjectBuilder()
                        .add("/something", jsonFactory.createObjectBuilder()
                                .add("get", jsonFactory.createObjectBuilder()
                                        .add("summary", "something")
                                        .add("description", "something describes")
                                        .add("operationId", "getDescription")
                                        .add("responses", jsonFactory.createObjectBuilder()
                                                .add("default", jsonFactory.createObjectBuilder()
                                                    .add("description", "default")
                                                    .add("content", jsonFactory.createObjectBuilder()
                                                        .add("application/json", jsonFactory.createObjectBuilder()
                                                            .add("schema", createReference("COMPOSITION"))
                                                        )
                                                    )
                                                )
                                        )
                                )
                        )
                )
                .add("components",
                        jsonFactory.createObjectBuilder().add("schemas", definitions)
                ).build();
//                .add("$schema", "http://json-schema.org/draft-07/schema")
//                .add("allOf", allOfArray)
//                .add("definitions", definitions)
//                .build();
    }

    private boolean shouldClassBeIncluded(BmmClass bmmClass) {
        return !ignoredTypes.contains(bmmClass.getClassKey()) && !primitiveTypeMapping.containsKey(bmmClass.getTypeName().toLowerCase());
    }

    private void addClass(JsonObjectBuilder definitions, BmmClass bmmClass) {
        String originalTypeName = bmmClass.getClassKey();
        String typeName = convertTypeName(originalTypeName);

        BmmClass flatBmmClass = bmmClass; //NO flattening!
        JsonArrayBuilder required = jsonFactory.createArrayBuilder();
        JsonObjectBuilder properties = jsonFactory.createObjectBuilder();

        boolean hasRequiredProperties = false;
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
                    hasRequiredProperties = true;
                }
                atLeastOneProperty = true;
            } else {

                JsonObjectBuilder propertyDef = createTypeDef(bmmProperty, bmmProperty.getType());
                extendPropertyDef(propertyDef, bmmProperty);
                properties.add(propertyName, propertyDef);

                if (bmmProperty.getMandatory()) {
                    required.add(propertyName);
                    hasRequiredProperties = true;
                }
                atLeastOneProperty = true;
            }
        }

        //properties.add("_type", jsonFactory.createObjectBuilder().add("type", "string"));//.add("pattern", "^" + typeName + "(<.*>)?$"));
        JsonObjectBuilder definition = jsonFactory.createObjectBuilder()
                .add("type", "object");
        if(hasRequiredProperties) {
            definition.add("required", required);
        }


        if(!allowAdditionalProperties && atLeastOneProperty) {
            definition.add("additionalProperties", false);
        }
        boolean addedAncestors = false;
        JsonArrayBuilder polymorphicArray = jsonFactory.createArrayBuilder();
        if(ancestorOverrides.containsKey(originalTypeName)) {
            List<String> ancestors = ancestorOverrides.get(originalTypeName);
            for (String ancestor : ancestors) {
                //no ignore support for ancestor overrides, just do it right here
                polymorphicArray.add(createReference(ancestor));
                addedAncestors = true;
            }
        } else {
            for (BmmClass ancestor : bmmClass.getAncestors().values()) {
                if (!shouldAncestorBeIgnored(ancestor)) {
                    polymorphicArray.add(createReference(ancestor.getClassKey()));
                    addedAncestors = true;
                }
            }
        }
        if(addedAncestors) {
            definition.add("properties", properties);
            polymorphicArray.add(definition);
            definitions.add(typeName, jsonFactory.createObjectBuilder().add("allOf", polymorphicArray).build());
        } else {
            if(!bmmClass.getImmediateDescendants().isEmpty()) {
                JsonObjectBuilder descendantsMappings = jsonFactory.createObjectBuilder();

                Map<String, BmmClass> allDescendants = bmmModel.getAllDescendantClassObjects(bmmClass);
                for(BmmClass descendant:allDescendants.values()) {
                    if(shouldClassBeIncluded(descendant)) {
                        descendantsMappings.add(descendant.getClassKey(), createReferenceTarget(descendant.getClassKey()));
                    }
                }
                properties.add("_type", createType("string"));
                definition.add("discriminator", jsonFactory.createObjectBuilder()
                        .add("propertyName", "_type")
                        .add("mapping",descendantsMappings)
                );
            }
            definition.add("properties", properties);
            definitions.add(typeName, definition);
        }
    }

    private boolean shouldAncestorBeIgnored(BmmClass bmmClass) {
        return //bmmClass.getTypeName().equalsIgnoreCase(baseType)  ||
                isJSPrimitive(bmmClass) || this.ignoredTypes.contains(bmmClass.getClassKey().toUpperCase());
    }


    private void extendPropertyDef(JsonObjectBuilder propertyDef, BmmProperty bmmProperty) {
        if(bmmProperty instanceof BmmContainerProperty) {
            BmmContainerProperty containerProperty = (BmmContainerProperty) bmmProperty;
            if(containerProperty.getCardinality() != null && containerProperty.getCardinality().getLower() > 0) {
                propertyDef.add("minItems", containerProperty.getCardinality().getLower());
            }
        }
    }

    private JsonObjectBuilder createTypeDef(BmmProperty property, BmmType type) {


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
            String containerTypeName = BmmDefinitions.typeNameToClassKey(containerType.getContainerType().getTypeName());
            if(containerTypeName.equalsIgnoreCase("Hash")) {
                return jsonFactory.createObjectBuilder()
                        .add("type", "object")
                        .add("additionalProperties", createTypeDef(property,  containerType.getBaseType())
                        );
            } else {
                return jsonFactory.createObjectBuilder()
                        .add("type", "array")
                        .add("items", createTypeDef(null, containerType.getBaseType()));
            }
        } else if (type instanceof BmmGenericType) {
            if(property != null && property instanceof BmmGenericProperty && BmmDefinitions.typeNameToClassKey(type.getTypeName()).equalsIgnoreCase("HASH")) {
                //a hash! Create an object with additionalProperties: type: whatever this thing points at
                BmmGenericProperty genericProperty = (BmmGenericProperty) property;
                if(genericProperty.getGenericTypeDef().getGenericParameters() != null &&
                        genericProperty.getGenericTypeDef().getGenericParameters().size() >= 2) {
                    BmmType hashValueType = genericProperty.getGenericTypeDef().getGenericParameters().get(1);
                    return jsonFactory.createObjectBuilder()
                            .add("type", "object")
                            .add("additionalProperties", createTypeDef(property, hashValueType)
                            );
                }
            } else {
                if (isJSPrimitive(type)) {
                    return getJSPrimitive(type);
                } else {
                    return createPolymorphicReference(type.getBaseClass());
                }
            }

        }
        throw new IllegalArgumentException("type must be a BmmType, but was " + type.getClass().getSimpleName());

    }

    private JsonObjectBuilder createPolymorphicReference(BmmClass type) {

        List<String> descendants = getAllNonAbstractDescendants( type);
        if(!type.isAbstract()) {
            descendants.add(type.getClassKey());
        }

        if(descendants.isEmpty()) {
            //this is an object of which only an abstract class exists.
            //it cannot be represented as standard json, one would think. this is mainly access control and authored
            //resource in the RM
            return createType("object");
        } else if (descendants.size() > 1) {
//            jsonFactory.createObjectBuilder().add("oneOf", )
//            JsonArrayBuilder array = jsonFactory.createArrayBuilder();
//            array.add(createRequiredArray("_type"));
//            for(String descendant:descendants) {
//                JsonObjectBuilder typePropertyCheck = createConstType(descendant);
//                JsonObjectBuilder typeCheck = jsonFactory.createObjectBuilder().add("properties", typePropertyCheck);
//
//                JsonObjectBuilder typeReference = createReference(descendant);
//                //IF the type matches
//                //THEN check the correct type from the definitions
//                JsonObjectBuilder ifObject = jsonFactory.createObjectBuilder()
//                        .add("if", typeCheck)
//                        .add("then", typeReference);
//                array.add(ifObject);
//
//            }
//
//
//            return jsonFactory.createObjectBuilder().add("allOf", array);
            return createReference(type.getClassKey());
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
                    result.add(classDefinition.getClassKey());
                }
                result.addAll(getAllNonAbstractDescendants(classDefinition));
            }
        }
        return result;
    }

    private boolean isJSPrimitive(BmmType bmmType) {
        return primitiveTypeMapping.containsKey(bmmType.getTypeName().toLowerCase());
    }

    private boolean isJSPrimitive(BmmClass bmmClass) {
        return primitiveTypeMapping.containsKey(bmmClass.getTypeName().toLowerCase());
    }


    private JsonObjectBuilder getJSPrimitive(BmmType bmmType) {
        return primitiveTypeMapping.get(bmmType.getTypeName().toLowerCase()).get();
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
        if(isJSPrimitive(rootType)) {
            return this.primitiveTypeMapping.get(rootType.toLowerCase()).get();
        }
//        if(rootType.equalsIgnoreCase("Any")) {
//            return jsonFactory.createObjectBuilder().add("type", "object");
//        }
        return jsonFactory.createObjectBuilder().add("$ref", createReferenceTarget(rootType));
    }

    private String createReferenceTarget(String typeName) {
        return "#/components/schemas/" + convertTypeName(typeName);
    }

    private boolean isJSPrimitive(String rootType) {
        return this.primitiveTypeMapping.containsKey(rootType.toLowerCase());
    }

    public OpenAPIModelCreator allowAdditionalProperties(boolean allowAdditionalProperties) {
        this.allowAdditionalProperties = allowAdditionalProperties;
        return this;
    }

    /**
     * Converts OpenEHR type naming to UpperCamelCase, as it should be for OpenAPI specs
     * @param typeName
     * @return
     */
    public String convertTypeName(String typeName) {
        switch(typeName) {
            case "DV_EHR_URI":
                return "DvEHRURI";
            case "UID_BASED_ID":
                return "UIDBasedId";
            default:
                return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, typeName);
        }
    }

}
