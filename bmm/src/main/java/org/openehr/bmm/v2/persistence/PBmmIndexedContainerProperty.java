package org.openehr.bmm.v2.persistence;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.*;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public class PBmmIndexedContainerProperty extends PBmmProperty<PBmmIndexedContainerType> {

    /**
     * We have to replicate cardinality here from PBmmContainerProperty since we are inheriting from
     * PBmmProperty<PBmmIndexedContainerType> (which creates correct typing of typeDef) not PBmmContainerProperty
     */
    private Interval<Integer> cardinality;

    public Interval<Integer> getCardinality() {
        return cardinality;
    }
    public void setCardinality(Interval<Integer> cardinality) {
        this.cardinality = cardinality;
    }
    public PBmmIndexedContainerProperty() {
        super();
    }
    @Override
    public BmmIndexedContainerProperty createBmmProperty(BmmClassProcessor classProcessor, BmmClass bmmClass) {
        PBmmIndexedContainerType typeRef = getTypeRef();
        if (typeRef != null) {
            BmmIndexedContainerType bmmType = (BmmIndexedContainerType) typeRef.createBmmType(classProcessor, bmmClass);
            BmmIndexedContainerProperty bmmProperty = new BmmIndexedContainerProperty(getName(), bmmType, getDocumentation(), nullToFalse(isMandatory()), nullToFalse(isComputed()));
            if (getCardinality() != null) {
                bmmProperty.setCardinality(new MultiplicityInterval(getCardinality()));
            }
            populateImBooleans(bmmProperty);
            return bmmProperty;
        }
        throw new RuntimeException("BmmTypeCreate failed for type " + typeRef + " of property "
                + getName() + " in class " + bmmClass.getName());
    }

}
