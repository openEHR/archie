package com.nedap.archie.base;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.nedap.archie.util.KryoUtil;

import java.io.Serializable;

/**
 * Common base class for all RM Objects. Should this be an interface instead?
 *
 * Created by pieter.bos on 01/03/16.
 */
public abstract class RMObject extends OpenEHRBase implements Serializable, Cloneable {

    public RMObject clone() {
        Kryo kryo = null;
        try {
            kryo = KryoUtil.getPool().obtain();
            return kryo.copy(this);
        } finally {
            KryoUtil.getPool().free(kryo);
        }
    }

}
