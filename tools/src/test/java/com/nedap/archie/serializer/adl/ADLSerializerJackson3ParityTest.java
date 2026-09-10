package com.nedap.archie.serializer.adl;

import com.nedap.archie.json3.ArchieRMObjectMapperProvider3;
import com.nedap.archie.rminfo.RMObjectMapperProvider3;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The Jackson 3 route must produce byte-identical ADL to the Jackson 2 route. */
public class ADLSerializerJackson3ParityTest {

    static List<AdlSerializationTestUtil.Case> cases() throws Exception {
        return AdlSerializationTestUtil.cases();
    }

    @ParameterizedTest
    @MethodSource("cases")
    public void jackson3OutputMatchesJackson2(AdlSerializationTestUtil.Case corpusCase) {
        String jackson2Output = ADLArchetypeSerializer.serialize(corpusCase.getArchetype(), null,
                ADLSerializerJackson2OutputStabilityTest.providerFor(corpusCase.getMapperKind()));
        String jackson3Output = ADLArchetypeSerializer.serializeWithJackson3(corpusCase.getArchetype(), null,
                providerFor(corpusCase.getMapperKind()));

        assertEquals(jackson2Output, jackson3Output, "Jackson 3 output differs for " + corpusCase.getName());
    }

    /** Checks that the comparison includes template overlays. */
    @Test
    public void templateOutputIncludesOverlaysAndMatchesJackson2() throws Exception {
        AdlSerializationTestUtil.Case template = AdlSerializationTestUtil.cases().stream()
                .filter(c -> c.getName().equals("template/blood_pressure"))
                .findFirst()
                .orElseThrow();

        String jackson3Output = ADLArchetypeSerializer.serializeWithJackson3(template.getArchetype(), null,
                providerFor(template.getMapperKind()));

        assertEquals(2, countOccurrences(jackson3Output, "template_overlay"), "expected two overlays in the output");
        assertTrue(jackson3Output.contains("------------------------------------------------------------------------"),
                "expected the overlay separator in the output");
        assertEquals(ADLArchetypeSerializer.serialize(template.getArchetype(), null,
                        ADLSerializerJackson2OutputStabilityTest.providerFor(template.getMapperKind())),
                jackson3Output);
    }

    private static RMObjectMapperProvider3 providerFor(AdlSerializationTestUtil.MapperKind mapperKind) {
        switch (mapperKind) {
            case NONE:
                return null;
            case JSON:
                return new ArchieRMObjectMapperProvider3();
            case ODIN:
                return new ArchieRMObjectMapperProvider3() {
                    // Without a JSON mapper, default values go through the output ODIN mapper instead.
                    @Override
                    public ObjectMapper getJsonObjectMapper() {
                        return null;
                    }
                };
            default:
                throw new AssertionError("unknown mapper kind " + mapperKind);
        }
    }

    private static int countOccurrences(String content, String needle) {
        int count = 0;
        for (int index = content.indexOf(needle); index >= 0; index = content.indexOf(needle, index + 1)) {
            count++;
        }
        return count;
    }
}
