package org.openehr.bmm.core;

import com.nedap.archie.base.MultiplicityInterval;

/**
 * Subtype of BMM_CONTAINER_PROPERTY that represents an indexed container type based on one of the inbuilt types
 * Hash &lt;&gt;.
 */
public class BmmIndexedContainerProperty extends BmmProperty<BmmIndexedContainerType> {

    /**
     * We have to replicate cardinality here from BmmContainerProperty since we are inheriting from
     * BmmProperty &lt;BmmIndexedContainerType&gt; (which creates correct typing of the 'type' property) not BmmContainerProperty
     */
    private MultiplicityInterval cardinality;

    public BmmIndexedContainerProperty (String aName, BmmIndexedContainerType aType, String aDocumentation, boolean isMandatoryFlag, boolean isComputedFlag) {
        super(aName, aType, aDocumentation, isMandatoryFlag, isComputedFlag);
        cardinality = MultiplicityInterval.createOpen();
    }

    public MultiplicityInterval getCardinality() {
        return cardinality;
    }
    public void setCardinality(MultiplicityInterval cardinality) {
        this.cardinality = cardinality;
    }
}
