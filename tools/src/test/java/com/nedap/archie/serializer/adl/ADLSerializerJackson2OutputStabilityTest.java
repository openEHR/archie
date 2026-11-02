package com.nedap.archie.serializer.adl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nedap.archie.json.ArchieRMObjectMapperProvider;
import com.nedap.archie.rminfo.RMObjectMapperProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Guards the exact bytes the Jackson 2 route produces. */
public class ADLSerializerJackson2OutputStabilityTest {

    private static final String BASELINE_RESOURCE = "adl-jackson2-output.sha256";

    @Test
    public void jackson2OutputIsUnchanged() throws Exception {
        List<AdlSerializationTestUtil.Case> cases = AdlSerializationTestUtil.cases();
        Map<String, String> actual = new LinkedHashMap<>();
        for (AdlSerializationTestUtil.Case corpusCase : cases) {
            String serialized = ADLArchetypeSerializer.serialize(corpusCase.getArchetype(), null,
                    providerFor(corpusCase.getMapperKind()));
            actual.put(corpusCase.getName(), sha256(serialized));
        }
        assertEquals(cases.size(), actual.size(), "corpus case names must be unique");

        Map<String, String> expected = readBaseline();
        assertEquals(expected.keySet(), actual.keySet(), "the corpus itself changed");
        List<String> differences = new ArrayList<>();
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            if (!entry.getValue().equals(actual.get(entry.getKey()))) {
                differences.add(entry.getKey());
            }
        }
        assertEquals(List.of(), differences, "Jackson 2 ADL output changed for these archetypes");
    }

    static RMObjectMapperProvider providerFor(AdlSerializationTestUtil.MapperKind mapperKind) {
        switch (mapperKind) {
            case NONE:
                return null;
            case JSON:
                return new ArchieRMObjectMapperProvider();
            case ODIN:
                return new ArchieRMObjectMapperProvider() {
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

    static String sha256(String content) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError(e);
        }
        StringBuilder result = new StringBuilder();
        for (byte b : digest.digest(content.getBytes(StandardCharsets.UTF_8))) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private Map<String, String> readBaseline() throws IOException {
        Map<String, String> result = new LinkedHashMap<>();
        try (InputStream stream = getClass().getResourceAsStream(BASELINE_RESOURCE)) {
            assertNotNull(stream, "missing baseline resource " + BASELINE_RESOURCE);
            for (String line : new String(stream.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (line.isBlank() || line.startsWith("#")) continue;
                int separator = line.lastIndexOf(' ');
                result.put(line.substring(0, separator), line.substring(separator + 1));
            }
        }
        return result;
    }
}
