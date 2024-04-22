package com.nedap.archie.rm.datavalues;

import org.junit.Test;
import org.openehr.rm.datavalues.DvURI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DvURITest {

	@Test
	public void testEquals() {
		DvURI dvURIOne = new DvURI("http://specifications.openehr.org/");
		DvURI dvURITwo = new DvURI("http://specifications.openehr.org/");
		DvURI dvURIThree = new DvURI("http://specifications.openehr.de/");

		assertEquals(dvURIOne, dvURITwo);
		assertNotEquals(dvURIOne, dvURIThree);


	}
}