package com.nedap.archie.archetypevalidator;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.nedap.archie.adlparser.antlr2.AdlParserBaseVisitor;
import com.nedap.archie.adlparser.antlr2.AdlLexer;
import com.nedap.archie.adlparser.antlr2.AdlParser;
import com.nedap.archie.adlparser.antlr2.AdlParserVisitor;
import com.nedap.archie.adlparser.antlr2.AdlLexer;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.commons.io.input.BOMInputStream;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

public class NewGrammarTest {

    private static final Logger log = LoggerFactory.getLogger(com.nedap.archie.archetypevalidator.BigArchetypeValidatorTest.class);

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
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-TEST_PKG-ENTRY.FAIL_definition_empty.v1");
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

        archetypeIdsThatShouldHaveParserErrors.add("openehr-TEST_PKG-WHOLE.child_with_uid_and_other_metadata.v1.0.0");
        archetypeIdsThatShouldHaveParserErrors.add("CIMI-CORE-ITEM_GROUP.complete_blood_count_auto_diff_result_group.v1.0.0");//adl 1.5
        archetypeIdsThatShouldHaveParserErrors.add("openEHR-EHR-OBSERVATION.value_set_binding.v1.0.0");
    }


    @Test
    public void parseAll() {

        Reflections reflections = new Reflections("adl2-tests", new ResourcesScanner());
        List<String> adlFiles = new ArrayList<>(reflections.getResources(Pattern.compile(".*\\.adls")));
        int ok = 0;
        int noArchetypeId = 0;
        int errorShouldParse = 0;
        int shouldErrorParses = 0;
        for(String file:adlFiles) {
            if (file.contains("legacy_adl_1.4")) {
                continue;
            }
            ANTLRParserErrors errors = null;
            try (InputStream stream = getClass().getResourceAsStream("/" + file)) {

                errors = new ANTLRParserErrors();
                ArchieErrorListener errorListener = new ArchieErrorListener(errors);
                errorListener.setLogEnabled(true);


                AdlLexer lexer = new AdlLexer(CharStreams.fromStream(new BOMInputStream(stream), Charsets.UTF_8));
                lexer.addErrorListener(errorListener);
                AdlParser parser = new AdlParser(new CommonTokenStream(lexer));
                parser.addErrorListener(errorListener);
                AdlParser.AdlObjectContext tree = parser.adlObject();// parse
                final String[] archetypeIdArray = {null};
                AdlParserBaseVisitor visitor = new AdlParserBaseVisitor() {
                    @Override public Object visitHeader(AdlParser.HeaderContext ctx) {
                        archetypeIdArray[0] = ctx.ARCHETYPE_HRID().getText();
                        return visitChildren(ctx);
                    }
                };
                visitor.visitAdlObject(tree);
                String archetypeId = archetypeIdArray[0];
                if(archetypeId == null) {
                    System.out.println("Couldn't determine archetype id for file " + file);
                    noArchetypeId++;
                } else if (errors.hasNoErrors() &&
                        archetypeIdsThatShouldHaveParserErrors.contains(archetypeId)) {
                    System.out.println("FAIL Archetype has no errors, but should: " + archetypeId);
                    shouldErrorParses++;
                } else if (errors.hasErrors() &&
                        !archetypeIdsThatShouldHaveParserErrors.contains(archetypeId)) {
                    System.out.println("FAIL Archetype has errors, but should not: " + archetypeId);
                    errorShouldParse++;
                } else {
                    System.out.println("Archetype ok: " + archetypeId);
                    ok++;
                }

            } catch (Exception e) {
            }
        }
        if(noArchetypeId > 0 || errorShouldParse > 0 || shouldErrorParses > 0) {
            fail(MessageFormat.format("Unexpected errors: {3} ok, {0} without parsable archetype id, {1} should parse, but errors, {2} should error, but parsed",
                    noArchetypeId, errorShouldParse, shouldErrorParses, ok));
        }
    }



}

