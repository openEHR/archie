package com.nedap.archie.opt14;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.rmoverlay.RmAttributeVisibility;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.paths.PathUtil;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.query.PartialMatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TODO: implement
 * Get paths from annotations and RM overlay
 * Do a partial lookup: find through the archetype and find the longest matching path
 * When encountering archetype root + archetype ref in path, lookup through the inheritance tree to find a matching archetype as well as the direct archetype id.
 *
 * then call getPath() on the found node, and replace the original content of the path with the newly converted path, which will be a more unique version, no longer
 * reliant on name/value=..., but id codes only
 */
public class Opt14PathConverter {

    private Template template;

    public void convertPaths(Template template, OperationalTemplate opt) {
        this.template = template;
        if(template.getRmOverlay() != null && template.getRmOverlay().getRmVisibility() != null) {
            Map<String, RmAttributeVisibility> newRmVisibility = new LinkedHashMap<>();
            Map<String, RmAttributeVisibility> rmVisibility = template.getRmOverlay().getRmVisibility();
            for(String path:rmVisibility.keySet()) {
                PartialMatch partial = findPartial(new APathQuery(path), opt.getDefinition());
                if(!partial.getFoundObjects().isEmpty() && !partial.getPathMatched().equals("/")) {
                    ArchetypeModelObject archetypeModelObject = partial.getFoundObjects().get(0);
                    String newPath = convertPath(path, partial, archetypeModelObject);
                    newRmVisibility.put(newPath , rmVisibility.get(path));

                } else {
                    newRmVisibility.put(path , rmVisibility.get(path));
                }
            }
            template.getRmOverlay().setRmVisibility(newRmVisibility);
        }

        if(template.getAnnotations() != null && template.getAnnotations().getDocumentation() != null) {
            Map<String, Map<String, Map<String, String>>> documentation = template.getAnnotations().getDocumentation();

            Map<String, Map<String, Map<String, String>>> newDocumentation = new LinkedHashMap<>();
            for(String language:documentation.keySet()) {
                Map<String, Map<String, String>> newDocs = new LinkedHashMap<>();
                for(String path:documentation.get(language).keySet()) {
                    PartialMatch partial = findPartial(new APathQuery(path), opt.getDefinition());
                    if(!partial.getFoundObjects().isEmpty() && !partial.getPathMatched().equals("/")) {
                        ArchetypeModelObject archetypeModelObject = partial.getFoundObjects().get(0);
                        String newPath = convertPath(path, partial, archetypeModelObject);
                        newDocs.put(newPath, documentation.get(language).get(path));
                    } else {
                        newDocs.put(path, documentation.get(language).get(path));
                    }
                }

                newDocumentation.put(language, newDocs);
            }

            template.getAnnotations().setDocumentation(newDocumentation);
        }

    }

    private String convertPath(String path, PartialMatch partial, ArchetypeModelObject archetypeModelObject) {
        String newPath;
        String remainingPath = partial.getRemainingPath();
        if(Objects.equals(remainingPath, "/")) {
            remainingPath = "";
        }
        if(archetypeModelObject instanceof CAttribute) {
            newPath = ((CAttribute) archetypeModelObject).getPath() + remainingPath;
        } else if (archetypeModelObject instanceof CObject){
            newPath = ((CObject) archetypeModelObject).getPath() + remainingPath;
        } else {
            newPath = path;
        }
        return newPath;
    }


