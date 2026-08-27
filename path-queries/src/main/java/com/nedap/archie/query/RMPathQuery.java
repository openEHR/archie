package com.nedap.archie.query;


import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.rminfo.AttributeAccessor;
import com.nedap.archie.rminfo.ModelInfoLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
public class RMPathQuery {
    private static final Logger logger = LoggerFactory.getLogger(RMPathQuery.class);

    private final List<PathSegment> pathSegments;
    private final boolean matchSpecialisedNodes;

    public RMPathQuery(String query) {
        this(query, false);
    }

    public RMPathQuery(String query, boolean matchSpecialisedNodes) {
        pathSegments = new APathQuery(query).getPathSegments();
        this.matchSpecialisedNodes = matchSpecialisedNodes;
    }


    //TODO: get diagnostic information about where the finder stopped in the path - could be very useful!

    public <T> T find(ModelInfoLookup lookup, Object root) {
        AttributeAccessor attributeAccessor = new AttributeAccessor(lookup);
        Object currentObject = root;
        // performance-relevant simple for-loop
        for (int i = 0, n = pathSegments.size(); i < n; i++) {
            PathSegment segment = pathSegments.get(i);
            if (currentObject == null) {
                return null;
            }
            if (!attributeAccessor.hasAttribute(currentObject, segment.getNodeName())) {
                return null;
            }
            currentObject = attributeAccessor.getValue(currentObject, segment.getNodeName());
            if (currentObject == null) {
                return null;
            }

            String archetypeNodeIdFromObject = lookup.getArchetypeNodeIdFromRMObject(currentObject);
            if (currentObject instanceof Collection) {
                Collection<?> collection = (Collection<?>) currentObject;
                if (!segment.hasExpressions()) {
                    //TODO: check if this is correct
                    currentObject = collection;
                } else {
                    currentObject = findRMObject(lookup, segment, collection);
                }
            } else if (archetypeNodeIdFromObject != null) {

                if (segment.hasExpressions()) {
                    if (segment.hasIdCode()) {
                        if (!archetypeNodeIdFromObject.equals(segment.getNodeId())) {
                            return null;
                        }
                    } else if (segment.hasNumberIndex()) {
                        int number = segment.getIndex();
                        if (number != 1) {
                            return null;
                        }
                    } else if (segment.hasArchetypeRef()) {
                        //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                        //we support. Other things not so much
                        if (!archetypeNodeIdFromObject.equals(segment.getNodeId())) {
                            throw new IllegalArgumentException("cannot handle RM-queries with node names or archetype references yet");
                        }

                    }
                }
            } else if (segment.hasNumberIndex()) {
                int number = segment.getIndex();
                if (number != 1) {
                    return null;
                }
            } else {
                //not a locatable, but that's fine
                //in openehr, in archetypes everything has node ids. Datavalues do not in the rm. a bit ugly if you ask
                //me, but that's why there's no 'if there's a nodeId set, this won't match!' code here.
            }
        }
        return (T) currentObject;
    }

    /**
     * You will want to use RMQueryContext in many cases. For performance reasons, this could still be useful
     */
    public List<RMObjectWithPath> findList(ModelInfoLookup lookup, Object root) {
        AttributeAccessor attributeAccessor = new AttributeAccessor(lookup);
        List<RMObjectWithPath> currentObjects = new ArrayList<>(1);
        currentObjects.add(new RMObjectWithPath(root, "/"));
        List<RMObjectWithPath> newCurrentObjects = new ArrayList<>(0);
        // performance-relevant simple for-loops
        for (int i = 0, m = pathSegments.size(); i < m; i++) {
            PathSegment segment = pathSegments.get(i);
            for (int j = 0, n = currentObjects.size(); j < n; j++) {
                RMObjectWithPath currentObject = currentObjects.get(j);
                Object currentRMObject = currentObject.getObject();
                if (!attributeAccessor.hasAttribute(currentRMObject, segment.getNodeName())) {
                    continue;
                }
                currentRMObject = attributeAccessor.getValue(currentRMObject, segment.getNodeName());
                if (currentRMObject == null) {
                    continue;
                }

                String newPath;
                if (currentObject.getPath().endsWith("/")) {
                    newPath = currentObject.getPath() + segment.getNodeName();
                } else {
                    newPath = currentObject.getPath() + "/" + segment.getNodeName();
                }

                if (currentRMObject instanceof Collection) {
                    Collection<?> collection = (Collection<?>) currentRMObject;
                    if (!segment.hasExpressions()) {
                        addAllFromCollection(lookup, newCurrentObjects, collection, newPath);
                    } else {
                        //TODO
                        addRMObjectsWithPathCollection(lookup, segment, collection, newPath, newCurrentObjects);
                    }
                } else {
                    String archetypeNodeIdFromObject = lookup.getArchetypeNodeIdFromRMObject(currentObject);
                    if (archetypeNodeIdFromObject != null) {

                        if (segment.hasExpressions()) {
                            if (segment.hasIdCode()) {
                                if (!archetypeNodeIdFromObject.equals(segment.getNodeId())) {
                                    continue;
                                }
                            } else if (segment.hasNumberIndex()) {
                                int number = segment.getIndex();
                                if (number != 1) {
                                    continue;
                                }
                            } else if (segment.hasArchetypeRef()) {
                                //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                                //we support. Other things not so much
                                if (!archetypeNodeIdFromObject.equals(segment.getNodeId())) {
                                    continue;
                                }

                            }
                            newCurrentObjects.add(createRMObjectWithPath(lookup, currentRMObject, newPath));
                        }
                    } else if (!segment.hasNumberIndex()) {
                        //The object does not have an archetypeNodeId
                        //in openehr, in archetypes everything has node ids. Datavalues do not in the rm. a bit ugly if you ask
                        //me, but that's why there's no 'if there's a nodeId set, this won't match!' code here.
                        newCurrentObjects.add(createRMObjectWithPath(lookup, currentRMObject, newPath));
                    }
                }
            }
            if(newCurrentObjects.isEmpty()){
                return Collections.emptyList();
            }
            // swap lists
            List<RMObjectWithPath> t = currentObjects;
            currentObjects = newCurrentObjects;
            t.clear();
            newCurrentObjects = t;
        }
        return currentObjects;

    }

