package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmEnumerationString;

public final class PBmmEnumerationString extends PBmmEnumeration<String> {

    @Override
    public BmmEnumerationString createBmmClass() {
        BmmEnumerationString bmmEnumerationString = new BmmEnumerationString(getName(), getDocumentation(), nullToFalse(isAbstract()));
        bmmEnumerationString.setSourceSchemaId(getSourceSchemaId());
        bmmEnumerationString.setOverride(nullToFalse(isOverride()));
        return bmmEnumerationString;
    }

}
