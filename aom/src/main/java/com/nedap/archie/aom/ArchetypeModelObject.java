package com.nedap.archie.aom;

import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.util.CloneUtil;

import java.io.Serializable;

/**
 * Common root class for all archetype objects - so we do not have to use java.lang.Object!
 * Created by pieter.bos on 15/10/15.
 */
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ArchetypeModelObject extends OpenEHRBase implements Serializable, Cloneable {

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ArchetypeModelObject clone() {
        return CloneUtil.clone(this);
    }

}
