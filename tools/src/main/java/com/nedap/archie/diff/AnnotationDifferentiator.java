package com.nedap.archie.diff;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceAnnotations;

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
        removeCommonEntriesLvl1(result.getAnnotations().getDocumentation(), flatParent.getAnnotations().getDocumentation());

        // If after removing common entries the documentation is empty, just set the annotations to null
        if (result.getAnnotations().getDocumentation().isEmpty()) {
            result.setAnnotations(null);
        }
    }

    private void removeCommonEntriesLvl1(Map<String, Map<String, Map<String, String>>> A,
                                            Map<String, Map<String, Map<String, String>>> B) {
        Iterator<Map.Entry<String, Map<String, Map<String, String>>>> iterator = A.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Map<String, Map<String, String>>> entryA = iterator.next();
            String keyA = entryA.getKey();
            Map<String, Map<String, String>> valueA = entryA.getValue();

            if (B.containsKey(keyA)) {
                Map<String, Map<String, String>> valueB = B.get(keyA);

                if (valueA.equals(valueB)) {
                    iterator.remove();
                } else {
                    removeCommonEntriesLvl2(valueA, valueB);
                }
            }
        }
    }

    private void removeCommonEntriesLvl2(Map<String, Map<String, String>> valueA,
                                            Map<String, Map<String, String>> valueB) {
        Iterator<Map.Entry<String, Map<String, String>>> iterator = valueA.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Map<String, String>> entryA = iterator.next();
            String keyA = entryA.getKey();
            Map<String, String> subValueA = entryA.getValue();

            if (valueB.containsKey(keyA)) {
                Map<String, String> subValueB = valueB.get(keyA);

                if (subValueA.equals(subValueB)) {
                    iterator.remove();
                } else {
                    removeCommonEntriesLvl3(subValueA, subValueB);
                }
            }
        }
    }

    private void removeCommonEntriesLvl3(Map<String, String> subValueA,
                                            Map<String, String> subValueB) {
        Iterator<Map.Entry<String, String>> iterator = subValueA.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entryA = iterator.next();
            String keyA = entryA.getKey();
            String valueA = entryA.getValue();

            if (subValueB.containsKey(keyA)) {
                String valueB = subValueB.get(keyA);

                if (valueA.equals(valueB)) {
                    iterator.remove();
                }
            }
        }
    }
}
