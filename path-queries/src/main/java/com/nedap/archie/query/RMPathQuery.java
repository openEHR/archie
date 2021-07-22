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

    public <T> T find(ModelInfoLookup lookup, Object root) {
        List<RMObjectWithPath> list = findList(lookup, root);
        if(list.isEmpty()) {
            return null;
        } else if( list.size() == 1) {
            return (T) list.get(0).getObject();
        } else {
            throw new UnsupportedOperationException("Cannot find a single object, " + list.size() + " objects were found");
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

        List<RMObjectWithPath> result = new ArrayList<>();
        int i = 0;
        for(Object object:collection) {
            String archetypeNodeId = lookup.getArchetypeNodeIdFromRMObject(object);

            if (segment.hasIdCode()) {
                if (segment.getNodeId().equals(archetypeNodeId)) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                }
            } else if (segment.hasArchetypeRef()) {
                //operational templates in RM Objects have their archetype node ID set to an archetype ref. That
                //we support. Other things not so much
                if (segment.getNodeId().equals(archetypeNodeId)) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                }
            } else if (segment.getNodeId() != null) {
                if(equalsName(lookup.getNameFromRMObject(object), segment.getNodeId())) {
                    result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
                }
            } else {
                result.add(new RMObjectWithPath(object, path + buildPathConstraint(i, archetypeNodeId)));
            }
            i++;
        }
        if(segment.hasNumberIndex()) {
            int number = segment.getIndex();
            if( number - 1 >= 0 && number - 1 < result.size()) {
                return Lists.newArrayList(result.get(number-1));
            } else {
                return new ArrayList<>();
            }
        }
        return result;
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