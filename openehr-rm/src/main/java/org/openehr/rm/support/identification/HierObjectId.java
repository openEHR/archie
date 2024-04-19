package org.openehr.rm.support.identification;

/**
 * Created by pieter.bos on 08/07/16.
 */

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HIER_OBJECT_ID")
public class HierObjectId extends UIDBasedId {

    public HierObjectId() {
    }

    public HierObjectId(String value) {
        super(value);
    }

    public static HierObjectId createRandomUUID() {
        return new HierObjectId(java.util.UUID.randomUUID().toString());
    }
}
