package com.nedap.archie.serializer.adl;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.rmoverlay.VisibilityType;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Test;
import org.openehr.referencemodels.AllMetaModelsInitialiser;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author markopi
 */
public class ADLArchetypeSerializerTest {
    @Test
    public void serializeHead() throws Exception {
        Archetype archetype = load("openEHR-EHR-COMPOSITION.report.v1.adls");

        String serialized = ADLArchetypeSerializer.serialize(archetype);

        String[] serializedLines = serialized.split("\\n");

        assertThat(serializedLines[0], startsWith("archetype "));
        assertThat(serializedLines[0], containsString("adl_version=2.0.5"));
        assertThat(serializedLines[0], containsString("rm_release=1.0.2"));

        assertThat(serializedLines[1], containsString("openEHR-EHR-COMPOSITION.report.v1.0.0"));
    }

    @Test
    public void serializeDescription() throws Exception {
        Archetype archetype = load("openEHR-EHR-COMPOSITION.report.v1.adls");

        String serialized = ADLArchetypeSerializer.serialize(archetype);

        assertThat(serialized, containsString("[\"name\"] = <\"Heather Leslie\">"));
        assertThat(serialized, containsString("keywords = <\"report\", ...>"));
        assertThat(serialized, containsString("[\"MD5-CAM-1.0.1\"] = <\"C4DF25AE97563196F340F6BD8D8D05F7\">"));

    }

    @Test
    public void serializeLanguage() throws Exception {
        Archetype archetype = load("openEHR-EHR-CLUSTER.device.v1.adls");

        String serialized = ADLArchetypeSerializer.serialize(archetype);
        assertThat(serialized, containsString("" +
                "language\n" +
                "    original_language = <[ISO_639-1::en]>"));

        // translation
        assertThat(serialized, containsString("[\"nb\"] = <"));
        // translation author
        assertThat(serialized, containsString("[\"name\"] = <\"Lars Bitsch-Larsen\">"));
   }

    @Test
    public void serializeTerminologyTermDefinition() throws Exception {
        Archetype archetype = load("openEHR-EHR-EVALUATION.term_constraint_variations.v0.0.1.adls");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        assertThat(serialized, containsString("[\"en\"] = <"));
        assertThat(serialized, containsString("[\"id5\"] = <"));
        assertThat(serialized, containsString("text = <\"Specific Substance/Agent\">"));
   }

    @Test
    public void serializeTerminologyTermBindings() throws Exception {
        Archetype archetype = load("openEHR-EHR-EVALUATION.term_constraint_variations.v0.0.1.adls");
        String serialized = ADLArchetypeSerializer.serialize(archetype);
        assertThat(serialized, containsString("term_bindings = <"));
        assertThat(serialized, containsString("[\"snomedct\"] = <"));
        assertThat(serialized, containsString("[\"at11\"] = <http://snomed.info/406470001>"));
   }

    @Test
    public void serializeTerminologyValueSets() throws Exception {
        Archetype archetype = load("openEHR-EHR-EVALUATION.term_constraint_variations.v0.0.1.adls");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        // value sets
        assertThat(serialized, containsString("value_sets = <"));
        assertThat(serialized, containsString("[\"ac1\"] = <"));
        assertThat(serialized, containsString("members = <\"at10\", \"at11\", \"at12\", \"at13\", \"at14\">"));
   }

    @Test
    public void serializeAnnotations() throws Exception {
        Archetype archetype = loadRoot("basic.adl");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        assertThat(serialized, containsString("documentation = <"));
        assertThat(serialized, containsString("[\"/context/start_time\"] = <"));
        assertThat(serialized, containsString("[\"local_name\"] = <\"consultation start time\">"));
   }

   @Test
   public void rmOverlay() throws Exception {
        Archetype archetype = TestUtil.parseFailOnErrors(this.getClass(),"/com/nedap/archie/flattener/openEHR-EHR-OBSERVATION.to_flatten_parent_with_overlay.v1.0.0.adls");
        String serialized = ADLArchetypeSerializer.serialize(archetype);

        assertTrue(serialized.contains("rm_overlay\n" +
                "    rm_visibility = <\n" +
                "        [\"/subject\"] = <\n" +
                "            visibility = <\"hide\">\n" +
                "            alias = <[local::at12]>\n" +
                "        >\n" +
                "        [\"/data[id2]/events[id3]/data[id4]/items[id5]\"] = <\n" +
                "            visibility = <\"show\">\n" +
                "        >\n" +
                "        [\"/data[id2]/events[id3]/data[id4]/items[id6]\"] = <\n" +
                "            visibility = <\"show\">\n" +
                "        >\n" +
                "    >"));

       Archetype parsed = new ADLParser(AllMetaModelsInitialiser.getMetaModels()).parse(serialized);
       assertEquals(VisibilityType.HIDE, parsed.getRmOverlay().getRmVisibility().get("/subject").getVisibility());
       assertEquals("at12", parsed.getRmOverlay().getRmVisibility().get("/subject").getAlias().getCodeString());
   }

    private Archetype load(String resourceName) throws ADLParseException, IOException {
        return new ADLParser().parse(ADLArchetypeSerializerTest.class.getResourceAsStream(resourceName));
    }

    private Archetype loadRoot(String resourceName) throws ADLParseException, IOException {
        return new ADLParser().parse(ADLArchetypeSerializerTest.class.getClassLoader().getResourceAsStream(resourceName));
    }

}