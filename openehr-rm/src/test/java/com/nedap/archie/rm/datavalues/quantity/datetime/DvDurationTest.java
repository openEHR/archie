package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class DvDurationTest {

	@Test
	public void testEquals() {
		DvDuration dvDurationOne = new DvDuration("P3Y6M4D");
		DvDuration dvDurationTwo = new DvDuration("P3Y6M4D");
		DvDuration dvDurationThree = new DvDuration("P3Y6M5D");
		DvDuration dvDurationFour = new DvDuration("P3Y6M");
		DvDuration dvDurationFive = new DvDuration("P3Y6M");

		assertEquals(dvDurationOne, dvDurationTwo);
		assertNotEquals(dvDurationOne, dvDurationThree);
		assertNotEquals(dvDurationOne, dvDurationFour);
		assertEquals(dvDurationFour, dvDurationFive);
	}

}