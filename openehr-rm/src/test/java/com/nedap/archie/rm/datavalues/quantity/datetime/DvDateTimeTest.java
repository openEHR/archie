package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DvDateTimeTest {

	private DvDateTime dvDateTimeThre;

	@Test
	public void testEquals() {
		DvDateTime dvDateTimeOne = new DvDateTime("2018-02-01T09:00:22");
		DvDateTime dvDateTimeTwo = new DvDateTime("2018-02-01T09:00:22");
		DvDateTime dvDateTimeThree = new DvDateTime("2018-02-01T09:00:24");
		DvDateTime dvDateTimeFour = new DvDateTime("2018-02-01T09:00");
		DvDateTime dvDateTimeFive = new DvDateTime("2018-02-01T09:00");

		assertEquals(dvDateTimeOne, dvDateTimeTwo);
		assertNotEquals(dvDateTimeOne, dvDateTimeThree);
		assertNotEquals(dvDateTimeOne, dvDateTimeFour);
		assertNotEquals(dvDateTimeOne, dvDateTimeFive);

		assertEquals(dvDateTimeFour, dvDateTimeFive);

	}
}