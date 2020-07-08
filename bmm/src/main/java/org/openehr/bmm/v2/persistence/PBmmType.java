package org.openehr.bmm.v2.persistence;

import org.openehr.bmm.core.*;

import java.util.List;

public abstract class PBmmType<T extends BmmType> extends PBmmBase {

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
     * @param schema
     * @param classDefinition
     */
    public abstract T createBmmType(BmmModel schema, BmmClass classDefinition);

}
