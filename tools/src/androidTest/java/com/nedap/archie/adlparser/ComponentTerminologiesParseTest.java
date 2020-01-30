package com.nedap.archie.adlparser;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ComponentTerminologiesParseTest {

    /**
     * Archie used to serialize a non-standard form. Kept here for backwards compatibility. It is deprecated and no longer
     * generated
     * @throws Exception
     */
    @Test
    public void nonStandardFirstForm() throws Exception {
        ADLParser parser = new ADLParser();
        parser.parse(getClass().getResourceAsStream("openEHR-EHR-ELEMENT.component_terminologies_1.v1.0.0.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
    }

    /**
     * component_terminologies
     *    component_terminologies = < [jfdlf] = ... >
     *
     * @throws Exception
     */
    @Test
    public void extendedForm() throws Exception {
        ADLParser parser = new ADLParser();
        parser.parse(getClass().getResourceAsStream("openEHR-EHR-ELEMENT.component_terminologies_2.v1.0.0.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
    }

    /**
     * component_terminologies
        [jfdlf] = <...>
     */
    @Test
    public void shortForm() throws Exception {
        ADLParser parser = new ADLParser();
        parser.parse(getClass().getResourceAsStream("openEHR-EHR-ELEMENT.component_terminologies_3.v1.0.0.adls"));
        assertTrue(parser.getErrors().hasNoErrors());
    }
}
