package com.nedap.archie.adlparser.treewalkers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.adlparser.antlr.AdlParser.*;
import com.nedap.archie.aom.*;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.base.OpenEHRBase;
import com.nedap.archie.rminfo.MetaModels;
import com.nedap.archie.rules.Assertion;
import com.nedap.archie.serializer.odin.AdlOdinToJsonConverter;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the definition part of an archetype
 *
 * Created by pieter.bos on 15/10/15.
 */
public class CComplexObjectParser extends BaseTreeWalker {

    private final PrimitivesConstraintParser primitivesConstraintParser;
    private final MetaModels metaModels;

    public CComplexObjectParser(ANTLRParserErrors errors, MetaModels metaModels) {
        super(errors);
        primitivesConstraintParser = new PrimitivesConstraintParser(errors, metaModels);
        this.metaModels = metaModels;
    }

    public RulesSection parseRules(RulesSectionContext context) {
        RulesSection result = new RulesSection();

        result.setContent(context.getText());
        RulesParser rulesParser = new RulesParser(getErrors(), metaModels);
        for(AssertionContext assertion:context.assertion_list().assertion()) {
            result.addRule(rulesParser.parse(assertion));
        }

        return result;
    }

    public CComplexObject parseComplexObject(C_complex_objectContext context) {
        CComplexObject object = new CComplexObject();
        if(context.type_id() != null) {
            object.setRmTypeName(context.type_id().getText());
        }
        if(context.ID_CODE() != null) {
            object.setNodeId(context.ID_CODE().getText());
        } else if (context.ROOT_ID_CODE() != null) {
            object.setNodeId(context.ROOT_ID_CODE().getText());
        }
        //TODO: object.setDeprecated(context.) ?;
        if (context.c_occurrences() != null) {
            object.setOccurrences(parseMultiplicityInterval(context.c_occurrences()));
        }
        for (C_attribute_defContext attribute : context.c_attribute_def()) {
            parseCAttribute(object, attribute);
        }
        return object;
    }

    private void parseCAttribute(CComplexObject parent, C_attribute_defContext attributeDefContext) {

        if (attributeDefContext.c_attribute() != null) {
            CAttribute attribute = new CAttribute();
            C_attributeContext attributeContext = attributeDefContext.c_attribute();
            if(attributeContext.attribute_id() != null) {
                attribute.setRmAttributeName(attributeContext.attribute_id().getText());
            } else {
                attribute.setDifferentialPath(attributeContext.ADL_PATH().getText());

                attribute.setRmAttributeName(getLastAttributeFromPath(attribute.getDifferentialPath()));
            }
            if (attributeContext.c_existence() != null) {
                attribute.setExistence(parseMultiplicityInterval(attributeContext.c_existence()));
            }

            if (attributeContext.c_cardinality() != null) {
                attribute.setCardinality(this.parseCardinalityInterval(attributeContext.c_cardinality()));
            }
            if (attributeContext.c_objects() != null) {
                attribute.setChildren(parseCObjects(attributeContext.c_objects()));
            } else if (attributeContext.CONTAINED_REGEXP() != null) {
                attribute.addChild(primitivesConstraintParser.parseRegex(attributeContext.CONTAINED_REGEXP()));
            }
            parent.addAttribute(attribute);
            if(attribute.getCardinality() != null) {
                //Sort of sensible default. Will be overwritten in ADLParser with the actual value from the RM
                attribute.setMultiple(true);
            }
        } else if (attributeDefContext.c_attribute_tuple() != null) {
            parent.addAttributeTuple(parseAttributeTuple(parent, attributeDefContext.c_attribute_tuple()));
        } else {
            Default_valueContext defaultValueContext = attributeDefContext.default_value();
            if (defaultValueContext != null) {
                if (defaultValueContext.odin_text().included_other_language() != null) {
                    parent.setDefaultValue(parseIncludedLanguageDefaultValue(defaultValueContext.odin_text().included_other_language().getText()));
                } else {
                    parseOdinDefaultValue(parent, defaultValueContext);
                }
            }
        }
    }

