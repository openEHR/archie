package org.openehr.utils.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openehr.utils.message.MessageCode;
import org.openehr.utils.message.MessageDescriptor;
import org.openehr.utils.message.MessageLogger;
import org.openehr.utils.message.MessageSeverity;

import java.util.List;

import static org.junit.Assert.*;

public class ErrorAccumulatorTest {

    private MessageLogger errorAccumulator;

    @BeforeEach
    public void setup() {
        errorAccumulator = new MessageLogger();
    }

    @Test
    public void lastAdded() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null );
        errorAccumulator.addErrorWithLocation(TestErrorCode.code3, null );
        assertEquals(errorAccumulator.lastAdded().getCode(), TestErrorCode.code3);
    }

    @Test
    public void getErrorCodes() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code2, null);
        errorAccumulator.addInfoWithLocation(TestErrorCode.code3, null);
        errorAccumulator.addDebugWithLocation(null, TestErrorCode.code4.getMessageTemplate());//debug does not accept codes, for some reason!
        List<MessageCode> codes = errorAccumulator.getErrorCodes();
        assertEquals(codes.size(), 1);
        assertEquals(codes.get(0), TestErrorCode.code1);
    }

    @Test
    public void getWarningCodes() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code2, null);
        errorAccumulator.addInfoWithLocation(TestErrorCode.code3, null);
        errorAccumulator.addDebugWithLocation(null, TestErrorCode.code4.getCode());
        List<MessageCode> codes = errorAccumulator.getWarningCodes();
        assertEquals(codes.size(), 1);
        assertEquals(codes.get(0), TestErrorCode.code2);
    }

    @Test
    public void isEmpty1() throws Exception {
        assertTrue(errorAccumulator.isEmpty());
    }

    @Test
    public void isEmpty2() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code, null);
        assertFalse(errorAccumulator.isEmpty());
    }

    @Test
    public void hasErrors() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code, null);
        assertTrue(errorAccumulator.hasErrors());
        assertFalse(errorAccumulator.hasWarnings());
        assertFalse(errorAccumulator.hasInfo());
    }

    @Test
    public void hasWarnings() throws Exception {
        errorAccumulator.addWarningWithLocation(TestErrorCode.code, null);
        assertTrue(errorAccumulator.hasWarnings());
        assertFalse(errorAccumulator.hasErrors());
        assertFalse(errorAccumulator.hasInfo());
    }

    @Test
    public void hasInfo() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code, null);
        assertTrue(errorAccumulator.hasInfo());
        assertFalse(errorAccumulator.hasWarnings());
        assertFalse(errorAccumulator.hasErrors());
    }

    @Test
    public void hasErrorsOrWarnings1() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code, null);
        assertTrue(errorAccumulator.hasErrorsOrWarnings());
    }

    @Test
    public void hasErrorsOrWarnings2() throws Exception {
        errorAccumulator.addWarningWithLocation(TestErrorCode.code, null);
        assertTrue(errorAccumulator.hasErrorsOrWarnings());
    }

    @Test
    public void hasErrorsOrWarnings3() throws Exception {
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertTrue(errorAccumulator.hasErrorsOrWarnings());
    }

    @Test
    public void hasErrorsOrWarnings4() throws Exception {
        assertFalse(errorAccumulator.hasErrorsOrWarnings());
    }

    @Test
    public void hasError1() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertTrue(errorAccumulator.hasError(TestErrorCode.code2));
        assertFalse(errorAccumulator.hasError(TestErrorCode.code3));
        assertFalse(errorAccumulator.hasError(TestErrorCode.code1));
    }

    @Test
    public void hasError2() throws Exception {
        assertFalse(errorAccumulator.hasError(TestErrorCode.code1));
    }

    @Test
    public void hasMatchingError() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertTrue(errorAccumulator.hasMatchingError(TestErrorCode.code2));
        assertFalse(errorAccumulator.hasMatchingError(TestErrorCode.code0));
        assertFalse(errorAccumulator.hasMatchingError(TestErrorCode.code1));
    }

    @Test
    public void hasMatchingWarning() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertTrue(errorAccumulator.hasMatchingWarning(TestErrorCode.code1));
        assertFalse(errorAccumulator.hasMatchingWarning(TestErrorCode.code2));
        assertFalse(errorAccumulator.hasMatchingWarning(TestErrorCode.code0));
    }

    @Test
    public void addError() throws Exception {
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertEquals(1, errorAccumulator.getMessageList().size());
    }

    @Test
    public void addWarning() throws Exception {
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        assertEquals(1, errorAccumulator.getMessageList().size());
    }

    @Test
    public void addInfo() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        assertEquals(1, errorAccumulator.getMessageList().size());
    }

    @Test
    public void addDebug() throws Exception {
        errorAccumulator.addDebugWithLocation(null, TestErrorCode.code0.getCode());//debug does not use a code?!
        assertEquals(1, errorAccumulator.getMessageList().size());
    }

    @Test
    public void add() throws Exception {
        errorAccumulator.add(new MessageDescriptor(TestErrorCode.code0, MessageSeverity.DEBUG, null, null));
        assertEquals(1, errorAccumulator.getMessageList().size());
    }

    @Test
    public void append() throws Exception {
        MessageLogger one = new MessageLogger();
        MessageLogger two = new MessageLogger();

        one.addInfoWithLocation(TestErrorCode.code0, null);
        one.addWarningWithLocation(TestErrorCode.code1, null);
        one.addErrorWithLocation(TestErrorCode.code2, null);

        two.addInfoWithLocation(TestErrorCode.code0, null);
        two.addWarningWithLocation(TestErrorCode.code1, null);
        two.addErrorWithLocation(TestErrorCode.code2, null);

        one.append(two);

        assertEquals(6, one.getMessageList().size());
        assertEquals(3, two.getMessageList().size());

        assertEquals(2, one.getErrorCodes().size());
        assertEquals(2, one.getWarningCodes().size());

    }

    @Test
    public void clear() throws Exception {
        assertEquals(errorAccumulator.getMessageList().size(), 0);
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);
        assertEquals(errorAccumulator.getMessageList().size(), 3);
        assertTrue(errorAccumulator.hasErrors());
        assertTrue(errorAccumulator.hasWarnings());
        assertTrue(errorAccumulator.hasInfo());
        errorAccumulator.clear();
        assertEquals(errorAccumulator.getMessageList().size(), 0);
        assertFalse(errorAccumulator.hasErrors());
        assertFalse(errorAccumulator.hasWarnings());
        assertFalse(errorAccumulator.hasInfo());
    }

    @Test
    public void getErrorList() throws Exception {
        errorAccumulator.addInfoWithLocation(TestErrorCode.code0, null);
        errorAccumulator.addWarningWithLocation(TestErrorCode.code1, null);
        errorAccumulator.addErrorWithLocation(TestErrorCode.code2, null);

        assertEquals(3, errorAccumulator.getMessageList().size());
    }

    @Test
    public void toStringFiltered() throws Exception {
        errorAccumulator.addInfo(TestErrorCode.code0);
        errorAccumulator.addWarning(TestErrorCode.code1);
        errorAccumulator.addError(TestErrorCode.code2);

        assertEquals("ERROR (code2) Error is {0}\n", errorAccumulator.toStringFiltered(true, false, false));
        assertEquals("WARNING (code1) Error is {0}\n", errorAccumulator.toStringFiltered(false, true, false));
        assertEquals("INFO (code0) Error is {0}\n", errorAccumulator.toStringFiltered(false, false, true));
        assertEquals("INFO (code0) Error is {0}\n"
                + "WARNING (code1) Error is {0}\n"
                + "ERROR (code2) Error is {0}\n",
        errorAccumulator.toStringFiltered(true, true, true));
    }

}