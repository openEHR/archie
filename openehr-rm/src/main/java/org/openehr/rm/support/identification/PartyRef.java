package org.openehr.rm.support.identification;

import com.nedap.archie.rminfo.Invariant;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by pieter.bos on 01/03/16.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PARTY_REF")
public class PartyRef extends ObjectRef<ObjectId> {

    public PartyRef() {
    }

    public PartyRef(ObjectId id, String namespace, String type) {
        super(id, namespace, type);
    }

    @Invariant("Type_validity")
    public boolean typeValidity() {
        String type = getType();
        if(type == null) {
            return true;
        }

        return type.equals("PERSON") ||
                type.equals("ORGANISATION") ||
                type.equals("GROUP") ||
                type.equals("AGENT") ||
                type.equals("ROLE") ||
                type.equals("PARTY") ||
                type.equals("ACTOR");
    }
}
