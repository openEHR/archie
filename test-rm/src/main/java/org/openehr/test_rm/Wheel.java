package org.openehr.test_rm;

import java.lang.String;
import java.util.List;
import javax.annotation.Nullable;

public class Wheel extends TestRMBase {
  @Nullable
  private String description;

  @Nullable
  private List<Rim> parts;

  public String getDescription() {
    return description;}

  public void setDescription(String value) {
    this.description = value;
  }

  public List<Rim> getParts() {
    return parts;}

  public void setParts(List<Rim> value) {
    this.parts = value;
  }
}
