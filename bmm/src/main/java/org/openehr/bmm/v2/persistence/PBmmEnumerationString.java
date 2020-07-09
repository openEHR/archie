package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmEnumerationInteger;
import org.openehr.bmm.core.BmmEnumerationString;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

public final class PBmmEnumerationString extends PBmmEnumeration<String> {

    @Override
    public BmmEnumerationString createBmmClass() {
        BmmEnumerationString bmmEnumerationString = new BmmEnumerationString(getName(), getDocumentation(), nullToFalse(isAbstract()));
        bmmEnumerationString.setSourceSchemaId(getSourceSchemaId());
        return bmmEnumerationString;
    }

}
