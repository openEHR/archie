package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.*;

import java.util.List;

public class PBmmEnumeration<ItemType> extends PBmmClass<BmmEnumeration> {

    private List<String> itemNames;
    protected List<ItemType> itemValues;

    public List<String> getItemNames() {
        return itemNames;
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    public List<ItemType> getItemValues() {
        return itemValues;
    }

    public void setItemValues(List<ItemType> itemValues) {
        this.itemValues = itemValues;
    }

    @Override
    public BmmEnumeration createBmmClass() {
        BmmEnumeration bmmClass = new BmmEnumeration(getName(), getDocumentation(), nullToFalse(isAbstract()));
        bmmClass.setSourceSchemaId(getSourceSchemaId());
        return bmmClass;
    }

    @Override
    public BmmEnumeration populateBmmClass(BmmModel schema) {
        BmmEnumeration bmmClass = super.populateBmmClass(schema);
        if (bmmClass != null) {
            bmmClass.setItemNames(itemNames);
            if (itemValues != null) {
                bmmClass.setItemValues(itemValues);
            } else {
                setDefaultItemValues(bmmClass);
            }
        }
        return bmmClass;
    }

    /**
     * add default values
     */
    protected void setDefaultItemValues(BmmEnumeration bmmClass) {
        // implement in descendants
    }

}
