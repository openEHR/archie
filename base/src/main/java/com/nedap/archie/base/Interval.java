package com.nedap.archie.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nedap.archie.rminfo.RMPropertyIgnore;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

/**
 * Interval abstraction, featuring upper and lower limits that may be open or closed, included or not included. Interval of ordered items.
 * <p>
 * Created by pieter.bos on 15/10/15.
 */
@XmlType(name = "INTERVAL")
@XmlAccessorType(XmlAccessType.FIELD)
public class Interval<T> extends OpenEHRBase {

    /**
     * lower bound.
     */
    @Nullable
    private T lower;
    /**
     * Upper bound.
     */
    @Nullable
    private T upper;
    /**
     * lower boundary open (i.e. = -infinity)
     */
	@XmlAttribute(name = "lower_unbounded")
    private boolean lowerUnbounded = false;
    /**
     * upper boundary open (i.e. = +infinity)
     */
	@XmlAttribute(name = "upper_unbounded")
    private boolean upperUnbounded = false;
    /**
     * lower boundary value included in range if not lower_unbounded.
     */
	@XmlAttribute(name = "lower_included")
    private boolean lowerIncluded = true;
    /**
     * upper boundary value included in range if not upper_unbounded.
     */
	@XmlAttribute(name = "upper_included")
    private boolean upperIncluded = true;

    public Interval() {

    }

    public Interval(T value) {
		this(value, value);
    }

    public Interval(T lower, T upper) {
		this(lower, upper, true, true);
    }

    public Interval(T lower, T upper, boolean lowerIncluded, boolean upperIncluded) {
        this.lower = lower;
        this.upper = upper;
        this.lowerIncluded = lowerIncluded;
        this.upperIncluded = upperIncluded;

		if (upper == null) {
			this.upperUnbounded = true;
			this.upperIncluded = false;
		}
		if (lower == null) {
			this.lowerUnbounded = true;
			this.lowerIncluded = false;
		}

    }

    public static <T> Interval<T> lowerUnbounded(T upper, boolean upperIncluded) {
        Interval<T> result = new Interval<>(null, upper, false, upperIncluded);
        result.setLowerUnbounded(true);
        return result;
    }

    public static <T> Interval<T> upperUnbounded(T lower, boolean lowerIncluded) {
        Interval<T> result = new Interval<>(lower, null, lowerIncluded, false);
        result.setUpperUnbounded(true);
        return result;
    }

    public T getLower() {
        return lower;
    }

    public void setLower(T lower) {
        this.lower = lower;
    }

    public T getUpper() {
        return upper;
    }

    public void setUpper(T upper) {
        this.upper = upper;
    }

    public boolean isLowerUnbounded() {
        return lowerUnbounded;
    }

    public void setLowerUnbounded(boolean lowerUnbounded) {
        this.lowerUnbounded = lowerUnbounded;
    }

    public boolean isUpperUnbounded() {
        return upperUnbounded;
    }

    public void setUpperUnbounded(boolean upperUnbounded) {
        this.upperUnbounded = upperUnbounded;
    }

    public boolean isLowerIncluded() {
        return lowerIncluded;
    }

    public void setLowerIncluded(boolean lowerIncluded) {
        this.lowerIncluded = lowerIncluded;
    }

    public boolean isUpperIncluded() {
        return upperIncluded;
    }

    public void setUpperIncluded(boolean upperIncluded) {
        this.upperIncluded = upperIncluded;
    }

