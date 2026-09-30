package com.nedap.archie.serializer.adl;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.flattener.SimpleArchetypeRepository;
import com.nedap.archie.testutil.TestUtil;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Shared test data for ADL compatibility checks. */
final class AdlSerializationTestUtil {

    /** Selects the default-value serialization path. */
    enum MapperKind {
        /** No provider at all: default values fall back to the generic ODIN serializer. */
        NONE,
        /** A full provider: default values are serialized by the JSON object mapper. */
        JSON,
        /** A provider without a JSON mapper: default values are serialized by the output ODIN object mapper. */
        ODIN
    }

    static final class Case {
        private final String name;
        private final Archetype archetype;
        private final MapperKind mapperKind;

        Case(String name, Archetype archetype, MapperKind mapperKind) {
            this.name = name;
            this.archetype = archetype;
            this.mapperKind = mapperKind;
        }

        String getName() {
            return name;
        }

        Archetype getArchetype() {
            return archetype;
        }

        MapperKind getMapperKind() {
            return mapperKind;
        }

        @Override
        public String toString() {
            return name + " [" + mapperKind + "]";
        }
    }

    private AdlSerializationTestUtil() {
    }

    /** The full corpus, in a stable order. */
    static List<Case> cases() throws ADLParseException, IOException {
        List<Case> cases = new ArrayList<>();
        addCkmMirror(cases);
        addDefaultValues(cases);
        addTemplates(cases);
        return cases;
    }

    private static void addCkmMirror(List<Case> cases) {
        FullArchetypeRepository ckm = TestUtil.parseCKM();
        List<Archetype> archetypes = new ArrayList<>(ckm.getAllArchetypes());
        archetypes.sort(Comparator.comparing(a -> a.getArchetypeId().getFullId()));
        for (Archetype archetype : archetypes) {
            cases.add(new Case("ckm/" + archetype.getArchetypeId().getFullId(), archetype, MapperKind.JSON));
        }
    }

    /** Uses typed RM default values for each mapper path. */
    private static void addDefaultValues(List<Case> cases) throws ADLParseException, IOException {
        for (MapperKind mapperKind : MapperKind.values()) {
            Archetype archetype = parseWithReferenceModels("openEHR-EHR-CLUSTER.default_values.v1.adls");
            cases.add(new Case("default_values/" + mapperKind, archetype, mapperKind));
        }
    }

    private static void addTemplates(List<Case> cases) throws ADLParseException, IOException {
        // A template with two template overlays, its overlays on their own, and the operational template flattened
        // out of it.
        Template bloodPressureComposition = (Template) parseFlattenerResource("openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt");
        cases.add(new Case("template/blood_pressure", bloodPressureComposition, MapperKind.JSON));
        for (int i = 0; i < bloodPressureComposition.getTemplateOverlays().size(); i++) {
            cases.add(new Case("template_overlay/blood_pressure/" + i,
                    bloodPressureComposition.getTemplateOverlays().get(i), MapperKind.JSON));
        }

        Template lengthTemplate = (Template) parseFlattenerResource("openEHR-EHR-COMPOSITION.length.v1.0.0.adlt");
        cases.add(new Case("template/length", lengthTemplate, MapperKind.JSON));

        cases.add(new Case("operational_template/blood_pressure", buildOperationalTemplate(), MapperKind.JSON));
    }

    /** Builds an operational template with component terminologies. */
    private static Archetype buildOperationalTemplate() throws ADLParseException, IOException {
        SimpleArchetypeRepository repository = new SimpleArchetypeRepository();
        for (String resource : List.of(
                "openEHR-EHR-COMPOSITION.report.v1.adls",
                "openEHR-EHR-COMPOSITION.report-result.v1.adls",
                "openEHR-EHR-CLUSTER.device.v1.adls",
                "openEHR-EHR-OBSERVATION.blood_pressure.v1.adls",
                "openEHR-EHR-OBSERVATION.height.v1.adls",
                "openEHR-EHR-COMPOSITION.length.v1.0.0.adlt")) {
            repository.addArchetype(parseFlattenerResource(resource));
        }
        Archetype bloodPressureComposition = parseFlattenerResource("openEHR-EHR-COMPOSITION.blood_pressure.v1.0.0.adlt");
        repository.addArchetype(bloodPressureComposition);

        Flattener flattener = new Flattener(repository, BuiltinReferenceModels.getMetaModelProvider()).createOperationalTemplate(true);
        return flattener.flatten(bloodPressureComposition);
    }

    private static Archetype parseFlattenerResource(String resourceName) throws ADLParseException, IOException {
        try (InputStream stream = AdlSerializationTestUtil.class.getResourceAsStream("/com/nedap/archie/flattener/" + resourceName)) {
            return new ADLParser().parse(stream);
        }
    }

    private static Archetype parseWithReferenceModels(String resourceName) throws ADLParseException, IOException {
        try (InputStream stream = AdlSerializationTestUtil.class.getResourceAsStream(resourceName)) {
            return new ADLParser(BuiltinReferenceModels.getMetaModelProvider()).parse(stream);
        }
    }
}
