package com.nedap.archie.rm.support.identification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_REF")
public class PartyRef extends ObjectRef {

    public PartyRef() {
    }

    public PartyRef(ObjectId id, String namespace, String type) {
        super(id, namespace, type);
    }
}
