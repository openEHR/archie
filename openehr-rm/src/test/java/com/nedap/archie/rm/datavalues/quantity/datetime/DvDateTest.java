package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.Test;
import org.openehr.rm.datavalues.quantity.datetime.DvDate;

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
		DvDate dvDateSix = new DvDate("2019");
		DvDate dvDateSeven = new DvDate("2019");

		assertEquals(dvDateOne, dvDateTwo);
		assertNotEquals(dvDateOne, dvDateThree);
		assertNotEquals(dvDateOne, dvDateFour);
		assertNotEquals(dvDateOne, dvDateFive);
		assertEquals(dvDateFour, dvDateFive);
		assertEquals(dvDateSix, dvDateSeven);
	}
}