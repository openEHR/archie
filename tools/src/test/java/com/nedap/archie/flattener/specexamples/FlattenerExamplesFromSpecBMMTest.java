package com.nedap.archie.flattener.specexamples;

import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.rminfo.SimpleMetaModelProvider;
import org.junit.jupiter.api.BeforeEach;
import org.openehr.referencemodels.BuiltinReferenceModels;

public class FlattenerExamplesFromSpecBMMTest extends FlattenerExamplesFromSpecTest {

    @BeforeEach
    public void setup() throws Exception {
        repository = new SimpleArchetypeRepository();
        metaModelProvider = new SimpleMetaModelProvider(null, BuiltinReferenceModels.getBmmRepository(), BuiltinReferenceModels.getAomProfiles());
    }
}
