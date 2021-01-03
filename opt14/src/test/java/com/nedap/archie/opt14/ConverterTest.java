package com.nedap.archie.opt14;

import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.Template;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

public class ConverterTest {

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
            Template template = new Opt14Converter().convert(opt14);
            System.out.println(ADLArchetypeSerializer.serialize(template));
        }
    }

    public Archetype parseAdl2(String resource) throws IOException {
        try(InputStream stream = getClass().getResourceAsStream("/adl2/" + resource)) {
            ADLParser adlParser = new ADLParser(BuiltinReferenceModels.getMetaModels());
            Archetype archetype = adlParser.parse(stream);
            if(adlParser.getErrors().hasErrors()) {
                throw new RuntimeException(adlParser.getErrors().toString());
            }
            return archetype;
        }
    }
}
