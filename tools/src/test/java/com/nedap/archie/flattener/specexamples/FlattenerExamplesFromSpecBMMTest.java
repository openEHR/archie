package com.nedap.archie.flattener.specexamples;

import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;
import org.junit.Before;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

public class FlattenerExamplesFromSpecBMMTest extends FlattenerExamplesFromSpecTest {

    @Before
    public void setup() throws Exception {
        repository = new SimpleArchetypeRepository();
        models = new MetaModels(null, AllMetaModelsInitialiser.getBmmRepository(), AllMetaModelsInitialiser.getAomProfiles());
    }
}
