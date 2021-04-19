package org.openehr.bmm.v2.persistence;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmType;
import org.openehr.bmm.core.BmmUnitaryType;
import org.openehr.bmm.persistence.validation.BmmDefinitions;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PBmmGenericType extends PBmmUnitaryType {
    private String rootType;
    private Map<String, PBmmType> genericParameterDefs;
    private List<String> genericParameters;

    public PBmmGenericType() {
    }

    public PBmmGenericType (String aTypeName, List<String> genericParams) {
        rootType = aTypeName;
        genericParameters = genericParams;
    }

    public String getRootType() {
        return rootType;
    }

    public void setRootType(String rootType) {
        this.rootType = rootType;
    }

    /**
     * Effective unitary type, ignoring containers and also generic parameters
     */
    @Override
    public String baseType() {
        return rootType;
    }

    public Map<String, PBmmType> getGenericParameterDefs() {
        if (genericParameterDefs == null) {
            genericParameterDefs = new LinkedHashMap<>();
        }
        return genericParameterDefs;
    }

    public void setGenericParameterDefs(Map<String, PBmmType> genericParameterDefs) {
        this.genericParameterDefs = genericParameterDefs;
    }

    public List<String> getGenericParameters() {
        if (genericParameters == null) {
            genericParameters = new ArrayList<>();
        }
        return genericParameters;
    }

    public void setGenericParameters(List<String> genericParameters) {
        this.genericParameters = genericParameters;
    }

    /**
     * Returns the generic parameters of the root_type in this type specifier. The order must match the order of the
     * owning class’s formal generic parameter declarations
     *
     * @return
     */
    @JsonIgnore
    public List<PBmmType> getGenericParameterRefs() {
        List<PBmmType> genericParameterReferences = new ArrayList<>();
        if (genericParameterDefs != null && genericParameterDefs.size() > 0) {
            genericParameterReferences.addAll(genericParameterDefs.values());
        }
        else {
            genericParameters.forEach(param -> {
                if (BmmDefinitions.isFormalGenericParameterName(param)) {
                    PBmmOpenType openType = new PBmmOpenType(param);
                    genericParameterReferences.add(openType);
                }
                else {
                    PBmmSimpleType simpleType = new PBmmSimpleType(param);
                    genericParameterReferences.add(simpleType);
                }
            });
        }
        return genericParameterReferences;
    }

    @Override
    public BmmGenericType createBmmType(BmmClassProcessor processor, BmmClass classDefinition) {
        BmmClass rootClassDef = processor.getClassDefinition(rootType);
        if (rootClassDef instanceof BmmGenericClass) {
            BmmGenericType bmmType = new BmmGenericType((BmmGenericClass)rootClassDef);
            for (PBmmType param: getGenericParameterRefs()) {
                BmmType paramBmmType = param.createBmmType(processor, classDefinition);
                if (paramBmmType instanceof BmmUnitaryType) {
                    bmmType.addGenericParameter(paramBmmType);
                } else {
                    throw new RuntimeException("BmmClass " + getRootType() + " generic parameter" +
                            param.asTypeString() + " not BmmUnitaryType");
                }
            }
            return bmmType;
        }

        throw new RuntimeException("BmmClass " + getRootType() + " is not defined in this model or not a generic type");
    }

    /**
     * Formal name of the type for display.
     *
     * @return
     */
    @Override
    public String asTypeString() {
        StringBuilder builder = new StringBuilder();
        builder.append(rootType).append(BmmDefinitions.GENERIC_LEFT_DELIMITER);
        List<PBmmType> parameterReferences = getGenericParameterRefs();
        for (int i = 0; i < parameterReferences.size(); i++) {
            builder.append(parameterReferences.get(i).asTypeString());
            if (i < parameterReferences.size() - 1) {
                builder.append(BmmDefinitions.GENERIC_SEPARATOR);
            }
        }
        builder.append(BmmDefinitions.GENERIC_RIGHT_DELIMITER);
        return builder.toString();
    }

    @Override
    public List<String> flattenedTypeList() {
        List<String> retVal = new ArrayList<>();
        getGenericParameterRefs().forEach( item -> retVal.addAll(item.flattenedTypeList()));
        return retVal;
    }
}
