package com.nedap.archie.openehrtestrm;

import javax.annotation.Nullable;

public class Rim extends TestRMBase {
  @Nullable
  private Long nuts;

  @Nullable
  private String hubcap;

  public Long getNuts() {
    return nuts;}

  public void setNuts(Long value) {
    this.nuts = value;
  }

  public String getHubcap() {
    return hubcap;}

  public void setHubcap(String value) {
    this.hubcap = value;
  }
}
