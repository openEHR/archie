package com.nedap.archie.openapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.nedap.archie.base.Interval;
import com.nedap.archie.json.flat.AttributeReference;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.v2.persistence.*;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelInfoLookupToPBmmConverter {

    private ModelInfoLookup modelInfoLookup;

    private boolean excludedJsonIgnoreFields;

    public boolean isExcludedJsonIgnoreFields() {
        return excludedJsonIgnoreFields;
    }

    public void setExcludedJsonIgnoreFields(boolean excludedJsonIgnoreFields) {
        this.excludedJsonIgnoreFields = excludedJsonIgnoreFields;
    }

    public PBmmSchema convert(ModelInfoLookup lookup, Set<AttributeReference> ignoredAttributes, BmmModel includedModel) {
        this.modelInfoLookup = lookup;
        PBmmSchema schema = new PBmmSchema();
        PBmmPackage pack = new PBmmPackage("default");
        Map<String, PBmmPackage> packageMap = new LinkedHashMap<>();
        packageMap.put("default", pack);
        schema.setPackages(packageMap);

        List<String> packageClasses = new ArrayList<>();

        Map<String, PBmmClass> classDefinitions = new LinkedHashMap<>();

        List<Class> foundEnums = new ArrayList<>();

        for(RMTypeInfo type:lookup.getAllTypes()) {
            if(includedModel != null && includedModel.getClassDefinition(convertTypeName(type.getRmName())) != null) {
                System.out.println("already in base:  " + type.getRmName());
                continue;
            }
            packageClasses.add(type.getRmName());
            PBmmClass bmmClass = new PBmmClass();
            bmmClass.setName(type.getRmName());
            boolean classIsAbstract = Modifier.isAbstract(type.getJavaClass().getModifiers());
            if(classIsAbstract) {
                bmmClass.setAbstract(classIsAbstract);
            }
            bmmClass.setAncestors(type.getDirectParentClasses().stream()
                        .map(t -> convertTypeName(t.getRmName())).collect(Collectors.toList())
            );
            if(bmmClass.getAncestors().isEmpty()) {
                bmmClass.setAncestors(Lists.newArrayList("Any"));
            }

            Map<String, PBmmProperty<?>> properties = new LinkedHashMap<>();
            for(RMAttributeInfo attribute:type.getAttributes().values()) {
                if(attribute.isFromAncestor() || ignoredAttributes.contains(new AttributeReference(type.getRmName(), attribute.getRmName()))) {
                    continue;
                }
                if(hasJsonIgnoreAnnotation(attribute)) {
                    continue;
                }
                if(attribute.getType().isEnum()) {
                    foundEnums.add(attribute.getType());
                }
                properties.put(attribute.getRmName(), createPBmmProperty(type, attribute));
            }
            bmmClass.setProperties(properties);
            classDefinitions.put(type.getRmName(), bmmClass);
        }

        schema.setClassDefinitions(classDefinitions);
        pack.setClasses(packageClasses);

        Map<String, BmmIncludeSpec> includes = new LinkedHashMap<>();
        BmmIncludeSpec include = new BmmIncludeSpec();
        include.setId("openehr_base_1.1.0");
        includes.put("1", include);

        schema.setIncludes(includes);

        setMetaInformation(schema);

        addEnums(foundEnums, pack, lookup, schema);

        return schema;
    }

    private boolean hasJsonIgnoreAnnotation(RMAttributeInfo attribute) {
        if(!this.excludedJsonIgnoreFields) {
            return false;
        }

        if(attribute.getGetMethod() != null) {
            if(attribute.getGetMethod().getAnnotation(JsonIgnore.class) != null) {
                return true;
            }
        }
        if(attribute.getSetMethod() != null) {
            if(attribute.getSetMethod().getAnnotation(JsonIgnore.class) != null) {
                return true;
            }
        }
        if(attribute.getField() != null) {
            if(attribute.getField().getAnnotation(JsonIgnore.class) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add all found enums to the schema
     * @param foundEnums all the enum classes found
     * @param pack
     * @param schema the P_BMM schema
     */
    private void addEnums(List<Class> foundEnums, PBmmPackage pack, ModelInfoLookup lookup, PBmmSchema schema) {
        for(Class clazz:foundEnums){
            System.out.println("enum");
            //this can be either a string or an integer, we really don't know based on just the enum
            PBmmEnumerationString bmmEnum = new PBmmEnumerationString();
            bmmEnum.setAncestors(Lists.newArrayList("String"));
            bmmEnum.setName(lookup.getNamingStrategy().getTypeName(clazz));
            bmmEnum.setItemNames(Arrays.stream(clazz.getEnumConstants()).map(t -> t.toString()).collect(Collectors.toList()));
            pack.getClasses().add(bmmEnum.getName());
            schema.getClassDefinitions().put(bmmEnum.getName(), bmmEnum);
            //["PROPORTION_KIND"] = (P_BMM_ENUMERATION_INTEGER) <
            //		name = <"PROPORTION_KIND">
            //		ancestors = <"Integer">
            //		item_names = <"pk_ratio", "pk_unitary", "pk_percent", "pk_fraction", "pk_integer_fraction">
            //	>
        }

    }

    private void setMetaInformation(PBmmSchema schema) {
        schema.setBmmVersion("2.3");

        schema.setRmPublisher("openehr");
        schema.setSchemaName("aom");
        schema.setRmRelease("2.3.0");

        schema.setSchemaAuthor("auto-generated by archie");
        schema.setSchemaRevision("0.0.1");
        schema.setSchemaLifecycleState("auto-generated experiment");
        schema.setSchemaDescription("Archetype Object model, autogenerated as implemented in Archie");
    }

    private PBmmProperty createPBmmProperty(RMTypeInfo type, RMAttributeInfo attribute) {
        String typeInCollection = convertType(type.getRmName(), attribute.getTypeNameInCollection());
        if(Map.class.isAssignableFrom(attribute.getType())) {
            PBmmGenericProperty property = new PBmmGenericProperty();
            setBasicsForProperty(attribute, property);
            PBmmGenericType pBmmType = createMapContainerType(attribute.getTypeInCollection(), (ParameterizedType) attribute.getGetMethod().getGenericReturnType(), typeInCollection);
            property.setTypeDef(pBmmType);
            return property;
//                    PBmmGenericProperty property = new PBmmGenericProperty();
//                    property.setComputed(attribute.isComputed());
//                    property.setName(attribute.getRmName());
//                    property.setMandatory(!attribute.isNullable());
//                    PBmmGenericType pBmmType = new PBmmGenericType();
//                    pBmmType.setRootType("Hash");
//                    property.setTypeDef(pBmmType);
//                    properties.put(attribute.getRmName(), property);
        } else if(attribute.isMultipleValued()) {

            PBmmContainerProperty property = new PBmmContainerProperty();
            setBasicsForProperty(attribute, property);
            //property.setCardinality(Interval.upperUnbounded(0, true));
            PBmmContainerType pBmmType = new PBmmContainerType();
            if(typeInCollection.startsWith("INTERVAL")) {
                //generics!
                pBmmType.setTypeDef(new PBmmGenericType(typeInCollection, new ArrayList<>()));//TODO: add generic parameters!
            } else {
                pBmmType.setType(typeInCollection);
            }
            pBmmType.setContainerType("List");
            property.setTypeDef(pBmmType);
            return property;
        } else {
            PBmmSingleProperty property = new PBmmSingleProperty();
            setBasicsForProperty(attribute, property);
            property.setType(typeInCollection);
            return property;
        }
    }

    private PBmmGenericType createHashType(PBmmType valueType) {
        PBmmGenericType pBmmType = new PBmmGenericType();
        pBmmType.setRootType("Hash");
        Map<String, PBmmType> paramdefs = new LinkedHashMap<>();
        pBmmType.setGenericParameterDefs(paramdefs);

        paramdefs.put("K", new PBmmSimpleType("String"));
        paramdefs.put("V", valueType);
        return pBmmType;
    }

    private PBmmGenericType createMapContainerType(Class classInCollection, ParameterizedType genericTypeOfCollection, String typeInCollectionName) {

        if(Map.class.isAssignableFrom(classInCollection)) {
            PBmmType valueType = null;

            Type mapContent = genericTypeOfCollection.getActualTypeArguments()[1];
            if(mapContent instanceof ParameterizedType) {
                ParameterizedType paramContent = (ParameterizedType) mapContent;
                valueType = createMapContainerType((Class) paramContent.getRawType(), paramContent, "Hash");
            } else if(mapContent instanceof Class){
                valueType = new PBmmSimpleType(modelInfoLookup.getNamingStrategy().getTypeName((Class) mapContent));
            } else {
                throw new UnsupportedOperationException("type with class " + mapContent.getClass().getName() + " Not implemented");
            }
            return createHashType(valueType);
//        } else if(Map.class.isAssignableFrom(classInCollection)) {
//            PBmmContainerType pBmmType = new PBmmContainerType();
//            pBmmType.setContainerType("Hash");
//            //ok need a typeDef here
//            //sorry about this construction
//            //TODO: rewrite into a proper method that does proper bounds and type checks
//            Type mapContent = genericTypeOfCollection.getActualTypeArguments()[1];
//
//            if(mapContent instanceof ParameterizedType) {
//                ParameterizedType paramContent = (ParameterizedType) mapContent;
//                pBmmType.setTypeDef(createMapContainerType((Class) paramContent.getRawType(), paramContent, "Hash"));
//            } else {
//                pBmmType.setType(mapContent.getTypeName());
//            }
//            return pBmmType;
//        }
        } else {
            return createHashType(new PBmmSimpleType(typeInCollectionName));
        }

    }

    private void setBasicsForProperty(RMAttributeInfo attribute, PBmmProperty property) {
        if(attribute.isComputed()) {
            property.setComputed(attribute.isComputed());
        }
        property.setName(convertTypeName(attribute.getRmName()));
        if(!attribute.isNullable()) {
            property.setMandatory(!attribute.isNullable());
        }
    }

    private String convertTypeName(String type) {
       return convertType(null, type);
    }

    private String convertType(String baseClass, String typeName) {
        if(baseClass != null) {
            switch (baseClass) {
                case "C_DATE":
                    if (typeName.equalsIgnoreCase("TEMPORAL_ACCESSOR")) {
                        return "Date";
                    }
                    break;
                case "C_TIME":
                    if (typeName.equalsIgnoreCase("TEMPORAL_ACCESSOR")) {
                        return "Time";
                    }
                    break;
                case "C_DATE_TIME":
                    if (typeName.equalsIgnoreCase("TEMPORAL_ACCESSOR")) {
                        return "Date_time";
                    }
                    break;
            }
        }
        switch(typeName.toLowerCase()) {
            case "double":
                return "Real";
            case "long":
                return "Integer";
            case "map":
                return "Hash";
            case "temporal_accessor":
                return "Iso8601_type"; //should have been caught above
            case "temporal_amount":
                return "Duration";
            case "string":
                return "String";
            case "boolean":
                return "Boolean";
            case "archetype_model_object":
            case "object":
                return "Any";
            default:
                return typeName;
        }
    }
}
