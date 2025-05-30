package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;
import java.util.List;

public class MultiplicityObject extends TestRMBase {
  @Nullable
  private List<Long> integerList;

  @Nullable
  private List<String> stringList;

  public List<Long> getLongList() {
    return integerList;}

  public void setLongList(List<Long> value) {
    this.integerList = value;
  }

  public List<String> getStringList() {
    return stringList;}

  public void setStringList(List<String> value) {
    this.stringList = value;
  }
}
