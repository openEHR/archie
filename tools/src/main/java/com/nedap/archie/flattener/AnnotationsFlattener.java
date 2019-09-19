package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.ResourceAnnotations;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationsFlattener {

    public void flatten(Archetype parent, Archetype child, Archetype result) {
        if( (isAnnotationsEmpty(parent) && isAnnotationsEmpty(child))) {
            return;
        }
        ResourceAnnotations resultAnnotation = ensureAnnotationsPresent(result);
        Map<String, Map<String, Map<String, String>>> resultDocumentation = resultAnnotation.getDocumentation();

        mergeInAnnotations(parent, resultDocumentation);
        mergeInAnnotations(child, resultDocumentation);
    }

    private ResourceAnnotations ensureAnnotationsPresent(Archetype result) {
        ResourceAnnotations resultAnnotation = result.getAnnotations();
        if(resultAnnotation == null) {
            resultAnnotation = new ResourceAnnotations();
            result.setAnnotations(resultAnnotation);
        }
        if(resultAnnotation.getDocumentation() == null) {
            resultAnnotation.setDocumentation(new LinkedHashMap<>());
        }
        return resultAnnotation;
    }

    /**
     * Merge the annotations of the given archetype ino the given result documentation
     * @param toBeMergedIn the archetype for which the annotation should be merged in
     * @param resultDocumentation the resulting documentation in which the archetypes are to be merged in. Must be a non-null editable Map.
     */
    private void mergeInAnnotations(Archetype toBeMergedIn, Map<String, Map<String, Map<String, String>>> resultDocumentation) {
        if(!isAnnotationsEmpty(toBeMergedIn)) {
            ResourceAnnotations toBeMergedInAnnotations = toBeMergedIn.getAnnotations();
            Map<String, Map<String, Map<String, String>>> toBeMergedInDocumentation = toBeMergedInAnnotations.getDocumentation();

            mergeDocumentation(resultDocumentation, toBeMergedInDocumentation);
        }
    }

    /**
     * Merges the second map into the first one, overwriting all keys already present, adding keys when not present.
     * Structure of maps must be:
     *   language -> path -> annotation name -> annotation value
     *   eg:
     *   nl -> /path/to/something -> design note -> text of design note
     * @param resultDocumentation the resulting documentation to be updated. Must be an empty map
     * @param documentationToBeMergedIn the documentation to be copied in the resulting documentation
     */
    private void mergeDocumentation(Map<String, Map<String, Map<String, String>>> resultDocumentation, Map<String, Map<String, Map<String, String>>> documentationToBeMergedIn) {
        for(String language:documentationToBeMergedIn.keySet()) {
            Map<String, Map<String, String>> languageAnnotationsToBeMergedIn = documentationToBeMergedIn.get(language);
            Map<String, Map<String, String>> resultLanguageAnnotations = resultDocumentation.get(language);
            if(resultLanguageAnnotations == null) {
                resultLanguageAnnotations = new LinkedHashMap<>();
                resultDocumentation.put(language, resultLanguageAnnotations);
            }
            for(String path: languageAnnotationsToBeMergedIn.keySet()) {
                Map<String, String> pathAnnotationsToBeMergedIn = languageAnnotationsToBeMergedIn.get(path);
                Map<String, String> resultPathAnnotations = resultLanguageAnnotations.get(path);
                if(resultPathAnnotations == null) {
                    resultPathAnnotations = new LinkedHashMap<>();
                    resultLanguageAnnotations.put(path, resultPathAnnotations);
                }
                resultPathAnnotations.putAll(pathAnnotationsToBeMergedIn);
            }
        }
    }

    private boolean isAnnotationsEmpty(Archetype archetype) {
        return archetype.getAnnotations() == null ||
                archetype.getAnnotations().getDocumentation() == null ||
                archetype.getAnnotations().getDocumentation().isEmpty();
    }

    public void addAnnotationsWithPathPrefix(String pathPrefix, Archetype archetype, OperationalTemplate result) {
        if(isAnnotationsEmpty(archetype)) {
            return;
        }
        Map<String, Map<String, Map<String, String>>> documentationToBeMergedIn = archetype.getAnnotations().getDocumentation();
        for(String language: documentationToBeMergedIn.keySet()) {

            Map<String, Map<String, String>> languageAnnotationsToBeMergedIn = documentationToBeMergedIn.get(language);
            for(String path: languageAnnotationsToBeMergedIn.keySet()) {
                String newPath = ensureNoSlashAtEnd(pathPrefix) + path;
                merge(language, newPath, languageAnnotationsToBeMergedIn.get(path), result);
            }
        }

    }

    private void merge(String language, String newPath, Map<String, String> annotationsMap, OperationalTemplate result) {
        ResourceAnnotations annotations = ensureAnnotationsPresent(result);
        Map<String, Map<String, String>> languageMap = annotations.getDocumentation().get(language);
        if(languageMap == null) {
            languageMap = new LinkedHashMap<>();
            annotations.getDocumentation().put(language, languageMap);
        }
        Map<String, String> existingAnnotations = languageMap.get(newPath);
        if(existingAnnotations == null) {
            existingAnnotations = new LinkedHashMap<>(annotationsMap);
            languageMap.put(newPath, existingAnnotations);
        } else {
            existingAnnotations.putAll(annotationsMap);
        }
    }

    private String ensureNoSlashAtEnd(String pathPrefix) {
        if(pathPrefix.endsWith("/")) {
            return pathPrefix.substring(0, pathPrefix.length() - 1);
        }
        return pathPrefix;
    }
}
