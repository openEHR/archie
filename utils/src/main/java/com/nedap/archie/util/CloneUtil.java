package com.nedap.archie.util;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.util.Pool;

import java.net.URI;

/**
 * Utility class for cloning objects.
 */
public class CloneUtil {
    private static final Pool<Kryo> pool = new Pool<Kryo>(true, false) {
        protected Kryo create() {
            Kryo kryo = new Kryo();
            kryo.setRegistrationRequired(false);
            kryo.setReferences(true);
            kryo.setCopyReferences(true);
            kryo.addDefaultSerializer(URI.class, new URISerializer());
            return kryo;
        }
    };

    /**
     * Returns a deep copy of the object.
     *
     * @param original Object to copy. May be null.
     * @return Deep copy of the object, or null if the original was null.
     */
    public static <T> T clone(T original) {
        Kryo kryo = pool.obtain();
        try {
            return kryo.copy(original);
        } finally {
            pool.free(kryo);
        }
    }

    private CloneUtil() {
        // Not allowed.
    }
}
