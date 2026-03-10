package com.nedap.archie.rm.datavalues.quantity.datetime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nedap.archie.json.JacksonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	public void testPartialDateTime(){
		assertEquals("2018-02-01T09:00", new DvDateTime("2018-02-01T09").getValue().toString());
		assertEquals("2018-02-01T09:00+02:00", new DvDateTime("2018-02-01T09+02:00").getValue().toString());
		assertEquals("2019-01-28", new DvDateTime("2019-01-28").getValue().toString());
		assertEquals("2019-01", new DvDateTime("2019-01").getValue().toString());
		assertEquals("2019", new DvDateTime("2019").getValue().toString());
	}

    @Test
    public void deserializeValueEmptyString() throws JsonProcessingException {
        String json = "{\"value\":\"\"}";
        DvDateTime actual = JacksonUtil.getObjectMapper().readValue(json, DvDateTime.class);
        assertNull(actual.getValue());
    }
}
