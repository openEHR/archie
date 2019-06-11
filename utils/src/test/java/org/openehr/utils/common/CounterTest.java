package org.openehr.utils.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class CounterTest {
  private static int expected = 0;

  @Test
  public void instanceShouldNotBeNull() throws Exception {
    assertNotNull(Counter.instance);
  }

  @Test
  public void getAndIncrement() throws Exception {
    int actual;
    for (int i = 0; i < 10; i++) {
      actual = Counter.instance.getAndIncrement();
      assertEquals(expected, actual);
      expected++;
    }
  }

  @Test
  public void getAndDecrement() throws Exception {
    int actual;
    for (int i = 0; i < 10; i++) {
      actual = Counter.instance.getAndDecrement();
      assertEquals(actual, expected);
      expected--;
    }
  }
}