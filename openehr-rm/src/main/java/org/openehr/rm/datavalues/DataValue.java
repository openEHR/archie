package org.openehr.rm.datavalues;

import com.nedap.archie.base.RMObject;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * TODO: check if we can add common fields here for convenience, for example value and magnitude
 * <p>
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATA_VALUE")
public abstract class DataValue extends RMObject {
}
