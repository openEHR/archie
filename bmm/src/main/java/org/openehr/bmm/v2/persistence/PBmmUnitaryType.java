package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmType;

public abstract class PBmmBaseType<T extends BmmType> extends PBmmType<T> {
    private String valueConstraint;

    public String getValueConstraint() {
        return valueConstraint;
    }

    public void setValueConstraint(String valueConstraint) {
        this.valueConstraint = valueConstraint;
    }
}
