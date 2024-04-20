package org.openehr.test_rm;

import org.openehr.rm.datavalues.DataValue;
import org.openehr.rm.datavalues.DvCodedText;

import javax.annotation.Nullable;

public class Element extends Item {
  private DvCodedText nullFlavour;

  @Nullable
  private DataValue value;

  public DvCodedText getNullFlavour() {
    return nullFlavour;}

  public void setNullFlavour(DvCodedText value) {
    this.nullFlavour = value;
  }

  public DataValue getValue() {
    return value;}

  public void setValue(DataValue value) {
    this.value = value;
  }
}
