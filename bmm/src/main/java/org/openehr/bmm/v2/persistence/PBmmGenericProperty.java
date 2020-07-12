package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.core.BmmUnitaryProperty;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public final class PBmmGenericProperty extends PBmmProperty<PBmmGenericType> {

    public PBmmGenericProperty() {
        super();
    }

    @Override
    public BmmUnitaryProperty createBmmProperty(BmmClassProcessor classProcessor, BmmClass bmmClass) {
        PBmmGenericType typeRef = getTypeRef();
        if (typeRef != null) {
            BmmGenericType bmmType = typeRef.createBmmType(classProcessor, bmmClass);
            if (bmmType != null) {
                BmmUnitaryProperty bmmProperty = new BmmUnitaryProperty(getName(), bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
                populateImBooleans(bmmProperty);
                return bmmProperty;
            }
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef.asTypeString() + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
