package com.nedap.archie.archetypevalidator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.nedap.archie.adlparser.antlr2.Adl2BaseVisitor;
import com.nedap.archie.adlparser.antlr2.Adl2Lexer;
import com.nedap.archie.adlparser.antlr2.Adl2Parser;
import com.nedap.archie.adlparser.antlr2.Adl2Visitor;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
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

public class CkmNewGrammarTest {

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


                Adl2Lexer lexer = new Adl2Lexer(CharStreams.fromStream(stream));
                lexer.addErrorListener(errorListener);
                Adl2Parser parser = new Adl2Parser(new CommonTokenStream(lexer));
                parser.addErrorListener(errorListener);
                Adl2Parser.Adl2ArchetypeContext tree = parser.adl2Archetype(); // parse
                final String[] archetypeId = {null};
                Adl2Visitor visitor = new Adl2BaseVisitor() {
                    @Override
                    public Object visitArchetypeHrid(Adl2Parser.ArchetypeHridContext ctx) {
                        if(archetypeId[0] == null) {
                            //hack: only do this for the first one, to not find template overlays
                            archetypeId[0] = ctx.getText();
                        }
                        return visitChildren(ctx);
                    }
                };
                visitor.visitAdl2Archetype(tree);
                if(archetypeId[0] == null) {
                    System.out.println("Couldn't determine archetype id for file " + file);
                    noArchetypeId++;
                } else if (errors.hasNoErrors() &&
                        archetypeIdsThatShouldHaveParserErrors.contains(archetypeId[0])) {
                    System.out.println("FAIL Archetype has no errors, but should: " + archetypeId[0]);
                    shouldErrorParses++;
                } else if (errors.hasErrors() &&
                        !archetypeIdsThatShouldHaveParserErrors.contains(archetypeId[0])) {
                    System.out.println("FAIL Archetype has errors, but should not: " + archetypeId[0]);
                    errorShouldParse++;
                } else {
                    System.out.println("Archetype ok: " + archetypeId[0]);
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

