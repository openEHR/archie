package org.openehr.utils.datatype;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataTypeTest {

  @Test
  public void createDataType() throws Exception {
    DataType<Integer> data = new DataType<>(1);
    assertNotNull(data);
  }

  @Test
  public void getValue() throws Exception {
    Integer expect = 1;
    DataType<Integer> data = new DataType<>(expect);
    Integer actual = data.getValue();
    assertEquals(actual, expect);
  }

  @Test
  public void setValue() throws Exception {
    DataType<Integer> data = new DataType<>(0);
    Integer expect = 1;
    data.setValue(expect);
    Integer actual = data.getValue();
    assertEquals(actual, expect);
  }
}