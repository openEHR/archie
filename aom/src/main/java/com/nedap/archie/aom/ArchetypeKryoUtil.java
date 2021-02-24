package com.nedap.archie.aom;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.nedap.archie.xml.types.MapBackedList;

import java.util.LinkedHashMap;

public class ArchetypeKryoUtil {

    // Build pool with SoftReferences enabled (optional)
    private static KryoPool pool;

    static {
        KryoFactory factory = new KryoFactory() {
            public Kryo create() {
                Kryo kryo = new Kryo();
                kryo.register(MapBackedList.class, new Serializer<MapBackedList>() {

                    @Override
                    public void write(Kryo kryo, Output output, MapBackedList object) {
                        kryo.writeObjectOrNull(output, object == null ? null : object.getMap(), LinkedHashMap.class);
                    }

                    @Override
                    public MapBackedList read(Kryo kryo, Input input, Class<MapBackedList> type) {
                        LinkedHashMap r = kryo.readObjectOrNull(input, LinkedHashMap.class);
                        return r == null ? null : new MapBackedList(r);
                    }

                    @Override
                    public MapBackedList copy (Kryo kryo, MapBackedList original) {
                        if (isImmutable()) return original;
                        if(original == null) {
                            return null;
                        }
                        if(original.getMap() == null) {
                            return new MapBackedList(null);
                        }
                        return new MapBackedList(kryo.copy(original.getMap()));
                    }
                });
                return kryo;
            }
        };
        pool = new KryoPool.Builder(factory).softReferences().build();
    }

    public static KryoPool getPool() {
        return pool;
    }
}
