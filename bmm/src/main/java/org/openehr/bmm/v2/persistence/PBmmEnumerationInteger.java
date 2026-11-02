package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmEnumeration;
import org.openehr.bmm.core.BmmEnumerationInteger;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PBmmEnumerationInteger extends PBmmEnumeration<Integer> {

    @Override
    public BmmEnumerationInteger createBmmClass() {
        BmmEnumerationInteger bmmEnumerationInteger = new BmmEnumerationInteger (getName(), getDocumentation(), nullToFalse(isAbstract()));
        bmmEnumerationInteger.setSourceSchemaId (getSourceSchemaId());
        bmmEnumerationInteger.setOverride(nullToFalse(isOverride()));
        return bmmEnumerationInteger;
    }

    /**
     * add default integer values 0 .. n-1 when none set
     */
    @Override
    protected void setDefaultItemValues(BmmEnumeration<Integer> bmmClass) {
        bmmClass.setItemValues(IntStream.rangeClosed(0, getItemNames().size()).boxed().collect(Collectors.toList()));
    }

}
