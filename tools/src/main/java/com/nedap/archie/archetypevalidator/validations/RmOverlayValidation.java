package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import com.nedap.archie.aom.ResourceAnnotations;
import com.nedap.archie.aom.rmoverlay.RmOverlay;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.archetypevalidator.ArchetypeValidationBase;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.query.AOMPathQuery;
import org.openehr.utils.message.I18n;

import java.util.Map;

public class RmOverlayValidation extends ArchetypeValidationBase {

    public RmOverlayValidation() {
        super();
    }

    @Override
    public void validate() {
        if(archetype instanceof AuthoredArchetype) {
            if(archetype.getRmOverlay() != null) {
                RmOverlay rmOverlay = archetype.getRmOverlay();
                if(rmOverlay.getRmVisibility() != null) {
                    for(String path:rmOverlay.getRmVisibility().keySet()) {

                        //we need the operational template to look up paths across included archetypes
                        //otherwise any path crossing an ARCHETYPE_ROOT will fail to lookup
                        Archetype operationalTemplate = repository.getOperationalTemplate(archetype.getArchetypeId().toString());
                        if(operationalTemplate == null) {
                            //apparently the operational template creation failed. Try to lookup the path anyway in the original archetype
                            operationalTemplate = archetype;
                        }
                        if(!AOMUtils.isPathInArchetypeOrRm(combinedModels.getSelectedModel(), path, operationalTemplate)) {
                            addMessage(ErrorType.VRANP, I18n.t("The path {0} referenced in the rm visibility does not exist in the flat archetype", path));
                        }

                        TerminologyCode alias = rmOverlay.getRmVisibility().get(path).getAlias();
                        if(alias != null && alias.getCodeString() != null && alias.getCodeString().startsWith("ad")) {
                            if(operationalTemplate.getTerm(archetype.getDefinition(), alias.getCodeString(), archetype.getOriginalLanguage().getCodeString()) == null) {
                                addMessage(ErrorType.VATID, I18n.t("The code {0} is missing in the terminology. It is defined in rm_visibility at path {1}", alias.getCodeString(), path));
                            }
                        }
                    }
                }
            }
        }


    }

    /**
     * Check if the archetype has this path directly, or a specialized variant of this path
     * @param path the path to check
     * @param archetype the archetype to find the path in
     * @return true if the archetype has the path, false if it does not
     */
    private boolean hasPath(String path, Archetype archetype) {
        return ! new AOMPathQuery(path).findList(archetype.getDefinition(), true).isEmpty();
    }

}
