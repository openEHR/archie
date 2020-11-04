package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.log.ADL2ConversionLog;
import com.nedap.archie.adl14.log.CreatedCode;
import com.nedap.archie.adl14.log.ReasonForCodeCreation;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ArchetypeModelObject;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CAttributeTuple;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveTuple;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import org.openehr.utils.message.I18n;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Takes the ConversionLog from a previous run and applies the changes that where applied there.
 *
 * Preconditions: an archetype that has it's node-ids already converted, but no synthesized node ids present
 * and no CTerminologyCodes processed, with the terminology still intact.
 */
public class PreviousConversionApplier {

    private ADL14NodeIDConverter converter;
    private final Archetype archetype;
    private final ADL2ConversionLog conversionLog;

    public PreviousConversionApplier(ADL14NodeIDConverter converter, Archetype archetype, ADL2ConversionLog conversionLog) {
        this.converter = converter;
        this.archetype = archetype;
        this.conversionLog = conversionLog;
    }

    public void addCreatedCodes() {
        if(this.conversionLog == null) {
            return;
        }

        if(conversionLog.getCreatedCodes() != null) {
            for (CreatedCode createdCode : conversionLog.getCreatedCodes().values()) {
                switch (createdCode.getReasonForCreation()) {
                    case C_OBJECT_WITHOUT_NODE_ID:
                        applyPreviousSynthesizedCode(createdCode);
                        break;
                    case CREATED_VALUE_SET:
                        //will be done elsewhere, since here the members are unknown
                        break;
                    case CREATED_VALUE_FOR_EXTERNAL_TERM:
                        applyPreviousTermCodeCreation(createdCode);
                        break;
                }
            }
        }
    }

    public void addValueSets() {
        if(this.conversionLog == null) {
            return;
        }
        if(conversionLog.getCreatedValueSets() != null && !conversionLog.getCreatedValueSets().isEmpty()) {
            if(archetype.getTerminology().getValueSets() == null) {
                archetype.getTerminology().setValueSets(new LinkedHashMap<>());
            }
            Map<String, ValueSet> valueSets = archetype.getTerminology().getValueSets();
            valueSets.putAll(conversionLog.getCreatedValueSets());
            for(Map.Entry<String, ValueSet> entry:conversionLog.getCreatedValueSets().entrySet()) {
                //store in the convert to preserve the conversion log
                converter.addCreatedValueSet(entry.getKey(), entry.getValue());
            }
        }
    }

