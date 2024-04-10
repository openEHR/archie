package com.nedap.archie.rules.evaluation;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.*;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pieter.bos on 05/04/2017.
 */
public class AssertionsFixer {

    private static final Logger logger = LoggerFactory.getLogger(AssertionsFixer.class);

    private final RMObjectCreator creator;
    private final RuleEvaluation<?> ruleEvaluation;
    private final RMObjectCreator rmObjectCreator;

    private ModelInfoLookup modelInfoLookup;

    public AssertionsFixer(RuleEvaluation<?> evaluation, RMObjectCreator creator) {
        this.creator = creator;
        this.ruleEvaluation = evaluation;
        this.modelInfoLookup = ruleEvaluation.getModelInfoLookup();
        rmObjectCreator = new RMObjectCreator(evaluation.getModelInfoLookup());
    }

    public Map<String, Object> fixAssertions(Archetype archetype, AssertionResult assertionResult) {
        Map<String, Object> result = new HashMap<>();


        Map<String, Value<?>> setPathValues = assertionResult.getSetPathValues();
        for(String path:setPathValues.keySet()) {
            Value<?> value = setPathValues.get(path);

            String pathOfParent = stripLastPathSegment(path);
            String lastPathSegment = getLastPathSegment(path);

            List<Object> parents = ruleEvaluation.findList(pathOfParent);
            int i = 0;
            while(parents.isEmpty() && i < 500) { //not more than 500 times because we do not want infinite loops, and 500 is a lot already here
                //there's object missing in the RMObject. Construct it here.
                constructMissingStructure(archetype, pathOfParent, lastPathSegment, parents);
                parents = ruleEvaluation.findList(pathOfParent);
                i++;
            }

            for(Object parent:parents) {
                RMAttributeInfo attributeInfo = ruleEvaluation.getModelInfoLookup().getAttributeInfo(parent.getClass(), lastPathSegment);
                if(attributeInfo == null) {
                    throw new IllegalStateException("attribute " + lastPathSegment + " does not exist on type " + parent.getClass());
                }
                if(value.getValue() == null) {
                    creator.set(parent, lastPathSegment, Lists.newArrayList(value.getValue()));
                } else if(attributeInfo.getType().equals(Long.class) && value.getValue().getClass().equals(Double.class)) {
                    Long convertedValue = ((Double) value.getValue()).longValue(); //TODO or should this round?
                    creator.set(parent, lastPathSegment, Lists.newArrayList(convertedValue));
                } else if(attributeInfo.getType().equals(Double.class) && value.getValue().getClass().equals(Long.class)) {
                    Double convertedValue = ((Long) value.getValue()).doubleValue(); //TODO or should this round?
                    creator.set(parent, lastPathSegment, Lists.newArrayList(convertedValue));
                } else {
                    creator.set(parent, lastPathSegment, Lists.newArrayList(value.getValue()));
                }

                result.putAll(modelInfoLookup.pathHasBeenUpdated(ruleEvaluation.getRMRoot(), archetype, pathOfParent, parent));
                ruleEvaluation.refreshQueryContext();
            }
        }


        return result;
    }


    private void constructMissingStructure(Archetype archetype, String pathOfParent, String lastPathSegment, List<Object> parents) {
        //TODO: this is great but not enough. Fix it by hardcoding support for DV_CODED_TEXT and DV_ORDINAL, here or in the FixableAssertionsChecker.
        String newPathOfParent = pathOfParent;
        String newLastPathSegment = lastPathSegment;
        while (parents.isEmpty()) {
            //lookup parent of parent until found. Create empty RM object. Then repeat original query
            newLastPathSegment = getLastPathSegment(newPathOfParent);
            newPathOfParent = stripLastPathSegment(newPathOfParent);
            parents = ruleEvaluation.findList(newPathOfParent);
        }
        List<ArchetypeModelObject> constraints;
        if (newPathOfParent.equals("/")) {
            constraints = archetype.itemsAtPath("/" + newLastPathSegment);
        } else {
            constraints = archetype.itemsAtPath(newPathOfParent + "/" + newLastPathSegment);
        }

        if (constraintsHasNoComplexObjects(constraints)) {
            Object object = parents.get(0);

            Object newEmptyObject = null;
            newEmptyObject = constructEmptySimpleObject(newLastPathSegment, object, newEmptyObject);

            creator.addElementToListOrSetSingleValues(object, newLastPathSegment, Lists.newArrayList(newEmptyObject));
            ruleEvaluation.refreshQueryContext();
        } else {
            CObject constraint = getCObjectFromResult(constraints);
            if (constraint != null) {
                Object object = parents.get(0);

                Object newEmptyObject = null;
                if (constraint instanceof CComplexObject || constraint instanceof ArchetypeSlot) {
                    newEmptyObject = rmObjectCreator.create(constraint);
                } else {
                    newEmptyObject = constructEmptySimpleObject(newLastPathSegment, object, newEmptyObject);
                }

                int bracketIndex = newLastPathSegment.indexOf('[');
                String attributeName = newLastPathSegment;
                if(bracketIndex > -1) {
                    attributeName = newLastPathSegment.substring(0, bracketIndex);
                }

                creator.addElementToListOrSetSingleValues(object, attributeName, Lists.newArrayList(newEmptyObject));
                ruleEvaluation.refreshQueryContext();

            }
        }
    }

    private CObject getCObjectFromResult(List<? extends ArchetypeModelObject> objects) {
        if(objects.size() != 1) {
            //if there's more than one CObject this represents a user choice and we cannot return a single object and this cannot be automatically fixed
            return null;
        } else {
            ArchetypeModelObject object = objects.get(0);
            if (object instanceof CAttribute) {
                return getCObjectFromResult(((CAttribute) object).getChildren());
            }
            return (CObject) object;
        }
    }

    private Object constructEmptySimpleObject(String newLastPathSegment, Object object, Object newEmptyObject) {
        RMAttributeInfo attributeInfo = ruleEvaluation.getModelInfoLookup().getAttributeInfo(object.getClass(), newLastPathSegment);
        try {
            newEmptyObject = attributeInfo.getTypeInCollection().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return newEmptyObject;
    }

    private boolean constraintsHasNoComplexObjects(List<? extends ArchetypeModelObject> constraints) {
        for(ArchetypeModelObject constraint:constraints) {
            if(constraint instanceof CAttribute) {
                if(!constraintsHasNoComplexObjects(((CAttribute) constraint).getChildren())) {
                    return false;
                }
            } else if(constraint instanceof CComplexObject || constraint instanceof ArchetypeSlot) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the path with everything except the last path segment, so /items/value becomes /items
     * @param path
     * @return
     */
    private String stripLastPathSegment(String path) {
        int lastIndex = path.lastIndexOf('/');
        if(lastIndex < 0) {
            return path;
        }

        String result = path.substring(0, lastIndex);
        if( result.equals("")) {
            return "/";
        }

        return result;
    }

    /**
     * Return the path with everything except the last path segment, so /items/value becomes /items
     * @param path
     * @return
     */
    private String getLastPathSegment(String path) {
        int lastIndex = path.lastIndexOf('/');
        if(lastIndex < 0) {
            return path;
        }
        return path.substring(lastIndex+1);
    }

}
