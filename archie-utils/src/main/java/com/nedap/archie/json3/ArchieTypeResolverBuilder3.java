package com.nedap.archie.json3;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.impl.DefaultTypeResolverBuilder;
import com.nedap.archie.aom.RulesSection;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.ArchieAOMInfoLookup;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.RMTypeInfo;
import com.nedap.archie.json.ArchieJacksonConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Jackson 3 port of {@link com.nedap.archie.json.ArchieTypeResolverBuilder}.
 */
public class ArchieTypeResolverBuilder3 extends DefaultTypeResolverBuilder {

    private final Set<Class<?>> classesToNotAddTypeProperty;

    public ArchieTypeResolverBuilder3(ArchieJacksonConfiguration configuration) {
        super(BasicPolymorphicTypeValidator.builder().allowIfBaseType(OpenEHRBase.class).build(),
                DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        classesToNotAddTypeProperty = new HashSet<>();
        if (!configuration.isAlwaysIncludeTypeProperty()) {
            List<RMTypeInfo> allTypes = new ArrayList<>(ArchieRMInfoLookup.getInstance().getAllTypes());
            allTypes.addAll(ArchieAOMInfoLookup.getInstance().getAllTypes());
            for (RMTypeInfo type : allTypes) {
                if (type.getDirectDescendantClasses().isEmpty()) {
                    classesToNotAddTypeProperty.add(type.getJavaClass());
                }
            }
        }
        if (configuration.isAlwaysIncludeTypeProperty() && configuration.isStandardsCompliantExpressions()) {
            classesToNotAddTypeProperty.add(RulesSection.class);
        }
    }

    @Override
    public boolean useForType(JavaType t) {
        return OpenEHRBase.class.isAssignableFrom(t.getRawClass())
                && !classesToNotAddTypeProperty.contains(t.getRawClass());
    }
}