    private void applyPreviousTermCodeCreation(CreatedCode createdCode) {
        try {
            Map<String, URI> termBindingsMap = archetype.getTerminology().getTermBindings().get(createdCode.getOriginalTerm().getTerminologyId());
            URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(createdCode.getOriginalTerm());
            if (termBindingsMap == null) {
                termBindingsMap = new LinkedHashMap<>();
                archetype.getTerminology().getTermBindings().put(createdCode.getOriginalTerm().getTerminologyId(), termBindingsMap);
            }

            String existingTermBinding = ADL14ConversionUtil.findExistingTermBinding(createdCode.getOriginalTerm().getTerminologyId(), archetype, null, uri, termBindingsMap);
            if (existingTermBinding == null) {
                String valueCode = createdCode.getGeneratedCode();
                termBindingsMap.put(valueCode, uri);
                //make sure the next conversion log will get the same value again
                CreatedCode newCreatedCode = new CreatedCode(valueCode, ReasonForCodeCreation.CREATED_VALUE_FOR_EXTERNAL_TERM);
                newCreatedCode.setOriginalTerm(createdCode.getOriginalTerm());
                converter.addCreatedCode(createdCode.getOriginalTerm().toString(), createdCode);

                for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
                    ArchetypeTerm term = new ArchetypeTerm();
                    term.setCode(valueCode);
                    term.setText("Term binding for " + createdCode.getGeneratedCode() + ", translation not known in ADL 1.4 -> ADL 2 converter");
                    term.setDescription(term.getText());
                    archetype.getTerminology().getTermDefinitions().get(language).put(valueCode, term);
                }
            }
        } catch (URISyntaxException e) {
            //TODO: warn? or add generated URL to CreatedCode?
        }
    }

    private void applyPreviousSynthesizedCode(CreatedCode createdCode) {
        ArchetypeModelObject object = archetype.itemAtPath(createdCode.getPathCreated());
        if(object == null) {
            //This happens when something has been deleted in the source archetype. That's fine
            this.converter.getConversionResult().getLog().addInfoWithLocation(ADL14ConversionMessageCode.INFO_PREVIOUSLY_CONVERTED_CODE_DELETED, createdCode.getPathCreated());
        } else if(!(object instanceof CAttribute)) {
            //this MUST be an attribute, so something is wrong
            throw new IllegalArgumentException("path in previously converted node id points to a CObject, must be a CAttribute");
        } else if (createdCode.getRmTypeName() == null) {
            throw new IllegalArgumentException("When applying a previously synthesized node id, the rm type name field must be present");
        } else {
            CAttribute attribute = (CAttribute) object;
            int index = attribute.getIndexOfChildWithMatchingRmTypeName(createdCode.getRmTypeName());
            if(index >= 0) {
                CObject cObject = attribute.getChildren().get(index);
                cObject.setNodeId(createdCode.getGeneratedCode());
                //store the created code so it appears in the new conversion log as well
                converter.addCreatedCode(createdCode.getGeneratedCode(), createdCode);

            } else {
                this.converter.getConversionResult().getLog().addInfoWithLocation(ADL14ConversionMessageCode.INFO_PREVIOUSLY_CONVERTED_CODE_DELETED, createdCode.getPathCreated());
            }
        }
    }

    /**
     * Return true if the node is a previously converted node, to make sure it does not get converted again
     * @return
     */
    public boolean isConvertedNode(String code) {
        return false;
    }

    /**
     * Remove all created value sets and term bindings that are not used in this archetype
     */
    public void removeCreatedUnusedTermCodesAndValueSets() {
        if(this.conversionLog == null) {
            return;
        }
        removeUnusedValuesets();
        removeUnusedValues();
    }

    private void removeUnusedValuesets() {
        Set<String> usedValueSets = gatherUsedValueSets(archetype.getDefinition());
        for(Map.Entry<String, ValueSet> valueSetEntry:this.conversionLog.getCreatedValueSets().entrySet()) {
            if(!usedValueSets.contains(valueSetEntry.getKey())) {
                //ok, unused, remove it
                archetype.getTerminology().getValueSets().remove(valueSetEntry.getKey());
                removeTerminologyEntry(valueSetEntry.getKey());
            }
        }
    }

    private void removeUnusedValues() {
        Set<String> usedCodes = archetype.getAllUsedCodes();
        for(Map.Entry<String, CreatedCode> valueEntry:this.conversionLog.getCreatedCodes().entrySet()) {
            if(valueEntry.getValue().getReasonForCreation() == ReasonForCodeCreation.CREATED_VALUE_FOR_EXTERNAL_TERM
                    && !usedCodes.contains(valueEntry.getValue().getGeneratedCode())) {
                //unused code found. Remove!
                removeTerminologyEntry(valueEntry.getValue().getGeneratedCode());
            }
        }
    }

    private void removeTerminologyEntry(String code) {
        archetype.getTerminology().getTermDefinitions().values().forEach(
                map -> map.remove(code)
        );

        if(archetype.getTerminology().getTermBindings() != null) {

            List<String> termCodeBindingsToRemove = new ArrayList<>();

            for (String terminologyId: archetype.getTerminology().getTermBindings().keySet()) {
                Map<String, URI> termBindings = archetype.getTerminology().getTermBindings().get(terminologyId);
                termBindings.remove(code);
                if(termBindings.isEmpty()) {
                    termCodeBindingsToRemove.add(terminologyId);
                }
            }

            for(String terminologyId:termCodeBindingsToRemove) {
                archetype.getTerminology().getTermBindings().remove(terminologyId);
            }
        }

    }

    private Set<String> gatherUsedValueSets(CObject cObject) {
        Set<String> result = new LinkedHashSet<>();
        for(CAttribute attribute:cObject.getAttributes()) {
            result.addAll(gatherUsedValueSets(attribute));
        }
        return result;
    }

    private Set<String> gatherUsedValueSets(CAttribute attribute) {
        Set<String> result = new LinkedHashSet<>();
        for(CObject child:attribute.getChildren()) {
            if(child instanceof CTerminologyCode) {
                for(String constraint:((CTerminologyCode) child).getConstraint()) {
                    if(constraint.startsWith("ac")) {
                        result.add(constraint);
                    }
                }
            }
            if(child.getRmTypeName().equalsIgnoreCase("DV_ORDINAL")) {
                //ordinal constraints have value sets created as well, even though they are not in the ADL
                CComplexObject complexObject = (CComplexObject) child;
                for(CAttributeTuple tuple:complexObject.getAttributeTuples()) {
                    int symbolIndex = tuple.getMemberIndex("symbol");
                    Set<String> atCodes = new HashSet<>();
                    if(symbolIndex >= 0) {
                        for(CPrimitiveTuple primitiveTuple:tuple.getTuples()) {
                            CTerminologyCode cTermCode = (CTerminologyCode) primitiveTuple.getMember(symbolIndex);
                            if(cTermCode != null) {
                                atCodes.addAll(cTermCode.getConstraint());
                            }
                        }
                    }
                    ValueSet set = findLocalValueSet(atCodes);
                    result.add(set.getId());
                }
            }
            result.addAll(gatherUsedValueSets(child));
        }
        return result;
    }

    private ValueSet findLocalValueSet(Set<String> localCodes) {
        return archetype.getTerminology().getValueSets().values().stream()
                .filter( v -> v.getMembers().equals(localCodes))
                .findFirst().orElse(null);
    }
}
