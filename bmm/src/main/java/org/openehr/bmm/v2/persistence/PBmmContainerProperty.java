package org.openehr.bmm.v2.persistence;

import com.nedap.archie.base.Interval;
import com.nedap.archie.base.MultiplicityInterval;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmContainerProperty;
import org.openehr.bmm.core.BmmModel;

public final class PBmmContainerProperty extends PBmmProperty<PBmmContainerType, BmmContainerProperty> {

    private Interval<Integer> cardinality;
    
    public Interval<Integer> getCardinality() {
        return cardinality;
    }

    public void setCardinality(Interval<Integer> cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public void createBmmProperty(BmmModel schema, BmmClass bmmClass) {
        if (typeDef != null) {
            typeDef.createBmmType(schema, bmmClass);
            if (typeDef.bmmType != null) {
                bmmProperty = new BmmContainerProperty(getName(), typeDef.bmmType, getDocumentation(), isMandatory(), isComputed());
                if (getCardinality() != null)
                    bmmProperty.setCardinality(new MultiplicityInterval(getCardinality()));
            }
            else
                throw new RuntimeException("BmmTypeCreate failed for type " + typeDef.asTypeString() + " of property "
                    + getName() + " in class " + bmmClass.getName());
        }
    }

}
