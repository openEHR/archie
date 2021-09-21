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
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

import com.nedap.archie.opt14.schema.*;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

public class Opt14ConverterTest {

    InMemoryFullArchetypeRepository repository;

    @Before
    public void readArchetypes() throws ADLParseException, IOException {
        Reflections reflections = new Reflections("adl2", new ResourcesScanner());
        List<String> adlFiles = new ArrayList<>(reflections.getResources(Pattern.compile(".*\\.adls")));
        repository = new InMemoryFullArchetypeRepository();
        MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
        for(String adlFile:adlFiles) {
            try(InputStream stream = getClass().getResourceAsStream("/" + adlFile)) {
                Archetype parsed = new ADLParser(metaModels).parse(stream);
                repository.addArchetype(parsed);
            }
        }
    }

    @Test
    public void procedureList() throws Exception {
        testTemplate("/procedure_list.opt");
    }

    @Test
    public void vitalSigns() throws Exception {
        ValidationResult result = testTemplate("/vital_signs.opt");
    }

    @Test
    @Ignore
    public void respectFromRipple() throws Exception {
        testTemplate("/RESPECT_NSS-v0.opt");
    }

    @Test
    public void ePrescription() throws Exception {
        testTemplate("/ePrescription.opt");
    }

    private ValidationResult testTemplate(String templateFileName) throws IOException, ADLParseException, JAXBException {
//        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
//        for(String sourcefile:sourceArchetypes) {
//            repository.addArchetype(parseAdl2(sourcefile));
//        }

        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try(InputStream stream = getClass().getResourceAsStream(templateFileName)) {

            OPERATIONALTEMPLATE opt14 =  ((JAXBElement<OPERATIONALTEMPLATE>) unmarshaller.unmarshal(stream)).getValue();
            ADL2ConversionResultList convert = new Opt14Converter().convert(opt14, repository);
            for(ADL2ConversionResult result:convert.getConversionResults()) {
                if(result.getException() != null) {
                    result.getException().printStackTrace();
                }
            }
            Template convertedTemplate = (Template) convert.getConversionResults().get(0).getArchetype();
            System.out.println(ADLArchetypeSerializer.serialize(convertedTemplate, new RepoFlatArchetypeProvider(repository)::getFlatArchetype, null));

            OperationalTemplate opt2 = (OperationalTemplate) new Flattener(repository, BuiltinReferenceModels.getMetaModels())
                    .createOperationalTemplate(true)
                    .keepLanguages("en")
                    .flatten(convertedTemplate);

            System.out.println(ADLArchetypeSerializer.serialize(opt2));

            ArchetypeValidator validator = new ArchetypeValidator(BuiltinReferenceModels.getMetaModels());
            ValidationResult validationResult = validator.validate(convertedTemplate, repository);
            assertTrue(validationResult.toString(), validationResult.passes());
            return validationResult;
        }
    }


    public Archetype parseAdl2(String resource) throws IOException, ADLParseException {
        try(InputStream stream = getClass().getResourceAsStream("/adl2/" + resource)) {
            ADLParser adlParser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            Archetype archetype = adlParser.parse(stream);
            return archetype;
        }
    }




}
