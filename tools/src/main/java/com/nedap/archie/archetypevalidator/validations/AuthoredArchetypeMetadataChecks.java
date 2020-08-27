package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.ResourceDescriptionItem;
import com.nedap.archie.aom.TranslationDetails;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import org.openehr.utils.message.I18n;

import java.util.Objects;

public class AuthoredArchetypeMetadataChecks extends ArchetypeValidationBase {

    public AuthoredArchetypeMetadataChecks() {
        super();
    }
    @Override
    public void validate() {
        if(archetype instanceof AuthoredArchetype) {
            AuthoredArchetype authoredArchetype = (AuthoredArchetype) archetype;

            checkOriginalLanguagePresent();
            checkAdlRmVersionIdFormats();
            validateDescription();
            if(this.hasPassed()) {
                checkLanguagesInTranslationAreInTerminology();
            }

        }
    }

    private void validateDescription() {
        if(archetype.getDescription().getDetails() != null) {
            for(String language:archetype.getDescription().getDetails().keySet()) {
                ResourceDescriptionItem resourceDescriptionItem = archetype.getDescription().getDetails().get(language);
                if(resourceDescriptionItem.getLanguage() == null || !Objects.equals(language, resourceDescriptionItem.getLanguage().getCodeString())){
                    addMessage(ErrorType.VRDLA, I18n.t("Resource description language {0} has an incorrect key: {1}", language, resourceDescriptionItem.getLanguage() == null ? null : resourceDescriptionItem.getLanguage().getCodeString()));
                }

            }
        }
    }

    private void checkLanguagesInTranslationAreInTerminology() {
        if(archetype.getTranslations() != null) {
            for(String language:archetype.getTranslations().keySet()) {
                TranslationDetails translationDetails = archetype.getTranslations().get(language);
                if(translationDetails.getLanguage() == null || !language.equals(translationDetails.getLanguage().getCodeString())) {
                    addMessage(ErrorType.VTRLA, I18n.t("Translation details language {0} has an incorrect key: {1}", language, translationDetails.getLanguage().getCodeString()));
                }
                //check if also defined in terminology
                if(archetype.getTerminology() == null || archetype.getTerminology().getTermDefinitions() == null || archetype.getTerminology().getTermDefinitions().get(language) == null) {
                    addMessage(ErrorType.VOTM, I18n.t("Language {0} is defined in the translations, but is not present in the terminology", language));
                }
            }
        }
    }

    private void checkAdlRmVersionIdFormats() {
        if(!isValidVersion(archetype.getAdlVersion())) {
            addMessage(ErrorType.VARAV, I18n.t("ADL version {0} is an invalid format for a version, should be x.x.x-(rc|alpha(x)?)?", archetype.getAdlVersion()));
        }
        if(!isValidVersion(archetype.getRmRelease())) {
            addMessage(ErrorType.VARRV, I18n.t("RM Release version {0} is an invalid format for a version, should be x.x.x-(rc|alpha(x)?)?", archetype.getRmRelease()));
        }
    }

    private void checkOriginalLanguagePresent() {
        if(archetype.getOriginalLanguage() != null) {
            String languageCode = archetype.getOriginalLanguage().getCodeString();;
            if(languageCode != null) {
                if(archetype.getTerminology().getTermDefinitions().get(languageCode) == null) {
                    addMessage(ErrorType.VOLT, I18n.t("Original language {0} is not defined in the terminology", languageCode));
                }
            }
        } else {
            addMessage(ErrorType.VDEOL, I18n.t("Original language is missing in archetype"));
        }
    }

    private boolean isValidVersion(String version) {
        //from grammar: DIGIT+ '.' DIGIT+ '.' DIGIT+ ( ( '-rc' | '-alpha' ) ( '.' DIGIT+ )? )? ;
        return version != null && version.matches("\\d+\\.\\d+\\.\\d+((-rc|-alpha)(\\.\\d+)?)?");
    }
}
