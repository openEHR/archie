package org.openehr.adl;

import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nedap.archie.adlparser.treewalkers.ComponentTerminologiesHelper;
import com.nedap.archie.adlparser.v2.antlr.AdlParser;
import com.nedap.archie.adlparser.treewalkers.CComplexObjectParser;
//import com.nedap.archie.adlparser.treewalkers.ComponentTerminologiesHelper;
import com.nedap.archie.adlparser.treewalkers.TerminologyParser;
import com.nedap.archie.adlparser.v2.antlr.AdlParserBaseListener;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.*;
import com.nedap.archie.aom.terminology.ArchetypeTerminology;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import com.nedap.archie.serializer.odin.OdinObjectParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.concurrent.ConcurrentHashMap;

public class AdlV2Listener extends AdlParserBaseListener {

        private ANTLRParserErrors errors;

        private Archetype rootArchetype;

        private Archetype archetype;
        private CComplexObjectParserV2 cComplexObjectParser;
        private MetaModels metaModels;

        public AdlV2Listener(ANTLRParserErrors errors, MetaModels metaModels) {
            this.errors = errors;
            cComplexObjectParser = new CComplexObjectParserV2(metaModels);
            this.metaModels = metaModels;
        }

        /** top-level constructs */
        @Override
        public void enterAuthoredArchetype(AdlParser.AuthoredArchetypeContext ctx) {
            rootArchetype = new AuthoredArchetype();
            setArchetype(rootArchetype);
        }

        private void setArchetype(Archetype archetype) {
            this.archetype = archetype;
        }

        @Override
        public void exitAuthoredArchetype(AdlParser.AuthoredArchetypeContext ctx) {
            rootArchetype.setDifferential(true); //TODO: not possible to check from the content of the archetype without spec change
        }

        @Override
        public void enterTemplate(AdlParser.TemplateContext ctx) {
            rootArchetype = new Template();
            setArchetype(rootArchetype);
        }

        @Override
        public void exitTemplate(AdlParser.TemplateContext ctx) {
            rootArchetype.setDifferential(true); //TODO: not possible to check from the content of the archetype without spec change
        }

        @Override
        public void enterTemplateOverlay(AdlParser.TemplateOverlayContext ctx) {
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
        }

        @Override
        public void enterOperationalTemplate(AdlParser.OperationalTemplateContext ctx) {
            rootArchetype = new OperationalTemplate();
            rootArchetype.setDifferential(false);//operational templates are flat by definition
            setArchetype(rootArchetype);
        }

