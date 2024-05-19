package org.openehr.test_rm;

import java.lang.String;
import javax.annotation.Nullable;

public class EnginePartItem extends TestRMBase {
  @Nullable
  private String model;

  @Nullable
  private String type;

  public String getModel() {
    return model;}

  public void setModel(String value) {
    this.model = value;
  }

  public String getType() {
    return type;}

  public void setType(String value) {
    this.type = value;
  }
}
