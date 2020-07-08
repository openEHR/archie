package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.*;

import java.util.ArrayList;

public final class PBmmEnumerationInteger extends PBmmEnumeration<Integer> {

    @Override
    public BmmEnumerationInteger createBmmClass() {
        BmmEnumerationInteger bmmClass = new BmmEnumerationInteger (getName(), getDocumentation(), isAbstract());
        bmmClass.setSourceSchemaId (getSourceSchemaId());
        return bmmClass;
    }

    /**
     * add default integer values 0 .. n-1 when none set
     */
    @Override
    protected void setDefaultItemValues(BmmEnumeration bmmClass) {
        itemValues = new ArrayList<>();
        for (int i = 0; i < bmmClass.getItemNames().size(); i++)
            itemValues.add(i);

        bmmClass.setItemValues(itemValues);
    }

}
