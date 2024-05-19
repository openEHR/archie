package com.nedap.archie.testutil;

import com.google.common.collect.Lists;
import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.CAttribute;
import com.nedap.archie.aom.CComplexObject;
import com.nedap.archie.aom.CObject;
import com.nedap.archie.aom.CPrimitiveObject;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.base.RMObject;
import com.nedap.archie.rminfo.ReflectionModelInfoLookup;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by pieter.bos on 06/04/16.
 */
public class TestUtil {

    public TestUtil (ReflectionModelInfoLookup metaModel) {
        creator = new RMObjectCreator(metaModel);
    }
    private static final Logger logger = LoggerFactory.getLogger(TestUtil.class);

    private RMObjectCreator creator;

    /**
     * Creates an empty RM Object, fully nested, one object per CObject found.
     * For those familiar to the old java libs: this is a simple skeleton generator.
     *
     * In a real system you would want user input/a parameter map. Plus just creating every CObject will
     * introduce cardinality/multiplicity problems in many case.
     *
     * @param object
     * @return
     */
    public RMObject constructEmptyRMObject(CObject object) {
        RMObject result = creator.create(object);
        for(CAttribute attribute: object.getAttributes()) {
            List<Object> children = new ArrayList<>();
            for(CObject childConstraint:attribute.getChildren()) {
                if(childConstraint instanceof CComplexObject) {
                    RMObject childObject = constructEmptyRMObject(childConstraint);
                    children.add(childObject);
//                    if(childConstraint.getRmTypeName().equals("EVENT")) {
//                        childObject = constructEmptyRMObject(childConstraint);
//                        children.add(childObject);
//                    }
                }
            }
            if(!children.isEmpty()) {
                if(attribute.isMultiple()) {
                    creator.set(result, attribute.getRmAttributeName(), children);
                } else if(!children.isEmpty()){
                    //set the first possible result in case of multiple children for a single valued value
                    creator.set(result, attribute.getRmAttributeName(), Lists.newArrayList(children.get(0)));
                }
            }
        }
        return result;
    }

    /**
     * Assert that two CObjects are equal, including the entire tree defined by their attributes
     *
     * Currently does only classes, node ids, attribute names and rm type names. To be extended for better tests
     * @param cobject1
     * @param cobject2
     * @throws Exception
     */
    public static void assertCObjectEquals(CObject cobject1, CObject cobject2) throws Exception {
        Assert.assertThat(cobject1.getClass(), CoreMatchers.is(CoreMatchers.equalTo(cobject2.getClass())));
        Assert.assertThat(cobject1.getRmTypeName(), CoreMatchers.is(cobject2.getRmTypeName()));
        Assert.assertThat(cobject1.getNodeId(), CoreMatchers.is(cobject2.getNodeId()));
        Assert.assertThat(cobject1.getAttributes().size(), CoreMatchers.is(cobject2.getAttributes().size()));
        Assert.assertThat(cobject1.getOccurrences(), CoreMatchers.is(cobject2.getOccurrences()));
        for(CAttribute attribute1:cobject1.getAttributes()) {
            String path = attribute1.getPath();
            CAttribute attribute2;
            if(attribute1.getDifferentialPath() != null) {
                attribute2 = cobject2.getAttribute(attribute1.getDifferentialPath());
            } else {
                attribute2 = cobject2.getAttribute(attribute1.getRmAttributeName());
            }
            Assert.assertThat(path, attribute2, CoreMatchers.is(CoreMatchers.notNullValue()));
            Assert.assertThat(path, attribute1.getCardinality(), CoreMatchers.is(attribute2.getCardinality()));
            Assert.assertThat(path, attribute1.getExistence(), CoreMatchers.is(attribute2.getExistence()));
            for(CObject childObject1:attribute1.getChildren()) {
                if(childObject1 instanceof  CComplexObject) {
                    List<CObject> childObjects2 = attribute2.getChildren().stream()
                        .filter(
                            o -> o.getNodeId().equals(childObject1.getNodeId())
                        ).collect(Collectors.toList());
                    boolean oneSucceeded = false;
                    AssertionError lastException = null;
                    for(CObject childObject2:childObjects2) { //it's possible to get the same id twice with C_ARCHETYPE_ROOT according to ADL specs
                        try {
                            assertCObjectEquals(childObject1, childObject2);
                            oneSucceeded = true;
                        } catch (AssertionError e) {
                            lastException = e;
                        }
                    }
                    if(!oneSucceeded) {
                        if(lastException != null) {
                            throw lastException;
                        } else {
                            Assert.fail("no objects for cobject: " + childObject1);
                        }
                    }


                } else if (childObject1 instanceof CPrimitiveObject) {
                    CPrimitiveObject<?, ?> primitiveChild = (CPrimitiveObject<?, ?>) childObject1;
                    List<CObject> childObjects2 = attribute2.getChildren().stream()
                        .filter(
                            o -> primitiveObjectMatches(primitiveChild, o)
                        ).collect(Collectors.toList());
                    Assert.assertFalse("a primitive object should have a matching primitive object", childObjects2.isEmpty());
                }

            }

        }

    }

    private static boolean primitiveObjectMatches(CPrimitiveObject<?, ?> o1, CObject o2) {
        return (o2 instanceof CPrimitiveObject) && Objects.equals(((CPrimitiveObject<?, ?>) o2).getConstraint(), o1.getConstraint());
    }

    public static Archetype parseFailOnErrors(Class<?> caller, String resourceName) throws IOException, ADLParseException {
        ADLParser parser = new ADLParser();
        try(InputStream stream = caller.getResourceAsStream(resourceName)) {
            if(stream == null) {
                throw new RuntimeException("Resource does not exist: " + resourceName);
            }
            Archetype archetype = parser.parse(stream);
            parser.getErrors().logToLogger();
            Assert.assertFalse(parser.getErrors().toString(), parser.getErrors().hasErrors());
            Assert.assertNotNull(archetype);
            return archetype;
        }
    }

    public static void parseExpectErrorCode(Class<?> caller, String resourceName, String errorCode) throws IOException {
        ADLParser parser = new ADLParser();
        try(InputStream stream = caller.getResourceAsStream(resourceName)) {
            if(stream == null) {
                throw new RuntimeException("Resource does not exist: " + resourceName);
            }
            try {
                Archetype archetype = parser.parse(stream);
                Assert.assertTrue("Parser expected to have errors, but there were none", parser.getErrors().hasErrors());
            } catch (ADLParseException ex) {
                parser.getErrors().logToLogger();
                Assert.assertTrue("Parser expected to have errors, but there were none", parser.getErrors().hasErrors());
                Assert.assertTrue("expected error code to be present: " + errorCode, parser.getErrors().getErrors().stream().anyMatch(e -> e.getShortMessage().equalsIgnoreCase(errorCode)));
            }
        }
    }

}
