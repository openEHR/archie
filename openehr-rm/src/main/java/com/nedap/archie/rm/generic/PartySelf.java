package com.nedap.archie.rm.generic;

import com.nedap.archie.rm.support.identification.PartyRef;

import javax.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_SELF")
public class PartySelf extends PartyProxy {

    public PartySelf() {
    }

    public PartySelf(@Nullable PartyRef externalRef) {
        super(externalRef);
    }
}