    public boolean has(T value) {
		if (lowerUnbounded && upperUnbounded) {
            return true;
        }
        //since TemporalAmount does not implement Comparable we have to do some magic here
        Comparable comparableValue;
        Comparable comparableLower;
        Comparable comparableUpper;
		if (value instanceof TemporalAmount && lower instanceof TemporalAmount && upper instanceof TemporalAmount) {
            //TemporalAmount is not comparable, but can always be converted to a duration that is comparable.
            comparableValue = toComparable(value);
            comparableLower = toComparable(lower);
            comparableUpper = toComparable(upper);
		} else if (!(isComparable(lower) && isComparable(upper) && isComparable(value))) {
            throw new UnsupportedOperationException("subclasses of interval not implementing comparable should implement their own has method");
        } else {
            comparableValue = (Comparable) value;
            comparableLower = (Comparable) lower;
            comparableUpper = (Comparable) upper;
        }

		if (value == null) {
            //interval values are not concerned with cardinality, so return true if not set
            return true;
        }

		if (!lowerUnbounded) {
            int comparedWithLower = comparableValue.compareTo(comparableLower);
            if (comparedWithLower < 0 || (!lowerIncluded && comparedWithLower == 0)) {
                return false;
            }
        }

		if (!upperUnbounded) {
            int comparedWithUpper = comparableValue.compareTo(comparableUpper);
            if (comparedWithUpper > 0 || (!upperIncluded && comparedWithUpper == 0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the lower value as a comparable amount. Required because some temporal amounts are not always directly comparable
     * @return
     */
    @JsonIgnore
    @XmlTransient
    @RMPropertyIgnore
    public Comparable getComparableLower() {
        return toComparable(lower);
    }

    /**
     * Get the lower value as a comparable amount. Required because some temporal amounts are not always directly comparable
     * @return
     */
    @JsonIgnore
    @XmlTransient
    @RMPropertyIgnore
    public Comparable getComparableUpper() {
        return toComparable(upper);
    }

    private Comparable toComparable(T value) {
        if(value == null) {
            return null;
        }
        if (value instanceof TemporalAmount && !(value instanceof Comparable) && isNonComparableTemporalAmount(value)) {
            //TemporalAmount is not comparable, but can always be converted to a duration that is comparable.
            return IntervalDurationConverter.from((TemporalAmount) value);

        } else if (!isComparable(value)) {
            throw new UnsupportedOperationException("subclasses of interval not implementing comparable should implement their own has method");
        } else {
            return (Comparable) value;
        }
    }

    private int compareTo(T intervalValue, T value) {
        Comparable comparableIntervalValue;
        Comparable comparableValue;
        if (value instanceof TemporalAmount && !(value instanceof Comparable) && isNonComparableTemporalAmount(intervalValue)) {
            //TemporalAmount is not comparable, but can always be converted to a duration that is comparable.
            comparableValue = value == null ? null : IntervalDurationConverter.from((TemporalAmount) value);
            comparableIntervalValue = intervalValue == null ? null : IntervalDurationConverter.from((TemporalAmount) intervalValue);
        } else if (!(isComparable(intervalValue) && isComparable(value))) {
            throw new UnsupportedOperationException("subclasses of interval not implementing comparable should implement their own has method");
        } else {
            comparableValue = (Comparable) value;
            comparableIntervalValue = (Comparable) intervalValue;
        }
        return comparableValue.compareTo(comparableIntervalValue);
    }

    private boolean isNonComparableTemporalAmount(T value) {
        return value == null || (!(value instanceof Comparable) && value instanceof TemporalAmount);
    }

    private boolean isComparable(T value) {
        return value == null || value instanceof Comparable;
    }


    /**
     * True if there is any overlap between intervals represented by Current and
     * `other'. True if at least one limit of other is strictly inside the limits
     * of this interval.
     *
     * @param other
     * @return
     */
    public Boolean intersects(Interval<T> other) {
        return (lowerUnbounded && other.lowerUnbounded) ||
                (upperUnbounded && other.upperUnbounded) ||
                (compareTo(lower, other.lower) < 0 && compareTo(upper, other.upper) < 0 && compareTo(other.lower, upper) < 0) ||
                (compareTo(other.lower, lower) < 0 && compareTo(other.upper, upper) < 0 && compareTo(lower, other.upper) < 0) ||
                other.contains(this) || this.contains(other);
    }

    /**
     * True if current interval properly contains `other'? True if all points of
     * `other' are inside the current interval.
     *
     * @param other
     * @return
     */
    public Boolean contains(Interval<T> other) {
        boolean otherHasLower = false;
        boolean otherHasUpper = false;
		if (other.lowerUnbounded) {
            otherHasLower = this.lowerUnbounded;
        } else {
            otherHasLower = has(other.lower);
        }
		if (other.upperUnbounded) {
            otherHasUpper = this.upperUnbounded;
        } else {
            otherHasUpper = has(other.upper);
        }
        return otherHasLower && otherHasUpper;
    }

    /**
     * Returns true if both sets subsume each other.
     *
     * @param other
     * @return
     */
    public Boolean setsAreEqual(Interval<T> other) {
        return this.contains(other) && other.contains(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interval<?> interval = (Interval<?>) o;

        return (lowerUnbounded == interval.lowerUnbounded) &&
            (upperUnbounded == interval.upperUnbounded) &&
            (lowerUnbounded || lowerIncluded == interval.lowerIncluded) &&
            (upperUnbounded || upperIncluded == interval.upperIncluded) &&
                (lowerUnbounded || Objects.equals(lower, interval.lower)) &&
                (upperUnbounded || Objects.equals(upper, interval.upper));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                lowerUnbounded,
                upperUnbounded,
                lowerUnbounded? false: lowerIncluded,
                upperUnbounded? false: upperIncluded,
                lowerUnbounded? null : lower,
                upperUnbounded? null : upper);
    }

    @Override
    public String toString() {

		if (lowerUnbounded) {
            return "|" + (upperIncluded ? "<=" : "<") + upper + "|";
        }
		if (upperUnbounded) {
            return "|" + (lowerIncluded ? ">=" : ">") + lower + "|";
        }

		if (lower != null && upper != null && lower == upper) {
            return lower.toString();
        }
        StringBuilder result = new StringBuilder();
        result.append("|");
		if (!lowerIncluded) {
            result.append(">");
        }
        result.append(lower);
        result.append("..");
		if (!upperIncluded) {
            result.append("<");
        }
        result.append(upper);
        result.append("|");
        return result.toString();
    }

    public void fixUnboundedIncluded() {
        if (upperUnbounded) {
            this.upperIncluded = false;
        }
        if (lowerUnbounded) {
            this.lowerIncluded = false;
        }
    }
}
