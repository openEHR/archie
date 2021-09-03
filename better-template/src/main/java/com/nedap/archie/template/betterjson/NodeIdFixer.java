package com.nedap.archie.template.betterjson;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.aom.utils.NodeIdUtil;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import org.openehr.utils.message.I18n;

public class NodeIdFixer {

    private Archetype archetype;
    private Archetype flatParent;

    public void fixNodeIds(Archetype archetype, FlatArchetypeProvider repo) {
        this.archetype = archetype;
        if(archetype.getParentArchetypeId() != null) {
            this.flatParent = repo.getFlatArchetype(archetype.getParentArchetypeId());
        }

        fixNodeId(archetype.getDefinition());

        if(archetype instanceof Template) {
            Template template = (Template) archetype;

            for(TemplateOverlay overlay:template.getTemplateOverlays()) {
                this.archetype = overlay;
                if(archetype.getParentArchetypeId() != null) {
                    this.flatParent = repo.getFlatArchetype(overlay.getParentArchetypeId());
                }
                fixNodeId(overlay.getDefinition());
            }
        }
    }

    private void fixNodeId(CObject cObject) {

        if (cObject.isRootNode() || !cObject.getParent().isSecondOrderConstrained()) {
            if (AOMUtils.getSpecializationDepthFromCode(cObject.getNodeId()) <= flatParent.specializationDepth()
                    || new NodeIdUtil(cObject.getNodeId()).isRedefined()) {
                if (!AOMUtils.isPhantomPathAtLevel(cObject.getPathSegments(), flatParent.specializationDepth())) {
                    String flatPath = AOMUtils.pathAtSpecializationLevel(cObject.getPathSegments(), flatParent.specializationDepth());
                    CObject parentCObject = getCObject(flatParent.itemAtPath(flatPath));

                    if (parentCObject != null) {
                        if (cObject.isProhibited()) {
                            if (!parentCObject.getNodeId().equals(cObject.getNodeId())) {
                               // System.out.println("fixing node id " + cObject.getNodeId() + " for archetype " + archetype.getArchetypeId());
                                String oldNodeId = cObject.getNodeId();
                                cObject.setNodeId(parentCObject.getNodeId());
                                ArchetypeTerminology terminology = cObject.getArchetype().getTerminology();
                                for(String language:terminology.getTermDefinitions().keySet()) {
                                    terminology.getTermDefinitions().get(language).remove(oldNodeId);
                                }
                            }
                        }
                    }
                }
            }
        }

        for(CAttribute attribute:cObject.getAttributes()) {
            fixNodeId(attribute);
        }
    }

    private void fixNodeId(CAttribute cAttribute) {
        for(CObject cObject:cAttribute.getChildren()) {
            fixNodeId(cObject);
        }
    }

    private CObject getCObject(ArchetypeModelObject archetypeModelObject) {
        if(archetypeModelObject instanceof CAttribute) {
            CAttribute attribute = (CAttribute) archetypeModelObject;
            if(attribute.getChildren().size() == 1) {
                return attribute.getChildren().get(0);
            }//TODO: add a numeric identifier to the getPath() method in CObject so this can be deleted and actually works in all cases!
        } else if(archetypeModelObject instanceof CObject) {
            return (CObject) archetypeModelObject;
        }
        return null;
    }
}
