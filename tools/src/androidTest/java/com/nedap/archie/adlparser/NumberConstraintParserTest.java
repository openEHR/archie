package com.nedap.archie.adlparser;

import com.google.common.collect.Lists;
import com.nedap.archie.aom.primitives.CInteger;
import com.nedap.archie.aom.primitives.CReal;
import com.nedap.archie.base.Interval;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pieter.bos on 01/11/15.
 */
public class NumberConstraintParserTest extends PrimitivesConstraintParserTest {

    /*
    integer_attr1 matches {55}
		integer_attr2 matches {10, 20, 30}
		integer_attr3 matches {|0..100|}
		integer_attr4 matches {|>0..100|}
		integer_attr5 matches {|0..<100|}
		integer_attr6 matches {|>0..<100|}
		integer_attr7 matches {|>10|}
		integer_attr8 matches {|<10|}
		integer_attr9 matches {|>=10|}
		integer_attr10 matches {|<=10|}
		integer_attr11 matches {|-10..-5|}
		integer_attr12 matches {10}
		integer_attr13
     */

    @Test
    public void integers() {
        CInteger integerAttr1 = getAttribute("integer_attr1");
        CInteger integerAttr2 = getAttribute("integer_attr2");
        CInteger integerAttr3 = getAttribute("integer_attr3");
		CInteger integerAttr4 = getAttribute("integer_attr4");
		CInteger integerAttr5 = getAttribute("integer_attr5");
		CInteger integerAttr6 = getAttribute("integer_attr6");
		CInteger integerAttr7 = getAttribute("integer_attr7");
		CInteger integerAttr8 = getAttribute("integer_attr8");
		CInteger integerAttr9 = getAttribute("integer_attr9");
		CInteger integerAttr10 = getAttribute("integer_attr10");
		CInteger integerAttr11 = getAttribute("integer_attr11");
		CInteger integerAttr12 = getAttribute("integer_attr12");
        assertEquals(new Interval<>(55l), integerAttr1.getConstraint().get(0));
		//2 = list!
		assertEquals(new Interval<>(10l), integerAttr2.getConstraint().get(0));
		assertEquals(new Interval<>(20l), integerAttr2.getConstraint().get(1));
		assertEquals(new Interval<>(30l), integerAttr2.getConstraint().get(2));

        assertEquals(new Interval<>(0l, 100l), integerAttr3.getConstraint().get(0));
        assertEquals(new Interval<>(0l, 100l, false, true), integerAttr4.getConstraint().get(0));
		assertEquals(new Interval<>(0l, 100l, true, false), integerAttr5.getConstraint().get(0));
		assertEquals(new Interval<>(0l, 100l, false, false), integerAttr6.getConstraint().get(0));
		assertEquals(Interval.upperUnbounded(10l, false), integerAttr7.getConstraint().get(0));
		assertEquals(Interval.lowerUnbounded(10l, false), integerAttr8.getConstraint().get(0));
		assertEquals(Interval.upperUnbounded(10l, true), integerAttr9.getConstraint().get(0));
		assertEquals(Interval.lowerUnbounded(10l, true), integerAttr10.getConstraint().get(0));
		assertEquals(new Interval<>(-10l, -5l), integerAttr11.getConstraint().get(0));
		assertEquals(new Interval<>(10l), integerAttr12.getConstraint().get(0));
    }


	/*
	real_attr1 matches {100.0}
		real_attr2 matches {10.0, 20.0, 30.0}
		real_attr3 matches {|0.0..100.0|}
		real_attr4 matches {|>0.0..100.0|}
		real_attr5 matches {|0.0..<100.0|}
		real_attr6 matches {|>0.0..<100.0|}
		real_attr7 matches {|>=10.0|}
		real_attr8 matches {|<=10.0|}
		real_attr9 matches {|>=10.0|}
		real_attr10 matches {|<=10.0|}
		real_attr11 matches {|-10.0..-5.0|}
		real_attr12 matches {10.0}
		real_attr13
	 */
    @Test
    public void reals() {
		CReal realAttr1 = getAttribute("real_attr1");
		CReal realAttr2 = getAttribute("real_attr2");
		CReal realAttr3 = getAttribute("real_attr3");
		CReal realAttr4 = getAttribute("real_attr4");
		CReal realAttr5 = getAttribute("real_attr5");
		CReal realAttr6 = getAttribute("real_attr6");
		CReal realAttr7 = getAttribute("real_attr7");
		CReal realAttr8 = getAttribute("real_attr8");
		CReal realAttr9 = getAttribute("real_attr9");
		CReal realAttr10 = getAttribute("real_attr10");
		CReal realAttr11 = getAttribute("real_attr11");
		CReal realAttr12 = getAttribute("real_attr12");
		assertEquals(new Interval<>(100d), realAttr1.getConstraint().get(0));
		//2 = list!
		assertEquals(new Interval<>(10d), realAttr2.getConstraint().get(0));
		assertEquals(new Interval<>(20d), realAttr2.getConstraint().get(1));
		assertEquals(new Interval<>(30d), realAttr2.getConstraint().get(2));

		assertEquals(new Interval<>(0d, 100d), realAttr3.getConstraint().get(0));
		assertEquals(new Interval<>(0d, 100d, false, true), realAttr4.getConstraint().get(0));
		assertEquals(new Interval<>(0d, 100d, true, false), realAttr5.getConstraint().get(0));
		assertEquals(new Interval<>(0d, 100d, false, false), realAttr6.getConstraint().get(0));
		assertEquals(Interval.upperUnbounded(10d, true), realAttr7.getConstraint().get(0));
		assertEquals(Interval.lowerUnbounded(10d, true), realAttr8.getConstraint().get(0));
		assertEquals(Interval.upperUnbounded(10d, true), realAttr9.getConstraint().get(0));
		assertEquals(Interval.lowerUnbounded(10d, true), realAttr10.getConstraint().get(0));
		assertEquals(new Interval<>(-10d, -5d), realAttr11.getConstraint().get(0));
		assertEquals(new Interval<>(10d), realAttr12.getConstraint().get(0));
    }


