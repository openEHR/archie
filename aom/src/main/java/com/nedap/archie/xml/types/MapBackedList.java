package com.nedap.archie.xml.types;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBackedList<K,V> extends AbstractList<V> {

    private LinkedHashMap<K,V> data;

    public MapBackedList() {
        this.data = new LinkedHashMap<>();
    }

    public MapBackedList(Map<K,V> map) {
        this.data = new LinkedHashMap<>(map);
    }

    @Override
    public V get(int index) {
        Iterator<V> values = data.values().iterator();
        for(int i = 0; i < index;i++) {
            values.next();
        }
        return values.next();
    }

    @Override
    public int size() {
        return data.size();
    }

    public void put(K key, V value) {
        data.put(key, value);
    }

    public void removeKey(K key) {
        data.remove(key);
    }

    public LinkedHashMap<K, V> getMap() {
        return data;
    }
}
