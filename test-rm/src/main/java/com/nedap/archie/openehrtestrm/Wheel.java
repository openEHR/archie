package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;
import java.util.List;

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