    private OpenEHRBase parseIncludedLanguageDefaultValue(String text) {
        String format = text.substring(1, text.indexOf(')'));
        int startIndex = text.indexOf("<#");
        int endIndex = text.lastIndexOf("#>");
        String content = text.substring(startIndex+2, endIndex);

        if(format.equalsIgnoreCase(DefaultValueContainer.JSON)) {
            ObjectMapper defaultValueJsonObjectMapper = getDefaultValueJsonObjectMapper();
            if (defaultValueJsonObjectMapper != null) {
                try {
                    return defaultValueJsonObjectMapper.readValue(
                            content, OpenEHRBase.class
                    );
                } catch (IOException e) {
                    //TODO: find how to add line number/character position information here
                    addError("error parsing json in default value: " + e.getMessage());
                    //and just store it as a DefaultValueContainer
                    return new DefaultValueContainer(content, format);
                    //throw new RuntimeException(e);
                }

            } else {
                return new DefaultValueContainer(format, content);
            }
        } else {
            return new DefaultValueContainer(format, content);
        }
    }

    private void parseOdinDefaultValue(CComplexObject parent, Default_valueContext defaultValueContext) {
        try {
            ObjectMapper defaultValueObjectMapper = getDefaultValueOdinObjectMapper();
            if (defaultValueObjectMapper != null) {
                OpenEHRBase value = defaultValueObjectMapper.readValue(
                        new AdlOdinToJsonConverter().convert(defaultValueContext.odin_text()), OpenEHRBase.class
                );

                parent.setDefaultValue(value);
            } else {
                parent.setDefaultValue(new DefaultValueContainer(DefaultValueContainer.ODIN, defaultValueContext.odin_text().getText()));
            }
        } catch (IOException e) {
            //TODO: find how to add line number/character position information here
            addError("error parsing json in default value: " + e.getMessage());
            //and just store it as a DefaultValueContainer
            parent.setDefaultValue(new DefaultValueContainer(DefaultValueContainer.ODIN, defaultValueContext.odin_text().getText()));
            //throw new RuntimeException(e);
        }
    }

    private ObjectMapper getDefaultValueJsonObjectMapper() {
        if(metaModels == null) {
            return null;
        }
        if(metaModels.getSelectedModel() == null) {
            return null;
        }
        return metaModels.getSelectedModel().getJsonObjectMapper();
    }

    private ObjectMapper getDefaultValueOdinObjectMapper() {
        if(metaModels == null) {
            return null;
        }
        if(metaModels.getSelectedModel() == null) {
            return null;
        }
        return metaModels.getSelectedModel().getOdinInputObjectMapper();

    }

    public static String getFirstAttributeOfPath(String path) {
        return path.substring(0, path.indexOf('/'));
    }

    public static String getPathMinusFirstAttribute(String path) {
        return path.substring(path.indexOf('/'));
    }

    public static String getLastAttributeFromPath(String path) {
       return path.substring(path.lastIndexOf('/')+1);
    }

    private CAttributeTuple parseAttributeTuple(CComplexObject parent, C_attribute_tupleContext attributeTupleContext) {
        List<Attribute_idContext> attributeIdList = attributeTupleContext.attribute_id();
        CAttributeTuple tuple = new CAttributeTuple();

        for(Attribute_idContext idContext:attributeIdList) {
            CAttribute attribute = new CAttribute();
            String id = idContext.getText();//TODO? parse odin string value?
            attribute.setRmAttributeName(id);
            tuple.addMember(attribute);
            parent.addAttribute(attribute);
        }
        List<C_object_tupleContext> tupleContexts = attributeTupleContext.c_object_tuple();
        for(C_object_tupleContext tupleContext:tupleContexts) {
            CPrimitiveTuple primitiveTuple = new CPrimitiveTuple();

            List<C_object_tuple_itemContext> primitiveObjectContexts = tupleContext.c_object_tuple_items().c_object_tuple_item();
            int i = 0;
            for(C_object_tuple_itemContext tupleObjectContext:primitiveObjectContexts) {
                CPrimitiveObject<?, ?> primitiveObject = null;
                if(tupleObjectContext.c_primitive_object() != null) {
                    primitiveObject = primitivesConstraintParser.parsePrimitiveObject(tupleObjectContext.c_primitive_object());
                } else if (tupleObjectContext.CONTAINED_REGEXP() != null) {
                    primitiveObject = primitivesConstraintParser.parseRegex(tupleObjectContext.CONTAINED_REGEXP());
                }
                tuple.getMembers().get(i).addChild(primitiveObject);
                primitiveTuple.addMember(primitiveObject);
                i++;
            }
            tuple.addTuple(primitiveTuple);
        }

        return tuple;
    }

