package com.nedap.archie.rminfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.openehr.bmm.core.BmmModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class MetaModelTest {

    private static class CountingProvider implements RMObjectMapperProvider {
        int inputCalls;
        int outputCalls;
        int jsonCalls;

        @Override
        public ObjectMapper getInputOdinObjectMapper() {
            inputCalls++;
            return new ObjectMapper();
        }

        @Override
        public ObjectMapper getOutputOdinObjectMapper() {
            outputCalls++;
            return new ObjectMapper();
        }

        @Override
        public ObjectMapper getJsonObjectMapper() {
            jsonCalls++;
            return new ObjectMapper();
        }
    }

    @Test
    public void eachObjectMapperIsFetchedOnceAndCached() {
        CountingProvider provider = new CountingProvider();
        MetaModel metaModel = new MetaModel(null, new BmmModel(), null, provider);

        metaModel.getOdinInputObjectMapper();
        assertEquals(1, provider.inputCalls);
        assertEquals(0, provider.outputCalls);
        assertEquals(0, provider.jsonCalls);

        metaModel.getOdinOutputObjectMapper();
        assertEquals(1, provider.inputCalls);
        assertEquals(1, provider.outputCalls);
        assertEquals(0, provider.jsonCalls);

        metaModel.getJsonObjectMapper();
        assertEquals(1, provider.inputCalls);
        assertEquals(1, provider.outputCalls);
        assertEquals(1, provider.jsonCalls);

        metaModel.getOdinInputObjectMapper();
        metaModel.getOdinOutputObjectMapper();
        metaModel.getJsonObjectMapper();
        assertEquals(1, provider.inputCalls);
        assertEquals(1, provider.outputCalls);
        assertEquals(1, provider.jsonCalls);
    }
}
