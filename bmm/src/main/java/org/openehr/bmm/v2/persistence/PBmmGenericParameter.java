package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmParameterType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public final class PBmmGenericParameter extends PBmmBase {

    private String documentation;
    private String name;
    private String conformsToType;

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

    public BmmParameterType createBmmGenericParameter (BmmClassProcessor processor) {
        if (conformsToType != null) {
            BmmClass confTypeClassDef = processor.getClassDefinition(conformsToType);
            return new BmmParameterType (name, confTypeClassDef.getType(), processor.getAnyTypeDefinition());
        }
        else {
            return new BmmParameterType(name, processor.getAnyTypeDefinition());
        }
    }
}

