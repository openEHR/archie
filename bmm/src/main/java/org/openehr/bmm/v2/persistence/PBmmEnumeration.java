package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmEnumeration;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.List;

public class PBmmEnumeration<ItemType> extends PBmmClass {

    private List<String> itemNames;
    private List<ItemType> itemValues;

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
    public BmmEnumeration<ItemType> createBmmClass() {
        BmmEnumeration<ItemType> newEnumeration = new BmmEnumeration<>(getName(), getDocumentation(), nullToFalse(isAbstract()));
        newEnumeration.setSourceSchemaId(getSourceSchemaId());
        newEnumeration.setOverride(nullToFalse(isOverride()));
        return newEnumeration;
    }

    @Override
    public BmmEnumeration<ItemType> populateBmmClass(BmmClassProcessor classProcessor, PBmmSchema schema) {
        BmmEnumeration<ItemType> bmmClass = (BmmEnumeration<ItemType>) super.populateBmmClass(classProcessor, schema);
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
    protected void setDefaultItemValues(BmmEnumeration<ItemType> bmmClass) {
        // implement in descendants
    }

}
