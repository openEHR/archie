package com.nedap.archie.query;


import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CComplexObjectProxy;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.paths.PathSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For now only accepts rather simple xpath-like expressions.
 *
 * The only queries fully supported at the moment are absolute queries with node ids, such as '/items[id1]/content[id2]/value'.
 *
 * Any expression after the ID-code, such as in '[id1 and name="ignored"] are currently ignored, but they parse and function
 * as long as you add the id-code as first part of the expression.
 *
 * Created by pieter.bos on 19/10/15.
 */
public class AOMPathQuery {

    private final List<PathSegment> pathSegments;

    /** If true, extend the search through C_COMPLEX_OBJECT_PROXY objects by looking up the replacement first.*/
    private final boolean findThroughCComplexObjectProxies;

    public AOMPathQuery(String query) {
        APathQuery apathQuery = new APathQuery(query);
        this.pathSegments = apathQuery.getPathSegments();
        findThroughCComplexObjectProxies = false;
    }

    private AOMPathQuery(List<PathSegment> pathSegments, boolean findThroughCComplexObjectProxies) {
        this.pathSegments = pathSegments;
        this.findThroughCComplexObjectProxies = findThroughCComplexObjectProxies;
    }

    public <T extends ArchetypeModelObject> T find(CComplexObject root) {
       List<T> list = findList(root);
        if(list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            throw new UnsupportedOperationException("cannot find without list with more than 1 element");
        }
    }

    /**
     * Return a new AOMPathQuery with the same query, but that finds through CComplexObjectProxy replacements as well
     * @return
     */
    public AOMPathQuery findThroughCComplexObjectProxies() {
        return new AOMPathQuery(pathSegments, true);
    }

    public <T extends ArchetypeModelObject> List<T> findList(CComplexObject root) {
        List<ArchetypeModelObject> result = new ArrayList<>();
        result.add(root);
        for(PathSegment segment:this.pathSegments) {
            if (result.size() == 0) {
                return Collections.emptyList();
            }
            result = findOneSegment(segment, result);
        }
        return (List<T>)result.stream().filter((object) -> object != null).collect(Collectors.toList());
    }

    private List<ArchetypeModelObject> findOneSegment(PathSegment pathSegment, List<ArchetypeModelObject> objects) {
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
            if(findThroughCComplexObjectProxies && object instanceof CComplexObjectProxy) {
                //use the complex object proxy replacement for further queries instead of the original
                ComplexObjectProxyReplacement complexObjectProxyReplacement = ComplexObjectProxyReplacement.getComplexObjectProxyReplacement((CComplexObjectProxy) object);
                if(complexObjectProxyReplacement != null && complexObjectProxyReplacement.getReplacement() != null) {
                    object = complexObjectProxyReplacement.getReplacement();
                }
            }
            if(object instanceof CObject) {
                CObject cobject = (CObject) object;
                CAttribute attribute = cobject.getAttribute(pathSegment.getNodeName());
                if(attribute != null) {
                    if (pathSegment.hasIdCode() || pathSegment.hasArchetypeRef()) {
                        result.add(attribute.getChild(pathSegment.getNodeId()));
                    } else if (pathSegment.hasNumberIndex()) {
                        result.add(attribute.getChildren().get(pathSegment.getIndex() - 1));//APath path numbers start at 1 instead of 0
                    } else if (pathSegment.getNodeId() != null) {
                        result.add(attribute.getChildByMeaning(pathSegment.getNodeId()));//TODO: the ANTLR grammar removes all whitespace. what to do here?
                    } else {
                        result.add(attribute);
                    }
                }
            }
        }
        return result;
    }

    //TODO: get diagnostic information about where the finder stopped in the path - could be very useful!


    public List<PathSegment> getPathSegments() {
        return pathSegments;
    }


    /**
     * Find any CComplexObjectProxy anywhere inside the APath query. Can be at the end of the full query, at the first matching CComplexObjectProxy or anywhere in between
     * Useful mainly when flattening, probably does not have many other uses
     */
    public CComplexObjectProxy findAnyInternalReference(CComplexObject root) {
        List<ArchetypeModelObject> result = new ArrayList<>();
        result.add(root);
        for(PathSegment segment:this.pathSegments) {
            if (result.size() == 0) {
                return null;
            }
            result = findOneSegment(segment, result);
            if(result.size() == 1 && result.get(0) instanceof CComplexObjectProxy) {
                return (CComplexObjectProxy) result.get(0);
            }
        }
        return null;

    }
}
