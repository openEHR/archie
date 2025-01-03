package com.nedap.archie.adl14;

import com.nedap.archie.adl14.log.ADL2ConversionLog;
import com.nedap.archie.adl14.log.ADL2ConversionRunLog;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.ResourceDescription;
import com.nedap.archie.aom.Template;
import com.nedap.archie.aom.TemplateOverlay;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcessor;
import com.nedap.archie.diff.Differentiator;
import com.nedap.archie.flattener.Flattener;
import com.nedap.archie.flattener.InMemoryFullArchetypeRepository;
import com.nedap.archie.rminfo.MetaModels;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class ADL14Converter {

    private final MetaModels metaModels;
    private final ADL14ConversionConfiguration conversionConfiguration;

    public ADL14Converter(MetaModels metaModels, ADL14ConversionConfiguration conversionConfiguration) {
        this.metaModels = metaModels;
        this.conversionConfiguration = conversionConfiguration;
    }

    public ADL2ConversionResultList convert(List<Archetype> archetypes) {
        return convert(archetypes, null);
    }

    public ADL2ConversionResultList convert(List<Archetype> archetypes, ADL2ConversionRunLog previousConversion) {
        ADL2ConversionResultList resultList = new ADL2ConversionResultList();
        InMemoryFullArchetypeRepository repository = new InMemoryFullArchetypeRepository();
        List<Archetype> unprocessed = new ArrayList<>(archetypes);

        // ADL 1.4 does not really have templates. This code is here for the Better Care template conversion
        // also it can be used to write our own template converter later.
        // So add the overlays in the right order here
        List<Archetype> templateOverlays = new ArrayList<>();
        for (Archetype archetype : unprocessed) {
            if (archetype instanceof Template) {
                Template t = (Template) archetype;
                for (TemplateOverlay overlay : t.getTemplateOverlays()) {
                    templateOverlays.add(overlay);
                    overlay.setRmRelease(t.getRmRelease());
                }
            }
        }
        unprocessed.addAll(templateOverlays);

        // Process the archetypes ordered by specialization level
        unprocessed.sort(Comparator.comparingInt(Archetype::specializationDepth));

        Differentiator differentiator = new Differentiator(metaModels);
        for (Archetype archetype : unprocessed) {
            ADL2ConversionResult result;
            try {
                if (archetype.getParentArchetypeId() != null) {
                    Archetype parent = repository.getArchetype(archetype.getParentArchetypeId());
                    if (parent == null) {
                        throw new RuntimeException(MessageFormat.format("Cannot find parent {0} for archetype {1}", archetype.getParentArchetypeId(), archetype.getArchetypeId()));
                    }
                    Archetype flatParent = new Flattener(repository, metaModels).flatten(parent);
                    result = convert(archetype, flatParent, previousConversion);
                    if (result.getArchetype() != null) {
                        if (conversionConfiguration.isApplyDiff()) {
                            result.setArchetype(differentiator.differentiate(result.getArchetype(), flatParent, true));
                        } else {
                            result.setArchetype(differentiator.differentiate(result.getArchetype(), flatParent, false));
                        }
                    }
                    resultList.addConversionResult(result);
                } else {
                    result = convert(archetype, previousConversion);
                    resultList.addConversionResult(result);
                }
                if (result.getArchetype() != null && result.getArchetype().getArchetypeId() != null) {
                    repository.addArchetype(result.getArchetype());
                }
            } catch (Exception e) {
                result = new ADL2ConversionResult(archetype.getArchetypeId().toString(), e);
                resultList.addConversionResult(result);
            }
        }
        return resultList;
    }

    private ADL2ConversionResult convert(Archetype archetype, ADL2ConversionRunLog previousConversion) {
        return convert(archetype, null, previousConversion);
    }

    private ADL2ConversionResult convert(Archetype archetype, Archetype flatParent, ADL2ConversionRunLog previousConversion) {
        ADL2ConversionLog previousLog = previousConversion == null ? null : previousConversion.getConversionLog(archetype.getArchetypeId().getSemanticId());
        Archetype convertedArchetype = archetype.clone();

        // Convert description section
        new ADL14DescriptionConverter().convert(convertedArchetype);

        // Convert or correct adl, rm and archetypeId versions
        setCorrectVersions(convertedArchetype);

        // Convert header section
        convertHeader(convertedArchetype);

        // Correct default multiplicities
        new ADL14DefaultMultiplicitiesSetter(metaModels).setDefaults(convertedArchetype);

        // Convert nodeId's
        ADL2ConversionResult result = new ADL2ConversionResult(convertedArchetype);
        ADL14NodeIDConverter adl14NodeIDConverter = new ADL14NodeIDConverter(this.metaModels, convertedArchetype, flatParent, conversionConfiguration, previousLog, result);
        ADL2ConversionLog conversionLog = adl14NodeIDConverter.convert();
        result.setConversionLog(conversionLog);

        // Remove structures that are not default in ADL1.4, but are default in ADL2
        new DefaultRmStructureRemover(metaModels, true).removeRMDefaults(convertedArchetype);

        // Set some values that are not directly in ODIN or ADL
        ArchetypeParsePostProcessor.fixArchetype(convertedArchetype);

        return result;
    }

    /**
     * Set correct ADL and RM version for the archetype
     * Add minor and patch version to archetypeId if minor version is not set in ADL1.4
     */
    private void setCorrectVersions(Archetype convertedArchetype) {
        convertedArchetype.setAdlVersion("2.0.6");
        convertedArchetype.setRmRelease(conversionConfiguration.getRmRelease());
        if (convertedArchetype.getArchetypeId().getMinorVersion() == null) {
            convertedArchetype.getArchetypeId().setReleaseVersion(convertedArchetype.getArchetypeId().getReleaseVersion() + ".0.0");
        }
    }

    /**
     * Move Uid and BuildUid to OtherDetails oid and build_oid respectively
     */
    private void convertHeader(Archetype convertedArchetype) {
        if (convertedArchetype.getUid() != null) {
            // If UID is in OID syntax, move it to the description
            if (convertedArchetype.getUid().matches("[0-9]+(\\.[0-9]+)+")) {
                moveOidToMetadata(convertedArchetype, convertedArchetype.getUid(), "oid");
                convertedArchetype.setUid(null);
            }
        }
        if (convertedArchetype.getBuildUid() != null) {
            if (convertedArchetype.getBuildUid().matches("[0-9]+\\.([0-9]+)+")) {
                moveOidToMetadata(convertedArchetype, convertedArchetype.getBuildUid(), "build_oid");
                convertedArchetype.setBuildUid(null);
            }
        }
    }

    private void moveOidToMetadata(Archetype convertedArchetype, String oid, String oidFieldName) {
        if (convertedArchetype.getDescription() == null) {
            convertedArchetype.setDescription(new ResourceDescription());
        }
        if (convertedArchetype.getDescription().getOtherDetails() == null) {
            convertedArchetype.getDescription().setOtherDetails(new LinkedHashMap<>());
        }
        convertedArchetype.getDescription().getOtherDetails().put(oidFieldName, oid);
    }
}
