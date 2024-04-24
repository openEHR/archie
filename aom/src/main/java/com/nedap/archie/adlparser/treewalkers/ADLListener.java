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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.nedap.archie.definitions.AdlDefinitions.*;

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

    Set<String> seenMetaDataIdentifiers = new HashSet<>();
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
            // Check if identifier is declared only once
            checkMetaDataIdentifier(identifier, ctx.identifier());

            // If metaDataValue present, value can be 'primitive_value', 'GUID' or 'VERSION_ID'
            switch (identifier) {
                case ADL_VERSION:
                    if (metaDataValue != null && VERSION_ID_REGEX.matcher(metaDataValue).matches()) {
                        authoredArchetype.setAdlVersion(metaDataValue);
                    } else {
                        addError("Encountered metadata tag '" + ADL_VERSION + "' with an invalid version id: " + metaDataValue,
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                case RM_RELEASE:
                    if (metaDataValue != null && VERSION_ID_REGEX.matcher(metaDataValue).matches()) {
                        authoredArchetype.setRmRelease(metaDataValue);
                    } else {
                        addError("Encountered metadata tag '" + RM_RELEASE + "' with an invalid version id: " + metaDataValue,
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                case BUILD_UID:
                    if (metaDataValue != null && GUID_REGEX.matcher(metaDataValue).matches()) {
                        authoredArchetype.setBuildUid(metaDataValue);
                    } else {
                        addError("Encountered metadata tag '" + BUILD_UID + "' with an invalid guid: " + metaDataValue,
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                case UID:
                    if (metaDataValue != null && GUID_REGEX.matcher(metaDataValue).matches()) {
                        authoredArchetype.setUid(metaDataValue);
                    } else {
                        addError("Encountered metadata tag '" + UID + "' with an invalid guid: " + metaDataValue,
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                case CONTROLLED:
                    if (metaDataValue == null) {
                        authoredArchetype.setControlled(true);
                    } else {
                        addError("Encountered metadata tag '" + CONTROLLED + "' with a value assignment while expecting none",
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                case GENERATED:
                    if (metaDataValue == null) {
                        authoredArchetype.setGenerated(true);
                    } else {
                        addError("Encountered metadata tag '" + GENERATED + "' with a value assignment while expecting none",
                                "incorrect header metadata format",
                                ctx.identifier());
                    }
                    break;
                default:
                    authoredArchetype.addOtherMetadata(identifier, metaDataValue);
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

    private void checkMetaDataIdentifier(String identifier, IdentifierContext identifierContext) {
        if(seenMetaDataIdentifiers.contains(identifier)) {
            addError("Encountered another metadata tag for '" + identifier + "' whilst single allowed",
                    "only one header metadata tag allowed",
                    identifierContext);
        } else {
            seenMetaDataIdentifiers.add(identifier);
        }
    }

    private void addError(String message, String shortMessage, ParserRuleContext identifierContext) {
        errors.addError(message, shortMessage,
                identifierContext.getStart().getLine(),
                identifierContext.getStart().getCharPositionInLine(),
                identifierContext.getStart().getText().length(),
                identifierContext.getStart().getText());
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
}
