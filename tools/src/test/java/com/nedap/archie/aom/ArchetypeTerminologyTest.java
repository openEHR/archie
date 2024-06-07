package com.nedap.archie.aom;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FlattenerConfiguration;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.testutil.ParseValidArchetypeTestUtil;
import org.junit.After;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArchetypeTerminologyTest {

    @After
    public void tearDown() {
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }
    
    @Test
    public void termForUseArchetype() throws IOException, ADLParseException {
        //getting the term for a use archetype is a bit of a tricky situation - it's not for the 'id1' code,
        //it's for the code in the parent. So do some specific test here
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(ParseValidArchetypeTestUtil.parse(this.getClass(), "/com/nedap/archie/aom/openEHR-EHR-COMPOSITION.parent.v1.0.0.adls"));
        repository.addArchetype(ParseValidArchetypeTestUtil.parse(this.getClass(), "/com/nedap/archie/aom/openEHR-EHR-GENERIC_ENTRY.included.v1.0.0.adls"));

        //check that they are valid, just to be sure
        repository.compile(AllMetaModelsInitialiser.getMetaModels());
        repository.getAllValidationResults().forEach(s -> assertTrue(s.getErrors().toString(), s.getErrors().isEmpty()));

        //create operational template
        Flattener flattener = new Flattener(repository, AllMetaModelsInitialiser.getMetaModels(), FlattenerConfiguration.forOperationalTemplate());
        OperationalTemplate opt = (OperationalTemplate) flattener.flatten(repository.getArchetype("openEHR-EHR-COMPOSITION.parent.v1.0.0"));

        //and check the getTerm() functionality
        CArchetypeRoot useArchetype = opt.itemAtPath("/content[id2]");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");
        assertEquals("included archetype nl", useArchetype.getTerm().getText());
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        assertEquals("included archetype en", useArchetype.getTerm().getText());

        //now to check if it uses the component terminology where possible
        CComplexObject element = opt.itemAtPath("/content[id2]/data[id3]/items[id2]");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");
        assertEquals("an element", element.getTerm().getText()); //no dutch translation, should fallback to English
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
        assertEquals("an element", element.getTerm().getText());


    }
    
    
}
