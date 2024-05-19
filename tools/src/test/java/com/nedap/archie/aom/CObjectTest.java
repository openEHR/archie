package com.nedap.archie.aom;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.base.Cardinality;
import com.nedap.archie.base.MultiplicityInterval;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pieter.bos on 20/04/16.
 */
public class CObjectTest {

    private Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors(this.getClass(),"/basic.adl");
    }

    @Test
    public void definitionMeaningAndLogicalPath() {
        assertEquals("Prescription", archetype.getDefinition().getMeaning());
        assertEquals("A document authorising supply and administration of one or more medicines, vaccines or other therapeutic goods (as a collection of medication instrations) to be communicated to a dispensing or administration provider.", archetype.getDefinition().getDescription());

        CObject qualitification = archetype.getDefinition().itemAtPath("/context[id11]/other_context[id2]/items[id3]");

        assertEquals("/context[id11]/other_context[id2]/items[Qualification]", qualitification.getLogicalPath());

        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("nl");

        assertEquals("Recept", archetype.getDefinition().getMeaning());
        assertEquals("Een document waarmee uitgifte van een of meerdere medicijnen of hulpmiddel wordt geautoriseerd.", archetype.getDefinition().getDescription());

        assertEquals("/context[id11]/other_context[id2]/items[Qualification]", qualitification.getLogicalPath());


        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage("nl");

        assertEquals("/context[id11]/other_context[id2]/items[Kwalificatie]", qualitification.getLogicalPath());
    }

    @Test
    public void testReplaceChild() {
        CObject orderId = archetype.getDefinition().itemAtPath("/context[id11]/other_context[id2]/items[id3]/items[id4]");
        CObject comment = archetype.getDefinition().itemAtPath("/context[id11]/other_context[id2]/items[id3]/items[id7]");
        CAttribute parent = orderId.getParent();
        parent.getChildren().remove(parent.getChildren().size() -1);//don't do this in real code :)
        parent.replaceChild("id4", comment);
        assertEquals(comment, parent.getChildren().get(0));


    }

    @Test
    public void effectiveOccurrencesReturnsOccurrencesWhenPresent() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("content");
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        object.setOccurrences(MultiplicityInterval.createBounded(3,5));
        parentAttribute.addChild(object);

        assertEquals(MultiplicityInterval.createBounded(3, 5), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(1,2);
            }
        }));
    }

    @Test
    public void effectiveOccurrencesReturnsDefaultOccurrencesBounded() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("content");
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        parentAttribute.addChild(object);

        assertEquals(MultiplicityInterval.createBounded(0, 2), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(1,2);
            }
        }));
    }

    @Test
    public void effectiveOccurrencesReturnsDefaultOccurrencesUnbounded() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("content");
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        parentAttribute.addChild(object);

        assertEquals(MultiplicityInterval.createOpen(), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return MultiplicityInterval.createUpperUnbounded(1);
            }
        }));
    }

    @Test
    public void effectiveOccurrencesReturnsDefaultOccurrencesNull() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("content");
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        parentAttribute.addChild(object);

        assertNull(object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return null;
            }
        }));
    }

    @Test
    public void effectiveOccurrencesCardinalityUnboundedDefinesUnboundedUpper() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setCardinality(Cardinality.mandatoryAndUnbounded());
        parentAttribute.setRmAttributeName("content");
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        parentAttribute.addChild(object);

        //even though it's mandatory, doesn't have to be _this_ element, so 0.
        assertEquals(MultiplicityInterval.createUpperUnbounded(0), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(1,2);
            }
        }));
    }

    @Test
    public void effectiveOccurrencesCardinalityBoundedDefinesUpper() {
        CComplexObject parentCObject = new CComplexObject();
        parentCObject.setRmTypeName("COMPOSITION");
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("content");
        Cardinality cardinality = new Cardinality();
        cardinality.setInterval(MultiplicityInterval.createBounded(3, 4));
        parentAttribute.setCardinality(cardinality);
        parentCObject.addAttribute(parentAttribute);
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("OBSERVATION");
        parentAttribute.addChild(object);

        //even though it's mandatory, doesn't have to be _this_ element, so 0.
        assertEquals(MultiplicityInterval.createBounded(0, 4), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(1,2);
            }
        }));
    }
    
    @Test
    public void effectiveOccurrencesNoParent() {
        CComplexObject object = new CComplexObject();
        object.setRmTypeName("DV_CODED_TEXT");

        assertEquals(MultiplicityInterval.createOpen(), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(0,5);
            }
        }));
    }

    @Test
    public void effectiveOccurrencesNoParentParent() {
        CAttribute parentAttribute = new CAttribute();
        parentAttribute.setRmAttributeName("value");

        CComplexObject object = new CComplexObject();
        object.setRmTypeName("DV_CODED_TEXT");
        parentAttribute.addChild(object);

        assertEquals(MultiplicityInterval.createOpen(), object.effectiveOccurrences(new BiFunction<String, String, MultiplicityInterval>() {
            @Override
            public MultiplicityInterval apply(String s, String s2) {
                return new MultiplicityInterval(0,5);
            }
        }));
    }


}
