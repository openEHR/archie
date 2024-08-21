package org.s2.rm.base.change_control;

import org.s2.rm.base.model_support.definitions.SampleFunctionKindEnum;
import org.s2.util.enumerations.*;

/**
* BMM name: Attestation_reason
* isAbstract: false | isPrimitiveType: false | isOverride: false
* BMM schema: S2RM 0.8.0
*/
public class AttestationReasonEnum extends EnumerationType<String> {
  static String[] _itemNames = {"signed", "witnessed"};
  static String[] _itemValues = {};

  public AttestationReasonEnum() {
    super(_itemNames, _itemValues);
  }

  private static AttestationReasonEnum INSTANCE;
  public static AttestationReasonEnum getInstance() {
    if (INSTANCE == null)
      INSTANCE = new AttestationReasonEnum();
    return INSTANCE;
  }
}
