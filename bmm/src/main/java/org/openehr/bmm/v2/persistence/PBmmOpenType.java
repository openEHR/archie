package org.openehr.bmm.v2.persistence;

import com.google.common.collect.Lists;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmParameterType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.List;

public final class PBmmOpenType extends PBmmUnitaryType {

    private String type;

    public PBmmOpenType() {
        super();
    }

    public PBmmOpenType (String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String baseType() {
        return type;
    }

    /**
     * Formal name of the type for display.
     *
     * @return
     */
    @Override
    public String asTypeString() {
        return type;
    }

    @Override
    public List<String> flattenedTypeList() {
        return Lists.newArrayList(type);
    }

    @Override
    public BmmParameterType createBmmType (BmmClassProcessor processor, BmmClass classDefinition) {
        if (classDefinition instanceof BmmGenericClass) {
            BmmParameterType bmmParamType = ((BmmGenericClass) classDefinition).getGenericParameters().get(type);
            if (bmmParamType != null)
                return bmmParamType;
        }
        throw new RuntimeException("error creating class");
    }
}
