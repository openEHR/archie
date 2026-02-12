package com.nedap.archie.rm.datavalues.quantity.datetime;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.json.JacksonUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

    @Test
    public void deserializeValueEmptyString() throws JsonProcessingException {
        String json = "{\"value\":\"\"}";
        DvDuration actual = JacksonUtil.getObjectMapper().readValue(json, DvDuration.class);
        assertNull(actual.getValue());
    }
}
