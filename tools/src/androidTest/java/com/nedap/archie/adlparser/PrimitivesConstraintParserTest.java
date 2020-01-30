package com.nedap.archie.adlparser;

import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.primitives.CDuration;
import com.nedap.archie.testutil.TestUtil;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by pieter.bos on 31/10/15.
 */
public abstract class PrimitivesConstraintParserTest {

    protected Archetype archetype;

    @Before
    public void setup() throws Exception {
        archetype = TestUtil.parseFailOnErrors("/adl2-tests/features/aom_structures/basic/openehr-TEST_PKG-WHOLE.primitive_types.v1.0.0.adls");
    }

    public <T> T getAttribute(String attributeName) {
        return (T) archetype.getDefinition().getAttribute(attributeName).getChildren().get(0);
    }

    public Archetype getAssumedValuesArchetype() throws Exception {
        return TestUtil.parseFailOnErrors("/adl2-tests/features/aom_structures/basic/openehr-TEST_PKG-WHOLE.assumed_values.v1.0.0.adls");
    }


}
