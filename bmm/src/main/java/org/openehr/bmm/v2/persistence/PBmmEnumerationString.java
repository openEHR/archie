package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmEnumerationString;

public final class PBmmEnumerationString extends PBmmEnumeration<String> {

    @Override
    public BmmEnumerationString createBmmClass() {
        BmmEnumerationString bmmClass = new BmmEnumerationString(getName(), getDocumentation(), isAbstract());
        bmmClass.setSourceSchemaId (getSourceSchemaId());
        return bmmClass;
    }

}
