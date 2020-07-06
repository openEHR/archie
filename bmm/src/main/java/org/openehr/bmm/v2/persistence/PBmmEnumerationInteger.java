package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.*;

import java.util.ArrayList;

public final class PBmmEnumerationInteger extends PBmmEnumeration<Integer> {

    @Override
    public void createBmmClass() {
        bmmClass = new BmmEnumerationInteger (getName(), getDocumentation(), isAbstract());
        bmmClass.setSourceSchemaId (getSourceSchemaId());
    }

    /**
     * add default integer values 0 .. n-1 when none set
     */
    @Override
    protected void setDefaultItemValues() {
        itemValues = new ArrayList<>();
        for (int i = 0; i < bmmClass.getItemNames().size(); i++)
            itemValues.add(i);

        bmmClass.setItemValues(itemValues);
    }

}
