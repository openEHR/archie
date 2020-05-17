package com.nedap.archie.openapi;

import com.nedap.archie.base.Interval;
import com.nedap.archie.json.flat.AttributeReference;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;
import org.openehr.bmm.v2.persistence.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ModelInfoLookupToPBmmConverter {



    public PBmmSchema convert(ModelInfoLookup lookup, Set<AttributeReference> ignoredAttributes) {
        PBmmSchema schema = new PBmmSchema();
        PBmmPackage pack = new PBmmPackage("default");
        Map<String, PBmmPackage> packageMap = new LinkedHashMap<>();
        packageMap.put("default", pack);
        schema.setPackages(packageMap);

        List<String> packageClasses = new ArrayList<>();

        Map<String, PBmmClass> classDefinitions = new LinkedHashMap<>();

        for(RMTypeInfo type:lookup.getAllTypes()) {
            packageClasses.add(type.getRmName());
            PBmmClass bmmClass = new PBmmClass();
            bmmClass.setName(type.getRmName());
            bmmClass.setAbstract(Modifier.isAbstract(type.getJavaClass().getModifiers()));
            bmmClass.setAncestors(type.getDirectParentClasses().stream()
                        .map(t -> t.getRmName()).collect(Collectors.toList())
            );
            Map<String, PBmmProperty> properties = new LinkedHashMap<>();
            for(RMAttributeInfo attribute:type.getAttributes().values()) {
                if(attribute.isFromAncestor() || ignoredAttributes.contains(new AttributeReference(type.getRmName(), attribute.getRmName()))) {
                    continue;
                }
                if(attribute.isMultipleValued()) {

                    PBmmContainerProperty property = new PBmmContainerProperty();
                    property.setComputed(attribute.isComputed());
                    property.setName(attribute.getRmName());
                    property.setMandatory(!attribute.isNullable());
                    property.setCardinality(Interval.upperUnbounded(0, true));
                    PBmmContainerType pBmmType = new PBmmContainerType();
                    pBmmType.setType(attribute.getTypeNameInCollection());
                    pBmmType.setContainerType("List");
                    property.setTypeDef(pBmmType);
                    properties.put(attribute.getRmName(), property);
                } else {
                    PBmmProperty property = new PBmmSingleProperty();
                    property.setComputed(attribute.isComputed());
                    property.setName(attribute.getRmName());
                    property.setMandatory(!attribute.isNullable());
                    PBmmSimpleType pBmmType = new PBmmSimpleType();
                    pBmmType.setType(attribute.getTypeNameInCollection());
                    property.setTypeDef(pBmmType);
                    properties.put(attribute.getRmName(), property);
                }
                if(Map.class.isAssignableFrom(attribute.getType())) {
                    System.out.println("check me!");
                    PBmmGenericProperty property = new PBmmGenericProperty();
                    property.setComputed(attribute.isComputed());
                    property.setName(attribute.getRmName());
                    property.setMandatory(!attribute.isNullable());
                    PBmmGenericType pBmmType = new PBmmGenericType();
                    pBmmType.setRootType("Hash");
                    property.setTypeDef(pBmmType);
                    properties.put(attribute.getRmName(), property);
                }
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
        return schema;
    }
}
