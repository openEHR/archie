package com.nedap.archie.flattener;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.aom.rmoverlay.RmAttributeVisibility;
import com.nedap.archie.aom.rmoverlay.RmOverlay;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnnotationsAndOverlaysFlattener {

    public void flattenAnnotations(Archetype parent, Archetype child, Archetype result) {
        if( (isAnnotationsEmpty(parent) && isAnnotationsEmpty(child))) {
            return;
        }
        ResourceAnnotations resultAnnotation = ensureAnnotationsPresent(result);
        Map<String, Map<String, Map<String, String>>> resultDocumentation = resultAnnotation.getDocumentation();

        mergeInAnnotations(parent, resultDocumentation);
        mergeInAnnotations(child, resultDocumentation);
    }

    public void flattenRmOverlay(Archetype parent, Archetype child, Archetype result) {
        if( (isRmOverlayEmpty(parent) && isRmOverlayEmpty(child))) {
            return;
        }
        RmOverlay resultOverlay = ensureRmOverlayPresent(result);
        Map<String, RmAttributeVisibility> resultVisibility = resultOverlay.getRmVisibility();

        mergeInVisibility(parent, resultVisibility);
        mergeInVisibility(child, resultVisibility);
    }

    public void addVisibilityWithPathPrefix(String pathPrefix, Archetype archetype, OperationalTemplate result) {
        if(isRmOverlayEmpty(archetype)) {
            return;
        }
        ensureRmOverlayPresent(result);

        Map<String, RmAttributeVisibility> rmVisibilityToBeMergedIn = archetype.getRmOverlay().getRmVisibility();

        for(String path: rmVisibilityToBeMergedIn.keySet()) {
            String newPath = ensureNoSlashAtEnd(pathPrefix) + path;
            result.getRmOverlay().getRmVisibility().put(newPath, (RmAttributeVisibility) rmVisibilityToBeMergedIn.get(path).clone());
        }
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

    /* visibility private methods */
    private void mergeInVisibility(Archetype toBeMergedIn, Map<String, RmAttributeVisibility> resultVisibility) {
        if(!isRmOverlayEmpty(toBeMergedIn)) {
            RmOverlay toBeMergedInRmOverlay = toBeMergedIn.getRmOverlay();
            Map<String, RmAttributeVisibility> toBeMergedInRmVisibility = toBeMergedInRmOverlay.getRmVisibility();
            mergeVisibility(resultVisibility, toBeMergedInRmVisibility);
        }
    }

    private void mergeVisibility(Map<String, RmAttributeVisibility> resultVisibility, Map<String, RmAttributeVisibility> toBeMergedInRmVisibility) {
        for(String path:toBeMergedInRmVisibility.keySet()) {
            RmAttributeVisibility toBeMergedInVisibility = toBeMergedInRmVisibility.get(path);
            RmAttributeVisibility targetVisibility = resultVisibility.get(path);
            if(targetVisibility == null) {
                targetVisibility = (RmAttributeVisibility) toBeMergedInVisibility.clone();
                resultVisibility.put(path, targetVisibility);
            } else {
                //two visibilities. One should override the other?
                targetVisibility = (RmAttributeVisibility) toBeMergedInVisibility.clone();
                resultVisibility.put(path, targetVisibility);
            }
        }
    }

    private RmOverlay ensureRmOverlayPresent(Archetype result) {
        if(result.getRmOverlay() == null) {
            result.setRmOverlay(new RmOverlay());
        }
        if(result.getRmOverlay().getRmVisibility() == null) {
            result.getRmOverlay().setRmVisibility(new LinkedHashMap<>());
        }
        return result.getRmOverlay();
    }

    private boolean isRmOverlayEmpty(Archetype archetype) {
        return archetype.getRmOverlay() == null
                || archetype.getRmOverlay().getRmVisibility() == null
                || archetype.getRmOverlay().getRmVisibility().isEmpty();
    }



    /* annotations private methods */

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

    /* shared private methods */
    private String ensureNoSlashAtEnd(String pathPrefix) {
        if(pathPrefix.endsWith("/")) {
            return pathPrefix.substring(0, pathPrefix.length() - 1);
        }
        return pathPrefix;
    }
}
