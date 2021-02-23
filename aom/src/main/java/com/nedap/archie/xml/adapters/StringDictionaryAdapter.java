package com.nedap.archie.xml.adapters;

import com.nedap.archie.xml.types.StringDictionaryItem;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Map;

public class StringDictionaryAdapter extends XmlAdapter<ArrayList<StringDictionaryItem>, Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(ArrayList<StringDictionaryItem> v) throws Exception {
        return StringDictionaryUtil.convertStringDictionaryListToStringMap(v);
    }

    @Override
    public ArrayList<StringDictionaryItem> marshal(Map<String, String> v) throws Exception {
        return StringDictionaryUtil.convertStringMapIntoStringDictionaryList(v);
    }
}
