package com.nedap.archie.xml.adapters;

import com.google.common.collect.Lists;
import com.nedap.archie.xml.types.StringDictionaryItem;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.Map;

public class StringDictionaryAdapter extends XmlAdapter<StringDictionaryItem[], Map<String, String>> {

    @Override
    public Map<String, String> unmarshal(StringDictionaryItem[] v) throws Exception {
        return StringDictionaryUtil.convertStringDictionaryListToStringMap(Lists.newArrayList(v));
    }

    @Override
    public StringDictionaryItem[] marshal(Map<String, String> v) throws Exception {
        return StringDictionaryUtil.convertStringMapIntoStringDictionaryList(v).toArray(new StringDictionaryItem[v.size()]);
    }
}
