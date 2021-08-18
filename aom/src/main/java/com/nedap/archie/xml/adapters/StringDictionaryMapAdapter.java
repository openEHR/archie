package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.xml.types.StringDictionaryItem;
import com.nedap.archie.xml.types.XmlTranslationDetails;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StringDictionaryMapAdapter extends XmlAdapter<ArrayList<StringDictionaryItem>, LinkedHashMap<String, String>> {
    @Override
    public LinkedHashMap<String, String> unmarshal(ArrayList<StringDictionaryItem> v) throws Exception {
        return StringDictionaryUtil.convertStringDictionaryListToStringMap(v);
    }

    @Override
    public ArrayList<StringDictionaryItem> marshal(LinkedHashMap<String, String> v) throws Exception {
        return StringDictionaryUtil.convertStringMapIntoStringDictionaryList(v);
    }
}
