package com.nedap.archie.json.flat;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.datetime.DateTimeSerializerFormatters;
import com.nedap.archie.rminfo.ModelInfoLookup;
import com.nedap.archie.rminfo.RMAttributeInfo;
import com.nedap.archie.rminfo.RMTypeInfo;

import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A generator that generates a Flat JSON format of an RM Object. Can handle any RM Object of any model if you supply it
 * with the correct ModelInfoLookup and configuration.
 *
 * Configurable to support several formats used by several vendors for the same concept
 *
 * This generator generates a Map&lt;String, Object&gt;, which can be serializes using the ObjectMapper in JacksonUtil.getObjectMapper()
 * or any other object mapper
 */
public class FlatJsonGenerator {

    private final ModelInfoLookup modelInfoLookup;

    private final List<IgnoredAttribute> ignoredAttributes;

    private final boolean writePipesForPrimitiveTypes;
    private final boolean humanReadableFormat;
    private final IndexNotation indexNotation;
    private final String typeIdPropertyName;
    private final boolean separateIndicesPerNodeId;
    private boolean filterNames;
    private boolean filterTypes;
    private IgnoredAttribute nameProperty;


    /**
     * Construct the FlatJsonGenerator
     * @param modelInfoLookup the model info lookup use to define the model
     * @param config the configuration for the flat json format
     */
    public FlatJsonGenerator(ModelInfoLookup modelInfoLookup, FlatJsonFormatConfiguration config) {
        this.modelInfoLookup = modelInfoLookup;
        this.writePipesForPrimitiveTypes = config.isWritePipesForPrimitiveTypes();
        this.humanReadableFormat = false;//TODO: this is quite a bit of work to do properly, so definately not doing this now.
        this.indexNotation = config.getIndexNotation();
        this.typeIdPropertyName = config.getTypeIdPropertyName();
        this.separateIndicesPerNodeId = config.isSeparateIndicesPerNodeId();
        this.filterNames = config.getFilterNames();
        this.filterTypes = config.getFilterTypes();
        ignoredAttributes = config.getIgnoredAttributes().stream()
                .map(a -> new IgnoredAttribute(modelInfoLookup.getTypeInfo(a.getTypeName()), a.getAttributeName()))
                .collect(Collectors.toList());
        nameProperty = new IgnoredAttribute(modelInfoLookup.getTypeInfo(config.getNameProperty().getTypeName()), config.getNameProperty().getAttributeName());

    }

    /**
     * Build the actual flat json format for the given RM Object
     * @param rmObject the RM Object to build the flat json format for
     * @return a Map with paths as the key, and primitive objects as the value, to be serialized with an ObjectMapper
     * @throws DuplicateKeyException in case converting this to flat json would result in having the two exact paths at once. Generally this means a problem in input, this should not happen.
     */

    public Map<String, Object> buildPathsAndValues(OpenEHRBase rmObject) throws DuplicateKeyException {
        return buildPathsAndValues(rmObject, null);
    }

    public Map<String, Object> buildPathsAndValues(OpenEHRBase rmObject, OperationalTemplate archetype, String language) throws DuplicateKeyException {
        String previousLanguage = ArchieLanguageConfiguration.getThreadLocalDescriptiongAndMeaningLanguage();
        if(language != null) {
            ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(language);
        }

        try {
            return buildPathsAndValues(rmObject, archetype);
        } finally {
            ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(previousLanguage);
        }
    }

    public Map<String, Object> buildPathsAndValues(OpenEHRBase rmObject, OperationalTemplate archetype) throws DuplicateKeyException {
        Map<String, Object> result = new LinkedHashMap<>();
        CObject definition = archetype == null ? null : archetype.getDefinition();
        buildPathsAndValuesInner(result, null, "/", rmObject, definition, false);

        if(humanReadableFormat) {
            String rootName = modelInfoLookup.getNameFromRMObject(rmObject);
            if(rootName != null) {
                Map<String, Object> humanReadableResult = new LinkedHashMap<>();
                result.forEach((key, value) -> humanReadableResult.put(addUnderScores(rootName) + key, value));
                return humanReadableResult;
            }
        }
        return result;
    }

