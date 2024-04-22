package org.openehr.test_rm;

import java.util.List;
import javax.annotation.Nullable;

public class EnginePart extends TestRMBase {
  @Nullable
  private List<EnginePartItem> items;

  public List<EnginePartItem> getItems() {
    return items;}

  public void setItems(List<EnginePartItem> value) {
    this.items = value;
  }
}
