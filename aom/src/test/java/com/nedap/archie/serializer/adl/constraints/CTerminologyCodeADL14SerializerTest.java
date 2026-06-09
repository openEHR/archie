package com.nedap.archie.serializer.adl.constraints;

import com.nedap.archie.aom.primitives.CTerminologyCodeADL14;
import com.nedap.archie.aom.primitives.ConstraintStatus;
import com.nedap.archie.base.terminology.TerminologyCode;
import com.nedap.archie.serializer.adl.ADLDefinitionSerializer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link CTerminologyCodeADL14Serializer}, covering each reconstruction branch
 * directly via the static {@link ADLDefinitionSerializer#serialize} entry point.
 */
public class CTerminologyCodeADL14SerializerTest {

    @Test
    public void serializesSingleLocalAtCode() {
        assertEquals("[at0001]", serialize("at0001"));
    }

    @Test
    public void serializesSingleAcCode() {
        assertEquals("[ac0001]", serialize("ac0001"));
    }

    @Test
    public void serializesSingleExternalTermCodeAsIs() {
        // Stored already in full bracketed form by the parser.
        assertEquals("[snomed-ct::12345]", serialize("[snomed-ct::12345]"));
    }

    @Test
    public void serializesMultipleLocalCodesAsLocalValueSet() {
        assertEquals("[local::at0007, at0008, at0009, at0010]",
                serialize("at0007", "at0008", "at0009", "at0010"));
    }

    @Test
    public void serializesExternalMultiCodeWithTerminologyIdFirst() {
        assertEquals("[openehr::271, 272, 273, 253]",
                serialize("openehr", "271", "272", "273", "253"));
    }

    @Test
    public void serializesConstraintStatusPrefix() {
        CTerminologyCodeADL14 cobj = new CTerminologyCodeADL14();
        cobj.addConstraint("at0001");
        cobj.setConstraintStatus(ConstraintStatus.PREFERRED);
        assertEquals("preferred [at0001]", ADLDefinitionSerializer.serialize(cobj));
    }

    @Test
    public void serializesAssumedValueForLocalCode() {
        CTerminologyCodeADL14 cobj = new CTerminologyCodeADL14();
        cobj.addConstraint("ac0001");
        cobj.setAssumedValue(TerminologyCode.createFromString("at0002"));
        assertEquals("[ac0001; at0002]", ADLDefinitionSerializer.serialize(cobj));
    }

    @Test
    public void serializesAssumedValueInsideExternalTermCodeRef() {
        CTerminologyCodeADL14 cobj = new CTerminologyCodeADL14();
        cobj.addConstraint("[snomed-ct::12345]");
        cobj.setAssumedValue(TerminologyCode.createFromString("12345"));
        assertEquals("[snomed-ct::12345; 12345]", ADLDefinitionSerializer.serialize(cobj));
    }

    @Test
    public void serializesEmptyConstraintAsEmptyString() {
        CTerminologyCodeADL14 cobj = new CTerminologyCodeADL14();
        assertEquals("", ADLDefinitionSerializer.serialize(cobj));
    }

    private String serialize(String... constraints) {
        CTerminologyCodeADL14 cobj = new CTerminologyCodeADL14();
        cobj.setConstraint(Arrays.asList(constraints));
        return ADLDefinitionSerializer.serialize(cobj);
    }
}