    private void buildPathsAndValuesInner(Map<String, Object> result, RMTypeInfo rmAttributeTypeInfo, String pathSoFar, OpenEHRBase rmObject, CObject cObject, boolean typeAlternativesPresent) throws DuplicateKeyException {

        if(rmObject == null) {
            return;
        }
        RMTypeInfo typeInfo = modelInfoLookup.getTypeInfo(rmObject.getClass());
        if(shouldAddTypeName(rmAttributeTypeInfo, rmObject, cObject, typeAlternativesPresent)) {
            storeValue(result, joinPath(pathSoFar, typeIdPropertyName, null, null, "/"), getTypeIdFromValue(rmObject));
        }

        String name = modelInfoLookup.getNameFromRMObject(rmObject);

        for(String attributeName:typeInfo.getAttributes().keySet()) {
            CAttribute cAttribute = cObject == null ? null : cObject.getAttribute(attributeName);
            RMAttributeInfo attributeInfo = typeInfo.getAttributes().get(attributeName);
            if(!attributeInfo.isComputed() && !isIgnored(typeInfo, attributeName) && attributeInfo.getGetMethod() != null) {
                if(filterNames && cObject != null && isNameAttribute(typeInfo, attributeName)) {
                    ArchetypeTerm term = cObject.getTerm();
                    if(term != null && name.equals(term.getText())) {
                        continue;
                    }
                }
                try {
                    Object child = attributeInfo.getGetMethod().invoke(rmObject);
                    addAttribute(result, pathSoFar, rmObject, child, attributeName,null, cAttribute);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);//TODO: fine for now...
                }
            }

        }
    }

    /**
     * Determine if the type name attribute should be added.
     *
     * The type name attribute should be added if:
     *  - The actual RM object is of a different type then the RM attribute type; AND
     *  - One of the following conditions applies:
     *     - {@link FlatJsonFormatConfiguration#getFilterTypes()}  is disabled
     *     - The actual RM object is of a different type than specified in the archetype constraint
     *       (or no archetype constraint is available)
     *     - There are multiple alternative archetype constrains present.
     *
     * Note that at the root node of the data and the archetype, rmAttributeTypeInfo will be null, and
     * the type info therefore will always be added.
     */
    private boolean shouldAddTypeName(RMTypeInfo rmAttributeTypeInfo, OpenEHRBase rmObject, CObject cObject, boolean typeAlternativesPresent) {
        return !sameType(rmAttributeTypeInfo, rmObject) &&
                ( !filterTypes || !sameType(cObject, rmObject) || typeAlternativesPresent);
    }

    private void storeValue(Map<String, Object> result, String path, Object value) throws DuplicateKeyException {
        if(result.containsKey(path)) {
            //whoops!
            throw new DuplicateKeyException("cannot add path twice: " + path + " with exis. value " + result.get(path) + " new value " + value);
        }
        result.put(path, value);
    }

    private boolean isIgnored(RMTypeInfo typeInfo, String attributeName) {
        return ignoredAttributes.stream()
                .filter( ignored ->
                    typeInfo.isDescendantOrEqual(ignored.getType()) && attributeName.equalsIgnoreCase(ignored.getAttributeName())
                ).findAny().isPresent();
    }


    private boolean isNameAttribute(RMTypeInfo typeInfo, String attributeName) {
        if(nameProperty == null || nameProperty.getType() == null) {
            return false;
        }
        return typeInfo.isDescendantOrEqual(nameProperty.getType()) && attributeName.equalsIgnoreCase(nameProperty.getAttributeName());
    }

    private Object getTypeIdFromValue(OpenEHRBase value) {
        RMTypeInfo typeInfo = modelInfoLookup.getTypeInfo(value.getClass());

        if(typeInfo != null) {
            //this case is faster and should always work. If for some reason it does not, the case below works fine instead.
            return typeInfo.getRmName();
        } else {
            return modelInfoLookup.getNamingStrategy().getTypeName(value.getClass());
        }
    }

    private boolean sameType(RMTypeInfo typeInfo, OpenEHRBase rmObject) {
        if(rmObject == null) {
            return false;
        }
        return modelInfoLookup.getTypeInfo(rmObject.getClass()).equals(typeInfo);
    }

    private boolean sameType(CObject cObject, OpenEHRBase rmObject) {
        if(cObject == null || rmObject == null) {
            return false;
        }
        return modelInfoLookup.getTypeInfo(rmObject.getClass()).getRmName().equals(cObject.getRmTypeName());
    }

    private boolean typeHasDescendants(RMTypeInfo typeInfo) {
        if(typeInfo == null) {
            return true;// we have no idea, so include @type/_type
        }
        return !typeInfo.getDirectDescendantClasses().isEmpty();
    }

    private void addAttribute(Map<String, Object> result, String pathSoFar, OpenEHRBase parent, Object child, String attributeName, Integer index, CAttribute cAttribute) throws DuplicateKeyException {

        if(child instanceof OpenEHRBase) {
            String newPath = joinPath(pathSoFar, attributeName, (OpenEHRBase) child, index, "/");
            //TODO: get correct type info here
            RMAttributeInfo attributeInfo = modelInfoLookup.getAttributeInfo(parent.getClass(), attributeName);
            RMTypeInfo typeInfo = getAttributeTypeInfo(attributeInfo);

            RMTypeInfo modelTypeInfo = modelInfoLookup.getTypeInfo(child.getClass());
            CObject cObject = null;
            //whether other alternatives exist that could have been added in the archetype
            boolean otherTypeAlternatives = false;

            String archetypeNodeIdFromRMObject = modelInfoLookup.getArchetypeNodeIdFromRMObject(child);
            if(cAttribute != null) {
                if (archetypeNodeIdFromRMObject == null) {
                    if(modelTypeInfo != null) {
                        //do a type-name lookup. Also look for sibling alternatives
                        List<CObject> childrenByRmTypeName = cAttribute.getChildrenByRmTypeName(modelTypeInfo.getRmName());
                        if(childrenByRmTypeName != null && childrenByRmTypeName.size() == 1) {
                            cObject = childrenByRmTypeName.get(0);
                            otherTypeAlternatives = cAttribute.getChildren().size() > 1;
                        }
                    }
                } else {
                    cObject = cAttribute.getChild(archetypeNodeIdFromRMObject);
                }
            }

            buildPathsAndValuesInner(result, typeInfo, newPath, (OpenEHRBase) child, cObject, otherTypeAlternatives);

            String archetypeId = modelInfoLookup.getArchetypeIdFromArchetypedRmObject(child);
            if(archetypeId != null) {
                storeValue(result, newPath, archetypeId);
            }
        } else if (child instanceof Collection) {
            if(separateIndicesPerNodeId) {
                Map<String, Integer> amountsPerNodeId = new HashMap<>();
                for (Object c : (Collection<?>) child) {

                    int numberOfNonLocatables = 1; //1-based, sory
                    String archetypeNodeId = modelInfoLookup.getArchetypeNodeIdFromRMObject(c);
                    if (archetypeNodeId != null) {
                        Integer numberOfPreviousOccurrences = amountsPerNodeId.get(archetypeNodeId);
                        addAttribute(result, pathSoFar, parent, c, attributeName, numberOfPreviousOccurrences, cAttribute);
                        numberOfPreviousOccurrences = numberOfPreviousOccurrences == null ? 1 : numberOfPreviousOccurrences + 1;
                        amountsPerNodeId.put(archetypeNodeId, numberOfPreviousOccurrences);
                    } else {
                        addAttribute(result, pathSoFar, parent, c, attributeName, numberOfNonLocatables == 1 ? null : numberOfNonLocatables, cAttribute);
                        numberOfNonLocatables++;
                    }
                }
                //TODO: do we need Map-support as well?
            } else {
                int collectionIndex = 1;
                for (Object c : (Collection<?>) child) {
                    addAttribute(result, pathSoFar, parent, c, attributeName, collectionIndex, cAttribute);
                    collectionIndex++;
                }
            }
        } else if(child != null) {
            String newPath = joinPath(pathSoFar, attributeName, null, index, writePipesForPrimitiveTypes ? "|" : "/");

            if(child instanceof Number) {
                storeValue(result, newPath, child);
            } else if (child instanceof TemporalAccessor) {
                Temporal t = (Temporal) child;
                boolean hoursSupported = t.isSupported(ChronoUnit.HOURS);
                boolean monthsSupported = t.isSupported(ChronoUnit.MONTHS);

                if(hoursSupported && monthsSupported) {
                    //datetime
                    storeValue(result, newPath, DateTimeSerializerFormatters.ISO_8601_DATE_TIME.format(t));
                } else if (monthsSupported) {
                    //date
                    storeValue(result, newPath, DateTimeSerializerFormatters.ISO_8601_DATE.format(t));
                } else if (hoursSupported) {
                    //time
                    storeValue(result, newPath, DateTimeSerializerFormatters.ISO_8601_TIME.format(t));
                }
            } else if (child instanceof TemporalAmount) {
                //duration or period. now just a toString, should this be a specific formatter?
                storeValue(result, newPath, child);
            } else if(child instanceof byte[]) {
                storeValue(result, newPath, Base64.getEncoder().encodeToString((byte[]) child));
            } else {
                storeValue(result, newPath, child.toString());
            }
        }
    }

    private RMTypeInfo getAttributeTypeInfo(RMAttributeInfo attributeInfo) {
        RMTypeInfo typeInfo = null;
        if(attributeInfo != null) {
            typeInfo = modelInfoLookup.getTypeInfo(attributeInfo.getTypeInCollection());
        }
        return typeInfo;
    }

    private String joinPath(String pathSoFar, String attributeName, OpenEHRBase rmObject, Integer index, String pathSeparator) {
        String name = modelInfoLookup.getNameFromRMObject(rmObject);
        boolean wroteHumanReadableName = name != null && humanReadableFormat;
        String newPathSegment = wroteHumanReadableName ? addUnderScores(name) : attributeName;
        String nodeId = modelInfoLookup.getArchetypeNodeIdFromRMObject(rmObject);

        if(nodeId != null && !wroteHumanReadableName) {

            if(indexNotation == IndexNotation.AFTER_A_COLON) {
                newPathSegment = newPathSegment + "[" + nodeId + "]";
                if (index != null) {
                    newPathSegment = newPathSegment + ":" + index;
                }
            } else {
                if(index == null) {
                    newPathSegment = newPathSegment + "[" + nodeId + "]";
                } else {
                    newPathSegment = newPathSegment + "[" + nodeId + "," + index + "]";
                }
            }
        } else if (index != null) {
            if(indexNotation == IndexNotation.AFTER_A_COLON) {
                newPathSegment = newPathSegment + ":" + index;
            } else {
                newPathSegment = newPathSegment + "[" + index + "]";
            }
        }

        if(pathSoFar.endsWith("/")) {
            return pathSoFar + newPathSegment;
        }
        return pathSoFar + pathSeparator + newPathSegment;
    }

    private String addUnderScores(String name) {
        if(name == null) {
            return null;
        }
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private class IgnoredAttribute {
        private RMTypeInfo type;
        private String attributeName;

        public IgnoredAttribute(RMTypeInfo type, String attributeName) {
            this.type = type;
            this.attributeName = attributeName;
        }

        public RMTypeInfo getType() {
            return type;
        }

        public void setType(RMTypeInfo type) {
            this.type = type;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }
    }
}