    private List<CObject> parseCObjects(C_objectsContext objectsContext) {
        ArrayList<CObject> result = new ArrayList<>();

        if (objectsContext.c_primitive_object() != null) {
            result.add(primitivesConstraintParser.parsePrimitiveObject(objectsContext.c_primitive_object()));
        } else {
            List<C_non_primitive_object_orderedContext> nonPrimitiveObjectOrderedContext = objectsContext.c_non_primitive_object_ordered();
            if (nonPrimitiveObjectOrderedContext != null) {

                for (C_non_primitive_object_orderedContext object : nonPrimitiveObjectOrderedContext) {

                    CObject cobject = parseNonPrimitiveObject(object.c_non_primitive_object());
                    Sibling_orderContext siblingOrderContext = object.sibling_order();
                    if(siblingOrderContext != null) {
                        SiblingOrder siblingOrder = new SiblingOrder();
                        if(siblingOrderContext.SYM_AFTER() != null) {
                            siblingOrder.setBefore(false);
                        } else if (siblingOrderContext.SYM_BEFORE() != null) {
                            siblingOrder.setBefore(true);
                        }
                        siblingOrder.setSiblingNodeId(siblingOrderContext.ID_CODE().getText());
                        cobject.setSiblingOrder(siblingOrder);
                    }

                    result.add(cobject);
                }
            }
        }
        return result;
    }

    private CObject parseNonPrimitiveObject(C_non_primitive_objectContext objectContext) {
        /*
          c_complex_object
        | c_archetype_root
        | c_complex_object_proxy
        | archetype_slot
        */
        if (objectContext.c_complex_object() != null) {
            return parseComplexObject(objectContext.c_complex_object());
        } else if (objectContext.c_archetype_root() != null) {
            return parseArchetypeRoot(objectContext.c_archetype_root());

        } else if (objectContext.c_complex_object_proxy() != null) {
            return parseCComplexObjectProxy(objectContext.c_complex_object_proxy());
        } else if (objectContext.archetype_slot() != null) {
            return parseArchetypeSlot(objectContext.archetype_slot());

        }
        return null;
    }

    private CComplexObjectProxy parseCComplexObjectProxy(C_complex_object_proxyContext proxyContext) {

        CComplexObjectProxy proxy = new CComplexObjectProxy();
        proxy.setOccurrences(this.parseMultiplicityInterval(proxyContext.c_occurrences()));
        proxy.setTargetPath(proxyContext.adl_path().getText());
        proxy.setRmTypeName(proxyContext.type_id().getText());
        proxy.setNodeId(proxyContext.ID_CODE().getText());
        return proxy;
    }

    private CArchetypeRoot parseArchetypeRoot(C_archetype_rootContext archetypeRootContext) {
        CArchetypeRoot root = new CArchetypeRoot();

        root.setRmTypeName(archetypeRootContext.type_id().getText());
        root.setNodeId(archetypeRootContext.ID_CODE().getText());
        if(archetypeRootContext.archetype_ref() != null) {
            root.setArchetypeRef(archetypeRootContext.archetype_ref().getText());
        }

        root.setOccurrences(this.parseMultiplicityInterval(archetypeRootContext.c_occurrences()));
        for (C_attribute_defContext attributeContext : archetypeRootContext.c_attribute_def()) {
            parseCAttribute(root, attributeContext);
        }
//((Archetype_slotContext) slotContext).start.getInputStream().getText(slotContext.getSourceInterval())
        return root;
    }

