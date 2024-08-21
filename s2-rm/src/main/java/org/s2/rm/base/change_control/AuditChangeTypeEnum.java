package org.s2.rm.base.change_control;

import org.s2.util.enumerations.*;

/**
* BMM name: Audit_change_type
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class AuditChangeTypeEnum extends EnumerationTypeString {
  static String[] _itemNames = {"creation", "amendment", "modification", "synthesis", "deleted", "attestation", "restoration", "format conversion", "other"};
  static String[] _itemValues = {};

  public AuditChangeTypeEnum() {
    super(_itemNames, _itemValues);
  }
}
