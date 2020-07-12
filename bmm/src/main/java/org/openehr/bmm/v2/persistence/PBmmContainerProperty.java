package org.openehr.bmm.v2.persistence;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmContainerType;
import org.openehr.bmm.core.BmmModel;
import org.openehr.bmm.core.BmmProperty;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public final class PBmmContainerProperty extends PBmmProperty<PBmmContainerType> {

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
    public BmmContainerProperty createBmmProperty(BmmClassProcessor classProcessor, BmmClass bmmClass) {
        PBmmContainerType typeRef = getTypeRef();
        if (typeRef != null) {
            BmmContainerType bmmType = typeRef.createBmmType(classProcessor, bmmClass);
            if (bmmType != null) {
                BmmContainerProperty bmmProperty = new BmmContainerProperty(getName(), bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
                if (getCardinality() != null) {
                    bmmProperty.setCardinality(new MultiplicityInterval(getCardinality()));
                }
                populateImBooleans(bmmProperty);
                return bmmProperty;
            }
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef.asTypeString() + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
