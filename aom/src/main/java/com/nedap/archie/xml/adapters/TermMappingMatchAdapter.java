package com.nedap.archie.xml.adapters;

import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.ValidationException;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class TermMappingMatchAdapter extends XmlAdapter<String, Character> {
    @Override
    public Character unmarshal(String v) throws Exception {
        if(v == null) {
            return null;
        }
        if(v.isEmpty()) {
            return null;
        }
        if(v.length() > 1) {
            throw new ValidationException("Term mapping match can only be a single character, but was longer in XML: " + v);
        }
        return v.charAt(0);

    }

    @Override
    public String marshal(Character v) throws Exception {
        return v == null ? null : v.toString();
    }
}
