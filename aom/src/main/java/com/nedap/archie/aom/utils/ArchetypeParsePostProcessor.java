package com.nedap.archie.aom.utils;

import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;

import java.util.Map;
import java.util.Stack;

/**
 * Sets some values that are not directly in ADL or ODIN, such as original language in terminology, etc.
 */
public class ArchetypeParsePostProcessor {

    public static void fixArchetype(Archetype archetype) {
        if (archetype.getTerminology() != null) {
            ArchetypeTerminology terminology = archetype.getTerminology();
            //codes are in model, but do not appear in odin. Set them here
            fillArchetypeTermCodes(terminology.getTermDefinitions());
            fillArchetypeTermCodes(terminology.getTerminologyExtracts());
            archetype.getTerminology().setConceptCode(archetype.getDefinition().getNodeId());
            String originalLanguage = null;
            if (archetype.getOriginalLanguage() != null) {
                originalLanguage = archetype.getOriginalLanguage().getCodeString();
            }
            archetype.getTerminology().setOriginalLanguage(originalLanguage);
        }
        setParents(archetype);
    }

    private static void setParents(Archetype archetype) {
        Stack<CObject> workList = new Stack<>();
        workList.add(archetype.getDefinition());
        while (!workList.empty()) {
            CObject cObject = workList.pop();
            if (cObject instanceof CPrimitiveObject) {
                cObject.setNodeId("id9999");//also in the implementation, but check to be sure
            }
            for (CAttribute attribute : cObject.getAttributes()) {

                attribute.setParent(cObject);
                for (CObject child : attribute.getChildren()) {
                    child.setParent(attribute);
                }
                workList.addAll(attribute.getChildren());
            }
            if (cObject instanceof CComplexObject) {
                //TODO: fix tuple so it has proper multiple references to the separate structures, instead of a complete duplication of data in json
                CComplexObject cComplexObject = (CComplexObject) cObject;
                for (CAttributeTuple tuple : cComplexObject.getAttributeTuples()) {
                    for (CAttribute attribute : tuple.getMembers()) {
                        attribute.setSocParent(tuple);
                        attribute.setParent(cObject);
                        workList.addAll(attribute.getChildren());
                        cComplexObject.replaceAttribute(attribute);
                    }
                    for (CPrimitiveTuple primitiveTuple : tuple.getTuples()) {
                        int index = 0;
                        for (CPrimitiveObject<?, ?> object : primitiveTuple.getMembers()) {
                            if (index < tuple.getMembers().size()) {
                                CAttribute attribute = tuple.getMember(index);
                                object.setSocParent(primitiveTuple);
                                object.setParent(attribute);
                                index++;
                            }

                        }
                    }
                }

            }
        }
    }

    private static void fillArchetypeTermCodes(Map<String, Map<String, ArchetypeTerm>> termSet) {
        if (termSet != null) {
            for (Map<String, ArchetypeTerm> language : termSet.values()) {
                for (String term : language.keySet()) {
                    language.get(term).setCode(term);
                }
            }
        }
    }
}
