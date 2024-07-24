package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeConstraint;
import com.nedap.archie.rules.Assertion;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlattenerUtil {

    public static List<Assertion> getPossiblyOverridenListValue(List<Assertion> parent, List<Assertion> child) {
        if(child != null && !child.isEmpty()) {
            return child;
        }
        return parent;
    }

    public static <T> T getPossiblyOverridenValue(T parent, T specialized) {
        if(specialized != null) {
            return specialized;
        }
        return parent;
    }

    /**
     * Removes annotations for the objects given, also removes annotations for children underneath the objects.
     */
    public static void removeAnnotationsForArchetypeConstraints(Archetype result, List<? extends ArchetypeConstraint> archetypeConstraints) {
        if (result.getAnnotations() == null ||
                result.getAnnotations().getDocumentation() == null ||
                result.getAnnotations().getDocumentation().isEmpty() ||
                archetypeConstraints.isEmpty()
        ) {
            return;
        }

        removeAnnotationsForPaths(result.getAnnotations().getDocumentation(), archetypeConstraints.stream().map(ArchetypeConstraint::getPath).collect(Collectors.toList()));
    }

    private static void removeAnnotationsForPaths(Map<String, Map<String, Map<String, String>>> annotations, List<String> pathsToRemove) {
        for (String pathToRemove : pathsToRemove) {
            for (String languageKeys : annotations.keySet()) {
                Map<String, Map<String, String>> languageAnnotations = annotations.get(languageKeys);
                List<String> toRemove = languageAnnotations.keySet().stream().filter(path -> path.startsWith(pathToRemove)).collect(Collectors.toList());
                toRemove.forEach(languageAnnotations::remove);
            }

        }
    }
}
