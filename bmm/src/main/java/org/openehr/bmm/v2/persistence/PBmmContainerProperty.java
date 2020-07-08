package org.openehr.bmm.v2.persistence;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;

public final class PBmmContainerProperty extends PBmmProperty<PBmmContainerType, BmmContainerProperty> {

    private Interval<Integer> cardinality;

    public PBmmContainerProperty() {
        super();
    }

    public Interval<Integer> getCardinality() {
        return cardinality;
    }

    public void setCardinality(Interval<Integer> cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public BmmProperty createBmmProperty(BmmModel schema, BmmClass bmmClass) {
        PBmmContainerType typeRef = getTypeRef();
        if (typeRef != null) {
            typeRef.createBmmType(schema, bmmClass);
            if (getTypeDef().bmmType != null) {
                BmmContainerProperty bmmProperty = new BmmContainerProperty(getName(), typeRef.bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
                if (getCardinality() != null) {
                    bmmProperty.setCardinality(new MultiplicityInterval(getCardinality()));
                }
                setValues(bmmProperty);
                return bmmProperty;
            }
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef.asTypeString() + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
