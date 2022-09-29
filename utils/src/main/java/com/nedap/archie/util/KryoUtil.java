package com.nedap.archie.util;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.Serializer;
import com.esotericsoftware.kryo.kryo5.util.Pool;

import java.util.HashMap;

/**
 * Created by pieter.bos on 03/11/15.
 */
public class KryoUtil {

    // Build pool with SoftReferences enabled (optional)
    private static final Pool<Kryo> pool;

    @SuppressWarnings("rawtypes")
    private static final HashMap<Class, Serializer> serializers = new HashMap<>();

    static {
        // Pool constructor arguments: thread safe, soft references, maximum capacity
        pool = new Pool<Kryo>(true, false) {
            protected Kryo create() {
                Kryo kryo = new Kryo();

                kryo.setRegistrationRequired(false);
                kryo.setReferences(true);
                kryo.setCopyReferences(true);

                serializers.forEach(kryo::register);

                return kryo;
            }
        };

    }

    public static Pool<Kryo> getPool() {
        return pool;
    }

    @SuppressWarnings("rawtypes")
    public static void addSerializer(Class clazz, Serializer serializer) {
        serializers.put(clazz, serializer);
    }
}
