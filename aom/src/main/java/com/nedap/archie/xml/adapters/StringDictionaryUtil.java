package com.nedap.archie.xml.adapters;

import com.nedap.archie.xml.types.StringDictionaryItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 01/08/16.
 */
public class StringDictionaryUtil {
    
    public static ArrayList<StringDictionaryItem> convertUriMapIntoStringDictionaryList(Map<String, URI> terms) {
        if(terms == null) {
            return null;
        }
        ArrayList<StringDictionaryItem> items = new ArrayList<>();
        for(String termId: terms.keySet()) {
            StringDictionaryItem item = new StringDictionaryItem();
            item.setId(termId);
            item.setValue(terms.get(termId).toString());
            items.add(item);
        }
        return items;
    }

    public static ArrayList<StringDictionaryItem> convertStringMapIntoStringDictionaryList(Map<String, String> map) {
        if(map == null) {
            return null;
        }
        ArrayList<StringDictionaryItem> items = new ArrayList<>();
        for(String termId: map.keySet()) {
            StringDictionaryItem item = new StringDictionaryItem();
            item.setId(termId);
            item.setValue(map.get(termId));
            items.add(item);
        }
        return items;
    }

    public static Map<String, URI> convertStringDictionaryListToUriMap(List<StringDictionaryItem> items) throws URISyntaxException {
        if(items == null) {
            return null;
        }
        Map<String, URI> termMap = new LinkedHashMap<>();
        for(StringDictionaryItem term:items) {
            termMap.put(term.getId(), new URI(term.getValue()));
        }
        return termMap;
    }

    public static Map<String, String> convertStringDictionaryListToStringMap(List<StringDictionaryItem> items) {
        if(items == null) {
            return null;
        }
        Map<String, String> termMap = new LinkedHashMap<>();
        for(StringDictionaryItem term:items) {
            termMap.put(term.getId(),term.getValue());
        }
        return termMap;
    }
}
