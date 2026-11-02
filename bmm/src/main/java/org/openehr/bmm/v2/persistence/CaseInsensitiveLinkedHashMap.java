package org.openehr.bmm.v2.persistence;

import java.util.*;

public class CaseInsensitiveLinkedHashMap<V>  implements Map<String, V> {

    LinkedHashMap<String,V> map = new LinkedHashMap<>();
    LinkedHashMap<String,String> lowerCaseKeyIndex = new LinkedHashMap<>();

    private String lowerCaseKey(String key) {
        return key == null ? null : key.toLowerCase();
    }

    private Object getMapKey(Object objectKey) {
        if(!(objectKey instanceof String)) {
            return objectKey;
        }
        String key = (String) objectKey;
        if(key == null) {
            return null;
        }
        String result = lowerCaseKeyIndex.get(lowerCaseKey(key));
        //in case of null values, return key
        return Objects.requireNonNullElse(result, key);
    }

    private String putMapKey(String key) {
        String internalKey = lowerCaseKey(key);
        if(lowerCaseKeyIndex.containsKey(internalKey)) {
            return lowerCaseKeyIndex.get(internalKey);
        }
        lowerCaseKeyIndex.put(internalKey, key);
        return key;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(getMapKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
       return  map.get(getMapKey(key));
    }

    @Override
    public V put(String key, V value) {
        String internalKey = putMapKey(key);
        return map.put(internalKey, value);
    }



    @Override
    public V remove(Object key) {
        return map.remove(getMapKey(key));
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        for(String key:m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public void clear() {
        map.clear();
        lowerCaseKeyIndex.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return map.entrySet();
    }
}
