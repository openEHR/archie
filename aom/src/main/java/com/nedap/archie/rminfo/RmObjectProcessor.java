package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;

import java.util.Map;

public interface RmObjectProcessor {
    /**
     * Callback after an RM Object has been created based on a constraint. Can for example be used
     * to set names or archetype ID Node values
     */
    void processCreatedObject(Object createdObject, CObject constraint);

    /**
     * Perform any actions necessary if the value at the given path has just been updated
     * For example, if an ordinal value has been set, this method should also set the symbol.
     * <p>
     * In addition to changing the actual values, it returns which additional paths have been updated as well.
     * For example, if an ordinal's symbol was updated, it will update both the value and the symbol of that ordinal
     * and return the value's path and updated value. This is done to obtain a full set of instructions of what must be
     * changed due to the rule evaluation.
     * <p>
     * This can be the most complex operation of this entire class to implement. If you just throw an exception instead
     * of implementing it everything will work fine except for the rule evaluation.
     * <p>
     * For now this is only needed in the rule evaluation to automatically fix assertions
     *
     * @return Each key is a path that was updated as a result of the previously updated path and each corresponding
     * value is this path's updated value
     */
    Map<String, Object> pathHasBeenUpdated(Object rmObject, Archetype archetype, String pathOfParent, Object parent);
}
