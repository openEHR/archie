package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DvDateTest {

	@Test
	public void testEquals() {

		DvDate dvDateOne = new DvDate("2019-07-11");
		DvDate dvDateTwo = new DvDate("2019-07-11");
		DvDate dvDateThree = new DvDate("2019-07-12");
		DvDate dvDateFour = new DvDate("2019-07");
		DvDate dvDateFive = new DvDate("2019-07");

		assertEquals(dvDateOne, dvDateTwo);
		assertNotEquals(dvDateOne, dvDateThree);
		assertNotEquals(dvDateOne, dvDateFour);
		assertNotEquals(dvDateOne, dvDateFive);
		assertEquals(dvDateFour, dvDateFive);
	}
}