package com.nedap.archie.adl14;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.AuthoredArchetype;
import org.junit.jupiter.api.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The rm_release of a converted ADL 1.4 archetype: other_details["rm_release"], else the rm_release header item,
 * else the configured rm_release (ADL 1.4 Extended Meta-data Guide).
 */
public class RmReleaseConversionTest {

    private static final String REVIEW = "openehr-EHR-COMPOSITION.review.v1.adl";
    private static final String REVIEW_HEADER = "archetype (adl_version=1.4)";
    private static final String OTHER_DETAILS = "other_details = <";

    @Test
    public void configuredRmReleaseWhenArchetypeStatesNone() throws Exception {
        ADL2ConversionResult result = convert(config(), parse(resource(REVIEW))).get(0);
        assertEquals("1.1.0", rmRelease(result));
        assertFalse(result.getLog().hasWarning(ADL14ConversionMessageCode.WARNING_INVALID_RM_RELEASE));
    }

    @Test
    public void rmReleaseFromHeader() throws Exception {
        String adl = resource(REVIEW).replace(REVIEW_HEADER, "archetype (adl_version=1.4; rm_release=1.0.4)");
        ADL2ConversionResult result = convert(config(), parse(adl)).get(0);
        assertEquals("1.0.4", rmRelease(result));
        assertFalse(result.getLog().hasWarning(ADL14ConversionMessageCode.WARNING_INVALID_RM_RELEASE));
    }

    @Test
    public void rmReleaseFromOtherDetailsIsMovedToHeader() throws Exception {
        Archetype adl14 = parse(withOtherDetailsRmRelease(resource(REVIEW), "1.0.4"));
        ADL2ConversionResult result = convert(config(), adl14).get(0);
        assertEquals("1.0.4", rmRelease(result));
        assertFalse(result.getArchetype().getDescription().getOtherDetails().containsKey("rm_release"));
        assertTrue(adl14.getDescription().getOtherDetails().containsKey("rm_release"), "input archetype must not be changed");
        assertFalse(result.getLog().hasWarning(ADL14ConversionMessageCode.WARNING_INVALID_RM_RELEASE));
    }

    @Test
    public void otherDetailsWinsOverHeader() throws Exception {
        String adl = withOtherDetailsRmRelease(resource(REVIEW), "1.0.4")
                .replace(REVIEW_HEADER, "archetype (adl_version=1.4; rm_release=1.0.3)");
        ADL2ConversionResult result = convert(config(), parse(adl)).get(0);
        assertEquals("1.0.4", rmRelease(result));
        assertTrue(result.getLog().hasInfo(ADL14ConversionMessageCode.INFO_RM_RELEASE_CONFLICT));
    }

    @Test
    public void invalidOtherDetailsRmReleaseIsKeptAndWarned() throws Exception {
        ADL2ConversionResult result = convert(config(), parse(withOtherDetailsRmRelease(resource(REVIEW), "1.1"))).get(0);
        assertEquals("1.1.0", rmRelease(result));
        assertEquals("1.1", result.getArchetype().getDescription().getOtherDetails().get("rm_release"));
        assertTrue(result.getLog().hasWarning(ADL14ConversionMessageCode.WARNING_INVALID_RM_RELEASE));
    }

    @Test
    public void twoPartHeaderRmReleaseParsesAndIsWarned() throws Exception {
        String adl = resource(REVIEW).replace(REVIEW_HEADER, "archetype (adl_version=1.4; rm_release=1.0)");
        ADL2ConversionResult result = convert(config(), parse(adl)).get(0);
        assertEquals("1.1.0", rmRelease(result));
        assertTrue(result.getLog().hasWarning(ADL14ConversionMessageCode.WARNING_INVALID_RM_RELEASE));
    }

    @Test
    public void configuredRmReleaseWhenArchetypeRmReleaseIsSwitchedOff() throws Exception {
        ADL14ConversionConfiguration config = config();
        config.setUseArchetypeRmRelease(false);
        String adl = resource(REVIEW).replace(REVIEW_HEADER, "archetype (adl_version=1.4; rm_release=1.0.4)");
        assertEquals("1.1.0", rmRelease(convert(config, parse(adl)).get(0)));
    }

    @Test
    public void specialisedArchetypeKeepsItsRmRelease() throws Exception {
        Archetype parent = parse(resource("openEHR-EHR-CLUSTER.exam_adl14.v2.adl"));
        Archetype child = parse(resource("openEHR-EHR-CLUSTER.exam-abdomen_adl14.v0.adl")
                .replace("archetype (adl_version=1.4;", "archetype (adl_version=1.4; rm_release=1.0.4;"));
        List<ADL2ConversionResult> results = convert(config(), parent, child);
        assertNull(results.get(1).getException());
        assertEquals("1.1.0", rmRelease(results.get(0)));
        assertEquals("1.0.4", rmRelease(results.get(1)));
    }

    private static ADL14ConversionConfiguration config() throws Exception {
        ADL14ConversionConfiguration config = ConversionConfigForTest.getConfig();
        config.setRmRelease("1.1.0");
        return config;
    }

    private static String withOtherDetailsRmRelease(String adl, String rmRelease) {
        assertTrue(adl.contains(OTHER_DETAILS));
        return adl.replace(OTHER_DETAILS, OTHER_DETAILS + "\n\t\t[\"rm_release\"] = <\"" + rmRelease + "\">");
    }

    private static List<ADL2ConversionResult> convert(ADL14ConversionConfiguration config, Archetype... archetypes) {
        ADL14Converter converter = new ADL14Converter(BuiltinReferenceModels.getMetaModelProvider(), config);
        List<ADL2ConversionResult> results = converter.convert(new ArrayList<>(List.of(archetypes))).getConversionResults();
        for (ADL2ConversionResult result : results) {
            assertNull(result.getException(), () -> "conversion failed: " + result.getException());
        }
        return results;
    }

    private static String rmRelease(ADL2ConversionResult result) {
        return ((AuthoredArchetype) result.getArchetype()).getRmRelease();
    }

    private Archetype parse(String adl) throws Exception {
        return new ADL14Parser(BuiltinReferenceModels.getMetaModelProvider()).parse(adl, config());
    }

    private String resource(String name) throws Exception {
        try (InputStream stream = getClass().getResourceAsStream(name)) {
            String adl = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return adl.startsWith("﻿") ? adl.substring(1) : adl;
        }
    }
}
