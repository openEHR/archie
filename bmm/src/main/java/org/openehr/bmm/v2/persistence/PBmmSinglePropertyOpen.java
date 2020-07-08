package org.openehr.bmm.v2.persistence;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmParameterType;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmSimpleType;
import org.openehr.bmm.core.BmmUnitaryProperty;

public final class PBmmSinglePropertyOpen extends PBmmProperty<PBmmOpenType, BmmUnitaryProperty> {

    private String type;

    /**
     * The P_BMM spec has quite oddly defined typing: there's a simple string form, and two structural forms. For most
     * typing, the simple string is enough. When this is not enough, there's an object variant called type_def.
     * So far, nothing strange going on. however, now there's a third variant:
     *
     * There's type_ref, which should be a computed (non-serialized) version. However, for terminology code constraints
     * it has to be a serialized version of type_ref. To distinguish between the computed typeRef (so, in our case, a method that returns without side-effects)
     * and the serialized type ref, a serialized type ref has been added. It maps to the json property type_ref.
     */
    private PBmmOpenType serializedTypeRef;

    public PBmmSinglePropertyOpen() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    @Override
    public PBmmOpenType getTypeRef() {
        if (serializedTypeRef != null)
            return serializedTypeRef;
        else if(getTypeDef() == null)
            return new PBmmOpenType(type);
        return getTypeDef();
    }

    @Override
    public BmmProperty createBmmProperty(BmmModel schema, BmmClass bmmClass) {
        PBmmOpenType typeRef = getTypeRef();
        if (typeRef != null) {
            BmmParameterType bmmType = typeRef.createBmmType(schema, bmmClass);
            if (bmmType != null) {
                BmmProperty bmmProperty = new BmmUnitaryProperty(getName(), bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
                populateImBooleans(bmmProperty);
                return bmmProperty;
            }
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef.asTypeString() + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

    @JsonProperty("type_ref")
    public PBmmOpenType getSerializedTypeRef() {
        return serializedTypeRef;
    }

    public void setSerializedTypeRef(PBmmOpenType serializedTypeRef) {
        this.serializedTypeRef = serializedTypeRef;
    }
}
