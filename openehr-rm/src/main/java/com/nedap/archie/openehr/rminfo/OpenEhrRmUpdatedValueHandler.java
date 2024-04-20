package com.nedap.archie.openehr.rminfo;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.Interval;
import com.nedap.archie.apath.APathQuery;
import com.nedap.archie.query.RMObjectWithPath;
import com.nedap.archie.query.RMPathQuery;
import org.openehr.rm.archetyped.Archetyped;
import org.openehr.rm.archetyped.Locatable;
import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.DvOrdered;
import org.openehr.rm.datavalues.quantity.DvOrdinal;
import org.openehr.rm.datavalues.quantity.DvScale;
import org.openehr.rm.support.identification.TerminologyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenEhrRmUpdatedValueHandler {

    private static final Logger logger = LoggerFactory.getLogger(OpenEhrRmUpdatedValueHandler.class);

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
                return fixDvOrdinalOrDvScale(rmObject, archetype, pathOfParent);
            } else {
            }
        } catch (Exception e) {
            logger.warn("cannot fix codephrase", e);
        }

        return new HashMap<>();
    }

    private static Map<String, Object> fixDvOrdinalOrDvScale(Object rmObject, Archetype archetype, String pathOfParent) throws XPathExpressionException {
        Map<String, Object> result = new HashMap<>();

        RMPathQuery rmPathQuery = new RMPathQuery(pathOfParent.replace("/symbol/defining_code", ""));
        DvOrdered ordered = rmPathQuery.find(OpenEhrRmInfoLookup.getInstance(), rmObject);
        Number value;
        CAttribute symbolAttribute = archetype.itemAtPath(pathOfParent.replace("/symbol/defining_code", "/symbol"));//TODO: remove all numeric indices from path!
        if (symbolAttribute != null) {
            CAttributeTuple socParent = (CAttributeTuple) symbolAttribute.getSocParent();
            if (socParent != null) {
                int valueIndex = socParent.getMemberIndex("value");
                int symbolIndex = socParent.getMemberIndex("symbol");
                if (valueIndex != -1 && symbolIndex != -1) {
                    for (CPrimitiveTuple tuple : socParent.getTuples()) {
                        if ((ordered instanceof DvOrdinal && tuple.getMembers().get(symbolIndex).getConstraint().get(0).equals(((DvOrdinal) ordered).getSymbol().getDefiningCode().getCodeString())) ||
                                ordered instanceof DvScale && tuple.getMembers().get(symbolIndex).getConstraint().get(0).equals(((DvScale) ordered).getSymbol().getDefiningCode().getCodeString())) {
                            List<Interval<Number>> valueConstraint = (List<Interval<Number>>) tuple.getMembers().get(valueIndex).getConstraint();
                            if(valueConstraint.size() == 1) {
                                Interval<Number> interval  = valueConstraint.get(0);
                                if(interval.getLower().equals(interval.getUpper()) && !interval.isLowerUnbounded() && !interval.isUpperUnbounded()) {
                                    value = interval.getLower();
                                    if (ordered instanceof DvOrdinal) {
                                        ((DvOrdinal) ordered).setValue((Long) value);
                                    } else {
                                        ((DvScale) ordered).setValue((Double) value);
                                    }
                                    String pathToValue = pathOfParent.replace("/symbol/defining_code", "/value");
                                    result.put(pathToValue, value);
                                }
                            }
                        }
                    }
                }
            }
        }

        if ((ordered instanceof DvOrdinal && (((DvOrdinal) ordered).getSymbol() != null && ((DvOrdinal) ordered).getSymbol().getDefiningCode() != null)) ||
                (ordered instanceof DvScale && (((DvScale) ordered).getSymbol() != null && ((DvScale) ordered).getSymbol().getDefiningCode() != null))) {
            //also fix the DvCodedText inside the DvOrdinal
            result.putAll(fixDvCodedText(rmObject, archetype, pathOfParent));
        }

        return result;
    }

    private static Map<String, Object> fixDvCodedText(Object rmObject, Archetype archetype, String pathOfParent) throws XPathExpressionException {
        String path = pathOfParent.replace("/defining_code", "");
        RMPathQuery rmPathQuery = new RMPathQuery(path);
        DvCodedText codedText = rmPathQuery.find(OpenEhrRmInfoLookup.getInstance(), rmObject);
        Archetyped details = findLastArchetypeDetails(rmObject, pathOfParent);
        ArchetypeTerm termDefinition;
        if(details != null && archetype instanceof OperationalTemplate) {
            OperationalTemplate template = (OperationalTemplate) archetype;
            termDefinition = getTermDefinition(template, details, codedText);
        } else {
            termDefinition = archetype.getTerminology().getTermDefinition(ArchieLanguageConfiguration.getMeaningAndDescriptionLanguage(), codedText.getDefiningCode().getCodeString());
        }
        return setDvCodedTextValue(codedText, termDefinition, path);
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
        if(codedText.getDefiningCode() != null &&
                (codedText.getDefiningCode().getTerminologyId() == null || Strings.isNullOrEmpty(codedText.getDefiningCode().getTerminologyId().getValue()))) {
            if(AOMUtils.isValueCode(codedText.getDefiningCode().getCodeString())) {
                codedText.getDefiningCode().setTerminologyId(new TerminologyId("local"));
                result.put(path + "/defining_code/terminology_id/value", "local");
            }
        }

        return result;
    }

    public static Archetyped findLastArchetypeDetails(Object rmObject, String path) throws XPathExpressionException {
        APathQuery query = new APathQuery(path);
        for(int i = query.getPathSegments().size();i > 0; i--) {
            String subpath = Joiner.on("").join(query.getPathSegments().subList(0, i));

            List<RMObjectWithPath> list = new RMPathQuery(subpath).findList(OpenEhrRmInfoLookup.getInstance(), rmObject);
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
}
