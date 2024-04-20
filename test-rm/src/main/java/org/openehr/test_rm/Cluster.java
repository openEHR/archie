package org.openehr.test_rm;

import java.util.List;

public class Cluster extends Item {
  private List<Item> items;

  public List<Item> getItems() {
    return items;}

  public void setItems(List<Item> value) {
    this.items = value;
  }
}
