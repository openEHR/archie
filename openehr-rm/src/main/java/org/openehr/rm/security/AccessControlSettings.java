package org.openehr.rm.security;

import com.nedap.archie.base.RMObject;

import javax.xml.bind.annotation.XmlType;

/**
 * Abstract class for access control settings. To be implemented by users of this library.
 *
 * Created by pieter.bos on 08/07/16.
 */
@XmlType(name="ACCESS_CONTROL_SETTINGS")
public abstract class AccessControlSettings extends RMObject {
}
