package com.nedap.archie.query;


import com.google.common.collect.Lists;
import com.nedap.archie.definitions.AdlCodeDefinitions;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

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

    public RMPathQuery(String query) {
        pathSegments = new APathQuery(query).getPathSegments();

    }


    //TODO: get diagnostic information about where the finder stopped in the path - could be very useful!

    /**
     * WARNING: this method has many quirks. Please use findList instead!
     * Quirks include:
     * - if at a certain level in the path several objects match, it will only search the first match
     * - it includes a collection if it matches a collection attribute, in all other cases a single item
     */
    @Deprecated
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
                        Predicate<Object> predicate = RmPathQueryPredicateConverter.convert(segment, lookup);

                        if(!predicate.test(currentObject)) {
                            predicate = RmPathQueryPredicateConverter.convertWithoutNodeId(segment, lookup);
                            //TODO: maybe build this ina  single predicate? BUt should we for something deprecated?
                            if(!predicate.test(currentObject)) {
                                return null;
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

    @Deprecated //please remove as soon as no longer used!
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
            Predicate<Object> predicate = RmPathQueryPredicateConverter.convert(segment, lookup);

            if(predicate.test(o)) {
                return o;
            }
        }
        return null;
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
                    if (currentRMObject instanceof Collection) {
                        Collection<?> collection = (Collection<?>) currentRMObject;
                        if (!segment.hasExpressions()) {
                            addAllFromCollection(lookup, newCurrentObjects, collection, newPath);
                        } else {
                            newCurrentObjects.addAll(findRMObjectsWithPathCollection(lookup, segment, collection, newPath));
                        }
                    } else {
                        newCurrentObjects.addAll(findRMObjectsWithPathCollection(lookup, segment, Lists.newArrayList(currentRMObject), newPath));
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

        collection = getNumberedElement(segment, collection);

        Predicate<Object> predicate = RmPathQueryPredicateConverter.convert(segment, lookup);
        List<RMObjectWithPath> result = new ArrayList<>();
        int i = 1;
        for(Object object:collection) {

            String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(object);
            if(predicate.test(object)) {
                result.add(new RMObjectWithPath(object, path + buildPathConstraint(collection.size() == 1 ? null : i, archetypeNodeId)));
            }
            i++;
        }
        if(result.isEmpty()) {
            predicate = RmPathQueryPredicateConverter.convertWithoutNodeId(segment, lookup);

            i = 1;
            for (Object object : collection) {

                String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(object);
                if (predicate.test(object)) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(collection.size() == 1 ? null : i, archetypeNodeId)));
                }
                i++;
            }
        }
        return result;
    }

    private Collection<?> getNumberedElement(PathSegment segment, Collection<?> collection) {
        if(segment.hasNumberIndex()) {
            int number = segment.getIndex();
            int i = 1;
            for(Object object:collection) {
                if(number == i) {
                    //TODO: check for other constraints as well
                    ArrayList<Object> newCollection = new ArrayList<>();
                    newCollection.add(object);
                    collection = newCollection;
                    break;
                }
                i++;
            }
        }
        return collection;
    }

    public List<PathSegment> getPathSegments() {
        return pathSegments;
    }

}