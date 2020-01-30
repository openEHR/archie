package com.nedap.archie.aom;

import com.nedap.archie.aom.primitives.CString;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 23/02/16.
 */
public class StringConstraintsTest {

    @Test
    public void stringConstants() {
        CString noConstraint = new CString();
        assertTrue(noConstraint.isValidValue("a"));
        assertTrue(noConstraint.isValidValue("b"));
        assertTrue(noConstraint.isValidValue("The complete works of William Shakespeare"));

        CString oneConstraint = new CString();
        oneConstraint.addConstraint("very specific secret string");
        assertTrue(oneConstraint.isValidValue("very specific secret string"));
        assertFalse(oneConstraint.isValidValue("not specific at all not secret string"));


        CString moreConstraints = new CString();
        moreConstraints.addConstraint("very specific secret string");
        moreConstraints.addConstraint("another string");
        assertTrue(moreConstraints.isValidValue("very specific secret string"));
        assertTrue(moreConstraints.isValidValue("another string"));
        assertFalse(moreConstraints.isValidValue("not specific at all not secret string"));


    }

    @Test
    public void stringRegexps() {

        CString oneConstraint = new CString();
        oneConstraint.addConstraint("/a+b*/");
        assertTrue(oneConstraint.isValidValue("a"));
        assertTrue(oneConstraint.isValidValue("aa"));
        assertTrue(oneConstraint.isValidValue("aaaaaaaabbbbb"));
        assertFalse(oneConstraint.isValidValue("aaaaaaaabbbbbc"));

        CString moreConstraints = new CString();
        moreConstraints.addConstraint("/a+b*/");
        moreConstraints.addConstraint("^dbca+^");
        assertTrue(moreConstraints.isValidValue("ab"));
        assertTrue(moreConstraints.isValidValue("aabb"));
        assertTrue(moreConstraints.isValidValue("dbcaa"));
        assertFalse(moreConstraints.isValidValue("Something else"));

        CString mixedConstraints = new CString();
        mixedConstraints.addConstraint("/a+b*/");
        mixedConstraints.addConstraint("^dbca+^");
        mixedConstraints.addConstraint("Something else");
        assertTrue(mixedConstraints.isValidValue("ab"));
        assertTrue(mixedConstraints.isValidValue("aabb"));
        assertTrue(mixedConstraints.isValidValue("dbcaa"));
        assertTrue(mixedConstraints.isValidValue("Something else"));
        assertFalse(mixedConstraints.isValidValue("what more?"));
    }
}
