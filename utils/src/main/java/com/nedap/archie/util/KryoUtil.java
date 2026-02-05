package com.nedap.archie.util;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.util.Pool;
import com.nedap.archie.kryo.URISerializer;

import java.net.URI;

/**
 * Created by pieter.bos on 03/11/15.
 *
 * @deprecated This class will be removed. Use {@link CloneUtil} instead.
 */
@Deprecated
public class KryoUtil {

    // Build pool with SoftReferences enabled (optional)
    private static Pool<Kryo> pool;

    static {
        // Pool constructor arguments: thread safe, soft references, maximum capacity
        pool = new Pool<Kryo>(true, false) {
            protected Kryo create () {
                Kryo kryo = new Kryo();
                kryo.setRegistrationRequired(false);
                kryo.setReferences(true);
                kryo.setCopyReferences(true);
                kryo.addDefaultSerializer(URI.class, URISerializer.class);
                return kryo;
            }
        };

    }

    public static Pool<Kryo> getPool() {
        return pool;
    }
}
