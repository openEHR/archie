package com.nedap.archie.creation;

import com.nedap.archie.aom.CObject;
import com.nedap.archie.rminfo.ModelInfoLookup;

/**
 * Utility to create Reference model objects based on their RM name.
 *
 * Created by pieter.bos on 03/02/16.
 */
public class RMObjectCreator {

    private final ModelInfoLookup modelInfoLookup;

    public RMObjectCreator(ModelInfoLookup lookup) {
        this.modelInfoLookup = lookup;
    }

    public <T> T create(CObject constraint) {
        Class<?> clazz = modelInfoLookup.getClassToBeCreated(constraint.getRmTypeName());
        if(clazz == null) {
            throw new IllegalArgumentException("cannot construct RMObject because of unknown constraint name " + constraint.getRmTypeName() + " full constraint " + constraint);
        }
        try {
            Object result = clazz.newInstance();

            modelInfoLookup.processCreatedObject(result, constraint);
            return (T) result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("error creating class " + constraint.getRmTypeName(), e);
        }
    }

}