    private ArchetypeSlot parseArchetypeSlot(Archetype_slotContext slotContext) {
        ArchetypeSlot slot = new ArchetypeSlot();

        slot.setNodeId(slotContext.ID_CODE().getText());
        slot.setRmTypeName(slotContext.type_id().getText());
        if(slotContext.SYM_CLOSED() != null) {
            slot.setClosed(true);
        }
        if (slotContext.c_occurrences() != null) {
            slot.setOccurrences(parseMultiplicityInterval(slotContext.c_occurrences()));
        }
        RulesParser assertionParser = new RulesParser(getErrors(), metaModels);
        if (slotContext.c_excludes() != null) {
            for (AssertionContext assertionContext : slotContext.c_excludes().assertion()) {
                slot.getExcludes().add((Assertion) assertionParser.parse(assertionContext));
            }
        }
        if (slotContext.c_includes() != null) {
            for (AssertionContext assertionContext : slotContext.c_includes().assertion()) {
                slot.getIncludes().add((Assertion) assertionParser.parse(assertionContext));
            }
        }
        return slot;
    }


    private Cardinality parseCardinalityInterval(C_cardinalityContext context) {
        Cardinality cardinality = new Cardinality();
        MultiplicityInterval interval = parseMultiplicity(context.cardinality().multiplicity());
        cardinality.setInterval(interval);

        List<Multiplicity_modContext> modContexts = context.cardinality().multiplicity_mod();
        for(Multiplicity_modContext modContext:modContexts) {
            if(modContext.ordering_mod() != null) {
                cardinality.setOrdered(modContext.ordering_mod().SYM_ORDERED() != null);
            } else {
                cardinality.setOrdered(true);//TODO: this should retrieve it from the RM. This now matches the serializer, but both should be fixed!
            }
            if(modContext.unique_mod() != null) {
                cardinality.setUnique(true);
            }

        }
        return cardinality;
    }

    private MultiplicityInterval parseMultiplicityInterval(C_existenceContext existenceContext) {
        MultiplicityInterval interval = new MultiplicityInterval();
        List<TerminalNode> integers = existenceContext.existence().INTEGER();
        if(integers.size() == 1) {
            interval.setLower(Integer.parseInt(integers.get(0).getText()));
            interval.setUpper(interval.getLower());
        } else if (integers.size() == 2) {
            interval.setLower(Integer.parseInt(integers.get(0).getText()));
            interval.setUpper(Integer.parseInt(integers.get(1).getText()));
        }
        return interval;
    }

    private MultiplicityInterval parseMultiplicityInterval(C_occurrencesContext occurrencesContext) {
        if(occurrencesContext == null) {
            return null;
        }

        return parseMultiplicity(occurrencesContext.multiplicity());
    }

    private MultiplicityInterval parseMultiplicity(MultiplicityContext multiplicity) {
        if(multiplicity == null) {
            return null;
        }
        MultiplicityInterval interval = new MultiplicityInterval();
        List<TerminalNode> integers = multiplicity.INTEGER();
        if(multiplicity.SYM_INTERVAL_SEP() != null) {
            if(multiplicity.getText().contains("*")) {
                interval.setLower(Integer.parseInt(integers.get(0).getText()));
                interval.setUpperUnbounded(true);
            } else {
                interval.setLower(Integer.parseInt(integers.get(0).getText()));
                interval.setUpper(Integer.parseInt(integers.get(1).getText()));
            }
        } else {
            //one integer or *
            if(multiplicity.getText().contains("*")) {
                interval.setLowerUnbounded(false);
                interval.setLower(0);
                interval.setUpperUnbounded(true);
            } else {
                interval.setLower(Integer.parseInt(integers.get(0).getText()));
                interval.setUpper(interval.getLower());
            }
        }
        interval.fixUnboundedIncluded();
        return interval;
    }

}
