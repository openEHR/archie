package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.BmmClass;
import org.openehr.bmm.core.BmmType;
import org.openehr.bmm.v2.validation.converters.BmmClassProcessor;

import java.util.List;

public abstract class PBmmType extends PBmmBase {

    /**
     * Effective unitary type, ignoring containers and also generic parameters
     */
    public abstract String baseType();

    /**
     * Formal name of the type for display.
     *
     * @return
     */
    public abstract String asTypeString();

    /**
     * Flattened list of type names making up full type.
     *
     * @return
     */
    public abstract List<String> flattenedTypeList();

    /**
     * build bmmType from classDefinition
     * @param classProcessor the classprocessor to use here
     * @param classDefinition the class definition in which this type is defined
     * @return
     */
    public abstract BmmType createBmmType(BmmClassProcessor classProcessor, BmmClass classDefinition);

}
