package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmUnitaryProperty;

public final class PBmmGenericProperty extends PBmmProperty<PBmmGenericType, BmmUnitaryProperty> {

    public PBmmGenericProperty() {
        super();
    }

    @Override
    public BmmProperty createBmmProperty(BmmModel schema, BmmClass bmmClass) {
        PBmmGenericType typeRef = getTypeRef();
        if (typeRef != null) {
            typeRef.createBmmType(schema, bmmClass);
            if (typeRef.bmmType != null) {
                BmmUnitaryProperty bmmProperty = new BmmUnitaryProperty(getName(), typeRef.bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
                setValues(bmmProperty);
                return bmmProperty;
            }
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef.asTypeString() + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
