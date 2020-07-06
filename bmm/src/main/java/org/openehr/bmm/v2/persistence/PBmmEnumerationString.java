package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmEnumerationString;

public final class PBmmEnumerationString extends PBmmEnumeration<String> {

    @Override
    public void createBmmClass() {
        bmmClass = new BmmEnumerationString(getName(), getDocumentation(), isAbstract());
        bmmClass.setSourceSchemaId (getSourceSchemaId());
    }

}