	/*integer_attr1 matches {55}
	integer_attr2 matches {10, 20, 30; 10}
	integer_attr3 matches {|0..100|; 10}
	integer_attr4 matches {|>10|; 11}
	integer_attr5 matches {|<10|; 9}
	integer_attr6 matches {|>=10|; 10}
	integer_attr7 matches {|<=10|; 5}
	integer_attr8 matches {|-10..-5|; -8}
	integer_attr9 matches {10}
	*/
    @Test
    public void assumedIntegerValues() throws Exception {
		archetype = getAssumedValuesArchetype();
		CInteger integerAttr1 = getAttribute("integer_attr1");
		CInteger integerAttr2 = getAttribute("integer_attr2");
		CInteger integerAttr3 = getAttribute("integer_attr3");
		CInteger integerAttr4 = getAttribute("integer_attr4");
		CInteger integerAttr5 = getAttribute("integer_attr5");
		CInteger integerAttr6 = getAttribute("integer_attr6");
		CInteger integerAttr7 = getAttribute("integer_attr7");
		CInteger integerAttr8 = getAttribute("integer_attr8");

		assertEquals(new Interval<>(55l), integerAttr1.getConstraint().get(0));
		//2 = list!
		assertEquals(new Interval<>(10l), integerAttr2.getConstraint().get(0));
		assertEquals(new Interval<>(20l), integerAttr2.getConstraint().get(1));
		assertEquals(new Interval<>(30l), integerAttr2.getConstraint().get(2));
		assertEquals((Long) 10l, integerAttr2.getAssumedValue());

		assertEquals(new Interval<>(0l, 100l), integerAttr3.getConstraint().get(0));
		assertEquals((Long) 10l, integerAttr3.getAssumedValue());

		assertEquals(Interval.upperUnbounded(10l, false), integerAttr4.getConstraint().get(0));
		assertEquals((Long) 11l, integerAttr4.getAssumedValue());
		assertEquals(Interval.lowerUnbounded(10l, false), integerAttr5.getConstraint().get(0));
		assertEquals((Long) 9l, integerAttr5.getAssumedValue());
		assertEquals(Interval.upperUnbounded(10l, true), integerAttr6.getConstraint().get(0));
		assertEquals((Long) 10l, integerAttr6.getAssumedValue());
		assertEquals(Interval.lowerUnbounded(10l, true), integerAttr7.getConstraint().get(0));
		assertEquals((Long) 5l, integerAttr7.getAssumedValue());


		assertEquals(new Interval<>(-10l, -5l), integerAttr8.getConstraint().get(0));
		assertEquals(new Long(-8l), integerAttr8.getAssumedValue());


    }

	/*
	real_attr1 matches {100.0}
	real_attr2 matches {10.0, 20.0, 30.0; 20.0}
	real_attr3 matches {|0.0..100.0|; 20.4}
	real_attr4 matches {|>=10.0|; 20.0}
	real_attr5 matches {|<=10.0|; 9.5}
	real_attr6 matches {|>=10.0|; 20.3}
	real_attr7 matches {|<=10.0|; 8.0}
	real_attr8 matches {|-10.0..-5.0|; -9.8}
	real_attr9 matches {10.0}
	 */
	@Test
	public void assumedRealValues() throws Exception {
		archetype = getAssumedValuesArchetype();
		CReal realAttr1 = getAttribute("real_attr1");
		CReal realAttr2 = getAttribute("real_attr2");
		CReal realAttr3 = getAttribute("real_attr3");
		CReal realAttr4 = getAttribute("real_attr4");
		CReal realAttr5 = getAttribute("real_attr5");
		CReal realAttr6 = getAttribute("real_attr6");
		CReal realAttr7 = getAttribute("real_attr7");
		CReal realAttr8 = getAttribute("real_attr8");

		assertEquals(new Interval<>(100d), realAttr1.getConstraint().get(0));
		assertNull(realAttr1.getAssumedValue());
		//2 = list!
		assertEquals(new Interval<>(10d), realAttr2.getConstraint().get(0));
		assertEquals(new Interval<>(20d), realAttr2.getConstraint().get(1));
		assertEquals(new Interval<>(30d), realAttr2.getConstraint().get(2));
		assertEquals(new Double(20d), realAttr2.getAssumedValue());


		assertEquals(new Interval<>(0d, 100d), realAttr3.getConstraint().get(0));
		assertEquals(new Double(20.4d), realAttr3.getAssumedValue());


		assertEquals(Interval.upperUnbounded(10d, true), realAttr4.getConstraint().get(0));
		assertEquals(new Double(20d), realAttr4.getAssumedValue());
		assertEquals(Interval.lowerUnbounded(10d, true), realAttr5.getConstraint().get(0));
		assertEquals(new Double(9.5d), realAttr5.getAssumedValue());
		assertEquals(Interval.upperUnbounded(10d, true), realAttr6.getConstraint().get(0));
		assertEquals(new Double(20.3d), realAttr6.getAssumedValue());
		assertEquals(Interval.lowerUnbounded(10d, true), realAttr7.getConstraint().get(0));
		assertEquals(new Double(8.0d), realAttr7.getAssumedValue());
		assertEquals(new Interval<>(-10d, -5d), realAttr8.getConstraint().get(0));

	}
}
