package com.nedap.archie.rminfo;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CObject;

import java.util.Map;

/**
 * @deprecated For backwards compatibility only.
 */
@Deprecated
public class BackwardsCompatibleRmObjectProcessor implements RmObjectProcessor {
    private final ModelInfoLookup modelInfoLookup;

    public BackwardsCompatibleRmObjectProcessor(ModelInfoLookup modelInfoLookup) {
        this.modelInfoLookup = modelInfoLookup;
    }

    @Override
    public void processCreatedObject(Object createdObject, CObject constraint) {
        modelInfoLookup.processCreatedObject(createdObject, constraint);
    }

    @Override
    public Map<String, Object> processUpdatedObject(
            Object rootRmObject,
            Archetype archetype,
            String path,
            Object updatedObject
    ) {
        return modelInfoLookup.pathHasBeenUpdated(rootRmObject, archetype, path, updatedObject);
    }
}
