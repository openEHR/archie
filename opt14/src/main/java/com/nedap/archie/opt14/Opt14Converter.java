package com.nedap.archie.opt14;

import com.google.common.collect.Lists;
import com.nedap.archie.adl14.ADL14ConversionConfiguration;
import com.nedap.archie.adl14.ADL14Converter;
import com.nedap.archie.adl14.ADL2ConversionResult;
import com.nedap.archie.adl14.ADL2ConversionResultList;
import com.nedap.archie.adl14.OpenEHRADL14ConversionConfiguration;
import com.nedap.archie.aom.ArchetypeHRID;
import com.nedap.archie.aom.OperationalTemplate;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.diff.Differentiator;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.json.ArchieRMObjectMapperProvider;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.adl.ADLArchetypeSerializer;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.nedap.archie.opt14.schema.*;

/**
 * TODO:
 * archetype root DEFAULTVALUES
 *
 */
public class Opt14Converter {
    
    public ADL2ConversionResultList convert(OPERATIONALTEMPLATE opt14, InMemoryFullArchetypeRepository adl2Archetypes) {
        try {
            MetaModels metaModels = BuiltinReferenceModels.getMetaModels();
            ADL14ConversionConfiguration config = OpenEHRADL14ConversionConfiguration.getConfig();
            config.setAllowEmptyNodeIdsForSpecializations(true);
            config.setApplyDiff(false);//the diff must be applied manually later, after converting node ids

            //First create an OPT 2
            OperationalTemplate opt2 = new OperationalTemplate();
            //TODO: should this include the concept, rather than just the template ID?
            opt2.setArchetypeId(new ArchetypeHRID("openEHR-EHR-" + opt14.getDefinition().getRmTypeName() + "." + opt14.getTemplateId().getValue() + ".v1.0.0"));
            opt2.setControlled(opt14.isIsControlled());
            opt2.setParentArchetypeId(opt14.getDefinition().getArchetypeId().getValue());
            if(opt14.getUid() != null) {
                opt2.setUid(opt14.getUid().getValue());
            }
            DescriptionConverter.convert(opt14, opt2);


            new DefinitionConverter().convert(opt2, opt14, config);

            new TConstraintApplier().apply(opt14, opt2);
            System.out.println(ADLArchetypeSerializer.serialize(opt2, null, new ArchieRMObjectMapperProvider()));

            Template template = new Template();
            template.setArchetypeId(new ArchetypeHRID("openEHR-EHR-" + opt14.getDefinition().getRmTypeName() + "." + opt14.getTemplateId().getValue() + ".v1.0.0"));
            template.setControlled(opt14.isIsControlled());
            template.setParentArchetypeId(opt14.getDefinition().getArchetypeId().getValue());
            template.setTerminology(opt2.getTerminology());
            if(opt14.getUid() != null) {
                template.setUid(opt14.getUid().getValue());
            }

            DescriptionConverter.convert(opt14, template);
            new OptToTemplateConverter().convert(opt2, template);
            //template.setDefinition(opt2.getDefinition());
            //TODO: convert to template overlays here

            RepoFlatArchetypeProvider flatParentProvider = new RepoFlatArchetypeProvider(adl2Archetypes);
            new NodeIdFixerBeforeConversion().fixNodeIds(template, flatParentProvider);
            Differentiator differentiator = new Differentiator(metaModels);

            ADL14Converter converter = new ADL14Converter(metaModels, config);
            converter.setExistingRepository(adl2Archetypes);
            ADL2ConversionResultList converted = converter.convert(Lists.newArrayList(template));
            ADL2ConversionResult adl2ConversionResult = converted.getConversionResults().get(0);

            if(adl2ConversionResult.getArchetype() != null) {
                Template convertedTemplate = (Template) adl2ConversionResult.getArchetype();

                new NodeIdSpecializer(metaModels).specializeNodeIds(convertedTemplate, flatParentProvider);
                new ArchetypeTermFixer().fixTerms(convertedTemplate, flatParentProvider);
                for(TemplateOverlay overlay: convertedTemplate.getTemplateOverlays()) {
                    new NodeIdSpecializer(metaModels).specializeNodeIds(overlay, flatParentProvider);
                    new ArchetypeTermFixer().fixTerms(overlay, flatParentProvider);
                }
                convertedTemplate = (Template) differentiator.differentiate(convertedTemplate, flatParentProvider.getFlatArchetype(template.getParentArchetypeId()), true);
                List<TemplateOverlay> newOverlays = new ArrayList();
                for(TemplateOverlay overlay: convertedTemplate.getTemplateOverlays()) {
                    TemplateOverlay newOverlay = (TemplateOverlay) differentiator.differentiate(overlay, flatParentProvider.getFlatArchetype(overlay.getParentArchetypeId()), true);
                    newOverlays.add(newOverlay);
                }
                adl2ConversionResult.setArchetype(convertedTemplate);
                convertedTemplate.setTemplateOverlays(newOverlays);
            }
            return converted;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
