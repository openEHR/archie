package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.query.AOMPathQuery;
import org.openehr.utils.message.I18n;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BasicTerminologyValidation extends ArchetypeValidationBase {

    public BasicTerminologyValidation() {
        super();
    }

    @Override
    public void validate() {

        validateFormatAndSpecializationLevelOfCodes();
        validateLanguageConsistency();
        validateTerminologyBindings();
        validateValueSets();
        warnAboutUnusedValues();

    }

    private void validateFormatAndSpecializationLevelOfCodes() {
        int terminologySpecialisationDepth = archetype.getTerminology().specialisationDepth();
        for(Map<String, ArchetypeTerm> languageSpecificTerminology:archetype.getTerminology().getTermDefinitions().values()) {
            for(ArchetypeTerm term:languageSpecificTerminology.values()) {
                if(!AOMUtils.isValidCode(term.getCode())) {
                    addMessage(ErrorType.VATCV, I18n.t("Id code {0} in terminology is not a valid term code, should be id, ac or at, followed by digits", term.getCode()));
                }
                if(archetype.isDifferential()) {
                    if(terminologySpecialisationDepth != AOMUtils.getSpecializationDepthFromCode(term.getCode())) {
                        addMessage(ErrorType.VTSD, I18n.t("Id code {0} in terminology is of a different specialization depth than the archetype", term.getCode()));
                    }
                } else {
                    if(AOMUtils.getSpecializationDepthFromCode(term.getCode()) > terminologySpecialisationDepth) {
                        addMessage(ErrorType.VTSD, I18n.t("Id code {0} in terminology is of a different specialization depth than the archetype", term.getCode()));
                    }
                }
            }
        }

    }

    public void validateLanguageConsistency() {
        List<String> codes = archetype.getTerminology().allCodes();
        for(String code:codes) {
            for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                if(!archetype.getTerminology().getTermDefinitions().get(language).containsKey(code)) {
                    addMessage(ErrorType.VTLC, "code " + code + " is not present in language " + language);
                }
            }
        }
    }

    private void validateTerminologyBindings() {
        ArchetypeTerminology terminology = archetype.getTerminology();
        Map<String, Map<String, URI>> termBindings = terminology.getTermBindings();
        if(termBindings != null) {
            for (String terminologyId : termBindings.keySet()) {
                if(termBindings.get(terminologyId) != null) {
                    for (String constraintCodeOrPath : termBindings.get(terminologyId).keySet()) {
                        boolean archetypeHasPath = false;
                        try {
                            archetypeHasPath = !new AOMPathQuery(constraintCodeOrPath).findList(archetype.getDefinition()).isEmpty();
                        } catch (Exception e) {
                            //if not a valid path, fine
                        }
                        if (!AOMUtils.isValidCode(constraintCodeOrPath) && !(
                                archetypeHasPath || combinedModels.hasReferenceModelPath(archetype.getDefinition().getRmTypeName(), constraintCodeOrPath)
                        )
                        ) {
                            addMessage(ErrorType.VTTBK, I18n.t("Term binding key {0} in path format is not present in archetype", constraintCodeOrPath));
                        } else if (AOMUtils.isValidCode(constraintCodeOrPath) &&
                                !terminology.hasCode(constraintCodeOrPath) &&
                                !(archetype.isSpecialized() && flatParent != null && !flatParent.getTerminology().hasCode(constraintCodeOrPath))
                        ) {
                            addMessage(ErrorType.VTTBK, I18n.t("Term binding key {0} is not present in terminology", constraintCodeOrPath));
                        } else {
                            //TODO: two warnings
                        }
                    }
                }
            }
        }
    }

    private void validateValueSets() {
        ArchetypeTerminology terminology = archetype.getTerminology();
        for(ValueSet valueSet:terminology.getValueSets().values()){
            if(!terminology.hasValueSetCode(valueSet.getId())) {
                addMessage(ErrorType.VTVSID, I18n.t("value set code {0} is not present in terminology", valueSet.getId()));
            }
            for(String value:valueSet.getMembers()) {
                if(flatParent == null) {
                    if(!terminology.hasValueCode(value)) {
                        addMessage(ErrorType.VTVSMD, I18n.t("value code {0} is not present in terminology", value));
                    }
                } else {
                    if(!(terminology.hasValueCode(value) || flatParent.getTerminology().hasValueCode(value))) {
                        addMessage(ErrorType.VTVSMD, I18n.t("value code {0} is not present in terminology", value));
                    }
                }
            }
            //TODO: we should check for uniqueness, but valueset is a java.util.Set, so there can be no duplicates by definition
        }
    }

    private void warnAboutUnusedValues() {
        Set<String> usedCodes = archetype.getAllUsedCodes();
        ArchetypeTerminology terminology = archetype.getTerminology();
        for(String language:terminology.getTermDefinitions().keySet()) {
            Map<String, ArchetypeTerm> archetypeTerms = terminology.getTermDefinitions().get(language);
            for(String key:archetypeTerms.keySet()) {
                if(!usedCodes.contains(key)) {
                    addWarning(ErrorType.WOUC, I18n.t("Code {0} is in the terminology, but not used in the archetype", key));
                }
            }
        }
    }


}
