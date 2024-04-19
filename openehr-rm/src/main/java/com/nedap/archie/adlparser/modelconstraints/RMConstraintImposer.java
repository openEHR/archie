package com.nedap.archie.adlparser.modelconstraints;

import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;

/**
 * Constraints imposer for the Archie reference model implementation.
 *
 * Created by pieter.bos on 04/11/15.
 */
public class RMConstraintImposer extends ReflectionConstraintImposer {

    public RMConstraintImposer() {
        super(OpenEhrRmInfoLookup.getInstance());
    }

}
