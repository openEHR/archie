package com.nedap.archie.archetypevalidator.validations;


import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import org.openehr.utils.message.I18n;

import java.util.Map;

public class AnnotationsValidation extends ArchetypeValidationBase {

    public AnnotationsValidation() {
        super();
    }

    @Override
    public void validate() {
        if(archetype instanceof AuthoredArchetype) {
            if(archetype.getAnnotations() != null) {
                ResourceAnnotations annotations = archetype.getAnnotations();
                for(String language: annotations.getDocumentation().keySet()) {
                    Map<String, Map<String, String>> annotationsForLanguage = annotations.getDocumentation().get(language);
                    for(String path: annotationsForLanguage.keySet()) {
                        //we need the operational template to look up paths across included archetypes
                        //otherwise any path crossing an ARCHETYPE_ROOT will fail to lookup
                        Archetype operationalTemplate = repository.getOperationalTemplate(archetype.getArchetypeId().toString());
                        if(operationalTemplate == null) {
                            //apparently the operational template creation failed. Try to lookup the path anyway in the original archetype
                            operationalTemplate = archetype;
                        }
                        if(!AOMUtils.isPathInArchetypeOrRm(metaModel, path, operationalTemplate)) {
                            addMessage(ErrorType.VRANP, I18n.t("The path {0} referenced in the annotations does not exist in the flat archetype or reference model", path));
                        }
                    }
                }
            }
        }
    }

}
