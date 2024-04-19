package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.Test;
import org.openehr.rm.datavalues.quantity.datetime.DvTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DvTimeTest {

	@Test
	public void testEquals() {

		DvTime dvTimeOne = new DvTime("17:33:16");
		DvTime dvTimeTwo = new DvTime("17:33:16");
		DvTime dvTimeThree = new DvTime("17:33:17");
		DvTime dvTimeFour = new DvTime("17");
		DvTime dvTimeFive = new DvTime("17");

		assertEquals(dvTimeOne, dvTimeTwo);
		assertNotEquals(dvTimeOne, dvTimeThree);
		assertNotEquals(dvTimeOne, dvTimeFour);

		assertEquals(dvTimeFour, dvTimeFive);
	}
}