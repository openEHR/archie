package com.nedap.archie.rm;

import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.util.CloneUtil;

import java.io.Serializable;

/**
 * Common base class for all RM Objects. Should this be an interface instead?
 *
 * Created by pieter.bos on 01/03/16.
 */
public abstract class RMObject extends OpenEHRBase implements Serializable, Cloneable {

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public RMObject clone() {
        return CloneUtil.clone(this);
    }

}
