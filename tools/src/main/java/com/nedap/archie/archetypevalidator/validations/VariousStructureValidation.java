package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.ArchetypeSlot;
import com.nedap.archie.aom.CArchetypeRoot;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ArchetypeValidatingVisitor;
import com.nedap.archie.flattener.ArchetypeRepository;
import com.nedap.archie.rules.Assertion;

import java.util.List;

public class VariousStructureValidation extends ArchetypeValidatingVisitor {

    public VariousStructureValidation() {
        super();
    }
    
    protected void beginValidation(Archetype archetype, Archetype flatParent, ArchetypeRepository repository) {
        this.archetype = archetype;
        this.flatParent = flatParent;
        this.repository = repository;
    }

    @Override
    protected void validate(ArchetypeSlot slot) {

        List<Assertion> includes = slot.getIncludes();
        List<Assertion> excludes = slot.getExcludes();
        if(!includes.isEmpty()) {
            if(includes.get(0).matchesAny()) {
                if(!(excludes.isEmpty() || !excludes.get(0).matchesAny())) {
                    addMessageWithPath(ErrorType.VDSEV, slot.path());
                }
            } else {
                if(!(excludes.isEmpty() || excludes.get(0).matchesAny())) {
                    addMessageWithPath(ErrorType.VDSEV, slot.path());
                }
            }
        }
    }

    @Override
    protected void validate(CComplexObject cComplexObject) {
        if(cComplexObject instanceof CArchetypeRoot) {
            CArchetypeRoot archetypeRoot = (CArchetypeRoot) cComplexObject;
            if(archetypeRoot.getArchetypeRef() != null) {
                if (repository.getArchetype(archetypeRoot.getArchetypeRef()) == null) {
                    addMessageWithPath(ErrorType.VARXRA, cComplexObject.path(), String.format("archetype with id %s not found", archetypeRoot.getArchetypeRef()));
                }

                ArchetypeHRID hrId = new ArchetypeHRID(archetypeRoot.getArchetypeRef());
                String archetypeRootTypeName = cComplexObject.getRmTypeName();
                String archetypeReferenceTypeName = hrId.getRmClass();

                if (combinedModels.typeNameExists(archetypeRootTypeName)) {
                    //if parent type info not found will be checked later in phase 2
                    if (!combinedModels.typeNameExists(archetypeReferenceTypeName)) {
                        addMessageWithPath(ErrorType.VCORM, cComplexObject.getPath(), cComplexObject.getRmTypeName());
                    } else if (!combinedModels.rmTypesConformant(archetypeReferenceTypeName, archetypeRootTypeName)) {
                        addMessageWithPath(ErrorType.VARXTV, cComplexObject.getPath(), cComplexObject.getRmTypeName());
                    }
                }
            } else {
                if(!(archetypeRoot.getOccurrences() != null && archetypeRoot.getOccurrences().isProhibited())) {
                    addMessageWithPath(ErrorType.VARXR, archetypeRoot.getPath(), "archetype root must have an archetype reference or be prohibited (occurrences matches {0})");
                }
            }
        }
    }



}
