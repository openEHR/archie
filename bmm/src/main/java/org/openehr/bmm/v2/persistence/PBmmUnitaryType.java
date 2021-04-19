package org.openehr.bmm.v2.persistence;

public abstract class PBmmUnitaryType extends PBmmType {

    private String valueConstraint;

    public String getValueConstraint() {
        return valueConstraint;
    }

    public void setValueConstraint(String valueConstraint) {
        this.valueConstraint = valueConstraint;
    }
}
