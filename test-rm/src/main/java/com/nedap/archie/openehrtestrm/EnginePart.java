package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;
import java.util.List;

public class EnginePart extends TestRMBase {
  @Nullable
  private List<EnginePartItem> items;

  public List<EnginePartItem> getItems() {
    return items;}

  public void setItems(List<EnginePartItem> value) {
    this.items = value;
  }
}
