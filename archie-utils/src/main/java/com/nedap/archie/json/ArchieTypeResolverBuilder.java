package com.nedap.archie.json;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;
import com.nedap.archie.rminfo.ReflectionModelInfoLookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TypeResolverBuilder that outputs type information for all RMObject classes, but not for java classes.
 * Otherwise, you get this for an arrayList: "ARRAY_LIST: []", while you would expect "[]" without type
 */
public class ArchieTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder {

    private Set<Class<?>> classesToNotAddTypeProperty;

    public ArchieTypeResolverBuilder(ReflectionModelInfoLookup rmInfoLookup, ArchieJacksonConfiguration configuration) {
        super(ObjectMapper.DefaultTyping.NON_FINAL, BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(OpenEHRBase.class).build());
        classesToNotAddTypeProperty = new HashSet<>();
        if (!configuration.isAlwaysIncludeTypeProperty()) {
            List<RMTypeInfo> allTypes = new ArrayList<>(rmInfoLookup.getAllTypes());
            allTypes.addAll(ArchieAOMInfoLookup.getInstance().getAllTypes());
            for (RMTypeInfo type : allTypes) {
                if (type.getDirectDescendantClasses().isEmpty()) {
                    classesToNotAddTypeProperty.add(type.getJavaClass());
                }
            }
        }
        if (configuration.isAlwaysIncludeTypeProperty() && configuration.isStandardsCompliantExpressions()) {
            //bit of a hack: the RulesSection is serialized as a List if configuration.isStandardsCompliantExpressions()
            //but then because it is actually a RulesSection class, jackson wants to add type information.
            //this will make it serialize ["ARRAY_LIST", [...]], which is incorrect.
            //so do not add type info for the RulesSection, it will be just a list anyway.
            classesToNotAddTypeProperty.add(RulesSection.class);
        }
    }

    @Override
    public boolean useForType(JavaType t) {
        return (OpenEHRBase.class.isAssignableFrom(t.getRawClass()) && !classesToNotAddTypeProperty.contains(t.getRawClass()));
    }

}
