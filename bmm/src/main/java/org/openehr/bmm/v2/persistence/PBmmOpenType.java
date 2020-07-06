package org.openehr.bmm.v2.persistence;

import com.google.common.collect.Lists;
import org.openehr.bmm.core.*;

import java.util.List;

public final class PBmmOpenType extends PBmmUnitaryType<BmmParameterType> {

    private String type;

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
    public void createBmmType (BmmModel schema, BmmClass classDefinition) {
        if (classDefinition instanceof BmmGenericClass) {
            BmmParameterType bmmParamType = ((BmmGenericClass) classDefinition).getGenericParameters().get(type);
            if (bmmParamType != null)
                bmmType = bmmParamType;
        }
    }
}
