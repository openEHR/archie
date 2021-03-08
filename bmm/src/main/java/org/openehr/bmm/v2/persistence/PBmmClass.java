package org.openehr.bmm.v2.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmDefinedType;
import org.openehr.bmm.core.BmmGenericClass;
import org.openehr.bmm.core.BmmParameterType;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmSimpleClass;
import org.openehr.bmm.core.BmmType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PBmmClass extends PBmmBase {

    private String documentation;//from P_BMM_MODEL_ELEMENT
    private String name;
    private List<String> ancestors;
    private Map<String, PBmmUnitaryType> ancestorDefs;
    private Map<String, PBmmProperty<?>> properties;
    private Boolean isAbstract;
    private Map<String, PBmmGenericParameter> genericParameterDefs;
    private Boolean isOverride;

    private transient String sourceSchemaId;


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

    public List<String> getAncestors() {
        if (ancestors == null) {
            ancestors = new ArrayList<>();
        }
        return ancestors;
    }

    /**
     * @return coalesced structural representation of ancestors from ancestorDefs and ancestors
     * @throws  RuntimeException if BmmSchema contains a non-existing ancestor reference
     */
    @JsonIgnore
    public Map<String, PBmmUnitaryType> ancestorRefs(PBmmSchema schema ) {
        if (ancestorDefs != null) {
            return ancestorDefs;
        } else {
            Map<String, PBmmUnitaryType> result = new LinkedHashMap<>();
            if (ancestors != null) {
                for (String anc: ancestors) {
                    PBmmClass pBmmClass = schema.getClassDefinition(anc);
                    if (pBmmClass != null) {
                        if (pBmmClass.isGeneric()) {
                            result.put(anc, new PBmmGenericType(anc, new ArrayList<>(pBmmClass.genericParameterDefs.keySet())));
                        } else {
                            result.put(anc, new PBmmSimpleType(anc));
                        }
                    } else {
                        //validation will catch this case.
                         throw new RuntimeException("Error retrieving class definition for ancestor \"" +
                                    anc  + "\" of PBmmClass " + name);
                    }
                }
            }
            return result;
        }
    }

    /**
     * Get a list of ancestors type names. Combines the ancestors and ancestor_defs attributes, so can always
     * be used instead of those two separately. Warning: generates type names including generic parameters,
     * you may have to strip those for certain types of operations
     * @return
     */
    @JsonIgnore
    public List<String> getAncestorTypeNames() {
        if(ancestorDefs != null && !ancestorDefs.isEmpty()) {
            return ancestorDefs.values().stream().map(type -> type.asTypeString()).collect(Collectors.toList());
        } else if (ancestors != null) {
            return ancestors;
        } else {
            return new ArrayList<>();
        }
    }

    public void setAncestors(List<String> ancestors) {
        this.ancestors = ancestors;
    }

    public Map<String, PBmmProperty<?>> getProperties() {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        return properties;
    }

    public void setProperties(Map<String, PBmmProperty<?>> properties) {
        this.properties = properties;
    }

    @JsonProperty(value = "is_abstract")
    public Boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    @JsonProperty(value = "is_override")
    public Boolean isOverride() {
        return isOverride;
    }

    public void setOverride(Boolean override) {
        isOverride = override;
    }

    public Map<String, PBmmGenericParameter> getGenericParameterDefs() {
        if (genericParameterDefs == null) {
            genericParameterDefs = new LinkedHashMap<>();
        }
        return genericParameterDefs;
    }

    public void setGenericParameterDefs(Map<String, PBmmGenericParameter> genericParameterDefs) {
        this.genericParameterDefs = genericParameterDefs;
    }

    /**
     * True if this class is a generic class.
     *
     * @return
     */
    @JsonIgnore
    public boolean isGeneric() {
        return this.getGenericParameterDefs() != null && this.getGenericParameterDefs().size() > 0;
    }

    @JsonIgnore
    public String getSourceSchemaId() {
        return sourceSchemaId;
    }

    public void setSourceSchemaId(String sourceSchemaId) {
        this.sourceSchemaId = sourceSchemaId;
    }

    public Map<String, PBmmUnitaryType> getAncestorDefs() {
        return ancestorDefs;
    }

    public BmmClass createBmmClass() {
        BmmClass bmmClass;
        if (getGenericParameterDefs().size() > 0) {
            bmmClass = new BmmGenericClass(getName(), getDocumentation(), nullToFalse(isAbstract()));
        } else {
            bmmClass = new BmmSimpleClass(getName(), getDocumentation(), nullToFalse(isAbstract()));
        }
        bmmClass.setOverride(nullToFalse(isOverride()));
        bmmClass.setSourceSchemaId(getSourceSchemaId());
        return bmmClass;
    }

    public BmmClass populateBmmClass(BmmClassProcessor classProcessor, PBmmSchema schema) {
        BmmClass bmmClass = classProcessor.getUnprocessedClassDefinition(getName());
        if (bmmClass != null) {
            // populate references to ancestor classes; should be every class except Any

            for (PBmmUnitaryType ancestorType : ancestorRefs(schema).values()) {
                BmmClass ancestorClass = classProcessor.getClassDefinition(ancestorType.baseType());
                if (ancestorClass == null) {
                    throw new RuntimeException("Error retrieving class definition for ancestor class " +
                            ancestorType.baseType() + " of BmmClass " + name);

                }
                BmmType bmmType = ancestorType.createBmmType(classProcessor, ancestorClass);

                if (bmmType instanceof BmmDefinedType) {
                    bmmClass.addAncestor((BmmDefinedType) bmmType);
                } else {
                    throw new RuntimeException("Ancestor class definition of the wrong type for ancestor class " +
                            ancestorType.baseType() + " of BmmClass " + name);
                }
            }

            // create generic parameters if a generic class
            // and add to the BMM_GENERIC_TYPE.generic_parameters list
            if (bmmClass instanceof BmmGenericClass) {
                for (PBmmGenericParameter param : getGenericParameterDefs().values()) {
                    ((BmmGenericClass) bmmClass).addGenericParameter(param.createBmmGenericParameter(classProcessor));
                }
            }
        } else {
            throw new RuntimeException("bmmClass for PBmmClass \"" + name + "\" is null. It may have been defined as a class or a primitive but not included in a package");
        }
        return bmmClass;
    }

    public BmmClass populateBmmClassProperties(BmmClassProcessor classProcessor) {
        BmmClass bmmClass = classProcessor.getUnprocessedClassDefinition(getName());
        if (bmmClass != null) {
            // populate properties
            for (PBmmProperty<?> pBmmProperty : getProperties().values()) {
                BmmProperty<?> bmmProperty = pBmmProperty.createBmmProperty(classProcessor, bmmClass);
                if (bmmProperty != null) {
                    bmmClass.addProperty(bmmProperty);
                } else {
                    throw new RuntimeException("BMM Property creation returned null");
                }
            }
        }
        return bmmClass;
    }
}
