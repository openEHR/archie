package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.json.JacksonUtil;
import org.junit.Test;

import static org.junit.Assert.*;

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

    @Test
    public void deserializeValueEmptyString() throws JsonProcessingException {
        String json = "{\"value\":\"\"}";
        DvDuration actual = JacksonUtil.getObjectMapper().readValue(json, DvDuration.class);
        assertNull(actual.getValue());
    }
}
