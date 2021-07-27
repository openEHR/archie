package com.nedap.archie.opt14;

import com.nedap.archie.adl14.ADL2ConversionResult;
import com.nedap.archie.adl14.ADL2ConversionResultList;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.archetypevalidator.ArchetypeValidator;
import com.nedap.archie.archetypevalidator.ValidationResult;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.ArchieRMObjectMapperProvider;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

import com.nedap.archie.opt14.schema.*;

public class Opt14ConverterTest {

    @Test
    public void procedureList() throws Exception {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        repository.addArchetype(parseAdl2("openEHR-EHR-ACTION.procedure.v1.4.1.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-COMPOSITION.health_summary.v1.0.1.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-SECTION.procedures_rcp.v1.0.0.adls"));


        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try(InputStream stream = getClass().getResourceAsStream("/procedure_list.opt")) {

            OPERATIONALTEMPLATE opt14 =  ((JAXBElement<OPERATIONALTEMPLATE>) unmarshaller.unmarshal(stream)).getValue();
            ADL2ConversionResultList convert = new Opt14Converter().convert(opt14, repository);
            for(ADL2ConversionResult result:convert.getConversionResults()) {
                if(result.getException() != null) {
                    result.getException().printStackTrace();
                }
            }
            Template convertedTemplate = (Template) convert.getConversionResults().get(0).getArchetype();
            System.out.println(ADLArchetypeSerializer.serialize(convertedTemplate));

            OperationalTemplate opt2 = (OperationalTemplate) new Flattener(repository, BuiltinReferenceModels.getMetaModels())
                    .createOperationalTemplate(true)
                    .keepLanguages("en")
                    .flatten(convertedTemplate);

            System.out.println(ADLArchetypeSerializer.serialize(opt2));

            ArchetypeValidator validator = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels());
            ValidationResult validationResult = validator.validate(convertedTemplate, repository);
            assertTrue(validationResult.toString(), validationResult.passes());

        }
    }

    public Archetype parseAdl2(String resource) throws IOException, ADLParseException {
        try(InputStream stream = getClass().getResourceAsStream("/adl2/" + resource)) {
            ADLParser adlParser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            Archetype archetype = adlParser.parse(stream);
            return archetype;
        }
    }

    @Test
    public void respectFromRipple() throws Exception {
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        //TODO: the correct archetypes
        repository.addArchetype(parseAdl2("openEHR-EHR-ACTION.procedure.v1.4.1.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-COMPOSITION.health_summary.v1.0.1.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-SECTION.procedures_rcp.v1.0.0.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-COMPOSITION.report.v1.0.0.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-SECTION.respect_headings.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ADMIN_ENTRY.respect_summary.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.clinical_synopsis.v1.0.2.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.advance_planning_documentation.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ADMIN_ENTRY.care_preference_uk.v1.0.0.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.care_preference_uk.v1.0.0.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.recommendation.v1.1.3-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.cpr_decision_uk.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ADMIN_ENTRY.capacity_respect.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ADMIN_ENTRY.involvement_respect.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ACTION.service.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-CLUSTER.practitioner_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-CLUSTER.practitioner_role_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-CLUSTER.name_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2(" openEHR-EHR-CLUSTER.identifier_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-ADMIN_ENTRY.careteam_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-CLUSTER.contact_cc.v0.0.1-alpha.adls"));
        repository.addArchetype(parseAdl2("openEHR-EHR-CLUSTER.telecom_cc.v0.0.1-alpha.adls"));
//        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.recommendation.v1.1.3-alpha.adls"));
//        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.recommendation.v1.1.3-alpha.adls"));
//        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.recommendation.v1.1.3-alpha.adls"));
//        repository.addArchetype(parseAdl2("openEHR-EHR-EVALUATION.recommendation.v1.1.3-alpha.adls"));
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try(InputStream stream = getClass().getResourceAsStream("/RESPECT_NSS-v0.opt")) {

            OPERATIONALTEMPLATE opt14 =  ((JAXBElement<OPERATIONALTEMPLATE>) unmarshaller.unmarshal(stream)).getValue();
            ADL2ConversionResultList convert = new Opt14Converter().convert(opt14, repository);
            for(ADL2ConversionResult result:convert.getConversionResults()) {
                if(result.getException() != null) {
                    result.getException().printStackTrace();
                }
            }
            Template convertedTemplate = (Template) convert.getConversionResults().get(0).getArchetype();
            System.out.println(ADLArchetypeSerializer.serialize(convertedTemplate, null, new ArchieRMObjectMapperProvider()));

            OperationalTemplate opt2 = (OperationalTemplate) new Flattener(repository, BuiltinReferenceModels.getMetaModels())
                    .createOperationalTemplate(true)
                    .keepLanguages("en")
                    .flatten(convertedTemplate);

            System.out.println(ADLArchetypeSerializer.serialize(opt2));

            ArchetypeValidator validator = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels());
            ValidationResult validationResult = validator.validate(convertedTemplate, repository);
            assertTrue(validationResult.toString(), validationResult.passes());

        }

    }
}
