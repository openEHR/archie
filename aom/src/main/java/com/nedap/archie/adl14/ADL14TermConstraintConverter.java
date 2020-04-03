package com.nedap.archie.adl14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.log.CreatedCode;
import com.nedap.archie.adl14.log.ReasonForCodeCreation;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.base.terminology.TerminologyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ADL14TermConstraintConverter {

    private static final Logger logger = LoggerFactory.getLogger(ADL14TermConstraintConverter.class);

    private final Archetype flatParentArchetype;
    private Archetype archetype;
    private ADL14NodeIDConverter converter;

    public ADL14TermConstraintConverter(ADL14NodeIDConverter converter, Archetype archetype, Archetype flatParentArchetype) {
        this.converter = converter;
        this.archetype = archetype;
        this.flatParentArchetype = flatParentArchetype;
    }

    public void convert() {
        convert(archetype.getDefinition());
    }

    private void convert(CObject cObject) {

        if (cObject instanceof CTerminologyCode) {
            convertCTerminologyCode((CTerminologyCode) cObject);
        }
        for(CAttribute attribute:cObject.getAttributes()) {
            convert(attribute);
        }
        if(cObject instanceof CComplexObject) {
            for (CAttributeTuple tuple : ((CComplexObject) cObject).getAttributeTuples()) {
                //tuples have not been properly converted to CAttributes in this parsed model, so we can ignore them above
                Set<Integer> tupleTermCodeIndices = getCTerminologyCodeIndices(tuple);
                for (Integer index : tupleTermCodeIndices) {
                    List<CPrimitiveObject> termCodes = tuple.getTuples().stream().map(p -> p.getMember(index)).collect(Collectors.toList());
                    Set<String> atCodes = new LinkedHashSet<>();
                    for (CPrimitiveObject cPrimitiveObject : termCodes) {
                        CTerminologyCode cTerminologyCode = (CTerminologyCode) cPrimitiveObject;
                        convertCTerminologyCode(cTerminologyCode);
                        if(cTerminologyCode.getConstraint().size() == 1) {
                            String constraint = cTerminologyCode.getConstraint().get(0);
                            if(AOMUtils.isValueCode(constraint)) {
                                atCodes.add(constraint);
                            }
                        }
                    }
                    if(!atCodes.isEmpty()) {
                        findOrCreateValueSet(archetype, atCodes, cObject);
                    }
                }
            }
        }
    }

    private Set<Integer> getCTerminologyCodeIndices(CAttributeTuple tuple) {
        Set<Integer> result = new LinkedHashSet<>();
        for (CPrimitiveTuple primitiveTuple : tuple.getTuples()) {
            int i = 0;
            for (CPrimitiveObject cPrimitiveObject : primitiveTuple.getMembers()) {
                if(cPrimitiveObject instanceof CTerminologyCode) {
                    result.add(i);
                }
                i++;
            }
        }
        return result;
    }

    private void convert(CAttribute attribute) {
        for(CObject object:attribute.getChildren()) {
            convert(object);
        }
    }

    private void convertCTerminologyCode(CTerminologyCode cTerminologyCode) {
        if(cTerminologyCode.getConstraint() != null && !cTerminologyCode.getConstraint().isEmpty()) {
            String firstConstraint = cTerminologyCode.getConstraint().get(0);
            TerminologyCode termCode = TerminologyCode.createFromString(firstConstraint);
            boolean isLocalCode = termCode.getTerminologyId() == null || termCode.getTerminologyId().equalsIgnoreCase("local");
            if(isLocalCode && AOMUtils.isValueCode(firstConstraint)) {
                //local codes
                if(cTerminologyCode.getConstraint().size() == 1) {
                    //do not create a value set, just convert the code
                    String newCode = converter.convertValueCode(firstConstraint);
                    converter.addConvertedCode(firstConstraint, newCode);
                    cTerminologyCode.setConstraint(Lists.newArrayList(newCode));
                } else {
                    Set<String> localCodes = new LinkedHashSet<>();
                    for(String code:cTerminologyCode.getConstraint()) {
                        String newCode = converter.convertValueCode(code);
                        converter.addConvertedCode(code, newCode);
                        localCodes.add(newCode);
                    }

                    ValueSet valueSet = findOrCreateValueSet(cTerminologyCode.getArchetype(), localCodes, cTerminologyCode);
                    cTerminologyCode.setConstraint(Lists.newArrayList(valueSet.getId()));
                }
                if(cTerminologyCode.getAssumedValue() != null) {
                    TerminologyCode assumedValue = cTerminologyCode.getAssumedValue();
                    String oldCode = assumedValue.getCodeString();
                    String newCode = converter.convertValueCode(oldCode);
                    assumedValue.setCodeString(newCode);
                }
            } else if (isLocalCode && AOMUtils.isValueSetCode(termCode.getCodeString())) {
                List<String> newConstraint = new ArrayList<>();
                for(String constraint:cTerminologyCode.getConstraint()) {
                    TerminologyCode code = TerminologyCode.createFromString(constraint);
                    String newCode = converter.convertValueSetCode(code.getCodeString());
                    converter.addConvertedCode(termCode.getCodeString(), newCode);
                    newConstraint.add(newCode);
                }
                cTerminologyCode.setConstraint(newConstraint);

            } else {
                if (cTerminologyCode.getConstraint().size() == 1) {
                    try {
                        //do not create a value set, create a code plus binding to the old non-local code
                        URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(termCode);
                        Map<String, URI> termBindingsMap = findOrCreateTermBindings(termCode);

                        //TODO: check if this is a converted or old term binding - old is unusual, but could be possible!
                        String termBinding = findOrAddTermBindingAndCode(termCode, uri, termBindingsMap);
                        cTerminologyCode.setConstraint(Lists.newArrayList(termBinding));
                    } catch (URISyntaxException e) {
                        //TODO
                        logger.error("error converting term", e);
                    }
                } else {
                    String terminologyId = termCode.getTerminologyId();
                    Map<String, URI> termBindingsMap = findOrCreateTermBindings(termCode);
                    List<String> atCodes = new ArrayList<>();
                    List<String> constraints = new ArrayList<>(cTerminologyCode.getConstraint());
                    cTerminologyCode.setConstraint(atCodes);
                    for(String constraint:constraints) {
                        try {
                            if(constraint.startsWith("[") && constraint.endsWith("]")) {
                                TerminologyCode constraintCode = TerminologyCode.createFromString(constraint);
                                URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(constraintCode);
                                atCodes.add(findOrAddTermBindingAndCode(constraintCode, uri, termBindingsMap));
                            } else {
                                TerminologyCode constraintCode = new TerminologyCode();
                                constraintCode.setTerminologyId(terminologyId);
                                constraintCode.setCodeString(constraint);
                                URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(constraintCode);
                                atCodes.add(findOrAddTermBindingAndCode(constraintCode, uri, termBindingsMap));
                            }

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    ValueSet valueSet = findOrCreateValueSet(cTerminologyCode.getArchetype(), new LinkedHashSet<>(atCodes), cTerminologyCode);
                    cTerminologyCode.setConstraint(Lists.newArrayList(valueSet.getId()));

                }
            }
            if(cTerminologyCode.getAssumedValue() != null) {
                TerminologyCode assumedValue = cTerminologyCode.getAssumedValue();
                if(isLocalCode) {
                    String newCode = converter.convertValueCode(assumedValue.getCodeString());
                    assumedValue.setCodeString(newCode);
                    assumedValue.setTerminologyId(null);
                } else {
                    try {
                        Map<String, URI> termBindingsMap = findOrCreateTermBindings(assumedValue);
                        URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(assumedValue);
                        assumedValue.setCodeString(findOrAddTermBindingAndCode(assumedValue, uri, termBindingsMap));
                        assumedValue.setTerminologyId(null);
                        assumedValue.setTerminologyVersion(null);
                    } catch (URISyntaxException e) {
                        //TODO
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Map<String, URI> findOrCreateTermBindings(TerminologyCode termCode) {
        Map<String, URI> termBindings =  archetype.getTerminology().getTermBindings().get(termCode.getTerminologyId());

        if(termBindings == null) {
            termBindings = new LinkedHashMap<>();
            archetype.getTerminology().getTermBindings().put(termCode.getTerminologyId(), termBindings);
        }
        return termBindings;
    }

    private String findOrAddTermBindingAndCode(TerminologyCode termCode, URI uri, Map<String, URI> termBindingsMap) {
        String existingTermBinding = ADL14ConversionUtil.findExistingTermBinding(termCode.getTerminologyId(), archetype, flatParentArchetype, uri, termBindingsMap);
        if(existingTermBinding != null) {
            return existingTermBinding;
        }
        String valueCode = converter.getIdCodeGenerator().generateNextValueCode();
        termBindingsMap.put(valueCode, uri);
        CreatedCode createdCode = new CreatedCode(valueCode, ReasonForCodeCreation.CREATED_VALUE_FOR_EXTERNAL_TERM);
        createdCode.setOriginalTerm(termCode);
        converter.addCreatedCode(termCode.toString(), createdCode);

        for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
            ArchetypeTerm term = new ArchetypeTerm();
            term.setCode(valueCode);
            term.setText("Term binding for " + termCode.toString() + ", translation not known in ADL 1.4 -> ADL 2 converter");
            term.setDescription(term.getText());
            archetype.getTerminology().getTermDefinitions().get(language).put(valueCode, term);
        }
        return valueCode;
    }

    private ValueSet findOrCreateValueSet(Archetype archetype, Set<String> localCodes, CObject owningConstraint) {
        //TODO: already checks for equal value sets. But if specialized check if parent contains a value set that  can be redefined to
        //be the same
        String idInparent = null;
        if(flatParentArchetype != null) {
            //let's find the exact same value set first
            ValueSet valueSet = findValueSet(flatParentArchetype, localCodes);
            if (valueSet != null) {
                //do not need to add a term in this case
                return valueSet;
            }
            //now if this has a parent with a value set already, we probably want to use that
            if(flatParentArchetype != null) {
                String parentPath = AOMUtils.pathAtSpecializationLevel(owningConstraint.getPathSegments(), archetype.specializationDepth() - 1);
                OpenEHRBase inParent = flatParentArchetype.itemAtPath(parentPath);
                if(inParent instanceof CAttribute) {
                    CAttribute cAttributeInParent = (CAttribute) inParent;
                    if(!cAttributeInParent.getChildren().isEmpty()) {
                        CObject cObject = cAttributeInParent.getChildren().get(0);
                        if(cObject instanceof CTerminologyCode) {
                            CTerminologyCode termCodeInParent = (CTerminologyCode) cObject;
                            if(termCodeInParent.getConstraint() != null && !termCodeInParent.getConstraint().isEmpty()) {
                                if(termCodeInParent.getConstraint().get(0).startsWith("ac")) {
                                    idInparent = termCodeInParent.getConstraint().get(0);
                                }
                            }
                        }
                    }
                }

            }
        }
        ValueSet valueSet = findValueSet(archetype, localCodes);
        if (valueSet != null) {
            //if this value set was added through the conversion log, it would not have had a term set. So ensure it's set every single time
            addTermForValueSet(archetype, owningConstraint, valueSet);
            return valueSet;
        }
        valueSet = new ValueSet();
        valueSet.setMembers(localCodes);
        if(idInparent == null) {
            valueSet.setId(converter.getIdCodeGenerator().generateNextValueSetCode());
        } else {
            valueSet.setId(this.converter.getIdCodeGenerator().generateNextSpecializedIdCode(idInparent));
            //TODO: is adding this in the conversionlog a good idea?
        }

        converter.addCreatedCode(valueSet.getId(), new CreatedCode(valueSet.getId(), ReasonForCodeCreation.CREATED_VALUE_SET));
        converter.addCreatedValueSet(valueSet.getId(), valueSet);
        archetype.getTerminology().getValueSets().put(valueSet.getId(), valueSet);

        addTermForValueSet(archetype, owningConstraint, valueSet);
        return valueSet;
    }

    private void addTermForValueSet(Archetype archetype, CObject owningConstraint, ValueSet valueSet) {
        for(String language: archetype.getTerminology().getTermDefinitions().keySet()) {
            //TODO: add new archetype term to conversion log!
            ArchetypeTerm term = getTerm(language, owningConstraint);
            if(term != null && archetype.getTerminology().getTermDefinitions().get(language).get(valueSet.getId()) == null) {
                ArchetypeTerm newTerm = new ArchetypeTerm();
                newTerm.setCode(valueSet.getId());
                newTerm.setText(term.getText() + " (synthesised)");
                newTerm.setDescription(term.getDescription() + " (synthesised)");
                archetype.getTerminology().getTermDefinitions().get(language).put(newTerm.getCode(), newTerm);
            }
        }
    }

    private ValueSet findValueSet(Archetype archetype, Set<String> localCodes) {
        for(ValueSet valueSet:archetype.getTerminology().getValueSets().values()) {
            if(valueSet.getMembers().equals(localCodes)) {
                return valueSet;
            }
        }
        return null;
    }

    /**
     * Get a term from the archetype terminology for a CObject that has the new node id, but with a yet unconverted
     * terminology, so using the newCodetoOldCodeMap to retrieve the old code first
     * @param owningConstraint
     */
    protected ArchetypeTerm getTerm(String language, CObject owningConstraint) {
        CObject cObject = owningConstraint;
        while(cObject != null) {
            if (cObject.getNodeId() != null) {
                String oldCode = converter.getOldCodeForNewCode(cObject.getNodeId());
                if(oldCode != null && archetype.getTerminology().getTermDefinition(language, oldCode) != null) {
                    ArchetypeTerm term = archetype.getTerminology().getTermDefinition(language, oldCode);
                    if(term != null) {
                        return term;
                    }
                }
            }
            cObject = cObject.getParent() == null ? null : cObject.getParent().getParent();
        }
        return null;
    }

}
