package com.nedap.archie.adlparser.treewalkers;

import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.adlparser.antlr.AdlBaseListener;
import com.nedap.archie.adlparser.antlr.AdlParser;
import com.nedap.archie.adlparser.antlr.AdlParser.*;
import com.nedap.archie.aom.rmoverlay.RmOverlay;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.odin.OdinObjectParser;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.nedap.archie.definitions.OpenEhrDefinitions.*;

/**
 * ANTLR listener for an ADLS file. Uses the listener construction for the topmost elements, switches to custom treewalker
 * for elements lower in the tree. This approach saves some code and complexity.
 *
 * Created by pieter.bos on 19/10/15.
 */
public class ADLListener extends AdlBaseListener {

    private static final Pattern VERSION_ID_REGEX = Pattern.compile("[0-9]+.[0-9]+.[0-9]+((-rc|-alpha)(.[0-9]+)?)?");
    private static final Pattern GUID_REGEX = Pattern.compile("[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+-[0-9a-fA-F]+");

    private ANTLRParserErrors errors;

    private Archetype rootArchetype;

    private Archetype archetype;
    private CComplexObjectParser cComplexObjectParser;
    private TerminologyParser terminologyParser;
    private MetaModels metaModels;

    public ADLListener(ANTLRParserErrors errors, MetaModels metaModels) {
        this.errors = errors;
        cComplexObjectParser = new CComplexObjectParser(errors, metaModels);
        terminologyParser = new TerminologyParser(errors);
        this.metaModels = metaModels;
    }

    /** top-level constructs */
    @Override
    public void enterArchetype(ArchetypeContext ctx) {
        rootArchetype = new AuthoredArchetype();
        setArchetype(rootArchetype);
        parseArchetypeHRID(ctx.ARCHETYPE_HRID());
    }

    private void setArchetype(Archetype archetype) {
        this.archetype = archetype;
    }

    @Override
    public void exitArchetype(ArchetypeContext ctx) {
        rootArchetype.setDifferential(true); //TODO: not possible to check from the content of the archetype without spec change
    }

    @Override
    public void enterTemplate(TemplateContext ctx) {
        rootArchetype = new Template();
        setArchetype(rootArchetype);
        parseArchetypeHRID(ctx.ARCHETYPE_HRID());
    }

    @Override
    public void exitTemplate(TemplateContext ctx) {
        rootArchetype.setDifferential(true); //TODO: not possible to check from the content of the archetype without spec change
    }

    @Override
    public void enterTemplateOverlay(TemplateOverlayContext ctx) {
        TemplateOverlay overlay =  new TemplateOverlay();
        overlay.setDifferential(true);
        if(rootArchetype != null) {
            if(rootArchetype instanceof Template) {
                Template owningTemplate = (Template) rootArchetype;
                owningTemplate.addTemplateOverlay(overlay);
                overlay.setOwningTemplate(owningTemplate);
            } else {
                throw new IllegalArgumentException("Template overlay in a non-template archetype is not allowed. This sounds like a grammar problem.");
            }
        } else {
            rootArchetype = overlay;
        }
        setArchetype(overlay);
        parseArchetypeHRID(ctx.ARCHETYPE_HRID());
    }

    @Override
    public void enterOperationalTemplate(OperationalTemplateContext ctx) {
        rootArchetype = new OperationalTemplate();
        rootArchetype.setDifferential(false);//operational templates are flat by definition
        setArchetype(rootArchetype);
        parseArchetypeHRID(ctx.ARCHETYPE_HRID());
    }

    private void parseArchetypeHRID(TerminalNode hrId) {
        if(hrId != null) {
            ArchetypeHRID archetypeID = new ArchetypeHRID(hrId.getText());
            archetype.setArchetypeId(archetypeID);
            if(metaModels != null) {
                metaModels.selectModel(archetype);
            }
        }
    }

    public void enterMetaDataItem(AdlParser.MetaDataItemContext ctx) {
        /*
        | identifier ( '=' meta_data_value )?
         */
        if (archetype instanceof AuthoredArchetype) {
            AuthoredArchetype authoredArchetype = (AuthoredArchetype) archetype;
            String identifier = ctx.identifier().getText();
            String metaDataValue = ctx.metaDataValue() != null ? ctx.metaDataValue().getText() : null;

            // If metaDataValue present, value can be 'primitive_value', 'GUID' or 'VERSION_ID'
            switch (identifier) {
                case ADL_VERSION:
                    setMetaDataItemWithValue(authoredArchetype::setAdlVersion, authoredArchetype.getAdlVersion(), VERSION_ID_REGEX, ADL_VERSION, metaDataValue);
                    break;
                case RM_RELEASE:
                    setMetaDataItemWithValue(authoredArchetype::setRmRelease, authoredArchetype.getRmRelease(), VERSION_ID_REGEX, RM_RELEASE, metaDataValue);
                    break;
                case BUILD_UID:
                    setMetaDataItemWithValue(authoredArchetype::setBuildUid, authoredArchetype.getBuildUid(), GUID_REGEX, BUILD_UID, metaDataValue);
                    break;
                case UID:
                    setMetaDataItemWithValue(authoredArchetype::setUid, authoredArchetype.getUid(), GUID_REGEX, UID, metaDataValue);
                    break;
                case CONTROLLED:
                    setMetaDataItemWithoutValue(authoredArchetype::setControlled, authoredArchetype.getControlled(), CONTROLLED, metaDataValue);
                    break;
                case GENERATED:
                    setMetaDataItemWithoutValue(authoredArchetype::setGenerated, authoredArchetype.getGenerated(), GENERATED, metaDataValue);
                    break;
                default:
                    if (authoredArchetype.getOtherMetaData().containsKey(identifier)) {
                        errors.addError("Encountered additional declaration for metadata tag '" + identifier + "' while only single is allowed");
                    } else {
                        authoredArchetype.addOtherMetadata(identifier, metaDataValue);
                    }
                    break;
            }
        }
    }

