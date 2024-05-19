package org.openehr.test_rm;

import java.lang.String;
import java.util.List;
import javax.annotation.Nullable;

public class CarBody extends TestRMBase {
  @Nullable
  private String model;

  @Nullable
  private String description;

  @Nullable
  private List<CarBodyPart> parts;

  public String getModel() {
    return model;}

  public void setModel(String value) {
    this.model = value;
  }

  public String getDescription() {
    return description;}

  public void setDescription(String value) {
    this.description = value;
  }

  public List<CarBodyPart> getParts() {
    return parts;}

  public void setParts(List<CarBodyPart> value) {
    this.parts = value;
  }
}
