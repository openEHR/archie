package org.openehr.bmm.v2.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public abstract class PBmmProperty<T extends PBmmType, U extends BmmProperty>  extends PBmmBase {

    private String documentation;
    private String name;
    private Boolean isMandatory;
    private Boolean isComputed;
    private Boolean isImInfrastructure;
    private Boolean isImRuntime;
    private T typeDef;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PBmmProperty () {
    }

    @JsonProperty(value = "is_mandatory")
    public Boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    @JsonProperty(value = "is_computed")
    public Boolean isComputed() {
        return isComputed;
    }

    public void setComputed(Boolean computed) {
        isComputed = computed;
    }

    @JsonProperty(value = "is_im_infrastructure")
    public Boolean isImInfrastructure() {
        return isImInfrastructure;
    }

    public void setIsImInfrastructure (Boolean imInfrastructure) {isImInfrastructure = imInfrastructure; }

    @JsonProperty(value = "is_im_runtime")
    public Boolean isImRuntime() {
        return isImRuntime;
    }

    public void setIsImRuntime (Boolean imRuntime) {isImRuntime = imRuntime; }

    public T getTypeDef() {
        return typeDef;
    }

    public void setTypeDef(T typeDef) {
        this.typeDef = typeDef;
    }

    public abstract BmmProperty createBmmProperty(BmmClassProcessor classProcessor, BmmClass bmmClass);

    /** set the values to the BmmProperty that come from this class
     */
    protected void populateImBooleans(BmmProperty property) {
        property.setImInfrastructure(nullToFalse(isImInfrastructure));
        property.setImRuntime(nullToFalse(isImRuntime));
    }

    /**
     * Calculate typeDef and return. Always returns a type, even if typeDef in the persisted schema is not set
     * In that case, using the other type attributes from this property
     * @return
     */
    @JsonIgnore
    public T getTypeRef() {
        return typeDef;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }
}
