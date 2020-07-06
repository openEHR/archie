package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmParameterType;

public final class PBmmGenericParameter extends PBmmBase {

    private String documentation;
    private String name;
    private String conformsToType;
    protected BmmParameterType bmmGenericParameter;

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConformsToType() {
        return conformsToType;
    }

    public void createBmmGenericParameter (BmmModel aBmmModel) {
        if (conformsToType != null) {
            BmmClass confTypeClassDef = aBmmModel.getClassDefinition(conformsToType);
            bmmGenericParameter = new BmmParameterType (name, confTypeClassDef.getType(), aBmmModel.getAnyTypeDefinition());
        }
        else
            bmmGenericParameter = new BmmParameterType (name, aBmmModel.getAnyTypeDefinition());
    }
}

