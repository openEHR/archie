package com.nedap.archie.adl14;

import com.nedap.archie.adl14.log.CreatedCode;
import com.nedap.archie.adl14.log.ReasonForCodeCreation;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.terminology.OpenEHRTerminologyAccess;
import com.nedap.archie.terminology.TermCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
                    List<CPrimitiveObject<?, ?>> termCodes = tuple.getTuples().stream().map(p -> p.getMember(index)).collect(Collectors.toList());
                    Set<String> atCodes = new LinkedHashSet<>();
                    for (CPrimitiveObject<?, ?> cPrimitiveObject : termCodes) {
                        CTerminologyCode cTerminologyCode = (CTerminologyCode) cPrimitiveObject;
                        convertCTerminologyCode(cTerminologyCode);
                        if(cTerminologyCode.getConstraint() != null) {
                            String constraint = cTerminologyCode.getConstraint();
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
            for (CPrimitiveObject<?, ?> cPrimitiveObject : primitiveTuple.getMembers()) {
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

    /**
     * Converts a single-code {@link CTerminologyCode} constraint from ADL 1.4 to ADL 2 format.
     * Multi-code constraints (stored via {@link CTerminologyCode#getPendingCodes()} by the ADL 1.4 parser)
     * are delegated to {@link #convertMultiCodeTerminologyCode}.
     * <p>
     * Three cases are handled:
     * <ul>
     *   <li>Local at-code (e.g. {@code at0001}): converted to its ADL 2 equivalent.</li>
     *   <li>Local ac-code (e.g. {@code ac0001}): converted to its ADL 2 value-set code equivalent.</li>
     *   <li>External terminology code (e.g. {@code [snomed-ct::12345]}): a term binding is created and
     *       a new local at-code is generated to reference it.</li>
     * </ul>
     */
    private void convertCTerminologyCode(CTerminologyCode cTerminologyCode) {
        List<String> pendingCodes = cTerminologyCode.getPendingCodes();
        if (pendingCodes != null && !pendingCodes.isEmpty()) {
            convertMultiCodeTerminologyCode(cTerminologyCode, pendingCodes);
            return;
        }

        String constraint = cTerminologyCode.getConstraint();
        if (constraint == null) {
            return;
        }

        TerminologyCode termCode = TerminologyCode.createFromString(constraint);
        boolean isLocalCode = termCode.getTerminologyId() == null
                || termCode.getTerminologyId().equalsIgnoreCase("local");

        if (isLocalCode && AOMUtils.isValueCode(constraint)) {
            // Single local at-code: convert it to its ADL 2 equivalent
            String newCode = converter.convertValueCode(constraint);
            converter.addConvertedCode(constraint, newCode);
            cTerminologyCode.setConstraint(newCode);
        } else if (isLocalCode && AOMUtils.isValueSetCode(termCode.getCodeString())) {
            // Local value-set reference: convert the ac-code to its ADL 2 equivalent
            String newCode = converter.convertValueSetCode(termCode.getCodeString());
            converter.addConvertedCode(termCode.getCodeString(), newCode);
            cTerminologyCode.setConstraint(newCode);
        } else {
            // External terminology: create a term binding and generate a new at-code to reference it
            try {
                URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(termCode);
                Map<String, URI> termBindingsMap = findOrCreateTermBindings(termCode);
                cTerminologyCode.setConstraint(findOrAddTermBindingAndCode(termCode, uri, termBindingsMap));
            } catch (URISyntaxException e) {
                logger.error("error converting term", e);
            }
        }

        convertAssumedValue(cTerminologyCode, isLocalCode);
    }

    /**
     * Converts a multi-code {@link CTerminologyCode} constraint from ADL 1.4 to ADL 2 format.
     * In ADL 1.4, a constraint may reference multiple codes inline (e.g.
     * {@code [local::at0001, at0002]} or {@code [snomed-ct::12345, 67890]}). ADL 2 represents
     * these as a value set (ac-code), which this method creates.
     * <p>
     * The {@code pendingCodes} list was populated by the ADL 1.4 parser:
     * <ul>
     *   <li>For local codes: raw at-codes, e.g. {@code ["at0001", "at0002"]}.</li>
     *   <li>For external codes: full term code refs normalised by the parser,
     *       e.g. {@code ["[snomed-ct::12345]", "[snomed-ct::67890]"]}.</li>
     * </ul>
     * In both cases a value set is created and the constraint is set to its ac-code.
     */
    private void convertMultiCodeTerminologyCode(CTerminologyCode cTerminologyCode, List<String> pendingCodes) {
        TerminologyCode firstCode = TerminologyCode.createFromString(pendingCodes.get(0));
        boolean isLocalCode = firstCode.getTerminologyId() == null
                || firstCode.getTerminologyId().equalsIgnoreCase("local");

        if (isLocalCode) {
            // Convert each at-code and group them into a new value set
            Set<String> convertedCodes = new LinkedHashSet<>();
            for (String code : pendingCodes) {
                String newCode = converter.convertValueCode(code);
                converter.addConvertedCode(code, newCode);
                convertedCodes.add(newCode);
            }
            ValueSet valueSet = findOrCreateValueSet(cTerminologyCode.getArchetype(), convertedCodes, cTerminologyCode);
            cTerminologyCode.setConstraint(valueSet.getId());
        } else {
            // Create a term binding for each external code, then group the resulting at-codes into a value set
            Map<String, URI> termBindingsMap = findOrCreateTermBindings(firstCode);
            List<String> atCodes = new ArrayList<>();
            for (String code : pendingCodes) {
                TerminologyCode termCode = TerminologyCode.createFromString(code);
                try {
                    URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(termCode);
                    atCodes.add(findOrAddTermBindingAndCode(termCode, uri, termBindingsMap));
                } catch (URISyntaxException e) {
                    logger.error("error converting term", e);
                }
            }
            if (!atCodes.isEmpty()) {
                ValueSet valueSet = findOrCreateValueSet(cTerminologyCode.getArchetype(), new LinkedHashSet<>(atCodes), cTerminologyCode);
                cTerminologyCode.setConstraint(valueSet.getId());
            }
        }

        convertAssumedValue(cTerminologyCode, isLocalCode);
    }

    /**
     * Converts the assumed value of a {@link CTerminologyCode} from ADL 1.4 to ADL 2 format,
     * mirroring the logic used for the constraint itself.
     */
    private void convertAssumedValue(CTerminologyCode cTerminologyCode, boolean isLocalCode) {
        if (cTerminologyCode.getAssumedValue() == null) {
            return;
        }
        TerminologyCode assumedValue = cTerminologyCode.getAssumedValue();
        if (isLocalCode) {
            assumedValue.setCodeString(converter.convertValueCode(assumedValue.getCodeString()));
            assumedValue.setTerminologyId(null);
        } else {
            try {
                Map<String, URI> termBindingsMap = findOrCreateTermBindings(assumedValue);
                URI uri = new ADL14ConversionUtil(converter.getConversionConfiguration()).convertToUri(assumedValue);
                assumedValue.setCodeString(findOrAddTermBindingAndCode(assumedValue, uri, termBindingsMap));
                assumedValue.setTerminologyId(null);
                assumedValue.setTerminologyVersion(null);
            } catch (URISyntaxException e) {
                logger.error("error converting term", e);
            }
        }
    }

    private Map<String, URI> findOrCreateTermBindings(TerminologyCode termCode) {
        return archetype.getTerminology().getTermBindings().computeIfAbsent(termCode.getTerminologyId(), k -> new LinkedHashMap<>());
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

        addTermBindingCode(archetype, termCode.toString(), uri, valueCode);
        return valueCode;
    }

    protected static void addTermBindingCode(Archetype archetype, String termCode, URI uri, String valueCode) {
        for (String language : archetype.getTerminology().getTermDefinitions().keySet()) {
            TermCode termFromTerminology = OpenEHRTerminologyAccess.getInstance().getTermByTerminologyURI(uri.toString(), language);
            TermCode fallbackCode = OpenEHRTerminologyAccess.getInstance().getTermByTerminologyURI(uri.toString(), "en");
            ArchetypeTerm term = new ArchetypeTerm();
            term.setCode(valueCode);
            if(termFromTerminology != null) {
                term.setText(termFromTerminology.getDescription());
            } else if (fallbackCode != null) {
                term.setText("* " + fallbackCode.getDescription() + " (en)");
            }else {
                term.setText("Term binding for " + termCode + ", translation not known in ADL 1.4 -> ADL 2 converter");

            }
            term.setDescription(term.getText());
            archetype.getTerminology().getTermDefinitions().get(language).put(valueCode, term);
        }
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
                            if(termCodeInParent.getConstraint() != null) {
                                if(termCodeInParent.getConstraint().startsWith("ac")) {
                                    idInparent = termCodeInParent.getConstraint();
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