    private RMObjectWithPath createRMObjectWithPath(ModelInfoLookup lookup, Object currentObject, String newPath) {
        String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(currentObject);
        String path = addPathConstraint(newPath, null, archetypeNodeId);
        return new RMObjectWithPath(currentObject, path);
    }


    /**
     * Add all the elements from the collection toAdd to the newCurrentObjects Lists.
     * basePath must be the path under which to add the elements, without the "[]" part
     * @param newCurrentObjects
     * @param toAdd
     * @param basePath
     */
    private void addAllFromCollection(ModelInfoLookup lookup, List<RMObjectWithPath> newCurrentObjects, Collection<?> toAdd, String basePath) {
        int index = 1;
        for(Object object:toAdd) {
            String path = addPathConstraint(basePath, index, lookup.getArchetypeNodeIdFromRMObject(object));
            newCurrentObjects.add(new RMObjectWithPath(object, path));
            index++;
        }
    }

    private String addPathConstraint(String path, Integer index, String archetypeNodeId) {
        if (index == null) {
            if (archetypeNodeIdPresent(archetypeNodeId)) {
                return path + "[" + archetypeNodeId + "]";
            } else {
                return path; //nothing to add
            }
        } else if (archetypeNodeIdPresent(archetypeNodeId)) {
            return path + "[" + archetypeNodeId + ", " + index + "]";
        } else {
            return path + "[" + index + "]";
        }
    }

    private boolean archetypeNodeIdPresent(String archetypeNodeId) {
        return archetypeNodeId != null && !archetypeNodeId.equals(AdlCodeDefinitions.PRIMITIVE_NODE_ID);
    }

    private void addRMObjectsWithPathCollection(ModelInfoLookup lookup, PathSegment segment, Collection<?> collection, String path, List<RMObjectWithPath> result) {

        if(segment.hasNumberIndex()) {
            int number = segment.getIndex();
            int i = 1;
            for(Object object:collection) {
                if(number == i) {
                    //TODO: check for other constraints as well
                    result.add(new RMObjectWithPath(object, addPathConstraint(path, i, lookup.getArchetypeNodeIdFromRMObject(object))));
                    return;
                }
                i++;
            }
        }
        int i = 1;
        for(Object object:collection) {
            String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(object);

            if (segment.hasIdCode()) {
                if (matchSpecialisedNodes) {
                    if (AOMUtils.codesConformant(archetypeNodeId, segment.getNodeId())) {
                        result.add(new RMObjectWithPath(object, addPathConstraint(path, i, archetypeNodeId)));
                    }
                } else {
                    if (segment.getNodeId().equals(archetypeNodeId)) {
                        result.add(new RMObjectWithPath(object, addPathConstraint(path, i, archetypeNodeId)));
                    }
                }

            } else if (segment.hasArchetypeRef()) {
                //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                //we support. Other things not so much
                if (segment.getNodeId().equals(archetypeNodeId)) {
                    result.add(new RMObjectWithPath(object, addPathConstraint(path, i, archetypeNodeId)));
                }
            } else {
                if(equalsName(lookup.getNameFromRMObject(object), segment.getNodeId())) {
                    logger.warn("Deprecation: Matching on object name is deprecated and will be removed. Use node id instead.");
                    result.add(new RMObjectWithPath(object, addPathConstraint(path, i, archetypeNodeId)));
                }
            }
            i++;
        }
    }

    private Object findRMObject(ModelInfoLookup lookup, PathSegment segment, Collection<?> collection) {

        if(segment.hasNumberIndex()) {
            int number = segment.getIndex();
            for(Object object:collection) {
                if(number == 1) {
                    return object;
                }
                number--;
            }
            return null;
        }
        for(Object o:collection) {
            String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(o);
            if (segment.hasIdCode()) {
                if (matchSpecialisedNodes) {
                    if (AOMUtils.codesConformant(archetypeNodeId, segment.getNodeId())) {
                        return o;
                    }
                } else {
                    if (segment.getNodeId().equals(archetypeNodeId)) {
                        return o;
                    }
                }
            } else if (segment.hasArchetypeRef()) {
                //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                //we support. Other things not so much
                if (segment.getNodeId().equals(archetypeNodeId)) {
                    return o;
                }
            } else {
                if(equalsName(lookup.getNameFromRMObject(o), segment.getNodeId())) {
                    logger.warn("Deprecation: Matching on object name is deprecated and will be removed. Use node id instead.");
                    return o;
                }
            }
        }
        return null;
    }

    private boolean equalsName(String name, String nameFromQuery) {
        //the grammar throws away whitespace. And it should, because it's kind of tricky otherwise. So match names without whitespace
        //TODO: should this be case sensitive?
        if(name == null) {
            return false;
        }
        name = name.replaceAll("( |\\t|\\n|\\r)+", "");
        nameFromQuery = nameFromQuery.replaceAll("( |\\t|\\n|\\r)+", "");
        return name.equalsIgnoreCase(nameFromQuery);

    }

    public List<PathSegment> getPathSegments() {
        return pathSegments;
    }

}