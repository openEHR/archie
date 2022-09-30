package com.nedap.archie.util;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.serializers.DefaultSerializers;
import com.esotericsoftware.kryo.kryo5.util.Pool;

/**
 * Created by pieter.bos on 03/11/15.
 */
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
                kryo.setDefaultSerializer(DefaultSerializers.URLSerializer.class);
                return kryo;
            }
        };

    }

    public static Pool<Kryo> getPool() {
        return pool;
    }
}