        @Override
        public void enterHeader(AdlParser.HeaderContext ctx) {
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

        @Override
        public void enterMetaDataItem(AdlParser.MetaDataItemContext ctx) {
        /*
         SYM_ADL_VERSION '=' VERSION_ID
        | SYM_UID '=' GUID
        | SYM_BUILD_UID '=' GUID
        | SYM_RM_RELEASE '=' VERSION_ID
        | SYM_IS_CONTROLLED
        | SYM_IS_GENERATED
        | identifier ( '=' meta_data_value )?

         */
            if(archetype instanceof AuthoredArchetype) {
                AuthoredArchetype authoredArchetype = (AuthoredArchetype) archetype;
                AdlParser.MetaDataFlagContext flag = ctx.metaDataFlag();
                AdlParser.MetaDataValueItemContext valueItem = ctx.metaDataValueItem();
                if(flag != null) {
                    String text = flag.ALPHANUM_ID().getText();
                    switch(text.toLowerCase()) {
                        case "generated":
                            authoredArchetype.setGenerated(true);
                            break;
                        case "controlled":
                            authoredArchetype.setControlled(true);
                            break;
                        default:
                            //TODO: add error!
                    }
                } else if (valueItem != null) {
                    String itemName = valueItem.ALPHANUM_ID().getText();
                    String value = valueItem.metaDataItemValue().getText();
                    switch(itemName.toLowerCase()) {
                        case "adl_version":
                            authoredArchetype.setAdlVersion(value);
                            break;
                        case "rm_release":
                            authoredArchetype.setRmRelease(value);
                            break;
                        case "uid":
                            authoredArchetype.setUid(value);
                            break;
                        case "build_uid":
                            authoredArchetype.setBuildUid(value);
                            break;
                        default:
                            authoredArchetype.addOtherMetadata(itemName, value);
                    }
                }
            }

        }

        /**
         * one level below: definition, language, etc.
         */
        @Override
        public void enterDefinitionSection(AdlParser.DefinitionSectionContext ctx) {
            CComplexObject definition = cComplexObjectParser.parseComplexObject(ctx.cadlText().getText(), getErrorListenerFor(ctx.cadlText()));
            archetype.setDefinition(definition);
        }

        @Override
        public void enterLanguageSection(AdlParser.LanguageSectionContext ctx) {
            archetype.setAuthoredResourceContent(OdinObjectParser.convert(ctx.odinText().getText(), LanguageSection.class, getErrorListenerFor(ctx.odinText())));
        }

        @Override
        public void enterTerminologySection(AdlParser.TerminologySectionContext ctx) {
            archetype.setTerminology(OdinObjectParser.convert(ctx.odinText().getText(), ArchetypeTerminology.class, getErrorListenerFor(ctx.odinText())));
        }

        @Override
        public void enterDescriptionSection(AdlParser.DescriptionSectionContext ctx) {
            archetype.setDescription(OdinObjectParser.convert(ctx.odinText().getText(), ResourceDescription.class, getErrorListenerFor(ctx.odinText())));
        }

        @Override
        public void enterSpecializeSection(AdlParser.SpecializeSectionContext ctx) {
            if(ctx != null && ctx.ARCHETYPE_REF() != null) {
                archetype.setParentArchetypeId(ctx.ARCHETYPE_REF().getText());
            }
        }

        @Override
        public void enterRulesSection(AdlParser.RulesSectionContext ctx) {
            archetype.setRules(cComplexObjectParser.parseRules(ctx.getText(), getErrorListenerFor(ctx)));
        }


        @Override
        public void enterAnnotationsSection(AdlParser.AnnotationsSectionContext ctx) {
            archetype.setAnnotations(OdinObjectParser.convert(ctx.odinText().getText(), ResourceAnnotations.class, getErrorListenerFor(ctx.odinText())));
        }

        private ArchieErrorListener getErrorListenerFor(ParserRuleContext context) {
            return new ArchieErrorListener(errors, context.getStart().getLine()-1);
        }

        @Override
        public void enterComponentTerminologiesSection(AdlParser.ComponentTerminologiesSectionContext ctx) {
            if (!(archetype instanceof OperationalTemplate)) {
                throw new IllegalArgumentException("cannot add component terminologies to anything but an operational template");
            }
            if(ctx.odinText() == null) {//TODO: attr_vals() != null must be checked here instead. Very very ugly
                //this is 'component_terminologies = <...>'
                OperationalTemplate template = (OperationalTemplate) archetype;

                ComponentTerminologiesHelper helper = OdinObjectParser.convert(ctx.odinText().getText(), ComponentTerminologiesHelper.class, getErrorListenerFor(ctx.odinText()));
                template.setComponentTerminologies(helper.getComponentTerminologies());
            } else {
                //this is a direct <["archetype_id"] = ...> syntax
                OperationalTemplate template = (OperationalTemplate) archetype;

                TypeFactory typeFactory = AdlOdinToJsonConverter.getObjectMapper().getTypeFactory();
                MapType mapType = typeFactory.constructMapType(ConcurrentHashMap.class, String.class, ArchetypeTerminology.class);

                template.setComponentTerminologies(OdinObjectParser.convert(ctx.odinText().getText(), mapType, getErrorListenerFor(ctx.odinText())));
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

