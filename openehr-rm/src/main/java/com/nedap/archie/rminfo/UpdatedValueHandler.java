package com.nedap.archie.rminfo;

import com.google.common.base.Joiner;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.base.Interval;
import com.nedap.archie.paths.PathSegment;
import com.nedap.archie.query.APathQuery;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.support.identification.TerminologyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatedValueHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdatedValueHandler.class);

    public static Map<String, Object> pathHasBeenUpdated(Object rmObject, Archetype archetype, String pathOfParent, Object parent) {
        if(parent instanceof CodePhrase) {
            return fixCodePhrase(rmObject, archetype, pathOfParent);
        }

        return new HashMap<>();
    }

    private static Map<String, Object> fixCodePhrase(Object rmObject, Archetype archetype, String pathOfParent) {
        try {
            //special case: if at-code has been set, we need to do more!
            if (pathOfParent.endsWith("value/defining_code") || pathOfParent.endsWith("null_flavour/defining_code")) {
                return fixDvCodedText(rmObject, archetype, pathOfParent);
            } else if (pathOfParent.endsWith("symbol/defining_code")) {
                return fixDvOrdinal(rmObject, archetype, pathOfParent);
            } else {
            }
        } catch (Exception e) {
            logger.warn("cannot fix codephrase", e);
        }

        return new HashMap<>();
    }

    private static Map<String, Object> fixDvOrdinal(Object rmObject, Archetype archetype, String pathOfParent) throws XPathExpressionException {
        Map<String, Object> result = new HashMap<>();

        RMPathQuery rmPathQuery = new RMPathQuery(pathOfParent.replace("/symbol/defining_code", ""));
        DvOrdinal ordinal = rmPathQuery.find(ArchieRMInfoLookup.getInstance(), rmObject);
        Long value = null;
        CAttribute symbolAttribute = archetype.itemAtPath(pathOfParent.replace("/symbol/defining_code", "/symbol"));//TODO: remove all numeric indices from path!
        if (symbolAttribute != null) {
            CAttributeTuple socParent = (CAttributeTuple) symbolAttribute.getSocParent();
            if (socParent != null) {
                int valueIndex = socParent.getMemberIndex("value");
                int symbolIndex = socParent.getMemberIndex("symbol");
                if (valueIndex != -1 && symbolIndex != -1) {
                    for (CPrimitiveTuple tuple : socParent.getTuples()) {
                        if (tuple.getMembers().get(symbolIndex).getConstraint().get(0).equals(ordinal.getSymbol().getDefiningCode().getCodeString())) {
                            List<Interval<Long>> valueConstraint = (List<Interval<Long>>) tuple.getMembers().get(valueIndex).getConstraint();
                            if(valueConstraint.size() == 1) {
                                Interval<Long> interval  = valueConstraint.get(0);
                                if(interval.getLower().equals(interval.getUpper()) && !interval.isLowerUnbounded() && !interval.isUpperUnbounded()) {
                                    value = interval.getLower();
                                    ordinal.setValue(value);
                                    String pathToValue = pathOfParent.replace("/symbol/defining_code", "/value");
                                    result.put(pathToValue, value);
                                }

                            }
                        }
                    }

                }
            }
        }

        return result;
    }

    private static Map<String, Object> fixDvCodedText(Object rmObject, Archetype archetype, String pathOfParent) throws XPathExpressionException {
        Map<String, Object> result = new HashMap<>();

        String path = pathOfParent.replace("/defining_code", "");
        RMPathQuery rmPathQuery = new RMPathQuery(path);
        DvCodedText codedText = rmPathQuery.find(ArchieRMInfoLookup.getInstance(), rmObject);
        Archetyped details = findLastArchetypeDetails(rmObject, pathOfParent);
        if(details != null && archetype instanceof OperationalTemplate) {

            OperationalTemplate template = (OperationalTemplate) archetype;

            String archetypePath = convertRMObjectPathToArchetypePath(pathOfParent);
            result.putAll(setTerminologyFromArchetype(archetype, codedText, archetypePath, path));

            ArchetypeTerm termDefinition = getTermDefinition(template, details, codedText);
            result.putAll(setDvCodedTextValue(codedText, termDefinition, path));
            //setValueFromTermDefinition(codedText, details, template);
        } else {
            //result.putAll(setDefaultTermDefinitionInCodedText(archetype, codedText, pathOfParent));
            ArchetypeTerm termDefinition = archetype.getTerminology().getTermDefinition(ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage(), codedText.getDefiningCode().getCodeString());
            result.putAll(setDvCodedTextValue(codedText, termDefinition, path));
        }

        return result;
    }

    private static ArchetypeTerm getTermDefinition(OperationalTemplate template, Archetyped details, DvCodedText codedText) {
        ArchetypeTerminology archetypeTerminology = template.getComponentTerminologies().get(details.getArchetypeId().toString());
        if(archetypeTerminology != null) {
            ArchetypeTerm termDefinition = archetypeTerminology.getTermDefinition(ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage(), codedText.getDefiningCode().getCodeString());
            if(termDefinition != null) {
                return termDefinition;
            }
        }
        return template.getTerminology().getTermDefinition(ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage(), codedText.getDefiningCode().getCodeString());
    }

    private static Map<String, Object> setDvCodedTextValue(DvCodedText codedText, ArchetypeTerm termDefinition, String path) {
        Map<String, Object> result = new HashMap<>();
        if(termDefinition != null) {
            String value = termDefinition.getText();
            codedText.setValue(value);
            result.put(path + "/value", value);
        }
        return result;
    }

    private static Map<String, Object> setTerminologyFromArchetype(Archetype archetype, DvCodedText codedText, String s, String path) {
        Map<String, Object> result = new HashMap<>();
        ArchetypeModelObject archetypeModelObject = archetype.itemAtPath(s);
        if(archetypeModelObject instanceof CAttribute) {
            CAttribute definingCodeConstraint = (CAttribute) archetypeModelObject;
            for(CObject child:definingCodeConstraint.getChildren()) {
                if(child instanceof CTerminologyCode) {
                    String value = ((CTerminologyCode) child).getConstraint().get(0);
                    if(value.startsWith("ac")) {
                        codedText.getDefiningCode().setTerminologyId(new TerminologyId(value));
                        result.put(path + "/defining_code/terminology_id/value", value);
                    }
                }
            }
        }
        return result;
    }

    public static Archetyped findLastArchetypeDetails(Object rmObject, String path) throws XPathExpressionException {
        APathQuery query = new APathQuery(path);
        for(int i = query.getPathSegments().size();i > 0; i--) {
            String subpath = Joiner.on("").join(query.getPathSegments().subList(0, i));

            List<RMObjectWithPath> list = new RMPathQuery(subpath).findList(ArchieRMInfoLookup.getInstance(), rmObject);
            for(RMObjectWithPath objectWithPath:list) {
                Object object = objectWithPath.getObject();
                if(object instanceof Locatable) {
                    Locatable object1 = (Locatable) object;
                    if(object1.getArchetypeDetails() != null) {
                        return object1.getArchetypeDetails();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Convert an RM path query into an AOM path query. Not a complete implementation though. this could actually be useful in more places.
     * @param path
     * @return
     */
    public static String convertRMObjectPathToArchetypePath(String path) {
        APathQuery query = new APathQuery(path);
        for(PathSegment segment:query.getPathSegments()) {
            segment.setIndex(null);
        }
        return Joiner.on("").join(query.getPathSegments());
    }
}
