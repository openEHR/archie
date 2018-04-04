package com.nedap.archie.adlchecker;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.primitives.CTerminologyCode;
import com.nedap.archie.aom.terminology.ArchetypeTerm;
import com.nedap.archie.aom.terminology.ValueSet;
import com.nedap.archie.aom.utils.AOMUtils;
import com.nedap.archie.rminfo.MetaModels;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * If you create an archetype, generates a line of empty terminology for every missing term in every language.
 * Adds the comments as default text
 */
public class TerminologyContentGenerator {

    private MetaModels models;
    Pattern commentPattern = Pattern.compile(".*\\[(?<idcode>id[0-9]+)(,|\\]).*--(?<comment>.*)");

    public TerminologyContentGenerator(MetaModels models) {
        this.models = models;
    }

    public Archetype addTerms(String adlContent) {
        ADLParser parser = new ADLParser();
        try {
            Archetype archetype = parser.parse(adlContent);
            models.selectModel(archetype);

            //TODO: just run the CodeValidation in the context of an archetype repository, and process the error messages?
            if(parser.getErrors().hasErrors()) {
                throw new RuntimeException("parse errors!" + parser.getErrors().toString());
            }
            Archetype resultingArchetype = parser.parse(adlContent); //instantiate twice
            walkArchetype(adlContent, archetype, resultingArchetype);
            visitValueSets(adlContent, resultingArchetype);

            sortTerminology(resultingArchetype);
            return resultingArchetype;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void visitValueSets(String adlContent, Archetype resultingArchetype) {
        Map<String, ValueSet> valueSets = resultingArchetype.getTerminology().getValueSets();
        for(String acCode:valueSets.keySet()) {
            if(!terminologyHasCodeForAllLanguages(resultingArchetype, acCode)) {
                addCodeToTerminology(adlContent, resultingArchetype, acCode);
            }
            for(String atCode:valueSets.get(acCode).getMembers()) {
                if(!terminologyHasCodeForAllLanguages(resultingArchetype, atCode)) {
                    addCodeToTerminology(adlContent, resultingArchetype, atCode);
                }
            }
        }
    }

    private void sortTerminology(Archetype resultingArchetype) {
        Map<String, Map<String, ArchetypeTerm>> termDefinitions = resultingArchetype.getTerminology().getTermDefinitions();
        for(String language: termDefinitions.keySet()) {
            Map<String, ArchetypeTerm> stringArchetypeTermMap = termDefinitions.get(language);
            Map<String, ArchetypeTerm> sortedArchetypeTermMap = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    String code1 = o1.substring(0, 2);
                    String code2 = o2.substring(0, 2);
                    if(code1.equalsIgnoreCase(code2)) {
                        int number1 = Integer.parseInt(o1.substring(2));
                        int number2 = Integer.parseInt(o2.substring(2));
                        return number1 - number2;
                    } else {
                        return code2.compareTo(code1);
                    }
                }
            });
            sortedArchetypeTermMap.putAll(stringArchetypeTermMap);
            termDefinitions.put(language, sortedArchetypeTermMap);

        }
    }

    private void walkArchetype(String sourceFile, Archetype archetype, Archetype resultingArchetype) {
        LinkedList<CObject> workList = new LinkedList<>();
        workList.push(archetype.getDefinition());
        List<CObject> cObjects = new ArrayList<>();
        while(!workList.isEmpty()) {
            CObject next = workList.pop();
            if(!terminologyHasCodeForAllLanguages(archetype, next)) {
                cObjects.add(next);
                addCodeToTerminology(sourceFile, resultingArchetype, next.getNodeId());
            }
            for(CAttribute attribute:next.getAttributes()) {
                for(CObject child:attribute.getChildren()) {
                    workList.push(child);
                    if(child instanceof CTerminologyCode) {
                        checkTerminologyCode(sourceFile, resultingArchetype, (CTerminologyCode) child);
                    }
                }
            }
        }
    }

    private void checkTerminologyCode(String sourceFile, Archetype archetype, CTerminologyCode child) {
        for(String constraint:child.getConstraint()) {
            if(!terminologyHasCodeForAllLanguages(archetype, constraint)) {
                addCodeToTerminology(sourceFile, archetype, constraint);
            }
        }
    }

    private void addCodeToTerminology(String sourceFile, Archetype resultingArchetype, String code) {
        String text = getCommentName(sourceFile, code);
        if(text == null) {
            text = "Add term for me!";
        }
        String description = text; //TODO: grep comment from sourcefile!
        Map<String, Map<String, ArchetypeTerm>> termDefinitions = resultingArchetype.getTerminology().getTermDefinitions();
        for(String language: termDefinitions.keySet()) {
            if(termDefinitions.get(language).get(code) == null) {
                ArchetypeTerm newCode = new ArchetypeTerm();
                newCode.setCode(code);
                newCode.setText(text);
                newCode.setDescription(description);
                termDefinitions.get(language).put(code, newCode);
            }
        }
    }

    private String getCommentName(String sourceFile, String nodeId) {
        String[] lines = sourceFile.split("\n");
        for(String line:lines) {
            Matcher matcher = commentPattern.matcher(line);
            if(matcher.matches()) {
                String idcode = matcher.group("idcode");
                String comment = matcher.group("comment");
                if(idcode.equalsIgnoreCase(nodeId)) {
                    return comment.trim();
                }
            }

        }
        return null;
    }

    public boolean terminologyHasCodeForAllLanguages(Archetype archetype, CObject cObject) {
        String nodeId = cObject.getNodeId();
        int codeSpecializationDepth = AOMUtils.getSpecializationDepthFromCode(nodeId);
        int archetypeSpecializationDepth = archetype.specializationDepth();
        if(codeSpecializationDepth > archetypeSpecializationDepth) {
            return true;//not exactly, this is a validation failure that needs to be fixed. log?
        } else if (cObject.isRoot() || parentIsMultiple(cObject)) {
            if( codeSpecializationDepth == archetypeSpecializationDepth &&  !archetype.getTerminology().hasIdCodeInAllLanguages(nodeId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if terminology has a code. Use only for at- and ac-codes, NOT For id codes!!
     * @param archetype
     * @param code
     * @return
     */
    public boolean terminologyHasCodeForAllLanguages(Archetype archetype, String code) {
        if(AOMUtils.isIdCode(code)) {
            throw new IllegalArgumentException("this method only checks at- and ac- codes, not id codes");
        }
        int codeSpecializationDepth = AOMUtils.getSpecializationDepthFromCode(code);
        int archetypeSpecializationDepth = archetype.specializationDepth();
        if (codeSpecializationDepth > archetypeSpecializationDepth) {
            return true;//not exactly, this is a validation failure that needs to be fixed. log?
        } else {
            if (codeSpecializationDepth == archetypeSpecializationDepth && !archetype.getTerminology().hasCodeInAllLanguages(code)) {
                return false;
            }
        }
        return true;
    }

    private boolean parentIsMultiple(CObject cObject) {
        if(cObject.getParent() != null) {

            CAttribute parent = cObject.getParent();
            CObject owningObject = parent.getParent();
            if (parent.getDifferentialPath() != null) {
                //not supported yet here.
                return false;
                /*&&
            } flatParent != null) {
                CAttribute attributeFromParent = (CAttribute) AOMUtils.getDifferentialPathFromParent(flatParent, parent);
                if(attributeFromParent != null) {
                    owningObject = attributeFromParent.getParent();
                }*/

            }
            if(owningObject != null) {
                return models.isMultiple(owningObject.getRmTypeName(), parent.getRmAttributeName());
            }
        }
        return false;
    }

}