package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.xml.types.XmlTranslationDetails;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by pieter.bos on 01/08/16.
 */
public class TranslationDetailsAdapter extends XmlAdapter<XmlTranslationDetails, TranslationDetails> {
    @Override
    public TranslationDetails unmarshal(XmlTranslationDetails v) throws Exception {
        TranslationDetails result = new TranslationDetails();
        result.setAuthor(StringDictionaryUtil.convertStringDictionaryListToStringMap(v.getAuthor()));
        result.setOtherDetails(StringDictionaryUtil.convertStringDictionaryListToStringMap(v.getOtherDetails()));
        result.setAccreditation(v.getAccreditation());
        result.setVersionLastTranslated(v.getVersionLastTranslated());
        result.setLanguage(v.getLanguage());
        return result;
    }

    @Override
    public XmlTranslationDetails marshal(TranslationDetails v) throws Exception {
        XmlTranslationDetails result = new XmlTranslationDetails();
        result.setAuthor(StringDictionaryUtil.convertStringMapIntoStringDictionaryList(v.getAuthor()));
        result.setOtherDetails(StringDictionaryUtil.convertStringMapIntoStringDictionaryList(v.getOtherDetails()));
        result.setAccreditation(v.getAccreditation());
        result.setVersionLastTranslated(v.getVersionLastTranslated());
        result.setLanguage(v.getLanguage());
        return result;
    }
}
