package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmUnitaryProperty;

public final class PBmmGenericProperty extends PBmmProperty<PBmmGenericType, BmmUnitaryProperty> {

    public PBmmGenericProperty() {
        super();
    }

    @Override
    public void createBmmProperty(BmmModel schema, BmmClass bmmClass) {
        if (typeDef != null) {
            typeDef.createBmmType(schema, bmmClass);
            if (typeDef.bmmType != null) {
                bmmProperty = new BmmUnitaryProperty(getName(), typeDef.bmmType, getDocumentation(), isMandatory(), isComputed());
            }
            else
                throw new RuntimeException("BmmTypeCreate failed for type " + typeDef.asTypeString() + " of property "
                        + getName() + " in class " + bmmClass.getName());
        }
    }

}
