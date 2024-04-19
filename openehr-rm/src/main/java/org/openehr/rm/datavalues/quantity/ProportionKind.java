package org.openehr.rm.datavalues.quantity;

/**
 * Created by pieter.bos on 01/03/16.
 */
public enum ProportionKind {
    RATIO(0), UNITARY(1), PERCENT(2), FRACTION(3), INTEGER_FRACTION(4);

    private final long pk;

    ProportionKind(long pk) {
        this.pk = pk;
    }

    public long getPk() {
        return pk;
    }
}