    /**
     * Find a partial match, also matching if halfway a query, including what has not yet been matched and what has not
     * Does not support finding through differential paths.
     * So, use on an OperationalTemplate!
     * @param root the CObject to find for
     * @return the partial match
     */
    public PartialMatch findPartial(APathQuery query, CComplexObject root) {
        List<PathSegment> pathSegments = query.getPathSegments();
        List<PathSegment> pathsMatched = new ArrayList<>();
        List<PathSegment> remainingSegments = new ArrayList<>(pathSegments);

        List<ArchetypeModelObject>  result = Lists.newArrayList(root);
        List<ArchetypeModelObject>  lastResult;

        while (!remainingSegments.isEmpty()) {
            lastResult = result;
            PathSegment segment = remainingSegments.remove(0);
            result = findOneSegment(segment, result, false);

            if (result.size() == 0) {
                //no more matches, return partial match.
                //the last segment did not match anything, add it again!
                remainingSegments.add(0, segment);
                return new PartialMatch(lastResult, PathUtil.getPath(pathsMatched), PathUtil.getPath(remainingSegments));
            } else {
                pathsMatched.add(segment);
            }
        }
        //full match, remainingSegments is empty
        return new PartialMatch(result, PathUtil.getPath(pathsMatched), PathUtil.getPath(remainingSegments));


    }

    protected List<ArchetypeModelObject> findOneSegment(PathSegment pathSegment, List<ArchetypeModelObject> objects, boolean matchSpecializedNodes) {
        List<ArchetypeModelObject> result = new ArrayList<>();

        List<ArchetypeModelObject> preProcessedObjects = new ArrayList<>();

        for(ArchetypeModelObject object:objects) {
            if (object instanceof CAttribute) {
                CAttribute cAttribute = (CAttribute) object;
                preProcessedObjects.addAll(cAttribute.getChildren());
            } else {
                preProcessedObjects.add(object);
            }

        }
        for(ArchetypeModelObject objectToCheck:preProcessedObjects) {
            ArchetypeModelObject object = objectToCheck;
            if(object instanceof CObject) {
                CObject cobject = (CObject) object;
                CAttribute attribute = cobject.getAttribute(pathSegment.getNodeName());
                if(attribute != null) {
                    ArchetypeModelObject r = findOneMatchingObject(attribute, pathSegment, matchSpecializedNodes);
                    if(r != null) {
                        result.add(r);
                    }
                }
            }
        }
        return result;
    }

    protected ArchetypeModelObject findOneMatchingObject(CAttribute attribute, PathSegment pathSegment, boolean matchSpecializedNodes) {
        List<ArchetypeModelObject> result = new ArrayList<>();

        if (pathSegment.hasIdCode()) {
            if(matchSpecializedNodes) {
                return attribute.getPossiblySpecializedChild(pathSegment.getNodeId());
            }
            return attribute.getChild(pathSegment.getNodeId());
        } else if (pathSegment.hasArchetypeRef()) {
            List<CObject> children = getChildrenByArchetypeRef(template, attribute, pathSegment.getNodeId());
            if(pathSegment.getObjectNameConstraint() != null) {
                for(CObject child:children) {
                    if(Objects.equals(pathSegment.getObjectNameConstraint(), child.getMeaning())) {
                        return child;
                    }
                }
            }
            if(!children.isEmpty()) {
                return children.get(0);
            }
            return null;
        } else if (pathSegment.hasNumberIndex()) {
            // APath path numbers start at 1 instead of 0
            int index = pathSegment.getIndex() - 1;
            return index < attribute.getChildren().size() ? attribute.getChildren().get(index) : null;
        } else if (pathSegment.getObjectNameConstraint() != null) {
            return attribute.getChildByMeaning(pathSegment.getObjectNameConstraint());//TODO: the ANTLR grammar removes all whitespace. what to do here?
        } else {
            return attribute;
        }
    }

    public List<CObject> getChildrenByArchetypeRef(Template template, CAttribute attribute, String archetypeRef) {
        List<CObject> result = new ArrayList<>();
        for(CObject child:attribute.getChildren()) {
            if(child instanceof CArchetypeRoot) {
                if (((CArchetypeRoot) child).getArchetypeRef().equals(archetypeRef)) {
                    result.add(child);
                } else {
                    TemplateOverlay templateOverlay = template.getTemplateOverlay(((CArchetypeRoot) child).getArchetypeRef());
                    if (AOMUtils.matchesArchetypeRef(archetypeRef, templateOverlay.getParentArchetypeId())) {
                        result.add(child);
                    }
                }
            }
        }
        return result;
    }

}
