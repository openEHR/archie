package com.nedap.archie.query;


import com.google.common.collect.Lists;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.apath.APathQuery;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import com.nedap.archie.apath.PathSegment;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private List<PathSegment> pathSegments = new ArrayList<>();
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
        Object currentObject = root;
        try {
            for (PathSegment segment : pathSegments) {
                if (currentObject == null) {
                    return null;
                }
                RMAttributeInfo attributeInfo = lookup.getAttributeInfo(currentObject.getClass(), segment.getNodeName());
                if (attributeInfo == null) {
                    return null;
                }
                Method method = attributeInfo.getGetMethod();
                currentObject = method.invoke(currentObject);
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
                    //in openehr, in archetypes everythign has node ids. Datavalues do not in the rm. a bit ugly if you ask
                    //me, but that's why there's no 'if there's a nodeId set, this won't match!' code here.
                }
            }
            return (T) currentObject;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * You will want to use RMQueryContext in many cases. For perforamnce reasons, this could still be useful
     */
    public <T> List<RMObjectWithPath> findList(ModelInfoLookup lookup, Object root) {
        List<RMObjectWithPath> currentObjects = Lists.newArrayList(new RMObjectWithPath(root, "/"));
        try {
            for (PathSegment segment : pathSegments) {
                if(currentObjects.isEmpty()){
                    return Collections.emptyList();
                }
                List<RMObjectWithPath> newCurrentObjects = new ArrayList<>();

                for(int i = 0; i < currentObjects.size(); i++) {
                    RMObjectWithPath currentObject = currentObjects.get(i);
                    Object currentRMObject = currentObject.getObject();
                    RMAttributeInfo attributeInfo = lookup.getAttributeInfo(currentRMObject.getClass(), segment.getNodeName());
                    if (attributeInfo == null) {
                        continue;
                    }
                    Method method = attributeInfo.getGetMethod();
                    currentRMObject = method.invoke(currentRMObject);
                    String pathSeparator = "/";
                    if(currentObject.getPath().endsWith("/")) {
                        pathSeparator = "";
                    }
                    String newPath = currentObject.getPath() + pathSeparator + segment.getNodeName();

                    if (currentRMObject == null) {
                        continue;
                    }
                    String archetypeNodeIdFromObject = lookup.getArchetypeNodeIdFromRMObject(currentObject);
                    if (currentRMObject instanceof Collection) {
                        Collection<?> collection = (Collection<?>) currentRMObject;
                        if (!segment.hasExpressions()) {
                            addAllFromCollection(lookup, newCurrentObjects, collection, newPath);
                        } else {
                            //TODO
                            newCurrentObjects.addAll(findRMObjectsWithPathCollection(lookup, segment, collection, newPath));
                        }
                    } else if (archetypeNodeIdFromObject != null) {

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
                    } else if (segment.hasNumberIndex()) {
                        int number = segment.getIndex();
                        if (number != 1) {
                            continue;
                        }
                    } else {
                        //The object does not have an archetypeNodeId
                        //in openehr, in archetypes everythign has node ids. Datavalues do not in the rm. a bit ugly if you ask
                        //me, but that's why there's no 'if there's a nodeId set, this won't match!' code here.
                        newCurrentObjects.add(createRMObjectWithPath(lookup, currentRMObject, newPath));
                    }
                }
                currentObjects = newCurrentObjects;
            }
            return currentObjects;
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private RMObjectWithPath createRMObjectWithPath(ModelInfoLookup lookup, Object currentObject, String newPath) {
        String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(currentObject);
        String pathConstraint = buildPathConstraint(null, archetypeNodeId);
        return new RMObjectWithPath(currentObject, newPath + pathConstraint);
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
            String constraint = buildPathConstraint(index, lookup.getArchetypeNodeIdFromRMObject(object));
            newCurrentObjects.add(new RMObjectWithPath(object, basePath + constraint));
            index++;
        }
    }

    private String buildPathConstraint(Integer index, String archetypeNodeId) {
        if(index == null && !archetypeNodeIdPresent(archetypeNodeId)) {
            return "";//nothing to add
        }
        if(archetypeNodeIdPresent(archetypeNodeId) && index == null) {
            return "[" + archetypeNodeId + "]";
        }
        StringBuilder constraint = new StringBuilder("[");
        boolean first = true;
        if(archetypeNodeIdPresent(archetypeNodeId)) {
            constraint.append(archetypeNodeId);
            first = false;
        }
        if(index != null) {
            if(!first) {
                constraint.append(", ");
            }
            constraint.append(Integer.toString(index));
        }

        constraint.append("]");
        return constraint.toString();
    }

    private boolean archetypeNodeIdPresent(String archetypeNodeId) {
        return archetypeNodeId != null && !archetypeNodeId.equals(AdlCodeDefinitions.PRIMITIVE_NODE_ID);
    }

    private Collection<RMObjectWithPath> findRMObjectsWithPathCollection(ModelInfoLookup lookup, PathSegment segment, Collection<?> collection, String path) {

        if(segment.hasNumberIndex()) {
            int number = segment.getIndex();
            int i = 1;
            for(Object object:collection) {
                if(number == i) {
                    //TODO: check for other constraints as well
                    return Lists.newArrayList(new RMObjectWithPath(object, path + buildPathConstraint(i, lookup.getArchetypeNodeIdFromRMObject(object))));
                }
                i++;
            }
        }
        List<RMObjectWithPath> result = new ArrayList<>();
        int i = 1;
        for(Object object:collection) {
            String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(object);

            if (segment.hasIdCode()) {
                if (matchSpecialisedNodes) {
                    if (AOMUtils.codesConformant(archetypeNodeId, segment.getNodeId())) {
                        result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                    }
                } else {
                    if (segment.getNodeId().equals(archetypeNodeId)) {
                        result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                    }
                }

            } else if (segment.hasArchetypeRef()) {
                //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                //we support. Other things not so much
                if (segment.getNodeId().equals(archetypeNodeId)) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                }
            } else {
                if(equalsName(lookup.getNameFromRMObject(object), segment.getNodeId())) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                }
            }
            i++;
        }
        return result;
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