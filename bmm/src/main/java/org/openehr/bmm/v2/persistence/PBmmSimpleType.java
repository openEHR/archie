package org.openehr.bmm.v2.persistence;

import com.google.common.collect.Lists;
import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmSimpleClass;
import org.openehr.bmm.core.BmmSimpleType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.List;

public final class PBmmSimpleType extends PBmmUnitaryType {

    private String type;

    public PBmmSimpleType() {

    }

    public PBmmSimpleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Effective unitary type, ignoring containers and also generic parameters
     */
    @Override
    public String baseType() {
        return type;
    }

    @Override
    public BmmSimpleType createBmmType(BmmClassProcessor classProcessor, BmmClass classDefinition) {
        BmmClass simpleClassDef = classProcessor.getClassDefinition (type);
        if(simpleClassDef == null) {
            throw new RuntimeException("BmmClass " + type + " is not defined in this model");
        } else if (simpleClassDef instanceof BmmSimpleClass) {
            return new BmmSimpleType((BmmSimpleClass) simpleClassDef);
        } else {
            throw new RuntimeException("BmmClass " + type + " should be a simple type, but was " + simpleClassDef.getClass().getSimpleName());
        }
    }

    /**
     * Formal name of the type for display.
     *
     * @return
     */
    @Override
    public String asTypeString() {
        return type;
    }

    @Override
    public List<String> flattenedTypeList() {
        return Lists.newArrayList(type);
    }

}
