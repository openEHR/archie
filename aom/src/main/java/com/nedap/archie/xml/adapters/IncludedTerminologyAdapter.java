package com.nedap.archie.xml.adapters;

import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.xml.types.XmlIncludedTerminology;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IncludedTerminologyAdapter {

    public static List<XmlIncludedTerminology> marshal(Map<String, ArchetypeTerminology> v) throws Exception {
        ArrayList<XmlIncludedTerminology> result = new ArrayList<>();
        for(String id:v.keySet()) {
            ArchetypeTerminology terminology = v.get(id);
            if(terminology != null) {
                XmlIncludedTerminology xmlTerm = new XmlIncludedTerminology();
                xmlTerm.setId(id);
                ArchetypeTerminologyAdapter.toXml(terminology, xmlTerm);
                result.add(xmlTerm);
            }

        }
        return result;
    }

    public static Map<String, ArchetypeTerminology> unmarshal(List<XmlIncludedTerminology> v) throws Exception {
        Map<String, ArchetypeTerminology> result = new LinkedHashMap<>();
        for(XmlIncludedTerminology t:v) {
            ArchetypeTerminology terminology = ArchetypeTerminologyAdapter.fromXml(t);
            result.put(t.getId(), terminology);
        }
        return result;
    }
}
