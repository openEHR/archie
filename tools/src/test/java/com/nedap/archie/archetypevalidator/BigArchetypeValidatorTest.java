package com.nedap.archie.archetypevalidator;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rminfo.ReferenceModels;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openehr.bmm.rmaccess.ReferenceModelAccess;
import org.openehr.referencemodels.BuiltinReferenceModels;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

/**
 * //TODO: add list of errortypes NOT tested by the testset, so we can add files for that
 *
 * Runs through the full test files of the validity directory of the archetype validator. Every archetype should either
 * fail to parse or fail to validate, unless it has regression = "PASS"
 *
 * Currently skips all unimplemented ErrorTypes. As soon as you add the enum constant, it will also test those
 */
public class BigArchetypeValidatorTest {

    private static final Logger log = LoggerFactory.getLogger(BigArchetypeValidatorTest.class);

    //if regression with hash key occures, any of the given values are also acceptable as valid results
    private static final Multimap<String, ErrorType> replaceableErrorTypes = HashMultimap.create();
    static {
        replaceableErrorTypes.put(ErrorType.VARXR.name(), ErrorType.VARXRA);
    }

    //all archetypes in the validity package that have errors that do not match the grammar go here
    private static final Set<String> archetypeIdsThatShouldHaveParserErrors = new HashSet<>();
    static {
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-CAR.VCOID_uncoded_interior_nodes.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_terminology_extra_end_mark.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_terminology_missing.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_definition_empty.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_archetype_id_empty.v1");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_archetype_id_missing.v1");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-EHR-OBSERVATION.FAIL_dadl_spurious_delimiter.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.VCOID_container_attribute_children_no_node_identifiers.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.SCOAT_object_empty.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.SCAS_attribute_empty.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openehr-TEST_PKG-WHOLE.VCOID_missing_root_node_id.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_definition_missing.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.VCOID_objects_with_no_node_identifiers.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_terminology_empty.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_terminology_term_definitions_missing.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.SADF_definition_after_terminology.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.VCOID_missing_ids_on_alternative_children.v1.0.0");
        //VOKU, being adl attribute uniqueness, is being handled by setting the jackson parser to STRICT_DUPLICATE_CHECKS very well indeed
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.VOKU_ac_code_duplicated_in_terminology.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.VOKU_at_code_duplicated_in_terminology.v1.0.0");
    }

    private static final Set<String> archetypesTestingNotImplementedFeatures = new HashSet<>();
    static {
        //TODO: we do not check generic types yet
        archetypesTestingNotImplementedFeatures.add("openEHR-EHR-OBSERVATION.VCORMT_rm_non_conforming_type1.v1.0.0");//no support for generic type validation yet
        //TODO: this archetype is incorrect, see https://openehr.atlassian.net/browse/SPECPR-243. Waiting on a fix
        archetypesTestingNotImplementedFeatures.add("openEHR-EHR-OBSERVATION.VPOV_redef_ac_code_node_to_local_codes.v1.0.0");
        //TODO: value set with multiple elements cannot be checked, as they are added as a set.
        //if we replace the valueSet set implementation with a guava MultiSet we have an occurrences attribute we can use
        //but we probably need a custom deserializer
        //jackson strict duplicates doesn't fix this for sets, only hashes
        archetypesTestingNotImplementedFeatures.add("openEHR-TEST_PKG-ENTRY.VTVSUQ_at_code_duplicated_in_ordinal.v1.0.0");
        archetypesTestingNotImplementedFeatures.add("openEHR-TEST_PKG-ENTRY.VTVSUQ_at_code_duplicated_in_internal_codes.v1.0.0");
        //no external terminology binding yet, so cannot implement
        archetypesTestingNotImplementedFeatures.add("openehr-TEST_PKG-SOME_TYPE.VETDF_wrong_property_code.v1.0.0");


    }

    private FullArchetypeRepository parseAll() {
        InMemoryFullArchetypeRepository result = new InMemoryFullArchetypeRepository();
        Reflections reflections = new Reflections("adl2-tests", new ResourcesScanner());
        List<String> adlFiles = new ArrayList(reflections.getResources(Pattern.compile(".*\\.adls")));
        for(String file:adlFiles) {
            if (file.contains("legacy_adl_1.4")) {
                continue;
            }
            Archetype archetype = null;
            Exception exception = null;
            ANTLRParserErrors errors = null;
            try (InputStream stream = getClass().getResourceAsStream("/" + file)) {
                ADLParser parser = new ADLParser();
                parser.setLogEnabled(false);
                archetype = parser.parse(stream);
                errors = parser.getErrors();
                if (errors.hasNoErrors()) {
                    result.addArchetype(archetype);
                }

            } catch (Exception e) {
            }
        }
        return result;
    }


    @Test
    public void testFullValidityPackageBmm() {
        testInner(new MetaModels(null, BuiltinReferenceModels.getBMMReferenceModels(), BuiltinReferenceModels.getAomProfiles()));

    }

    @Test
    public void testFullValidityPackageModelInfoLookup() {

        ReferenceModels models = new ReferenceModels();
        models.registerModel(ArchieRMInfoLookup.getInstance());
        models.registerModel(com.nedap.archie.openehrtestrm.TestRMInfoLookup.getInstance());

        ReferenceModelAccess access = new ReferenceModelAccess();
//        access.initializeAll(schemaDirectories);
        testInner(new MetaModels(models, null));

    }

    public void testInner(MetaModels metaModels) {

        Reflections reflections = new Reflections("adl2-tests.validity", new ResourcesScanner());
        List<String> adlFiles = new ArrayList(reflections.getResources(Pattern.compile(".*\\.adls")));


        int errorCount = 0;
        int correctCount = 0;
        int shouldBeFineButWasinvalid = 0;
        int notImplemented = 0;
        int unexpectedParseErrors = 0;
        List<String> errorStrings = new ArrayList<>();
        ArchetypeValidator validator = new ArchetypeValidator(metaModels);
        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        for(String file:adlFiles) {
            if(file.contains("legacy_adl_1.4")){
                continue;
            }
            Archetype archetype = null;
            Exception exception = null;
            ANTLRParserErrors errors = null;
            try (InputStream stream = getClass().getResourceAsStream("/" + file)) {
                ADLParser parser = new ADLParser();
                parser.setLogEnabled(false);
                archetype = parser.parse(stream);
                errors = parser.getErrors();
            } catch (Exception e) {
                exception = e;
            }
            if(exception != null || errors.hasErrors()) {
                try {
                    String a = new String(Resources.toByteArray(Resources.getResource(file)));
                    if(a.contains("PASS")) {
                        log.info("{} has parse errors but should not!", file);
                        if(exception != null) {
                            if(errors != null) {
                                log.error(errors.toString());
                                errorStrings.add(archetype.getArchetypeId() + " has unexpected parse errors: " + errors.toString());
                            }
                            log.error("exception:", exception);
                            errorStrings.add(archetype.getArchetypeId() + " has exception: " + exception.getMessage());
                        } else {
                            log.error(errors.toString());
                        }
                        unexpectedParseErrors++;
                        errorCount++;
                    } else if (isInOkToHaveParseErrorsList(file)) {
                        log.info("{} has parse errors, so that's fine", file);
                        correctCount++;
                    } else {
                        log.info("{} has parse errors, but we don't know if it's fine:", file);
                        if(errors != null) {
                            log.error(errors.toString());
                            errorStrings.add(archetype.getArchetypeId() + " has unknown parse errors: " + errors.toString());
                        }
                        log.error("exception:", exception);
                        errorStrings.add(archetype.getArchetypeId() + " has exception: " + exception.getMessage());
                        unexpectedParseErrors++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                repository.addArchetype(archetype);
            }
        }

        FullArchetypeRepository all = parseAll();
        for(Archetype archetype:repository.getAllArchetypes()) {
            ArchetypeValidationResult validation = validator.validate(archetype, all);
            String regression = archetype.getDescription().getOtherDetails().get("regression");
            if(regression != null && !regression.equalsIgnoreCase("PASS")) {
                if(!errorTypeImplemented(archetype, regression)) {
                    log.info("regression {} not implemented yet, so not yet tested", regression);
                    notImplemented++;
                } else {
                    if (!validation.hasWarningsOrErrors()) {

                        log.error("test failed: archetype {} considered valid, it should not", archetype.getArchetypeId());
                        errorStrings.add("test failed: archetype " + archetype.getArchetypeId() + " considered valid, it should not");
                        errorCount++;
                    } else {
                        boolean found = false;
                        Set<ErrorType> errorTypes = new LinkedHashSet<>();
                        List<ArchetypeValidationMessage> errors = validation.getErrors();
                        for (ArchetypeValidationMessage message : errors) {
                            errorTypes.add(message.getType());
                            if (errorMatches(message.getType(), regression)) {
                                found = true;
                                correctCount++;
                            }
                        }
                        if (!found) {
                            log.error("validation failed: archetype {} invalid but with wrong message, was {} but should be {}",
                                    archetype.getArchetypeId(),
                                    Joiner.on(", ").join(errorTypes),
                                    regression);
                            errorStrings.add("validation failed: archetype " + archetype.getArchetypeId() + " invalid but with wrong message, was " +
                                    Joiner.on(", ").join(errorTypes) + " but should be " +
                                    regression);
                            printErrors(validation);
                            errorCount++;
                        }

                    }
                }
            } else {
                if(validation.hasWarningsOrErrors()) {
                    log.error("Validation should pass, but it failed for archetype {}, {}", archetype.getArchetypeId(), regression);
                    errorStrings.add("Validation should pass, but it failed for archetype " + archetype.getArchetypeId() + ", " + regression);
                    printErrors(validation);
                    shouldBeFineButWasinvalid++;
                } else {
                    correctCount++;
                }
            }
        }
        if(errorCount > 0) {
            Assert.fail(String.format("%s validated but should not, %s correct, %s did not validate but should, %s not yet implemented, %s unexpected parser errors: \n%s", errorCount, correctCount, shouldBeFineButWasinvalid, notImplemented, unexpectedParseErrors, Joiner.on(", ").join(errorStrings)));
        }
        log.info("{} not implemented yet", notImplemented);


    }

    private boolean errorMatches(ErrorType type, String regression) {
        if(regression.startsWith(type.name())) {
            return true;
        }
        Collection<ErrorType> alsoMatchingErrorTypes = this.replaceableErrorTypes.get(regression);
        if(alsoMatchingErrorTypes != null && alsoMatchingErrorTypes.contains(type)) {
            return true;
        }
        return false;
    }

    private boolean isInOkToHaveParseErrorsList(String filename) {
        return archetypeIdsThatShouldHaveParserErrors.contains(FilenameUtils.getBaseName(filename));
    }

    private boolean errorTypeImplemented(Archetype archetype, String regression) {
        if(BigArchetypeValidatorTest.archetypesTestingNotImplementedFeatures.contains(archetype.getArchetypeId().toString())) {
            return false;
        }
        if(regression.equals("FAIL")) {
            return true;//this should not be implemented, but the archetype fixed, or it is a parse error
        }
        for(ErrorType errorType: ErrorType.values()) {
            if(regression.startsWith(errorType.name())) {
                return true;
            }
        }
        return false;
    }

    private void printErrors(ArchetypeValidationResult result) {
        for(ArchetypeValidationMessage message:result.getErrors()) {
            log.error(message.toString());
        }
    }
}
