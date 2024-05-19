package org.openehr.test_rm;

import java.lang.String;
import java.util.List;
import javax.annotation.Nullable;

public class Book extends TestRMBase {
  private String author;

  private String title;

  @Nullable
  private List<Chapter> chapters;

  public String getAuthor() {
    return author;}

  public void setAuthor(String value) {
    this.author = value;
  }

  public String getTitle() {
    return title;}

  public void setTitle(String value) {
    this.title = value;
  }

  public List<Chapter> getChapters() {
    return chapters;}

  public void setChapters(List<Chapter> value) {
    this.chapters = value;
  }
}
