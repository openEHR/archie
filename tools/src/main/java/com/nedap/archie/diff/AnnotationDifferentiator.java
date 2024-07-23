package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;

import java.util.Iterator;
import java.util.Map;

public class AnnotationDifferentiator {

    /**
     * Keeps all annotations in the result, that are not in the flatParent
     * Remove annotations that can also be found in the parent
     */
    public void differentiate(Archetype result, Archetype flatParent) {
        if (result.getAnnotations() == null) {
            return;
        }
        if (result.getAnnotations().getDocumentation() == null) {
            result.setAnnotations(null);
            return;
        }
        if (flatParent.getAnnotations() == null || flatParent.getAnnotations().getDocumentation() == null) {
            return;
        }

        // Remove common entries in the annotations map
        removeCommonEntries(result.getAnnotations().getDocumentation(), flatParent.getAnnotations().getDocumentation());

        // If after removing common entries the documentation is empty, just set the annotations to null
        if (result.getAnnotations().getDocumentation().isEmpty()) {
            result.setAnnotations(null);
        }
    }

    private <V> void removeCommonEntries(Map<String, V> subValueA, Map<String, V> subValueB) {
        Iterator<Map.Entry<String, V>> iterator = subValueA.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, V> entryA = iterator.next();
            String keyA = entryA.getKey();
            V valueA = entryA.getValue();

            if (subValueB.containsKey(keyA)) {
                V valueB = subValueB.get(keyA);

                if (valueA.equals(valueB)) {
                    iterator.remove();
                } else if (valueA instanceof Map && valueB instanceof Map) {
                    removeCommonEntries((Map<String, V>) valueA, (Map<String, V>) valueB);
                }
            }
        }
    }
}
