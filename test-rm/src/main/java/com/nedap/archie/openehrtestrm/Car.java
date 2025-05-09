package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;
import java.util.List;

public class Car extends TestRMBase {
  @Nullable
  private CarBody body;

  @Nullable
  private List<EnginePart> engineParts;

  @Nullable
  private List<Wheel> wheels;

  public CarBody getBody() {
    return body;}

  public void setBody(CarBody value) {
    this.body = value;
  }

  public List<EnginePart> getEngineParts() {
    return engineParts;}

  public void setEngineParts(List<EnginePart> value) {
    this.engineParts = value;
  }

  public List<Wheel> getWheels() {
    return wheels;}

  public void setWheels(List<Wheel> value) {
    this.wheels = value;
  }
}
