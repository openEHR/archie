package org.openehr.test_rm;

import org.openehr.rm.datatypes.CodePhrase;
import org.openehr.rm.datavalues.DataValue;
import org.openehr.rm.datavalues.DvCodedText;
import org.openehr.rm.datavalues.quantity.DvInterval;
import org.openehr.rm.datavalues.quantity.DvOrdinal;
import org.openehr.rm.datavalues.quantity.DvQuantity;

import javax.annotation.Nullable;

public class SomeType extends TestRMBase {
  @Nullable
  private DvQuantity standardQuantityAttr;

  @Nullable
  private DvQuantity clinicalQuantityAttr_1;

  @Nullable
  private DvQuantity clinicalQuantityAttr_2;

  @Nullable
  private DvQuantity clinicalQuantityAttr_3;

  @Nullable
  private DataValue clinicalQuantityAttr_4;

  @Nullable
  private DataValue clinicalQuantityAttr_5;

  @Nullable
  private DataValue clinicalQuantityAttr_6;

  @Nullable
  private DvOrdinal standardOrdinalAttr;

  @Nullable
  private DataValue clinicalOrdinalAttr_1;

  @Nullable
  private DataValue clinicalOrdinalAttr_2;

  @Nullable
  private CodePhrase clinicalCodedAttr_1;

  @Nullable
  private CodePhrase clinicalCodedAttr_2;

  @Nullable
  private CodePhrase clinicalCodedAttr_3;

  @Nullable
  private CodePhrase clinicalCodedAttr_4;

  @Nullable
  private DvCodedText standardCodedTextAttr;

  @Nullable
  private DvCodedText standardCodedTextAttr_2;

  @Nullable
  private DvCodedText clinicalCodedTextAttr_1;

  @Nullable
  private DvCodedText clinicalCodedTextAttr_2;

  @Nullable
  private DvInterval<DvQuantity> qtyIntervalAttr_1;

  @Nullable
  private DvInterval<DvQuantity> qtyIntervalAttr_2;

  public DvQuantity getStandardQuantityAttr() {
    return standardQuantityAttr;}

  public void setStandardQuantityAttr(DvQuantity value) {
    this.standardQuantityAttr = value;
  }

  public DvQuantity getClinicalQuantityAttr_1() {
    return clinicalQuantityAttr_1;}

  public void setClinicalQuantityAttr_1(DvQuantity value) {
    this.clinicalQuantityAttr_1 = value;
  }

  public DvQuantity getClinicalQuantityAttr_2() {
    return clinicalQuantityAttr_2;}

  public void setClinicalQuantityAttr_2(DvQuantity value) {
    this.clinicalQuantityAttr_2 = value;
  }

  public DvQuantity getClinicalQuantityAttr_3() {
    return clinicalQuantityAttr_3;}

  public void setClinicalQuantityAttr_3(DvQuantity value) {
    this.clinicalQuantityAttr_3 = value;
  }

  public DataValue getClinicalQuantityAttr_4() {
    return clinicalQuantityAttr_4;}

  public void setClinicalQuantityAttr_4(DataValue value) {
    this.clinicalQuantityAttr_4 = value;
  }

  public DataValue getClinicalQuantityAttr_5() {
    return clinicalQuantityAttr_5;}

  public void setClinicalQuantityAttr_5(DataValue value) {
    this.clinicalQuantityAttr_5 = value;
  }

  public DataValue getClinicalQuantityAttr_6() {
    return clinicalQuantityAttr_6;}

  public void setClinicalQuantityAttr_6(DataValue value) {
    this.clinicalQuantityAttr_6 = value;
  }

  public DvOrdinal getStandardOrdinalAttr() {
    return standardOrdinalAttr;}

  public void setStandardOrdinalAttr(DvOrdinal value) {
    this.standardOrdinalAttr = value;
  }

  public DataValue getClinicalOrdinalAttr_1() {
    return clinicalOrdinalAttr_1;}

  public void setClinicalOrdinalAttr_1(DataValue value) {
    this.clinicalOrdinalAttr_1 = value;
  }

  public DataValue getClinicalOrdinalAttr_2() {
    return clinicalOrdinalAttr_2;}

  public void setClinicalOrdinalAttr_2(DataValue value) {
    this.clinicalOrdinalAttr_2 = value;
  }

  public CodePhrase getClinicalCodedAttr_1() {
    return clinicalCodedAttr_1;}

  public void setClinicalCodedAttr_1(CodePhrase value) {
    this.clinicalCodedAttr_1 = value;
  }

  public CodePhrase getClinicalCodedAttr_2() {
    return clinicalCodedAttr_2;}

  public void setClinicalCodedAttr_2(CodePhrase value) {
    this.clinicalCodedAttr_2 = value;
  }

  public CodePhrase getClinicalCodedAttr_3() {
    return clinicalCodedAttr_3;}

  public void setClinicalCodedAttr_3(CodePhrase value) {
    this.clinicalCodedAttr_3 = value;
  }

  public CodePhrase getClinicalCodedAttr_4() {
    return clinicalCodedAttr_4;}

  public void setClinicalCodedAttr_4(CodePhrase value) {
    this.clinicalCodedAttr_4 = value;
  }

  public DvCodedText getStandardCodedTextAttr() {
    return standardCodedTextAttr;}

  public void setStandardCodedTextAttr(DvCodedText value) {
    this.standardCodedTextAttr = value;
  }

  public DvCodedText getStandardCodedTextAttr_2() {
    return standardCodedTextAttr_2;}

  public void setStandardCodedTextAttr_2(DvCodedText value) {
    this.standardCodedTextAttr_2 = value;
  }

  public DvCodedText getClinicalCodedTextAttr_1() {
    return clinicalCodedTextAttr_1;}

  public void setClinicalCodedTextAttr_1(DvCodedText value) {
    this.clinicalCodedTextAttr_1 = value;
  }

  public DvCodedText getClinicalCodedTextAttr_2() {
    return clinicalCodedTextAttr_2;}

  public void setClinicalCodedTextAttr_2(DvCodedText value) {
    this.clinicalCodedTextAttr_2 = value;
  }

  public DvInterval<DvQuantity> getQtyIntervalAttr_1() {
    return qtyIntervalAttr_1;}

  public void setQtyIntervalAttr_1(DvInterval<DvQuantity> value) {
    this.qtyIntervalAttr_1 = value;
  }

  public DvInterval<DvQuantity> getQtyIntervalAttr_2() {
    return qtyIntervalAttr_2;}

  public void setQtyIntervalAttr_2(DvInterval<DvQuantity> value) {
    this.qtyIntervalAttr_2 = value;
  }
}
