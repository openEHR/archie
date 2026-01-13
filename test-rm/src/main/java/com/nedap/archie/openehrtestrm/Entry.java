package com.nedap.archie.openehrtestrm;

import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DataValue;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;

import javax.annotation.Nullable;
import java.util.List;

public class Entry extends TestRMBase {
  @Nullable
  private CodePhrase value;

  @Nullable
  private DvCodedText codedTextValue;

  @Nullable
  private DvOrdinal ordinalAttr_1;

  @Nullable
  private Element elementAttr;

  @Nullable
  private List<Element> elementAttr_2;

  @Nullable
  private DataValue item;

  public CodePhrase getValue() {
    return value;}

  public void setValue(CodePhrase value) {
    this.value = value;
  }

  public DvCodedText getCodedTextValue() {
    return codedTextValue;}

  public void setCodedTextValue(DvCodedText value) {
    this.codedTextValue = value;
  }

  public DvOrdinal getOrdinalAttr_1() {
    return ordinalAttr_1;}

  public void setOrdinalAttr_1(DvOrdinal value) {
    this.ordinalAttr_1 = value;
  }

  public Element getElementAttr() {
    return elementAttr;}

  public void setElementAttr(Element value) {
    this.elementAttr = value;
  }

  public List<Element> getElementAttr_2() {
    return elementAttr_2;}

  public void setElementAttr_2(List<Element> value) {
    this.elementAttr_2 = value;
  }

  public DataValue getItem() {
    return item;}

  public void setItem(DataValue value) {
    this.item = value;
  }
}