    /**
     * one level below: definition, language, etc.
     */
    @Override
    public void enterDefinitionSection(DefinitionSectionContext ctx) {
        CComplexObject definition = cComplexObjectParser.parseComplexObject(ctx.c_complex_object());
        archetype.setDefinition(definition);
    }

    @Override
    public void enterLanguageSection(LanguageSectionContext ctx) {
        archetype.setAuthoredResourceContent(OdinObjectParser.convert(ctx.odin_text(), LanguageSection.class));
    }

    @Override
    public void enterTerminologySection(TerminologySectionContext ctx) {
        archetype.setTerminology(terminologyParser.parseTerminology(ctx));
    }

    @Override
    public void enterDescriptionSection(AdlParser.DescriptionSectionContext ctx) {
        archetype.setDescription(OdinObjectParser.convert(ctx.odin_text(), ResourceDescription.class));
    }

    @Override
    public void enterSpecializationSection(SpecializationSectionContext ctx) {
        if(ctx != null && ctx.archetype_ref() != null) {
            archetype.setParentArchetypeId(ctx.archetype_ref().getText());
        }
    }

    @Override
    public void enterRulesSection(RulesSectionContext ctx) {
        archetype.setRules(cComplexObjectParser.parseRules(ctx));
    }

    @Override
    public void enterAnnotationsSection(AdlParser.AnnotationsSectionContext ctx) {
        archetype.setAnnotations(OdinObjectParser.convert(ctx.odin_text(), ResourceAnnotations.class));
    }

    @Override
    public void enterRmOverlaySection(AdlParser.RmOverlaySectionContext ctx) {
        archetype.setRmOverlay(OdinObjectParser.convert(ctx.odin_text(), RmOverlay.class));
    }

    public void enterComponentTerminologiesSection(AdlParser.ComponentTerminologiesSectionContext ctx) {
        if (!(archetype instanceof OperationalTemplate)) {
            throw new IllegalArgumentException("cannot add component terminologies to anything but an operational template");
        }
        if(ctx.odin_text().attr_vals() != null) {
            //this is 'component_terminologies = <...>'
            OperationalTemplate template = (OperationalTemplate) archetype;

            ComponentTerminologiesHelper helper = OdinObjectParser.convert(ctx.odin_text(), ComponentTerminologiesHelper.class);
            template.setComponentTerminologies(helper.getComponentTerminologies());
        } else {
            //this is a direct <["archetype_id"] = ...> syntax
            OperationalTemplate template = (OperationalTemplate) archetype;

            TypeFactory typeFactory = AdlOdinToJsonConverter.getObjectMapper().getTypeFactory();
            MapType mapType = typeFactory.constructMapType(ConcurrentHashMap.class, String.class, ArchetypeTerminology.class);

            template.setComponentTerminologies(OdinObjectParser.convert(ctx.odin_text(), mapType));
        }
    }


    /* getters for result */
    public Archetype getArchetype() {
        return rootArchetype;
    }

    public ANTLRParserErrors getErrors() {
        return errors;
    }

    /**
     * Sets MetaDataItem with identifier and value in archetype.
     *
     * @param setNewValue   Set method for value of the identifier in archetype to set the new value if it does not exist
     * @param existingValue Not null means the identifier has been declared before in the metadata of the archetype
     * @param valuePattern  Pattern to which the value should match
     * @param identifier    Identifier of the MetaDataItem
     * @param newValue      New value to set for the identifier
     */
    private void setMetaDataItemWithValue(Consumer<String> setNewValue, String existingValue, Pattern valuePattern, String identifier, String newValue) {
        if (newValue != null && valuePattern.matcher(newValue).matches()) {
            if (existingValue != null) {
                errors.addError("Encountered additional declaration for metadata tag '" + identifier + "' while only single is allowed");
            } else {
                setNewValue.accept(newValue);
            }
        } else {
            errors.addError("Encountered metadata tag '" + identifier + "' with an invalid value: " + newValue + ". Value should match pattern: " + valuePattern.pattern());
        }
    }

    /**
     * Sets MetaDataItem with identifier and no value. Identifier has boolean value which will only be set to true whenever conditions are met.
     *
     * @param setBooleanTrue  Set method for boolean of the identifier in archetype to set the boolean to true if not done before
     * @param existingBoolean Not null and true means the identifier has been declared before in the metadata of the archetype
     * @param identifier      Identifier of the MetaDataItem
     * @param unexpectedValue Expected to be null because identifier should not have a value
     */
    private void setMetaDataItemWithoutValue(Consumer<Boolean> setBooleanTrue, Boolean existingBoolean, String identifier, String unexpectedValue) {
        if (existingBoolean != null && existingBoolean) {
            errors.addError("Encountered additional metadata tag for '" + identifier + "' while only single is allowed");
        } else if (unexpectedValue == null) {
            setBooleanTrue.accept(Boolean.TRUE);
        } else {
            errors.addError("Encountered metadata tag '" + identifier + "' with a value assignment while expecting none");
        }
    }
}
