package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmGenericType;
import org.openehr.bmm.core.BmmUnitaryProperty;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public final class PBmmGenericProperty extends PBmmProperty<PBmmGenericType> {

    @Override
    public BmmUnitaryProperty createBmmProperty(BmmClassProcessor classProcessor, BmmClass bmmClass) {
        PBmmGenericType typeRef = getTypeRef();
        if (typeRef != null) {
            BmmGenericType bmmType = typeRef.createBmmType(classProcessor, bmmClass);

            BmmUnitaryProperty bmmProperty = new BmmUnitaryProperty(getName(), bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
            populateImBooleans(bmmProperty);
            return bmmProperty;
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
